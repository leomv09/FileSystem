package fs.command;

import fs.App;
import fs.Disk;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leo
 */
public class CopyRealToVirtualCommand extends Command {
    
    public static final String COMMAND = "cpr2v";

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
            String realPath = args[1];
            String virtualPath = args[2];
            App app = App.getInstance();
            Disk disk = app.getDisk();
            disk.copyRealToVirtual(realPath, virtualPath);
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
        return "Copies a content from a real path to a node of the File System";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + " REAL_PATH VIRTUAL_PATH";
    }
    
}
