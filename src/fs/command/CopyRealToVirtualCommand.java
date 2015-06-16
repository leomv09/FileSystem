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
public class CopyRealToVirtualCommand extends Command{
    
    public static final String COMMAND = "copyRtoV";

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
            String realPath = args[1];
            String virtualPath = args[3];
            App app = App.getInstance();
            Disk disk = app.getDisk();
            disk.copyRealToVirtual(realPath, virtualPath);
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
        return "Copies a content from a real path to a node of the File System";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + " RealPath /to/ VirtualPath";
    }
    
}
