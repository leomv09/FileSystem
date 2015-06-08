
package fs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;

/**
 * Represents the nodes information of the three.
 * 
 * @author Leonardo
 */
public class Node {
    
    private String name;
    private final boolean isDirectory;
    private final Date creationDate;
    private Date lastModificationDate;
    private final List<Sector> sectors;
    
    
    /**
     * Creates a new file Node object.
     * 
     * @param Name The name of the file.
     * @param Sectors List of sectors related to the file.
     */
    public Node(String Name, List<Sector> Sectors)
    {
        this.name = Name;
        this.isDirectory = false;
        this.creationDate = new Date();
        this.lastModificationDate = creationDate;
        this.sectors = Sectors;
    }
    
    /**
     * Creates a new directory node object.
     * 
     * @param Name The name of the directory.
     */
   public Node(String Name)
    {
        this.name = Name;
        this.isDirectory = true;
        this.creationDate = new Date();
        this.lastModificationDate = creationDate;
        this.sectors = null;
    }
   
   /**
    * Obtains the name of a node.
    * 
    * @return The name of the node.
    */
   public String getName()
   {
       return this.name;
   }

    public List<Sector> getSectors() {
        return sectors;
    }
   
   /**
    * Obtains the creation date of the node.
    * 
    * @return A Date object with the creation date information.
    */
   public Date getCreationDate()
   {
       return this.creationDate;
   }
   
   /**
    * Obtains the last modification date of the node.
    * 
    * @return A Date object with the modification date information.
    */
   public Date getLastModificationDate()
   {
       return this.lastModificationDate;
   }
   
   /**
    * Sets the name of a node.
    * 
    * @param Name The name of the file/directory.
    */
   public void setName(String Name)
   {
       this.name = Name;
       this.lastModificationDate = new Date();
   }
   
   /**
    * Adds a sector to the list of sectors of the file.
    * 
    * @param sector The sector object to add.
    */
   public void addSector(Sector sector)
   {
       this.sectors.add(sector);
   }

   /**
    * Verifies if a node is a directory.
    * 
    * @return true if the node is a directory, false otherwise.
    */
   public boolean isDirectory()
   {
       return this.isDirectory;
   }
   
   /**
    * Obtains the extension of a file.
    * 
    * @return The file extension.
    */
   public String getExtension()
   {
       if(!this.isDirectory())
       {
            int index = this.name.indexOf(".");
            if(index != -1)
            {
                return this.name.substring(index, name.length());
            }
            else
            {
                return "";
            }
       }
       else
       {
           return "";
       }
   }
   
   /**
    * Obtains the size of a file.
    * 
    * @param file The disk storage.
    * @param sectorSize The size of each sector.
    * @return The bytes length of the file.
    */
   public int getSize(File file, int sectorSize)
   {
       return getContent(file, sectorSize).getBytes().length;
   }
   
   
   /**
    * Obtains the content of the current node.
    * 
    * @param file The disk storage.
    * @param sectorSize The size of each sector.
    * @return A string containing the node content.
    */
   public String getContent(File file, int sectorSize)
   {
       try
       {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            byte[] bytes = null;
            char[] currentChunk = null;
            raf.readFully(bytes);
            String text = new String(bytes);
            StringBuilder res = new StringBuilder();
            for(Sector sector : sectors)
            {
                int beginIndex = sector.getIndex() * sectorSize;
                currentChunk = text.substring(beginIndex, beginIndex + sectorSize).toCharArray();
                
                for(int i = 0; i < currentChunk.length; i++)
                {
                    if(currentChunk[i] == Disk.ZERO)
                    {
                        break;
                    }
                    res.append(currentChunk[i]);
                }
            }
            
            return res.toString();
       }
       catch(IOException ex)
       {
           return null;
       }
   }
   
   
}
