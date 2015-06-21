package fs;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class File extends Node {

    public File(String name, List<Sector> sectors) {
        super(name, sectors);
    }
    
    public File(String name) {
        this(name, new ArrayList<>());
    }
    
    public File(File file) {
        this(file.name);
        this.creationDate = file.creationDate;
        this.lastModificationDate = file.lastModificationDate;
    }
    
}
