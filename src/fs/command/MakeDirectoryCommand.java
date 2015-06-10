package fs.command;

import fs.App;
import fs.Disk;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class MakeDirectoryCommand extends Command {

    public static final String COMMAND = "mkdir";
    
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            reportSyntaxError();
            return;
        }
        
        String path = args[1];
        App app = App.getInstance();
        Disk disk = app.getDisk();
        
        try
        {
            if(disk.exists(path))
            {
                System.out.print("There is already a directory with the same name. Do you want to override it? [y/n] ");
                    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                    String line = input.readLine();
                    if (!line.equalsIgnoreCase("y")) 
                    {
                        return;
                    }
                    disk.deleteDirectory(path);
                    disk.createDirectory(path);
                    
                    System.out.println("Directory created successfully.");
            }
        }
        catch (Exception ex) 
        {
            reportError(ex.getMessage());
        }
    }

    @Override
    protected String getSyntax() {
        return getName() + " Directory name...";
    }

    @Override
    protected String getDescription() {
        return "Create the DIRECTORY(ies), if they do not already exist.";
    }

    @Override
    protected String getName() {
        return COMMAND;
    }
    
}
