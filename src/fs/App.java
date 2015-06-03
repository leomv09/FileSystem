package fs;

import fs.command.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class App {
    
    private static App instance;
    private final Map<String, Command> commands;
    private Disk disk;
    
    private App() {
        this.commands = initializeCommands();
        this.disk = new Disk("disk.txt", 1000, 100);
    }
    
    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }
    
    public void start() {
        try {
	    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            Command command;
            String[] args;
            String line;
            
	    while (true) {
		System.out.print("> ");
		line = input.readLine();
                
                if (line.length() > 0) {
                    args = line.split("\\s+");
                    command = commands.get(args[0]);
                    
                    if (command != null) {
                        command.execute(args);
                    }
                    else {
                        reportError();
                    }
                }
	    }
	}
	catch (IOException ex) { }
    }
    
    public Map<String, Command> getCommands() {
        return commands;
    }
    
    private void reportError() {
	System.err.println("Invalid command. Try 'help' for more information.");
    }
    
    private Map<String, Command> initializeCommands() {
        Map<String, Command> result = new HashMap<>();
	result.put(ListFilesCommand.COMMAND, new ListFilesCommand());
        result.put(MakeDirectoryCommand.COMMAND, new MakeDirectoryCommand());
        result.put(HelpCommand.COMMAND, new HelpCommand());
        result.put(ExitCommand.COMMAND, new ExitCommand());
	return result;
    }

    public Disk getDisk() {
        return disk;
    }

    public void setDisk(Disk disk) {
        this.disk = disk;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        App fs = App.getInstance();
        fs.start();
    }
    
}
