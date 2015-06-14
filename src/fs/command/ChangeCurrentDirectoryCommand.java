/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fs.command;

import fs.App;
import fs.Disk;
import java.io.IOException;

/**
 *
 * @author Leo
 */
public class ChangeCurrentDirectoryCommand extends Command{
    
    public static final String COMMAND = "cd";

    @Override
    public void execute(String[] args) {
        try 
        {
            if (args.length != 2)
            {
                reportSyntaxError();
                return;
            }
            
            App app = App.getInstance();
            Disk disk = app.getDisk();
            String path = args[1];
            
            disk.changeCurrentDirectory(path);
        } 
        catch (IOException ex) {
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
        return "Changes the current directory to the specified path.";   
    }

    @Override
    protected String getSyntax() {
       return getName() + " PATH";
    }
    
}
