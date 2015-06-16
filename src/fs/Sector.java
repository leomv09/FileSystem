package fs;

/**
 * A sector in a disk.
 * 
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class Sector implements Comparable<Sector> {
    
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.index;
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
        final Sector other = (Sector) obj;
        return this.index == other.index;
    }

    @Override
    public int compareTo(Sector other) {
        return this.index - other.index;
    }

}
