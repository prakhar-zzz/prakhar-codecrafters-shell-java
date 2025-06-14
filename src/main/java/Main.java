import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main 
{
  public static void main(String[] args) throws Exception 
  {
    Scanner scanner = new Scanner(System.in);

    Set<String> builtins = Set.of("echo", "exit", "type", "pwd", "cd");

    File currentDirectory = new File(System.getProperty("user.dir"));

    while (true) 
    {
      System.out.print("$ ");
      String input = scanner.nextLine().trim();

      if (input.equals("exit 0"))                          //exit
      {
        System.exit(0);
      } 
      
      else if (input.startsWith("echo "))                    //echo

      {
        String toEcho = input.substring(5);
        if(toEcho.startsWith("'"))
        {
          return;
        }
        else
        toEcho = toEcho.replaceAll("\\s+", " ");
         if (toEcho.startsWith("'") && toEcho.endsWith("'") && toEcho.length() >= 2) {
        toEcho = toEcho.substring(1, toEcho.length() - 1);  
    }
        System.out.println(toEcho);
      } 

      else if (input.equals("pwd"))                         //pwd
      
      {
       System.out.println(currentDirectory.getCanonicalPath());
      } 
       

      else if (input.startsWith("cd"))                            //cd
      {    
       String directory = input.substring(3).trim();
       File f;
          if (directory.equals("./")) 
             {
               System.out.println(currentDirectory.getCanonicalPath());
             } 
          else if(directory.equals("~"))   
                {
                  String homePath = System.getenv("HOME");
                  File targetDir = new File(homePath);   
                  if (targetDir.exists() && targetDir.isDirectory()) 
                   {
                       currentDirectory = targetDir;            
                   }
                  }   
          else if (directory.startsWith("../")) 
             {
               File targetDir = new File(currentDirectory, directory).getCanonicalFile();              
               if (targetDir.exists() && targetDir.isDirectory()) 
               {
                 currentDirectory = targetDir;
               }
               
              else 
                 {
                   System.out.println(directory + ": No such file or directory");
                 }
             } 
          else if (directory.startsWith("./"))   
          {
                File relative = new File(currentDirectory, directory);
                if (relative.exists() && relative.isDirectory()) 
                {
                  currentDirectory = relative;
                } 
                 else 
                 {
                   System.out.println(directory + ": No such file or directory");
                 }
          }    
           else 
              {
                f = new File(directory);
                if (f.isAbsolute()) 
                {
                   if (f.exists() && f.isDirectory()) 
                   {
                    currentDirectory = f;
                   } 
                  else
                  {
                   System.out.println(directory + ": No such file or directory");
                   }
                } 
              }
       }
      else if (input.startsWith("type "))                           //type
       {
        String cmd = input.substring(5).trim();
        if (builtins.contains(cmd)) 
        {
          System.out.println(cmd + " is a shell builtin");
        } 
        else 
        {
          String pathEnv = System.getenv("PATH");
          boolean found = false;
          if (pathEnv != null) 
          {
            String[] paths = pathEnv.split(":");
            for (String dir : paths) 
            {
              File file = new File(dir, cmd);
              if (file.exists() && file.canExecute()) 
              {
                System.out.println(cmd + " is " + file.getAbsolutePath());
                found = true;
                break;
              }
            }
          }
          if (!found) 
          {
            System.out.println(cmd + ": not found");
          }
        }
      }
        
       else 
       {
        String[] parts = input.split("\\s+");
        String command = parts[0];
        String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

        List<String> fullCommand = new ArrayList<>();
        fullCommand.add(command);
        fullCommand.addAll(Arrays.asList(commandArgs));

        ProcessBuilder pb = new ProcessBuilder(fullCommand);
        pb.directory(currentDirectory); // respect cd'd directory
        pb.redirectErrorStream(true);

        try 
        {
          Process process = pb.start();
          BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

          String line;
          while ((line = reader.readLine()) != null) 
          {
            System.out.println(line);
          }

          process.waitFor();
        }
         catch (IOException | InterruptedException e) 
        {
          System.out.println(command + ": command not found");
        }
      }
      }
    }
  }
  

  
        
  
