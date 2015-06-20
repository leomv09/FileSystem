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
public class ListFilesCommand extends Command {

    public static final String COMMAND = "ls";
    
    @Override
    public void execute(String[] args) {
        App app = App.getInstance();
        Disk disk = app.getDisk();
        String directory;
        
        switch (args.length) {
            case 1:
                directory = disk.getCurrentDirectory();
                break;
            case 2:
                directory = args[1];
                break;
            default:
                reportSyntaxError();
                return;
        }
        
        try {
            List<Node> files = disk.getFiles(directory);
            for (Node file : files) {
                System.out.println(file);
            }
        } 
        catch (IOException ex) {
            reportError(ex);
        }
    }

    @Override
    protected String getSyntax() {
        return getName() + " <DIRECTORY>";
    }

    @Override
    protected String getDescription() {
        return "List the files in a directory.";
    }

    @Override
    protected String getName() {
        return COMMAND;
    }
    
}
