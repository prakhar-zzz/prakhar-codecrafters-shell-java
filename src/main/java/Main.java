import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
  public static void main(String[] args) throws Exception {
    Scanner scanner = new Scanner(System.in);

    Set<String> builtins = Set.of("echo", "exit", "type", "pwd", "cd");

    File currentDirectory = new File(System.getProperty("user.dir"));

    while (true) {
      System.out.print("$ ");

      String input = scanner.nextLine().trim();

      if (input.equals("exit 0"))                                  //exit
      {
        System.exit(0);
      }
       else if (input.startsWith("echo "))                           //echo
      {
        String toEcho = input.substring(5);
        System.out.println(toEcho);
      }
       else if (input.startsWith("pwd"))                             //pwd
       {
        System.out.println(currentDirectory.getAbsolutePath());
      } 
      else if(input.startsWith("cd"))                                 //cd
      {
       String directory = input.substring(3);
       File f = new File(directory);
       if(f.exists() && f.isDirectory())
       {
          currentDirectory = f;
       }
       else if (directory.startsWith("./"))
       {
        continue;
       }
       else if(directory.startsWith("../"))
       {
        f= currentDirectory.getParentFile();
       }
       
       else
        System.out.println(directory + ": No such file or directory");
       
      }
      
      else if (input.startsWith("type "))                            //type
      {
        String cmd = input.substring(5).trim();

        if (builtins.contains(cmd)) {
          System.out.println(cmd + " is a shell builtin");
        } else {
          String pathEnv = System.getenv("PATH");
          boolean found = false;

          if (pathEnv != null) {
            String[] paths = pathEnv.split(":");
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
        pb.redirectErrorStream(true); // Merge stdout and stderr

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
