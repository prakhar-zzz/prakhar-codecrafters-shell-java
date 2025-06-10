import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Set<String> builtins = Set.of("echo", "exit", "type");

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split(" ");
            String command = parts[0];
            String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

            if (input.equals("exit 0")) {
                System.exit(0);
            } else if (command.equals("echo")) {
                System.out.println(input.substring(5));
            } else if (command.equals("type")) {
                if (parts.length < 2) {
                    System.out.println("type: missing operand");
                    continue;
                }
                String target = parts[1];
                if (builtins.contains(target)) {
                    System.out.println(target + " is a shell builtin");
                } else {
                    String path = System.getenv("PATH");
                    boolean found = false;
                    if (path != null) {
                        for (String dir : path.split(":")) {
                            File file = new File(dir, target);
                            if (file.exists() && file.canExecute()) {
                                System.out.println(target + " is " + file.getAbsolutePath());
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        System.out.println(target + ": not found");
                    }
                }
            } else {
                // Try to find and run external command
                String path = System.getenv("PATH");
                boolean executed = false;
                if (path != null) {
                    for (String dir : path.split(":")) {
                        File file = new File(dir, command);
                        if (file.exists() && file.canExecute()) {
                            List<String> fullCommand = new ArrayList<>();
                            fullCommand.add(file.getAbsolutePath());
                            fullCommand.addAll(Arrays.asList(commandArgs));

                            ProcessBuilder pb = new ProcessBuilder(fullCommand);
                            pb.redirectErrorStream(true);
                            Process process = pb.start();

                            // Print process output
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(process.getInputStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    System.out.println(line);
                                }
                            }

                            process.waitFor();
                            executed = true;
                            break;
                        }
                    }
                }

                if (!executed) {
                    System.out.println(command + ": command not found");
                }
            }
        }
    }
}
