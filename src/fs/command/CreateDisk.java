/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fs.command;

import fs.App;
import fs.Disk;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Leo
 */
public class CreateDisk extends Command{
    
    
    public final static String COMMAND = "CREATE"; 

    @Override
    public void execute(String[] args) {
        if ( args.length != 3) {
            reportSyntaxError();
            return;
        }
        try
        {
            int sectorsQuantity = Integer.parseInt(args[1]);
            int sectorSize = Integer.parseInt(args[2]);
            App app = App.getInstance();
            Disk disk = new Disk("disk.txt", sectorsQuantity, sectorSize);
            app.setDisk(disk);
            Path currentRelativePath = Paths.get("");
            System.out.println("Disk created successfully. Disk path: " + currentRelativePath.toAbsolutePath().toString() + "/disk.txt");
        }
        catch(NumberFormatException ex)
        {
            reportError(ex.getMessage());
        }
    }

    @Override
    protected String getName() {
        return COMMAND;
    }

    @Override
    protected String getDescription() 
    {
        return "Creates a virtual disk defining the sectors quantity and its size.";
    }

    @Override
    protected String getSyntax() {
        return getName() + " SECTORS | SECTOR_SIZE";
    }
    
}
