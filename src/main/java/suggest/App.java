package suggest;

import suggest.Shell;
import suggest.loader.Loader;
import suggest.loader.FileLoader;

import suggest.store.Store;
import suggest.store.InMemoryStore;

public class App {
  private static Store inMemoryStore = new InMemoryStore();
  private static Loader fileLoader = new FileLoader().bindStore(inMemoryStore);

  private static final Shell shell = new Shell()
    .bindCommand(
      "loadFile",
      "<path> load from disk",
      fileLoader::load
    );

  public static void main(String[] args) {
    shell.run();
  }
}
