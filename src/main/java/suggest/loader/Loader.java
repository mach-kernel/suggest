package suggest.loader;
import suggest.loader.FileLoader;

import java.util.stream.Stream;
import java.util.Optional;

interface Loader {
  public static FileLoader file() {
    return new FileLoader();
  }

  public void load(String[] args);
  public Optional<Stream<String>> data = Optional.empty();
}