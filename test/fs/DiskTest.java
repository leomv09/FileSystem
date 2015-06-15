package fs;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static fs.matchers.ContainsNodeMatcher.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class DiskTest {
    
    private static final String diskName = "test-disk.txt";
    private Disk disk;
    
    @Before
    public void setUp() {
        this.disk = new Disk(DiskTest.diskName, 1000, 10);
    }
    
    @AfterClass
    public static void tearDownClass() {
        java.io.File file = new java.io.File(DiskTest.diskName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testCreateFile() throws Exception {
        String dir = disk.getCurrentDirectory();
        String name = "file.txt";
        String content = "abcdefghij 0123456789";
        String newContent = "#@$%&*(){}[]\t\n\r";
        disk.createFile(name, content);
        assertThat(disk.getFiles(dir), containsNode(new File(name)));
        assertThat(disk.getFileContent(name), is(content));
        disk.changeFileContent(name, newContent);
        assertThat(disk.getFileContent(name), is(newContent));
        disk.delete(name);
        assertThat(disk.getFiles(dir), not(containsNode(new File(name))));
    }
    
    @Test
    public void testCreateDirectory() throws Exception {
        String dir = disk.getCurrentDirectory();
        String name = "downloads";
        disk.createDirectory(name);
        assertThat(disk.getFiles(dir), containsNode(new Directory(name)));
        disk.changeCurrentDirectory(dir + name);
        assertThat(disk.getCurrentDirectory(), is(dir + name));
        disk.delete(dir + name);
        assertThat(disk.getFiles(dir), not(containsNode(new Directory(name))));
        assertThat(disk.getCurrentDirectory(), is(dir));
    }
    
    @Test
    public void testExists() throws Exception {
        String file = "file.txt";
        String directory = "downloads";
        disk.createDirectory(directory);
        disk.createFile(file, "");
        assertTrue(disk.exists(file));
        assertTrue(disk.isFile(file));
        assertTrue(disk.exists(directory));
        assertTrue(disk.isDirectory(directory));
    }
    
    @Test
    public void testMoveFile() throws Exception {
        
    }
    
    @Test
    public void testVirtualToVirtualCopy() throws Exception {
        
    }
    
    @Test
    public void testVirtualToRealCopy() throws Exception {
        
    }
    
    @Test
    public void testRealToVirtualCopy() throws Exception {
        
    }
    
    @Test
    public void testFind() throws Exception {
        
    }
    
}
