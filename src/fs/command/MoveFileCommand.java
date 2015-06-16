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
public class MoveFileCommand extends Command{

    public static final String COMMAND = "move";
    @Override
    public void execute(String[] args) 
    {
        try 
        {
            if (args.length != 4)
            {
                reportSyntaxError();
                return;
            }
            String oldPath = args[1];
            String newPath = args[3];
            App app = App.getInstance();
            Disk disk = app.getDisk();
            disk.moveFile(oldPath, newPath);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MoveFileCommand.class.getName()).log(Level.SEVERE, null, ex);
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
        return "Moves a file or directory to another directory.";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + "CurrentDirName /to/ NewDirName";
    }
    
}
