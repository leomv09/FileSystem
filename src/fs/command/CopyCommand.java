package fs.command;

import fs.App;
import fs.Disk;
import fs.util.FileUtils;
import java.io.IOException;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class CopyCommand extends Command {

    public static final String COMMAND = "cp";
    
    @Override
    public void execute(String[] args) {
        if (args.length != 3 && args.length != 4) {
            reportSyntaxError();
            return;
        }
        
        App app = App.getInstance();
        Disk disk = app.getDisk();
        String src, dest;
        
        if (args.length == 3) {
            src = args[1];
            dest = args[2];
            copyVirtualToVirtual(disk, src, dest);
        }
        else {
            int index = getFlagIndex(args);
            
            if (index != 1 && index != 2) {
                reportSyntaxError();
                return;
            }
            
            if (index == 1) {
                src = args[2];
                dest = args[3];
                copyRealToVirtual(disk, src, dest);
            }
            else {
                src = args[1];
                dest = args[3];
                copyVirtualToReal(disk, src, dest);
            }
        }
    }
    
    private int getFlagIndex(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-r") || args[i].equalsIgnoreCase("--real")) {
                return i;
            }
        }
        return -1;
    }
    
    private void copyVirtualToVirtual(Disk disk, String src, String dest) {
        try {
            if (disk.isFile(dest) && !FileUtils.promptForVirtualOverride(dest)) {
                return;
            }
            disk.copyVirtualToVirtual(src, dest);
        } 
        catch (Exception ex) {
            reportError(ex);
        }
    }
    
    private void copyVirtualToReal(Disk disk, String src, String dest) {
        try {
            java.io.File file = new java.io.File(dest);
            if (file.isFile() && !FileUtils.promptForRealOverride(dest)) {
                return;
            }
            disk.copyVirtualToReal(src, dest);
        }
        catch (IOException ex) {
            reportError(ex);
        }
    }
    
    private void copyRealToVirtual(Disk disk, String src, String dest) {
        try {
            if (disk.isFile(dest) && !FileUtils.promptForVirtualOverride(dest)) {
                return;
            }
            disk.copyRealToVirtual(src, dest);
        } 
        catch (Exception ex) {
            reportError(ex);
        }
    }

    @Override
    protected String getName() {
        return CopyCommand.COMMAND;
    }

    @Override
    protected String getDescription() {
        return "Copy a file or directory to another location.";
    }

    @Override
    protected String getSyntax() {
        return getName() + " <-r | --real> SOURCE <-r | --real> DEST";
    }
    
}
