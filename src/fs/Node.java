
package fs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents the nodes information of the three.
 * 
 * @author Leonardo
 */
public class Node {
    
    private String name;
    private final boolean isDirectory;
    private Date creationDate;
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
        this.sectors = new ArrayList<>();
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

   /**
    * Obtains the sector list.
    * 
    * @return The sectors.
    */
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
    * Sets the creation date of a node.
    * 
    * @param date The creation date.
    */
   public void setCreationDate(Date date)
   {
       this.creationDate = date;
   }
   
   
   public void setLastModificationDate(Date date)
   {
       this.lastModificationDate = date;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + (this.isDirectory ? 1 : 0);
        hash = 29 * hash + Objects.hashCode(this.creationDate);
        hash = 29 * hash + Objects.hashCode(this.lastModificationDate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.isDirectory != other.isDirectory) {
            return false;
        }
        if (!Objects.equals(this.creationDate, other.creationDate)) {
            return false;
        }
        return Objects.equals(this.lastModificationDate, other.lastModificationDate);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d k:m");
        sb.append(sdf.format(this.lastModificationDate)).append(" ");
        sb.append(this.name);
        if (this.isDirectory) {
            sb.append("/");
        }
        return sb.toString();
    }
    
    

}
