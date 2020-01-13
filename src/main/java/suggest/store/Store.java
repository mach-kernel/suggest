package suggest.store;

import suggest.Pair;
import java.util.stream.Stream;

public interface Store {
  public final class RankedQuery {
    public final String query;
    // The number of times users searched the same exact query
    public final Long rank;

    RankedQuery(String query, Long rank) {
      this.query = query;
      this.rank = rank;
    }
  }

  /**
   * Get all queries (id, meta)
   * @return
   */
  public Stream<Pair<Long, RankedQuery>> allQueries();

  /**
   * Add a query to the store
   * @param query
   */
  public void registerQuery(String query);

  /**
   * Signal that load is complete
   */
  public void finishedRegisteringQueries();

  /**
   * Map a fragment back to the complete query
   * @param token
   * @param id
   */
  public void registerFragment(String token, Long id);
}