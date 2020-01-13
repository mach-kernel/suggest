package suggest.loader;

import java.io.File;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.util.Optional;
import java.io.IOException;

public final class FileLoader extends Loader {
  public void connect(String[] args) {
    if (args.length < 1) {
      System.out.println("Error: must provide a path argument");
      return;
    }

    final File file = new File(args[0]);

    if (!file.isFile()) {
      System.out.println("Error: path must be file");
      return;
    }

    if (!file.canRead()) {
      System.out.println("Error: must be able to read file");
      return;
    }

    try {
      BufferedReader reader = Files.newBufferedReader(file.toPath());
      this.data = Optional.ofNullable(reader.lines());
    } catch (IOException e) {
      System.out.printf("Error: unable to read file %s", e.getMessage());
    }
  }
}