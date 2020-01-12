package suggest;

import suggest.Shell;
import suggest.loader.FileLoader;

public class App {
  private static final Shell shell = new Shell()
    .bindCommand(
      "load",
      "<path> load from disk",
      new FileLoader()::load
    );

  public static void main(String[] args) {
    shell.run();
  }
}
