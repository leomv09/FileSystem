package fs.command;

import fs.App;
import fs.Disk;
import java.io.IOException;

/**
 *
 * @author Leo
 */
public class MoveFileCommand extends Command {

    public static final String COMMAND = "mv";
    @Override
    public void execute(String[] args) 
    {
        try 
        {
            if (args.length != 3)
            {
                reportSyntaxError();
                return;
            }
            String oldPath = args[1];
            String newPath = args[2];
            App app = App.getInstance();
            Disk disk = app.getDisk();
            disk.moveFile(oldPath, newPath);
        } 
        catch (IOException ex) 
        {
            reportError(ex);
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
        return "Moves a file or directory to another directory.";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + " OLD_PATH NEW_PATH";
    }
    
}
