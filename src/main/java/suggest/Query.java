package suggest;

import suggest.store.Store;
import suggest.store.Store.RankedQuery;
import java.util.Optional;
import java.util.Comparator;

public final class Query {
  private Optional<Store> maybeStore = Optional.empty();

  public Query bindStore(Store store) {
    this.maybeStore = Optional.of(store);
    return this;
  }

  public void suggest(String[] args) {
    if (!this.maybeStore.isPresent()) {
      System.out.println("Error: no store data store defined");
      return;
    }

    final Store store = this.maybeStore.get();
    final String query = String.join(" ", args);

    store.suggestionForFragment(query)
         .sorted(Comparator.reverseOrder())
         .limit(10)
         .forEach((RankedQuery q) -> System.out.printf("%d - %s\n", q.rank, q.query));
  }
}