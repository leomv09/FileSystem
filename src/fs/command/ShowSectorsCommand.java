package fs.command;

import fs.App;
import fs.Disk;
import java.util.List;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class ShowSectorsCommand extends Command {

    public static final String COMMAND = "sectors";
    
    @Override
    public void execute(String[] args) {
        int start = 1, end = -1;
        
        switch (args.length) {
            case 1:
                break;
            case 2:
                try {
                    end = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException ex) {
                    reportError("Invalid index: " + args[1]);
                    return;
                }
                break;
            case 3:
                try {
                    start = Integer.parseInt(args[1]);
                    end = Integer.parseInt(args[2]);
                }
                catch (NumberFormatException ex) {
                    reportError("Invalid indexes: " + args[1] + ", " + args[2]);
                    return;
                }
                break;
            default:
                reportSyntaxError();
                return;
        }
        
        App app = App.getInstance();
        Disk disk = app.getDisk();
        List<String> content = disk.getSectorsContent();
        
        if (start <= 0) {
            start = 1;
        }
        if (end <= 0 || end > content.size()) {
            end = content.size();
        }
        
        int padding = calculatePadding(content.size());
        
        for (int i = start - 1; i < end; i++) {
            System.out.format("%0" + padding + "d: %s\n", i+1, content.get(i));
        }
    }

    private int calculatePadding(int number) {
        int i = 1;
        while (number >= 10) {
            number /= 10;
            i++;
        }
        return i;
    }
    
    @Override
    protected String getName() {
        return ShowSectorsCommand.COMMAND;
    }

    @Override
    protected String getDescription() {
        return "Show the content of the sectors in the disk.";
    }

    @Override
    protected String getSyntax() {
        return getName() + " <START> <END>";
    }

}
