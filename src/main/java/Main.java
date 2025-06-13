public class Main {
  public static void main(String[] args) throws Exception {
    Scanner scanner = new Scanner(System.in);

    Set<String> builtins = Set.of("echo", "exit", "type", "pwd", "cd");

    File currentDirectory = new File(System.getProperty("user.dir"));

    while (true) {
      System.out.print("$ ");

      String input = scanner.nextLine().trim();
      if (input.isEmpty()) continue;

      if (input.equals("exit 0")) {
        System.exit(0);
      } else if (input.startsWith("echo ")) {
        String toEcho = input.substring(5);
        System.out.println(toEcho);
      } else if (input.equals("pwd")) {
        System.out.println(currentDirectory.getAbsolutePath());
      } else if (input.startsWith("cd")) {
        String directory = input.substring(3).trim();
        File f = new File(directory);

        if (f.exists() && f.isDirectory()) {
          currentDirectory = f;
        } else {
          System.out.println("cd: directory not found");
        }
      } else if (input.startsWith("type ")) {
        String cmd = input.substring(5).trim();

        if (builtins.contains(cmd)) {
          System.out.println(cmd + " is a shell builtin");
        } else {
          String pathEnv = System.getenv("PATH");
          boolean found = false;

          if (pathEnv != null) {
            String[] paths = pathEnv.split(File.pathSeparator);
            for (String dir : paths) {
              File file = new File(dir, cmd);
              if (file.exists() && file.canExecute()) {
                System.out.println(cmd + " is " + file.getAbsolutePath());
                found = true;
                break;
              }
            }
          }

          if (!found) {
            System.out.println(cmd + ": not found");
          }
        }
      } else {
        String[] parts = input.split("\\s+"); 
        String command = parts[0];
        String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

        List<String> fullCommand = new ArrayList<>();
        fullCommand.add(command);
        fullCommand.addAll(Arrays.asList(commandArgs));

        ProcessBuilder pb = new ProcessBuilder(fullCommand);
        pb.directory(currentDirectory); // Important!
        pb.redirectErrorStream(true);

        try {
          Process process = pb.start();
          BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

          String line;
          while ((line = reader.readLine()) != null) {
            System.out.println(line);
          }

          process.waitFor();
        } catch (IOException | InterruptedException e) {
          System.out.println(command + ": command not found");
        }
      }
    }
  }
}
