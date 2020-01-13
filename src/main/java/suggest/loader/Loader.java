package suggest.loader;
import suggest.Tokenizer;
import suggest.loader.FileLoader;
import suggest.store.Store;

import java.util.stream.Stream;
import java.util.Optional;

/**
 * Uses args to retrieve data from an arbitrary source
 * providing it as a string stream. Loads data into the
 * provided store.
 */
public abstract class Loader {
  public static FileLoader file() {
    return new FileLoader();
  }

  public Optional<Stream<String>> data = Optional.empty();  
  abstract void connect(String[] args);

  public Loader bindStore(Store store) {
    this.target = Optional.of(store);
    return this;
  }

  public void load(String[] args) {
    if (!this.target.isPresent()) {
      System.out.println("Error: no target data store defined");
      return;
    }

    this.connect(args);

    if (!this.data.isPresent()) {
      System.out.println("Error: unable to produce data stream");
      return;
    }
    
    Store targetStore = this.target.get();
    Stream<String> rawQueries = this.data.get().parallel();

    System.out.println("Registering queries");
    rawQueries.forEach(targetStore::registerQuery);
    targetStore.finishedRegisteringQueries();
    System.out.printf("Finished registering %d queries\n", targetStore.getNumQueries());

    System.out.println("Making suggestion tokens");
    Tokenizer.buildSuggestions(targetStore);
    System.out.printf("Load complete, %d total tokens\n", targetStore.getNumTokens());
  }

  private Optional<Store> target = Optional.empty();
}