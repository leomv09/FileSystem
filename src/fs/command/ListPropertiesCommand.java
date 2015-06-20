package fs.command;

import fs.App;
import fs.Disk;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Leo
 */
public class ListPropertiesCommand extends Command{
    
    public static final String COMMAND = "prop";

    @Override
    public void execute(String[] args) 
    {
        if (args.length != 2)
        {
            reportSyntaxError();
            return;
        }
        
        try 
        {
            App app = App.getInstance();
            Disk disk = app.getDisk();
            String path = args[1];
            Map<String, Object> properties = disk.getFileProperties(path);
            
            for (Entry<String, Object> property : properties.entrySet()) {
                System.out.println(property.getKey() + ": " + property.getValue());
            }
        } 
        catch (IOException ex) 
        {
            reportError(ex);
        }
    }

    @Override
    protected String getName() {
        return COMMAND;
    }

    @Override
    protected String getDescription() 
    {
        return "Print the properties of a file.";
    }

    @Override
    protected String getSyntax() {
        return getName() + " FILE";
    }
    
}
