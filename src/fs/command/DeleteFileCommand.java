package fs.command;

import fs.App;
import fs.Disk;
import java.io.IOException;

/**
 *
 * @author Leo
 */
public class DeleteFileCommand extends Command {
    
    public static final String COMMAND = "rm";

    @Override
    public void execute(String[] args) 
    {
        if (args.length != 2) {
            reportSyntaxError();
            return;
        }
        
        App app = App.getInstance();
        Disk disk = app.getDisk();
        String path;
        
        for (int i = 1; i< args.length; i++) {
            try {
                path = args[i];
                disk.delete(path);
            } 
            catch (IOException ex) {
                reportError(ex);
            }
        }
    }

    @Override
    protected String getName() 
    {
        return COMMAND;
    }

    @Override
    protected String getDescription() 
    {
        return "Deletes the FILE(s) from the File System.";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + " FILE..."; 
    }
    
}
