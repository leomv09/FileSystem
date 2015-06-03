package fs;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link Sector}
 * 
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class SectorBuilder {
    
    /**
     * Counter of builded sectors.
     */
    private int count;
    
    /**
     * Creates a new SectorBuilder.
     */
    public SectorBuilder() {
        this.count = 0;
    }
    
    /**
     * Creates n Sectors.
     * 
     * @param amount The amount to create.
     * @return The list of sectors created.
     */
    public List<Sector> create(int amount) {
        List<Sector> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            list.add(create());
        }
        return list;
    }
    
    /**
     * Create a sector.
     * 
     * @return The sector.
     */
    public Sector create() {
        return new Sector(count++);
    }
    
    /**
     * Reset the counter of sectors created.
     */
    public void reset() {
        count = 0;
    }
    
}
