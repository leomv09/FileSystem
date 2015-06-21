package fs;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class Directory extends Node {

    public Directory(String name) {
        super(name);
    }
    
    public Directory(Directory directory) {
        this(directory.name);
        this.creationDate = directory.creationDate;
        this.lastModificationDate = directory.lastModificationDate;
    }
    
}
