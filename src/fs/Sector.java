package fs;

/**
 * A sector in a disk.
 * 
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class Sector {
    
    /**
     * The sector index.
     */
    private final int index;

    /**
     * Create a sector.
     * 
     * @param index The index.
     */
    public Sector(int index) {
        this.index = index;
    }

    /**
     * Get the sector index.
     * 
     * @return The index.
     */
    public int getIndex() {
        return index;
    }
    
}
