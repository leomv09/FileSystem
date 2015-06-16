package fs.command;

import fs.App;
import fs.Disk;
import fs.Node;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class FindFilesCommand extends Command {

    public static final String COMMAND = "find";
    
    @Override
    public void execute(String[] args) {
        App app = App.getInstance();
        Disk disk = app.getDisk();
        String directory, regex;
        
        switch (args.length) {
            case 2:
                directory = disk.getCurrentDirectory();
                regex = args[1];
                break;
            case 3:
                directory = args[1];
                regex = args[2];
                break;
            default:
                reportSyntaxError();
                return;
        }
        
        try {
            List<String> files = disk.getFiles(directory, regex);
            for (String file : files) {
                System.out.println(file);
            }
        }
        catch (IOException ex) {
            reportError(ex);
        }
    }

    @Override
    protected String getName() {
        return FindFilesCommand.COMMAND;
    }

    @Override
    protected String getDescription() {
        return "Find all files or directories that match REGEX";
    }

    @Override
    protected String getSyntax() {
        return getName() + " <DIRECTORY> REGEX";
    }
    
}
