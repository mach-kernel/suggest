package suggest;

import java.util.stream.IntStream;

import suggest.store.Store;
import suggest.store.Store.RankedQuery;

public class Tokenizer {
  private static double FRAGMENT_MAX_LEN_RATIO = 0.75;

  public static void buildSuggestions(Store store) {
    store.allQueries().parallel().forEach((Pair<Long, RankedQuery> p) -> {
      final String[] tokens = fragmentQuery(p.r.query);
      for (final String token : tokens) {
        store.registerFragment(token, p.l);
      }
    });
  }

  private static String[] fragmentQuery(String query) {
    final Integer maxIndex = new Double(Math.ceil(query.length() * FRAGMENT_MAX_LEN_RATIO)).intValue();
    return IntStream.rangeClosed(1, maxIndex > 50 ? 50 : maxIndex)
                    .mapToObj((int max) -> query.substring(0, max))
                    .toArray(String[]::new);
  }
}