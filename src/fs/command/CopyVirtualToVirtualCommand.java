/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fs.command;

import fs.App;
import fs.Disk;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leo
 */
public class CopyVirtualToVirtualCommand extends Command{
    
    public static final String COMMAND = "copyVtoV";

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
            String virtualOriginPath = args[1];
            String virtualDestinationPath = args[3];
            App app = App.getInstance();
            Disk disk = app.getDisk();
            disk.copyVirtualToVirtual(virtualOriginPath, virtualDestinationPath);
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(CopyRealToVirtualCommand.class.getName()).log(Level.SEVERE, null, ex);
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
        return "Copies a content from a virtual node to another virtual node of the File System";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + "VirtualOriginPath /to/ VirtualDestinationPath";
    }
    
}
