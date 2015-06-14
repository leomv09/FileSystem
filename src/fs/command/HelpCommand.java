package fs.command;

import fs.App;
import java.util.Map;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class HelpCommand extends Command {
    
    public static final String COMMAND = "help";

    @Override
    public void execute(String[] args) {
        App fs = App.getInstance();
        Map<String, Command> commands = fs.getCommands();
        
        if (args.length == 1) {
            System.out.println("Commands: ");
            for (Command command : commands.values()) {
                System.out.print("  ");
                printShortCommandInfo(command);
            }
            System.out.println("Try 'help COMMAND' for more information.");
        }
        else if (args.length == 2) {
            Command command = commands.get(args[1]);
            if (command != null) {
                printLongCommandInfo(command);
            }
            else {
                reportError("Invalid command '" + args[1] + "'");
            }
        }
        else {
            reportSyntaxError();
        }
    }
    
    private void printShortCommandInfo(Command command) {
        System.out.print(command.getName());
        System.out.print(" = ");
        System.out.println(command.getDescription());
    }
    
    private void printLongCommandInfo(Command command) {
        System.out.println("Usage: " + command.getSyntax());
        System.out.println(command.getDescription());
    }

    @Override
    protected void reportSyntaxError() {
	String message = "Invalid Syntax. Use: " + getSyntax();
	reportError(message);
    }
    
    @Override
    protected String getSyntax() {
        return getName() + " <COMMAND>";
    }

    @Override
    protected String getDescription() {
        return "Show this help.";
    }

    @Override
    protected String getName() {
        return COMMAND;
    }
}
