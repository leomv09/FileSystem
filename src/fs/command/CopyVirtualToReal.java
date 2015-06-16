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
public class CopyVirtualToReal extends Command{
    
    public static final String COMMAND = "copyVtoR";

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
            String virtualPath = args[1];
            String realPath = args[3];
            App app = App.getInstance();
            Disk disk = app.getDisk();
            disk.copyVirtualToReal(virtualPath, realPath);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(CopyVirtualToReal.class.getName()).log(Level.SEVERE, null, ex);
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
        return "Copies a content from a virtual node to a real path";
    }

    @Override
    protected String getSyntax() 
    {
        return getName() + "VitualPath /to/ RealPath";
    }
    
}
