package fs.command;

import fs.App;
import fs.Disk;
import fs.util.FileUtils;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class CreateDirectoryCommand extends Command {

    public static final String COMMAND = "mkdir";

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            reportSyntaxError();
            return;
        }

        App app = App.getInstance();
        Disk disk = app.getDisk();
        String path;

        for (int i = 1; i < args.length; i++) {
            path = args[i];
            
            if (disk.exists(path) && !FileUtils.promptForVirtualOverride(path)) {
                continue;
            }

            try {
                disk.createDirectory(path);
            } 
            catch (Exception ex) {
                reportError(ex);
            }
        }
    }

    @Override
    protected String getSyntax() {
        return getName() + " DIRECTORY...";
    }

    @Override
    protected String getDescription() {
        return "Create a set of directories, if they do not already exist.";
    }

    @Override
    protected String getName() {
        return COMMAND;
    }

}
