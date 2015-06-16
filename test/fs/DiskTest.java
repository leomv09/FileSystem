package fs;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static fs.matchers.ContainsNodeMatcher.*;
import java.nio.file.Files;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import org.junit.Ignore;

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
        assertThat(disk.getFiles(dir), containsFile(name));
        assertThat(disk.getFileContent(name), is(content));
        disk.changeFileContent(name, newContent);
        assertThat(disk.getFileContent(name), is(newContent));
        disk.delete(name);
        assertThat(disk.getFiles(dir), not(containsFile(name)));
    }
    
    @Test
    public void testCreateDirectory() throws Exception {
        String dir = disk.getCurrentDirectory();
        String name = "downloads";
        disk.createDirectory(name);
        assertThat(disk.getFiles(dir), containsDirectory(name));
        disk.changeCurrentDirectory(dir + name);
        assertThat(disk.getCurrentDirectory(), is(dir + name));
        disk.delete(dir + name);
        assertThat(disk.getFiles(dir), not(containsDirectory(name)));
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
    public void testMoveFile() throws Exception 
    {
        String dir = disk.getCurrentDirectory();
        String file = "file.txt";
        String downloads = "downloads";
        String desktop = "desktop";
        disk.createDirectory(downloads);
        disk.createDirectory(desktop);
        disk.changeCurrentDirectory(downloads);
        disk.createFile(file, "");
        disk.moveFile(file, dir + desktop);
        assertThat(disk.getFiles(dir + downloads), not(containsFile(file)));
        assertThat(disk.getFiles(dir + desktop), containsFile(file));
    }
    
    @Test
    @Ignore
    public void testVirtualToVirtualCopy() throws Exception 
    {
        String dir = disk.getCurrentDirectory();
        System.out.println(dir);
        String file = "file.txt";
        String file2 = "file2.txt";
        String directory = "downloads";
        disk.createDirectory(directory);
        disk.changeCurrentDirectory(dir + directory);
        disk.createFile(file, "algo");
        disk.createFile(file2, "algomas");
        disk.copyVirtualToVirtual(dir + directory+"/"+file, dir + directory+"/"+file2);
        assertThat(disk.getFileContent(file2), is(disk.getFileContent(file)));
        
    }
    
    @Test
    @Ignore
    public void testRealToVirtualCopy() throws Exception 
    {
        String dir = disk.getCurrentDirectory();
        System.out.println(dir);
        String file = "file.txt";
        String directory = "downloads";
        disk.createDirectory(directory);
        disk.changeCurrentDirectory(dir + directory);
        disk.createFile(file, "algo");
        disk.copyRealToVirtual("C:\\Users\\Leo\\Desktop\\realFile.txt", disk.getCurrentDirectory() + "/" + file);
        assertThat(disk.getFileContent(file), is("cosas."));
    }
    
    @Test
    @Ignore
    public void testVirtualToRealCopy() throws Exception 
    {
        String path = "C:\\Users\\Leo\\Desktop\\realFile2.txt";
        java.io.File originFile = new java.io.File(path);
        String dir = disk.getCurrentDirectory();
        System.out.println(dir);
        String file = "file.txt";
        String directory = "downloads";
        disk.createDirectory(directory);
        disk.changeCurrentDirectory(dir + directory);
        disk.createFile(file, "algo");
        disk.copyVirtualToReal(disk.getCurrentDirectory() + "/" + file, path);
        byte[] bytes = Files.readAllBytes(originFile.toPath());
        String content = new String(bytes);
        assertThat(content, is(disk.getFileContent(file)));
    }
    
    @Test
    public void testFind() throws Exception 
    {
        String dir = disk.getCurrentDirectory();
        System.out.println(dir);
        String file = "file.txt";
        String file2 = "file2.txt";
        String directory = "downloads";
        disk.createDirectory(directory);
        disk.changeCurrentDirectory(dir + directory);
        disk.createFile(file, "algo");
        disk.createFile(file2, "algo");
        List<Node> files = disk.getFiles(disk.getCurrentDirectory(), "*.txt");
        assertThat(files.size(), is(2));
    }
    
}
