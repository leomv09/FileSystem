package fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NotDirectoryException;
import java.util.List;

/**
 * Virtual Disk.
 * 
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class Disk {
    
    /**
     * The file where the disk is stored.
     */
    private final File file;
    
    /**
     * List of available sectors.
     */
    private final List<Sector> availableSectors;
    
    /**
     * The root node of the file system tree.
     */
    private final Tree<Node> root;
    
    /**
     * The current directory node in the file system.
     */
    private Tree<Node> current;
    
    /**
     * The size of a single sector.
     * Amount of characters that a sector can hold.
     */
    private final int sectorSize;
    
    /**
     * The amount of sectors in the disk.
     */
    private final int sectorAmount;
    
    /**
     * Create a new disk.
     * 
     * @param path The path where the disk will be write.
     * @param sectorAmount The amount of sectors of the disk.
     * @param sectorSize The size of a single sector.
     */
    public Disk(String path, int sectorAmount, int sectorSize) {
        SectorBuilder builder = new SectorBuilder();
        this.file = new File(path);
        this.root = new Tree<>(null);
        this.availableSectors = builder.create(sectorAmount);
        this.sectorSize = sectorSize;
        this.sectorAmount = sectorAmount;
        this.current = root;
    }
    
    /**
     * Search a subtree in the current directory or in the root.
     * If the path starts with '/' the search will start in the root,
     * otherwise if will start in the current directory.
     * 
     * @param path The path to search.
     * @return The subtree if found, otherwise null.
     */
    private Tree<Node> searchTree(String path) {
        String[] array = path.split("/");
        Tree<Node> actual = current;
        boolean changed;
        Node node;
        
        if (path.startsWith("/")) {
            actual = root;
        }
        
        for (String dir : array) {
            changed = false;
            for (Tree<Node> child : actual.getChildren()) {
                node = child.getData();
                if (node.getName().equals(dir)) {
                    actual = child;
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                return null;
            }
        }
        
        return actual;
    }
    
    /**
     * Search a node in the current directory or in the root.
     * If the path starts with '/' the search will start in the root,
     * otherwise if will start in the current directory.
     * 
     * @param path The path to search.
     * @return The node if found, otherwise null.
     */
    private Node searchNode(String path) {
        Tree<Node> actual = searchTree(path);
        if (actual != null) {
            return actual.getData();
        }
        return null;
    }
    
    /**
     * Change the current directory.
     * 
     * @param path Path of the new directory
     * @return true if the current directory is changed by this call.
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     * @throws java.nio.file.NotDirectoryException If the path isn't a directory.
     */
    public boolean changeCurrentDirectory(String path) throws FileNotFoundException, NotDirectoryException {
        Tree<Node> actual = searchTree(path);
        if (actual == null) {
            throw new FileNotFoundException("File not found");   
        }
        if (!actual.getData().isDirectory()) {
            throw new NotDirectoryException("The path is not a directory");
        }
        current = actual;
        return true;
    }
    
    /**
     * Check if a file or directory exists;
     * 
     * @param path The file or directory path.
     * @return true if a file or directory exists;
     */
    public boolean exists(String path) {
        Tree<Node> actual = searchTree(path);
        return actual != null;
    }
    
    /**
     * Get the content of a file.
     * 
     * @param path The path of the file.
     * @return The file content.
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     */
    public String getFileContent(String path) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Change the content of a file.
     * 
     * @param path The path of the file.
     * @param content The new content.
     * @return true if the file content is changed by this call.
     * @throws FileNotFoundException If the file doesn't exists.
     */
    public boolean changeFileContent(String path, String content) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Create a new file.
     * 
     * @param path The path where the file will be created.
     * @param content The content to write in the file.
     * @return true if the file is created successfully.
     * @throws java.nio.file.FileAlreadyExistsException If the file already exist.
     */
    public boolean createFile(String path, String content) throws FileAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Delete a file.
     * 
     * @param path The path of the file.
     * @return true if the file is deleted.
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     */
    public boolean deleteFile(String path) throws FileNotFoundException {
       throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Create a new directory.
     * 
     * @param path The path where the directory will be created.
     * @return true if the directory is created successfully.
     * @throws java.nio.file.FileAlreadyExistsException If the directory already exists.
     */
    public boolean createDirectory(String path) throws FileAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Delete a directory.
     * 
     * @param path The path of the directory.
     * @return true if the directory is deleted.
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     * @throws java.nio.file.NotDirectoryException If the path isn't a directory.
     */
    public boolean deleteDirectory(String path) throws FileNotFoundException, NotDirectoryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Change a file location.
     * 
     * @param oldPath The path of the file.
     * @param newPath The new path of the file.
     * @return true if the file is moved.
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     */
    public boolean moveFile(String oldPath, String newPath) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Get the files and directories in a directory.
     * 
     * @param directory The path of the directory.
     * @return The list of children of the directory.
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     * @throws java.nio.file.NotDirectoryException If the path isn't a directory.
     */
    public List<Node> getFiles(String directory) throws FileNotFoundException, NotDirectoryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Get the files that satisfies certain regex in a directory.
     * 
     * @param directory The path of the directory.
     * @param regex The regular expression.
     * @return The list of children of the directory.
     * @throws java.io.FileNotFoundException If the directory doesn't exists.
     * @throws java.nio.file.NotDirectoryException If the path isn't a directory.
     */
    public List<Node> getFiles(String directory, String regex) throws FileNotFoundException, NotDirectoryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Get the file system tree.
     * 
     * @return The file system tree.
     */
    public Tree<Node> getTree() {
        return this.root;
    }

}
