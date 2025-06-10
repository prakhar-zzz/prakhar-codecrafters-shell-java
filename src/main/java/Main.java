import java.util.Scanner;
import java.util.Set;

public class Main {
  public static void main(String[] args) throws Exception {
    Scanner scanner = new Scanner(System.in);
    Set<String> builtins = Set.of("echo", "exit", "type");

    while (true) {
      System.out.print("$ ");
      String input = scanner.nextLine().trim();

      if (input.equals("exit 0")) {
        System.exit(0);
      } else if (input.startsWith("echo ")) {
        String toEcho = input.substring(5);
        System.out.println(toEcho);
      } else if (input.startsWith("type ")) {
        String cmd = input.substring(5).trim();
        if (builtins.contains(cmd)) {
          System.out.println(cmd + " is a shell builtin");
        } else {
          System.out.println(cmd + ": not found");
        }
      } else {
        System.out.println(input + ": command not found");
      }
    }
  }
}
