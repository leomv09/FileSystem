package fs.command;

import fs.App;
import fs.Disk;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class PrintCurrentDirectoryCommand extends Command {

    public static final String COMMAND = "pwd";
    
    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            reportSyntaxError();
            return;
        }
        
        App app = App.getInstance();
        Disk disk = app.getDisk();
        String directory = disk.getCurrentDirectory();
        System.out.println(directory);
    }

    @Override
    protected String getName() {
        return PrintCurrentDirectoryCommand.COMMAND;
    }

    @Override
    protected String getDescription() {
        return "Print the current working directory.";
    }

    @Override
    protected String getSyntax() {
        return getName();
    }
    
}
