import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws Exception {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.print("$ ");
      String input = scanner.nextLine().trim();

      if ("exit 0".equals(input)) {
        System.exit(0);
      } else if (input.startsWith("echo ")) {
        String toEcho = input.substring(5); // get everything after "echo "
        System.out.println(toEcho);
      } else {
        System.out.println(input + ": command not found");
      }
    }
  }
}
