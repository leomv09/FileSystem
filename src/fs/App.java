package fs;

import fs.command.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class App {
    
    private static App instance;
    private final Map<String, Command> commands;
    private Disk disk;
    
    /**
     * Create a new App.
     */
    private App() {
        this.commands = initializeCommands();
        this.disk = new Disk("disk.txt", 1000, 10);
    }
    
    /**
     * Gets the app instance.
     * 
     * @return The instance.
     */
    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }
    
    /**
     * Starts the simulation.
     */
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
                    args = parseLine(line);
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
    
    /**
     * Get the commands.
     * 
     * @return The commands
     */
    public Map<String, Command> getCommands() {
        return commands;
    }

    /**
     * Get the disk.
     * 
     * @return The disk.
     */
    public Disk getDisk() {
        return disk;
    }

    /**
     * Set the disk.
     * 
     * @param disk The new disk.
     */
    public void setDisk(Disk disk) {
        this.disk = disk;
    }
    
    /**
     * Report a syntax error.
     */
    private void reportError() {
	System.err.println("Invalid command. Try 'help' for more information.");
    }
    
    /**
     * Initialize the commands.
     * 
     * @return The commands.
     */
    private Map<String, Command> initializeCommands() {
        Map<String, Command> result = new HashMap<>();
	result.put(ListFilesCommand.COMMAND, new ListFilesCommand());
        result.put(CreateFileCommand.COMMAND, new CreateFileCommand());
        result.put(ChangeFileCommand.COMMAND, new ChangeFileCommand());
        result.put(MakeDirectoryCommand.COMMAND, new MakeDirectoryCommand());
        result.put(ChangeCurrentDirectoryCommand.COMMAND, new ChangeCurrentDirectoryCommand());
        result.put(FindFilesCommand.COMMAND, new FindFilesCommand());
        result.put(PrintCurrentDirectoryCommand.COMMAND, new PrintCurrentDirectoryCommand());
        result.put(PrintFileContentCommand.COMMAND, new PrintFileContentCommand());
        result.put(PrintTreeCommand.COMMAND, new PrintTreeCommand());
        result.put(ListPropertiesCommand.COMMAND, new ListPropertiesCommand());
        result.put(CopyCommand.COMMAND, new CopyCommand());
        result.put(CreateDiskCommand.COMMAND, new CreateDiskCommand());
        result.put(DeleteFileCommand.COMMAND, new DeleteFileCommand());
        result.put(MoveFileCommand.COMMAND, new MoveFileCommand());
        result.put(HelpCommand.COMMAND, new HelpCommand());
        result.put(ExitCommand.COMMAND, new ExitCommand());
        result.put(CopyRealToVirtualCommand.COMMAND, new CopyRealToVirtualCommand());
        result.put(CopyVirtualToRealCommand.COMMAND, new CopyVirtualToRealCommand());
        result.put(CopyVirtualToVirtualCommand.COMMAND, new CopyVirtualToVirtualCommand());
	return result;
    }
    
    /**
     * Parse a user input line.
     * Split the line using all spaces that are not surrounded by single or double quotes.
     * 
     * @param line The line.
     * @return The parsed elements.
     */
    private String[] parseLine(String line) {
        List<String> result = new ArrayList<>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher matcher = regex.matcher(line);
        
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                result.add(matcher.group(1));
            }
            else if (matcher.group(2) != null) {
                result.add(matcher.group(2));
            }
            else {
                result.add(matcher.group());
            }
        }
        
        String[] array = new String[result.size()];
        result.toArray(array);
        
        return array;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        App fs = App.getInstance();
        fs.start();
    }
    
}
