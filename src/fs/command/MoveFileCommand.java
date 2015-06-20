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
            App app = App.getInstance();
            Disk disk = app.getDisk();
            String src = args[1];
            String dest = args[2];
            disk.moveFile(src, dest);
        } 
        catch (Exception ex) 
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
        return "Moves a file or directory to another location.";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + " SRC DEST";
    }
    
}
