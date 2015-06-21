package fs;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import java.nio.file.Files;
import java.util.List;
import static fs.matchers.ContainsNodeMatcher.*;
import fs.util.FileUtils;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class DiskTest {

    private static final String diskName = "test-disk.txt";
    private static final java.io.File realFile = new java.io.File("file1.txt");
    private static final java.io.File realDirectory = new java.io.File("real");
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
        if (DiskTest.realFile.exists()) {
            FileUtils.delete(DiskTest.realFile);
        }
        if (DiskTest.realDirectory.exists()) {
            FileUtils.delete(DiskTest.realDirectory);
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
    public void testMoveFile_Rename() throws Exception {
        String dir = disk.getCurrentDirectory();
        String file1 = "file1.txt";
        String file2 = "file2.txt";

        // Test move file to file (rename)
        // [ move(file1.txt, file2.txt) ]
        disk.createFile(file1, "");
        disk.moveFile(file1, file2);
        assertThat(disk.getFiles(dir), not(containsFile(file1)));
        assertThat(disk.getFiles(dir), containsFile(file2));
    }

    @Test
    public void testMoveFile_Move() throws Exception {
        String dir = disk.getCurrentDirectory();
        String file1 = "file1.txt";
        String desktop = "desktop";

        disk.createDirectory(desktop);
        disk.createFile(file1, "");

        // Test move file to directory keeping the same name. (move)
        // [ move(file1.txt, desktop) ]
        disk.moveFile(file1, desktop);
        assertThat(disk.getFiles(dir), not(containsFile(file1)));
        assertThat(disk.getFiles(desktop), containsFile(file1));
    }

    @Test
    public void testMoveFile_MoveAndRename() throws Exception {
        String dir = disk.getCurrentDirectory();
        String file1 = "file1.txt";
        String file2 = "file2.txt";
        String desktop = "desktop";

        disk.createDirectory(desktop);
        disk.createFile(file1, "");

        // Test move file to directory with other name. (move + rename)
        // [ move(file1.txt, desktop/file2.txt) ]
        disk.moveFile(file1, desktop + "/" + file2);
        assertThat(disk.getFiles(dir), not(containsFile(file1)));
        assertThat(disk.getFiles(desktop), containsFile(file2));
    }

    @Test
    public void testMoveDirectory_Rename() throws Exception {
        String dir = disk.getCurrentDirectory();
        String downloads = "downloads";
        String desktop = "desktop";

        disk.createDirectory(downloads);

        // Test move directory to directory (rename)
        // [ move(downloads, desktop) ]
        disk.moveFile(downloads, desktop);
        assertThat(disk.getFiles(dir), not(containsDirectory(downloads)));
        assertThat(disk.getFiles(dir), containsDirectory(desktop));
    }

    @Test
    public void testMoveDirectory_Move() throws Exception {
        String dir = disk.getCurrentDirectory();
        String downloads = "downloads";
        String desktop = "desktop";

        disk.createDirectory(downloads);
        disk.createDirectory(desktop);

        // Test move directory to other directory keeping the same name. (move)
        // [ move(desktop, downloads) ]
        disk.moveFile(desktop, downloads);
        assertThat(disk.getFiles(dir), not(containsDirectory(desktop)));
        assertThat(disk.getFiles(downloads), containsDirectory(desktop));
    }

    @Test
    public void testMoveDirectory_MoveAndRename() throws Exception {
        String dir = disk.getCurrentDirectory();
        String downloads = "downloads";
        String desktop = "desktop";

        disk.createDirectory(downloads);
        disk.createDirectory(desktop);

        // Test move directory to other directory with other name. (move + rename)
        // [ move(desktop, downloads/downloads) ]
        disk.moveFile(desktop, downloads + "/" + downloads);
        assertThat(disk.getFiles(dir), not(containsDirectory(desktop)));
        assertThat(disk.getFiles(downloads), containsDirectory(downloads));
    }

    @Test
    public void testVirtualToVirtualCopyFile_FileToFile() throws Exception {
        String dir = disk.getCurrentDirectory();
        String file1 = "file1.txt";
        String file2 = "file2.txt";

        disk.createFile(file1, "SOME CONTENT HERE");

        // Test copy file to file.
        // [ copy(file1.txt, file2.txt) ]
        disk.copyVirtualToVirtual(file1, file2);
        assertThat(disk.getFiles(dir), containsFile(file1));
        assertThat(disk.getFiles(dir), containsFile(file2));
        assertThat(disk.getFileContent(file2), is(disk.getFileContent(file1)));
    }

    @Test
    public void testVirtualToVirtualCopyFile_FileToDirectory_KeepName() throws Exception {
        String dir = disk.getCurrentDirectory();
        String file1 = "file1.txt";
        String desktop = "desktop";

        disk.createDirectory(desktop);
        disk.createFile(file1, "SOME CONTENT HERE");

        // Test copy file to directory keeping the same name.
        // [ copy(file1.txt, desktop) ]
        disk.copyVirtualToVirtual(file1, desktop);
        assertThat(disk.getFiles(dir), containsFile(file1));
        assertThat(disk.getFiles(desktop), containsFile(file1));
        assertThat(disk.getFileContent(desktop + "/" + file1), is(disk.getFileContent(file1)));
    }

    @Test
    public void testVirtualToVirtualCopyFile_FileToDirectory_ChangeName() throws Exception {
        String dir = disk.getCurrentDirectory();
        String file1 = "file1.txt";
        String file2 = "file2.txt";
        String desktop = "desktop";

        disk.createDirectory(desktop);
        disk.createFile(file1, "SOME CONTENT HERE");

        // Test copy file to directory with other name.
        // [ copy(file1.txt, desktop/file2.txt) ]
        disk.copyVirtualToVirtual(file1, desktop + "/" + file2);
        assertThat(disk.getFiles(dir), containsFile(file1));
        assertThat(disk.getFiles(desktop), containsFile(file2));
        assertThat(disk.getFileContent(desktop + "/" + file2), is(disk.getFileContent(file1)));
    }

    @Test
    public void testVirtualToVirtualCopyDirectory_DirectoryToDirectory() throws Exception {
        String dir = disk.getCurrentDirectory();
        String file1 = "file1.txt";
        String downloads = "downloads";
        String desktop = "desktop";

        disk.createDirectory(downloads);
        disk.changeCurrentDirectory(downloads);
        disk.createFile(file1, "SOME CONTENT HERE [1]");
        disk.changeCurrentDirectory(dir);

        // Test copy directory to directory
        // [ copy(downloads, desktop) ]
        disk.copyVirtualToVirtual(downloads, desktop);
        assertThat(disk.getFiles(dir), containsDirectory(downloads));
        assertThat(disk.getFiles(dir), containsDirectory(desktop));
        assertThat(disk.getFiles(dir + downloads), containsFile(file1));
        assertThat(disk.getFiles(dir + desktop), containsFile(file1));
    }

    @Test
    public void testVirtualToVirtualCopyDirectory_DirectoryToOtherDirectory_KeepName() throws Exception {
        String dir = disk.getCurrentDirectory();
        String file1 = "file1.txt";
        String downloads = "downloads";
        String desktop = "desktop";

        disk.createDirectory(downloads);
        disk.createDirectory(desktop);
        disk.changeCurrentDirectory(downloads);
        disk.createFile(file1, "SOME CONTENT HERE [1]");
        disk.changeCurrentDirectory(dir);

        // Test copy directory to other directory keeping the same name.
        // [ copy(desktop, downloads) ]
        disk.copyVirtualToVirtual(downloads, desktop);
        assertThat(disk.getFiles(dir), containsDirectory(downloads));
        assertThat(disk.getFiles(dir + desktop), containsDirectory(downloads));
        assertThat(disk.getFiles(dir + desktop + "/" + downloads), containsFile(file1));
    }

    @Test
    public void testVirtualToVirtualCopyDirectory_DirectoryToOtherDirectory_ChangeName() throws Exception {
        String dir = disk.getCurrentDirectory();
        String file1 = "file1.txt";
        String downloads = "downloads";
        String desktop = "desktop";

        disk.createDirectory(downloads);
        disk.createDirectory(desktop);
        disk.changeCurrentDirectory(downloads);
        disk.createFile(file1, "SOME CONTENT HERE [1]");
        disk.changeCurrentDirectory(dir);

        // Test copy directory to other directory with other name.
        // [ copy(downloads, desktop/desktop) ]
        disk.copyVirtualToVirtual(downloads, desktop + "/" + desktop);
        assertThat(disk.getFiles(dir), containsDirectory(downloads));
        assertThat(disk.getFiles(dir + desktop), containsDirectory(desktop));
        assertThat(disk.getFiles(dir + desktop + "/" + desktop), containsFile(file1));
    }

    @Test
    public void testRealToVirtualCopyFile() throws Exception {
        String real = "test/files/file1.txt";
        String virtual = "file.txt";

        disk.copyRealToVirtual(real, virtual);
        assertThat(disk.getFileContent(virtual), is("BAZINGA"));
    }

    @Test
    public void testRealToVirtualCopyDirectory() throws Exception {
        String real = "test/files";
        String virtual = "downloads";
        String file1 = "file1.txt";
        String file2 = "file2.txt";

        disk.copyRealToVirtual(real, virtual);
        assertTrue(disk.exists(virtual));
        assertThat(disk.getFiles(virtual), containsFile(file1));
        assertThat(disk.getFiles(virtual), containsFile(file2));
        assertThat(disk.getFileContent(virtual + "/" + file1), is("BAZINGA"));
        assertThat(disk.getFileContent(virtual + "/" + file2), is("SMEGMA"));
    }

    @Test
    public void testVirtualToRealCopyFile() throws Exception {
        String virtual = "file2.txt";
        String real = DiskTest.realFile.getName();

        disk.createFile(virtual, "SOME CONTENT HERE");
        disk.copyVirtualToReal(virtual, real);

        assertTrue(DiskTest.realFile.exists());
        assertTrue(DiskTest.realFile.isFile());

        byte[] bytes = Files.readAllBytes(DiskTest.realFile.toPath());
        String content = new String(bytes);

        assertThat(content, is(disk.getFileContent(virtual)));
    }

    @Test
    public void testVirtualToRealCopyDirectory() throws Exception {
        String dir = disk.getCurrentDirectory();
        String virtual = "downloads";
        String real = DiskTest.realDirectory.getName();
        String file1 = "file1.txt";
        String file2 = "file2.txt";

        disk.createDirectory(virtual);
        disk.changeCurrentDirectory(virtual);
        disk.createFile(file1, "BAZINGA");
        disk.createFile(file2, "SMEGMA");
        disk.changeCurrentDirectory(dir);

        disk.copyVirtualToReal(virtual, real);

        assertTrue(DiskTest.realDirectory.exists());
        assertTrue(DiskTest.realDirectory.isDirectory());

        java.io.File real1 = new java.io.File(DiskTest.realDirectory, file1);
        java.io.File real2 = new java.io.File(DiskTest.realDirectory, file2);

        assertTrue(real1.exists());
        assertTrue(real1.isFile());
        assertTrue(real2.exists());
        assertTrue(real2.isFile());

        byte[] bytes1 = Files.readAllBytes(real1.toPath());
        String content1 = new String(bytes1);

        assertThat(content1, is(disk.getFileContent(virtual + "/" + file1)));

        byte[] bytes2 = Files.readAllBytes(real2.toPath());
        String content2 = new String(bytes2);

        assertThat(content2, is(disk.getFileContent(virtual + "/" + file2)));
    }

    @Test
    public void testFind() throws Exception {
        String file1 = "file1.txt";
        String file2 = "file2.txt";
        String downloads = "downloads";

        disk.createDirectory(downloads);
        disk.changeCurrentDirectory(downloads);
        disk.createFile(file1, "");
        disk.createFile(file2, "");

        List<String> files = disk.getFiles("/", "*.txt");
        assertThat(files.size(), is(2));
    }

}
