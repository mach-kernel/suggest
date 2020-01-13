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
import java.util.stream.Stream;

public final class InMemoryStore implements Store {
  private final AtomicBoolean sealed = new AtomicBoolean(false);
  private final ConcurrentHashMap<String, Long> fullToCount = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, List<Long>> fragmentToSuggestion = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Long, RankedQuery> fullQueries = new ConcurrentHashMap<>();
  private final AtomicLong idSerial = new AtomicLong(0);

  public Stream<Pair<Long, RankedQuery>> allQueries() {
    return Stream.generate(this.fullQueries.entrySet().iterator()::next)
                 .parallel()
                 .map(
                   (Entry<Long, RankedQuery> e) ->
                     new Pair<Long, RankedQuery>(e.getKey(), e.getValue())
                  );
  }

  public void registerQuery(String query) {
    if (this.sealed.get()) {
      return;
    }
    this.fullToCount.computeIfPresent(query, (String _k, Long v) -> v + 1);
    this.fullToCount.putIfAbsent(query, 1L);
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
    }
  }

  private void indexQueries() {
    this.fullToCount.forEachEntry(1L, (Entry<String, Long> entry) -> {
      this.fullQueries.put(
        idSerial.incrementAndGet(),
        new RankedQuery(entry.getKey(), entry.getValue())
      );
    });
  }
}