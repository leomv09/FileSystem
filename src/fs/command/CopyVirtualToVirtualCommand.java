package fs.command;

import fs.App;
import fs.Disk;

/**
 *
 * @author Leo
 */
public class CopyVirtualToVirtualCommand extends Command {
    
    public static final String COMMAND = "cp";

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
            String virtualOriginPath = args[1];
            String virtualDestinationPath = args[2];
            App app = App.getInstance();
            Disk disk = app.getDisk();
            disk.copyVirtualToVirtual(virtualOriginPath, virtualDestinationPath);
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
        return "Copies a content from a virtual node to another virtual node of the File System";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + "SRC_PATH DEST_PATH";
    }
    
}
