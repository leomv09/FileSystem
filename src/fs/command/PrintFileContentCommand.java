package fs.command;

import fs.App;
import fs.Disk;
import fs.StringUtils;
import java.io.IOException;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class PrintFileContentCommand extends Command {

    public static final String COMMAND = "cat";
    
    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            reportSyntaxError();
            return;
        }
        
        App app = App.getInstance();
        Disk disk = app.getDisk();
        String file = args[1];
        
        try {
            String content = disk.getFileContent(file);
            int block = 80;
            int ceil = (int) Math.ceil((double) content.length() / block);
            for (int i = 0; i < ceil; i++) {
                System.out.println(StringUtils.substring(content, i*block, (i+1)*block));
            }
        } 
        catch (IOException ex) {
            reportError(ex);
        }
    }

    @Override
    protected String getName() {
        return PrintFileContentCommand.COMMAND;
    }

    @Override
    protected String getDescription() {
        return "Print the content of a file";
    }

    @Override
    protected String getSyntax() {
        return getName() + " FILE";
    }
    
}
