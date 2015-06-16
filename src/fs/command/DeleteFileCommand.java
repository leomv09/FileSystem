/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fs.command;

import fs.App;
import fs.Disk;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leo
 */
public class DeleteFileCommand extends Command{
    
    public static final String COMMAND = "delete";

    @Override
    public void execute(String[] args) 
    {
        try 
        {
            if (args.length != 2)
            {
                reportSyntaxError();
                return;
            }
            String path = args[1];
            App app = App.getInstance();
            Disk disk = app.getDisk();
            disk.delete(path);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(DeleteFileCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected String getName() 
    {
        return COMMAND;
    }

    @Override
    protected String getDescription() 
    {
        return "Deletes a node(or several nodes) from the File System tree";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + "Directory path"; 
    }
    
}
