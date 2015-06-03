package fs.command;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class MakeDirectoryCommand extends Command {

    public static final String COMMAND = "mkdir";
    
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            reportSyntaxError();
            return;
        }
    }

    @Override
    protected String getSyntax() {
        return getName() + " DIRECTORY...";
    }

    @Override
    protected String getDescription() {
        return "Create the DIRECTORY(ies), if they do not already exist.";
    }

    @Override
    protected String getName() {
        return COMMAND;
    }
    
}
