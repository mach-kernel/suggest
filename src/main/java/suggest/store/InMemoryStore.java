package suggest.store;
import suggest.Pair;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Spliterators;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class InMemoryStore implements Store {
  private final AtomicBoolean sealed = new AtomicBoolean(false);
  private final ConcurrentHashMap<String, Long> fullToCount = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, List<Long>> fragmentToSuggestion = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Long, RankedQuery> fullQueries = new ConcurrentHashMap<>();
  private final AtomicLong idSerial = new AtomicLong();
  private final AtomicLong numQueries = new AtomicLong();
  private final AtomicLong numTokens = new AtomicLong();

  public Stream<RankedQuery> suggestionForFragment(String fragment) {
    return this.fragmentToSuggestion
               .getOrDefault(fragment, new LinkedList<Long>())
               .stream()
               .map(fullQueries::get);
  }

  public Stream<Pair<Long, RankedQuery>> allQueries() {
    Spliterator<Entry<Long, RankedQuery>> spliterator = Spliterators.spliteratorUnknownSize(
      this.fullQueries.entrySet().iterator(),
      0
    );

    return StreamSupport.stream(spliterator, true)
                        .map(
                          (Entry<Long, RankedQuery> e) ->
                            new Pair<Long, RankedQuery>(e.getKey(), e.getValue())
                          );
  }

  public void registerQuery(String query) {
    if (this.sealed.get()) { return; }
    this.fullToCount.computeIfPresent(query, (String _k, Long v) -> v + 1);
    if (!this.fullToCount.containsKey(query)) {
      this.fullToCount.put(query, 1L);
      this.numQueries.incrementAndGet();
    }
  }

  public void finishedRegisteringQueries() {
    this.sealed.set(true);
    this.indexQueries();
    this.fullToCount.clear();
  }

  public void registerFragment(String token, Long id) {
    final Optional<List<Long>> maybeHits = Optional.ofNullable(this.fragmentToSuggestion.get(token));
    if (maybeHits.isPresent()) {
      final List<Long> hits = maybeHits.get();
      synchronized (hits) { hits.add(id); }
    } else {
      this.fragmentToSuggestion.put(token, new LinkedList<Long>(Arrays.asList(id)));
      this.numTokens.incrementAndGet();
    }
  }

  public Long getNumQueries() { return this.numQueries.get(); }
  public Long getNumTokens() { return this.numTokens.get(); }

  private void indexQueries() {
    this.fullToCount.forEachEntry(1L, (Entry<String, Long> entry) -> {
      this.fullQueries.put(
        idSerial.incrementAndGet(),
        new RankedQuery(entry.getKey(), entry.getValue())
      );
    });
  }
}