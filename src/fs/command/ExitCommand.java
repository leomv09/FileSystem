package fs.command;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class ExitCommand extends Command {

    public static final String COMMAND = "exit";
    
    @Override
    public void execute(String[] args) {
         System.exit(0);
    }

    @Override
    protected String getSyntax() {
        return getName();
    }

    @Override
    protected String getDescription() {
        return "Finish the execution.";
    }

    @Override
    protected String getName() {
        return COMMAND;
    }
    
}
