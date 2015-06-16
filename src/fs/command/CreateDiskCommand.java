package fs.command;

import fs.App;
import fs.Disk;

/**
 *
 * @author Leo
 */
public class CreateDiskCommand extends Command {
    
    
    public final static String COMMAND = "mkdisk"; 

    @Override
    public void execute(String[] args) {
        if ( args.length != 3) {
            reportSyntaxError();
            return;
        }
        try
        {
            int sectorsQuantity = Integer.parseInt(args[1]);
            int sectorSize = Integer.parseInt(args[2]);
            App app = App.getInstance();
            Disk disk = new Disk("disk.txt", sectorsQuantity, sectorSize);
            app.setDisk(disk);
        }
        catch (Exception ex)
        {
            reportError(ex);
        }
    }

    @Override
    protected String getName() {
        return COMMAND;
    }

    @Override
    protected String getDescription() 
    {
        return "Creates a virtual disk defining the sectors quantity and its size.";
    }

    @Override
    protected String getSyntax() {
        return getName() + " SECTORS SECTOR_SIZE";
    }
    
}
