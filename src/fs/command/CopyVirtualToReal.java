package fs.command;

import fs.App;
import fs.Disk;
import java.io.IOException;
/**
 *
 * @author Leo
 */
public class CopyVirtualToReal extends Command {
    
    public static final String COMMAND = "cpv2r";

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
            String virtualPath = args[1];
            String realPath = args[2];
            App app = App.getInstance();
            Disk disk = app.getDisk();
            disk.copyVirtualToReal(virtualPath, realPath);
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
        return "Copies a content from a virtual node to a real path";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + "VIRTUAL_PATH REAL_PATH";
    }
    
}
