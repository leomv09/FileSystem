package fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NotDirectoryException;
import java.util.List;

/**
 * Virtual Disk.
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class Disk {

    public static char ZERO = '0';

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
     * The size of a single sector. Amount of characters that a sector can hold.
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
        this.sectorSize = sectorSize;
        this.sectorAmount = sectorAmount;
        this.root = new Tree<>(null); // CREATE ROOT NODE HERE
        this.availableSectors = builder.create(this.sectorAmount);
        this.current = root;
        writeZeros(this.availableSectors);
    }

    /**
     * Change the current directory.
     *
     * @param path Path of the new directory
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     * @throws java.nio.file.NotDirectoryException If the path isn't a
     * directory.
     */
    public void changeCurrentDirectory(String path) throws FileNotFoundException, NotDirectoryException {
        Tree<Node> actual = searchTree(path);
        if (actual == null) {
            throw new FileNotFoundException("File not found");
        }
        if (!actual.getData().isDirectory()) {
            throw new NotDirectoryException("The path is not a directory");
        }
        current = actual;
    }

    /**
     * Check if a file or directory exists;
     *
     * @param path The file or directory path.
     * @return true if a file or directory exists;
     */
    public boolean exists(String path) {
        return searchTree(path) != null;
    }

    /**
     * Get the content of a file.
     *
     * @param path The path of the file.
     * @return The file content.
     * @throws java.io.IOException if an I/O error occurs reading the file.
     */
    public String getFileContent(String path) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Change the content of a file.
     *
     * @param path The path of the file.
     * @param content The new content.
     * @throws java.io.IOException if an I/O error occurs writing to the file.
     */
    public void changeFileContent(String path, String content) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Create a new file.
     *
     * @param path The path where the file will be created.
     * @param content The content to write in the file.
     * @throws java.io.IOException if an I/O error occurs writing to or creating
     * the file.
     */
    public void createFile(String path, String content) throws IOException {
        if (!isValidPath(path)) {
            throw new MalformedURLException("Invalid file name");
        }
        if (exists(path)) {
            throw new FileAlreadyExistsException("File already exists");
        }

        String fileName = getFileName(path);
        String directory = getDirectory(path);

        Tree<Node> parent = searchTree(directory);
        if (parent == null || !parent.getData().isDirectory()) {
            throw new FileNotFoundException("Directory '" + directory + "' doesn't exists");
        }

        int required = requiredSectors(content);
        if (required < availableSectors.size()) {
            throw new IOException("Insufficient disk space");
        }

        List<Sector> sectors = getSectors(required);
        Node node = new Node(fileName, sectors);
        writeToSectors(sectors, content);
        parent.add(node);
    }

    /**
     * Delete a file.
     *
     * @param path The path of the file.
     * @throws java.io.IOException if an I/O error occurs deleting the file.
     */
    public void deleteFile(String path) throws IOException {
        // Use function deleteTree()
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Create a new directory.
     *
     * @param path The path where the directory will be created.
     * @throws java.io.IOException if an I/O error occurs creating the
     * directory.
     */
    public void createDirectory(String path) throws IOException {
        if (!isValidPath(path)) {
            throw new MalformedURLException("Invalid directory name");
        }
        if (exists(path)) {
            throw new FileAlreadyExistsException("Directory already exists");
        }

        String[] array = path.split("/");
        String name = array[array.length - 1];
        String directory = path.substring(0, path.lastIndexOf("/"));

        Tree<Node> parent = searchTree(directory);
        if (parent == null || !parent.getData().isDirectory()) {
            throw new FileNotFoundException("Directory '" + directory + "' doesn't exists");
        }

        Node node = new Node(name);
        parent.add(node);
    }

    /**
     * Delete a directory.
     *
     * @param path The path of the directory.
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     * @throws java.io.IOException if an I/O error occurs deleting the
     * directory.
     */
    public void deleteDirectory(String path) throws IOException {
        Tree<Node> tree = searchTree(path);
        if (tree == null) {
            throw new FileNotFoundException("Directory doesn't exists");
        }
        if (tree.isRoot()) {
            throw new AccessDeniedException("Root folder cannot be deleted");
        }
        deleteTree(tree);
    }

    /**
     * Change a file location.
     *
     * @param oldPath The path of the file.
     * @param newPath The new path of the file.
     * @throws java.io.IOException if an I/O error occurs moving the file.
     */
    public void moveFile(String oldPath, String newPath) throws IOException {
        Tree<Node> node = searchTree(oldPath);
        if (node == null) {
            throw new FileNotFoundException("File doesn't exists");
        }
        
        String fileName = getFileName(newPath);
        String directory = getDirectory(newPath);
        Tree<Node> newDir = searchTree(directory);
        if (newDir == null) {
            throw new FileNotFoundException("Directory '" + directory + "' doesn't exists");
        }
        
        node.setParent(newDir);
        if (!fileName.isEmpty()) {
            node.getData().setName(fileName);
        }
    }

    /**
     * Get the files and directories in a directory.
     *
     * @param directory The path of the directory.
     * @return The list of children of the directory.
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     * @throws java.io.IOException if an I/O error occurs reading the directory.
     */
    public List<Node> getFiles(String directory) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the files that satisfies certain regex in a directory.
     *
     * @param directory The path of the directory.
     * @param regex The regular expression.
     * @return The list of children of the directory.
     * @throws java.io.IOException if an I/O error occurs reading the directory.
     */
    public List<Node> getFiles(String directory, String regex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the file system tree.
     *
     * @return The file system tree.
     */
    public Tree<Node> getTree() {
        return root;
    }

    /**
     * Search a subtree in the current directory or in the root. If the path
     * starts with '/' the search will start in the root, otherwise if will
     * start in the current directory.
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

        for (String curr : array) {
            changed = false;
            for (Tree<Node> child : actual.children()) {
                node = child.getData();
                if (node.getName().equals(curr)) {
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
     * Search a node in the current directory or in the root. If the path starts
     * with '/' the search will start in the root, otherwise if will start in
     * the current directory.
     *
     * @param path The path to search.
     * @return The node if found, otherwise null.
     */
    private Node searchNode(String path) {
        Tree<Node> actual = searchTree(path);
        return actual != null ? actual.getData() : null;
    }
    
    /**
     * Delete a tree in the file system from memory and disk
     * 
     * @param tree The tree to delete.
     */
    private void deleteTree(Tree<Node> tree) {
        
    }

    /**
     * Check if a file or directory name is valid.
     *
     * @param name The name.
     * @return true if the name is valid.
     */
    private boolean isValidName(String name) {
        return !name.isEmpty();
    }

    /**
     * Check if a path is valid.
     *
     * @param path The path.
     * @return true if the path is valid.
     */
    private boolean isValidPath(String path) {
        String[] array = path.split("/");
        for (String curr : array) {
            if (!isValidName(curr)) {
                return false;
            }
        }
        return true;
    }
    
    private String getFileName(String path) {
        String[] array = path.split("/");
        return array[array.length - 1];
    }
    
    private String getDirectory(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

    /**
     * Calculate the amount of sectors required to store a string in disk.
     *
     * @param content The string to store.
     * @return The required sectors.
     */
    private int requiredSectors(String content) {
        if (content.isEmpty()) {
            return 0;
        } else {
            return (int) Math.ceil((double) content.length() / (double) sectorSize);
        }
    }

    /**
     * Remove and return n sectors from the list of available sectors.
     *
     * @param count The number of sectors to remove.
     * @return The sectors.
     */
    private List<Sector> getSectors(int count) {
        List<Sector> sectors = availableSectors.subList(0, count);
        availableSectors.removeAll(sectors);
        return sectors;
    }

    /**
     * Add a list of sectors to the available sectors.
     *
     * @param sectors The sectors.
     */
    private void addSectors(List<Sector> sectors) {
        availableSectors.addAll(sectors);
    }

    /**
     * Write a string to file on a given sector.
     *
     * @param sector The sector to write.
     * @param content The string to write.
     *
     * @return true if no errors occurs.
     */
    private boolean writeToSector(Sector sector, String content) {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            byte[] bytes = null;
            int index = sector.getIndex();
            raf.readFully(bytes);
            String text = new String(bytes);
            text = text.substring(0, index) + content + text.substring(index + sectorSize);
            raf.writeChars(text);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Write a string to a file in the given sectors.
     *
     * @param sectors The sectors.
     * @param content The content to write.
     * @return true if no errors occurs.
     */
    private boolean writeToSectors(List<Sector> sectors, String content) {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            byte[] bytes = null;
            Sector sector;
            String chunk;
            int index;

            raf.readFully(bytes);
            String text = new String(bytes);

            for (int i = 0; i < sectors.size(); i++) {
                sector = sectors.get(i);
                index = sector.getIndex();
                chunk = content.substring(i * sectorSize, (i + 1) * sectorSize);
                text = text.substring(0, index) + chunk + text.substring(index + sectorSize);
            }

            raf.writeChars(text);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Create a string containing only the ZERO character.
     *
     * @param length The length of the string.
     * @return The string.
     */
    private String createZeros(int length) {
        String text = "";
        for (int i = 0; i < length; i++) {
            text += Disk.ZERO;
        }
        return text;
    }

    /**
     * Delete the content of a sector.
     *
     * @param sector The sector.
     */
    private void writeZeros(Sector sector) {
        String text = createZeros(sectorSize * sectorAmount);
        writeToSector(sector, text);
    }

    /**
     * Delete the content of a set of sectors.
     *
     * @param sectors The sectors.
     */
    private void writeZeros(List<Sector> sectors) {
        String text = createZeros(sectorSize * sectorAmount);
        writeToSectors(sectors, text);
    }

}
