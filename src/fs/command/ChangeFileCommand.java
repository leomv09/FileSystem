package fs.command;

import fs.App;
import fs.Disk;
import java.io.IOException;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class ChangeFileCommand extends Command {

    public static final String COMMAND = "mod";
    
    @Override
    public void execute(String[] args) {
        if (args.length != 3) {
            reportSyntaxError();
            return;
        }
        
        App app = App.getInstance();
        Disk disk = app.getDisk();
        String path = args[1];
        String content = args[2];
        
        try {
            disk.changeFileContent(path, content);
        } 
        catch (IOException ex) {
            reportError(ex);
        }
    }

    @Override
    protected String getName() {
        return ChangeFileCommand.COMMAND;
    }

    @Override
    protected String getDescription() {
        return "Change the content of a file.";
    }

    @Override
    protected String getSyntax() {
        return getName() + " FILE \"CONTENT\"";
    }
    
}
