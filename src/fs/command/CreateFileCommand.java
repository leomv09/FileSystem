package fs.command;

import fs.App;
import fs.Disk;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class CreateFileCommand extends Command {

    public final static String COMMAND = "mkfile"; 
    
    @Override
    public void execute(String[] args) {
        if (args.length != 2 && args.length != 3) {
            reportSyntaxError();
            return;
        }
        
        String path = args[1];
        String content = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        App app = App.getInstance();
        Disk disk = app.getDisk();
        
        try {
            if (disk.exists(path)) {
                System.out.print("File already exists. Do you want to override it? [y/n] ");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                String line = input.readLine();
                if (!line.equalsIgnoreCase("y")) {
                    return;
                }
                disk.deleteFile(path);
            }
            disk.createFile(path, content);
        }
        catch (Exception ex) {
            reportError(ex.getMessage());
        }
    }

    @Override
    protected String getName() {
        return CreateFileCommand.COMMAND;
    }

    @Override
    protected String getDescription() {
        return "Create a file and set its content";
    }

    @Override
    protected String getSyntax() {
        return getName() + " FILENAME \"<CONTENT>\"";
    }
    
}
