package fs.command;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public abstract class Command {
    
    public abstract void execute(String[] args);
    
    protected abstract String getName();
    
    protected abstract String getDescription();

    protected abstract String getSyntax();
    
    protected void reportError(String message) {
	System.err.println("ERROR: " + message);
    }
    
    protected void reportError(Exception ex) {
	reportError(ex.getMessage());
    }

    protected void reportSyntaxError() {
	String message = "Invalid Syntax. Try 'help " + getName() + "' for more information.";
	reportError(message);
    }
    
}
