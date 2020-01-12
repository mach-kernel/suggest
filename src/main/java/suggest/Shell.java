package suggest;

import java.util.Scanner;
import java.util.function.Consumer;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Arrays;

/**
 * Parse commands from stdin
 */
public final class Shell {
  
  /**
   * Basic data class for command entries
   */
  private final class ShellCommandEntry {
    public final String description;
    public final Consumer<String[]> handler;

    ShellCommandEntry(
      final String description,
      final Consumer<String[]> handler
    ) {
      this.description = description;
      this.handler = handler;
    }
  }

  private static Scanner scanner = new Scanner(System.in);
  private final Map<String, ShellCommandEntry> commands = new HashMap<>();

  Shell() {
    this.commands.put("help", new ShellCommandEntry(
      "Print list of commands",
      this::showHelp
    ));
  }

  public void run() {
    while (true) {
      System.out.print("suggest> ");
      if (!scanner.hasNextLine()) { break; }

      final String[] args = scanner.nextLine().split(" ");
      final Optional<ShellCommandEntry> entry = Optional.ofNullable(this.commands.get(args[0]));

      if (entry.isPresent()) {
        entry.get().handler.accept(Arrays.copyOfRange(args, 1, args.length));
      } else {
        System.out.printf("Invalid command %s; try help.\n", args[0]);
      }
    }
  }

  /**
   * Add a command by name with handler
   * 
   * @param commandName
   * @param handler
   * @return
   */
  public Shell bindCommand(final String commandName, final String description, final Consumer<String[]> handler) {
    if (commandName.equalsIgnoreCase("help")) {
      throw new IllegalArgumentException("'help' is a reserved command name");
    }

    this.commands.put(
      commandName.toLowerCase(), 
      new ShellCommandEntry(description, handler)
    );

    return this;
  }

  private void showHelp(String[] _args) {
    this.commands.forEach(
      (String command, ShellCommandEntry entry) ->
        System.out.printf("%s -- %s\n", command, entry.description)
    );
  }
} 