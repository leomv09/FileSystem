package fs.command;

import fs.App;
import fs.Disk;
import fs.util.FileUtils;
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
        
        if (disk.exists(path) && !FileUtils.promptForVirtualOverride(path)) {
            return;
        }
        
        try {
            disk.createFile(path, content);
        }
        catch (Exception ex) {
            reportError(ex);
        }
    }

    @Override
    protected String getName() {
        return CreateFileCommand.COMMAND;
    }

    @Override
    protected String getDescription() {
        return "Create a file and set its content.";
    }

    @Override
    protected String getSyntax() {
        return getName() + " FILENAME \"<CONTENT>\"";
    }
    
}
