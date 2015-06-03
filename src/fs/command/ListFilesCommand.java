package fs.command;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class ListFilesCommand extends Command {

    public static final String COMMAND = "ls";
    
    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            reportSyntaxError();
            return;
        }
    }

    @Override
    protected String getSyntax() {
        return getName();
    }

    @Override
    protected String getDescription() {
        return "List the files in the current directory.";
    }

    @Override
    protected String getName() {
        return COMMAND;
    }
    
}
