package fs;

import fs.util.Tree;
import fs.util.StringUtils;
import fs.util.FileUtils;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Virtual Disk.
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class Disk {

    public final static char ZERO = 0x7F; // DEL character

    /**
     * The file where the disk is stored.
     */
    private final java.io.File file;

    /**
     * List of available oldSectors.
     */
    private final List<Sector> availableSectors;

    /**
     * The root srcTree of the file system tree.
     */
    private final Tree<Node> root;

    /**
     * The current directory srcTree in the file system.
     */
    private Tree<Node> current;

    /**
     * The size of a single sector. Amount of characters that a sector can hold.
     */
    private final int sectorSize;

    /**
     * The amount of oldSectors in the disk.
     */
    private final int sectorAmount;

    /**
     * Create a new disk.
     *
     * @param path The path where the disk will be write.
     * @param sectorAmount The amount of oldSectors of the disk.
     * @param sectorSize The size of a single sector.
     */
    public Disk(String path, int sectorAmount, int sectorSize) {
        SectorBuilder builder = new SectorBuilder();
        this.file = new java.io.File(path);
        this.sectorSize = sectorSize;
        this.sectorAmount = sectorAmount;
        this.root = new Tree<>(new Directory(""));
        this.availableSectors = builder.create(this.sectorAmount);
        this.current = root;
        if (file.exists()) {
            file.delete();
        }
        writeZeros();
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
            throw new FileNotFoundException("Directory not found");
        }
        if (!actual.getData().isDirectory()) {
            throw new NotDirectoryException("The path is not a directory");
        }
        current = actual;
    }
    
    /**
     * Get the current directory.
     * 
     * @return The current directory.
     */
    public String getCurrentDirectory() {
        return getAbsolutePath(current);
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
     * Check if a path is a file.
     * 
     * @param path The path.
     * @return True if a file exists in the given path;
     */
    public boolean isFile(String path) {
        Node node = searchNode(path);
        return node != null && !node.isDirectory();
    }
    
    /**
     * Check if a path is a directory.
     * 
     * @param path The path.
     * @return True if a directory exists in the given path;
     */
    public boolean isDirectory(String path) {
        Node node = searchNode(path);
        return node != null && node.isDirectory();
    }

    /**
     * Get the content of a file.
     *
     * @param path The path of the file.
     * @return The file content.
     * @throws java.io.IOException if an I/O error occurs reading the file.
     */
    public String getFileContent(String path) throws IOException {
        Node node = searchNode(path);
        
        if (node == null)
        {
            throw new FileNotFoundException("File not found");
        }
        
        return readSectors(node.getSectors());
    }
    
    public String getAbsolutePath(String path) throws IOException {
       Tree<Node> tree = searchTree(path);
       
       if (tree == null) {
           throw new FileNotFoundException("File not found");
       }
       
       return getAbsolutePath(tree);
   }

    /**
     * Change the content of a file.
     *
     * @param path The path of the file.
     * @param content The new content.
     * @throws java.io.IOException if an I/O error occurs writing to the file.
     */
    public void changeFileContent(String path, String content) throws IOException {
        Node node = searchNode(path);
        
        if (node == null) {
            throw new FileNotFoundException("File not found");
        }
        
        List<Sector> oldSectors = node.getSectors();
        markSectorsAsAvailable(oldSectors);
        
        int required = requiredSectors(content);
        if (required > availableSectors.size()) {
            throw new IOException("Insufficient disk space");
        }

        List<Sector> newSectors = getSectors(required);
        writeToSectors(newSectors, content);
        node.setSectors(newSectors);
    }
    
    /**
     * Obtains the size of a file. The size is given as the total of chars that are in the file.
     * 
     * @param path The path of the file.
     * @return The size of the file.
     * @throws IOException 
     */
    public int getFileSize(String path) throws IOException
    {
        return getFileContent(path).toCharArray().length;
    }
    
    /**
     * Obtains the properties of a file.
     * 
     * @param path The path of the file.
     * @return The file properties as a dictionary.
     * @throws IOException If any error occur.
     */
    public Map<String, Object> getFileProperties(String path) throws IOException
    {
        Node node = searchNode(path);
        
        if (node == null) {
            throw new FileNotFoundException("File not found");
        }
        
        Map<String, Object> properties = new TreeMap<>((Object o1, Object o2) -> 1);
        
        properties.put("name", node.getName());
        properties.put("extension", node.getExtension());
        properties.put("size", getFileSize(path));
        properties.put("absolute_path", getAbsolutePath(path));
        properties.put("creation_date", node.getCreationDate());
        properties.put("modification_date", node.getLastModificationDate());
        
        return properties;
    }
    
    /**
     * Create a new file.
     *
     * @param path The path where the file will be created.
     * @param content The content to write in the file.
     * @throws java.io.IOException if an I/O error occurs writing to or creating
     * the file.
     */
    public void createFile(String path, String content) throws Exception {
        if (!FileUtils.isValidPath(path)) {
            throw new MalformedURLException("Invalid file name");
        }
        if (exists(path)) {
            throw new FileAlreadyExistsException("File already exists");
        }

        String fileName = FileUtils.getFileName(path);
        String directory = FileUtils.getDirectory(path);

        Tree<Node> parent = searchTree(directory);
        if (parent == null || !parent.getData().isDirectory()) {
            throw new FileNotFoundException("Directory doesn't exists");
        }

        int required = requiredSectors(content);
        if (required > availableSectors.size()) {
            throw new IOException("Insufficient disk space");
        }

        List<Sector> sectors = getSectors(required);
        Node node = new File(fileName, sectors);
        writeToSectors(sectors, content);
        parent.add(node);
    }
    
    /**
     * Delete a file or directory.
     *
     * @param path The path of the file.
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     * @throws java.io.IOException if an I/O error occurs deleting the file.
     */
    public void delete(String path) throws IOException {
        Tree<Node> tree = searchTree(path);
        Node node = tree.getData();
        if (tree == null) {
            throw new FileNotFoundException("Directory doesn't exist");
        }
        if (tree.isRoot()) {
            throw new AccessDeniedException("Root folder cannot be deleted");
        }
        deleteTree(tree);
        if (current.getData().equals(node)) {
            current = tree.parent();
        }
    }

    /**
     * Create a new directory.
     *
     * @param path The path where the directory will be created.
     * @throws java.io.IOException if an I/O error occurs creating the
     * directory.
     */
    public void createDirectory(String path) throws Exception {
        if (!FileUtils.isValidPath(path)) {
            throw new MalformedURLException("Invalid directory name");
        }
        if (exists(path)) {
            throw new FileAlreadyExistsException("Directory already exist");
        }

        String name = FileUtils.getFileName(path);
        String directory = FileUtils.getDirectory(path);

        Tree<Node> parent = searchTree(directory);
        if (parent == null || !parent.getData().isDirectory()) {
            throw new FileNotFoundException("Directory doesn't exist");
        }

        Node node = new Directory(name);
        parent.add(node);
    }

    /**
     * Change a file location.
     *
     * @param src The path of the file.
     * @param dest The new path of the file.
     * @throws java.io.IOException if an I/O error occurs moving the file.
     */
    public void moveFile(String src, String dest) throws Exception {
        Tree<Node> srcTree = searchTree(src);

        if (srcTree == null) {
            throw new FileNotFoundException("File doesn't exist");
        }
        
        Tree<Node> destTree = searchTree(dest);
        
        if (destTree != null && destTree.getData().isDirectory()) {
            srcTree.setParent(destTree);
        }
        else {
            String fileName = FileUtils.getFileName(dest);
            String directory = FileUtils.getDirectory(dest);
            
            if (!directory.isEmpty()) {
                destTree = searchTree(directory);

                if (destTree == null) {
                    throw new FileNotFoundException("Directory '" + directory + "' doesn't exist");
                }

                srcTree.setParent(destTree);
            }
            
            srcTree.getData().setName(fileName);
        }
    }

    /**
     * Get the files and directories in a directory.
     *
     * @param directory The path of the directory.
     * @return The list of children of the directory. Null if the srcTree is not a directory.
     * @throws java.io.FileNotFoundException If the file doesn't exists.
     * @throws java.io.IOException if an I/O error occurs reading the directory.
     */
    public List<Node> getFiles(String directory) throws IOException 
    {
        Tree treeNode = searchTree(directory);
        List<Node>  nodesList = new ArrayList();
        if(treeNode == null)
        {
            throw new FileNotFoundException("Directory not found.");
        }
        if(((Node)treeNode.getData()).isDirectory())
        {
            for(Tree tree : (List<Tree<Node>>)treeNode.children())
            {
                nodesList.add((Node)tree.getData());
            }
            
            return nodesList;
        }
        return nodesList;
    }

    /**
     * Get the files that satisfies certain regular expression in a directory.
     *
     * @param directory The path of the directory.
     * @param regex The regular expression.
     * @return The list of children of the directory.
     * @throws java.io.IOException if an I/O error occurs reading the directory.
     */
    public List<String> getFiles(String directory, String regex) throws IOException {
        Tree<Node> tree = searchTree(directory);
        
        if (tree == null) {
            throw new FileNotFoundException("Directory not found.");
        }
        
        try {
            return getFiles(tree, regex.replace("*", ".*"));
        }
        catch (PatternSyntaxException ex) {
            throw new IOException("Invalid regular expression " + regex);
        }
    }

    /**
     * Get the file system tree.
     *
     * @param path The path of the directory.
     * @return The file system tree.
     * @throws java.io.IOException If the directory is not found.
     */
    public Tree<Node> getTree(String path) throws IOException {
        Tree<Node> tree = searchTree(path);
        
        if (tree == null) {
            throw new FileNotFoundException("Directory not found.");
        }
        if (!tree.getData().isDirectory()) {
            throw new NotDirectoryException("The path is not a directory");
        }
        
        return tree;
    }
    
    /**
     * Get the files that satisfies certain regular expression in the tree.
     *
     * @param tree The tree.
     * @param regex The regular expression.
     * @return The list of children that satisfies regex.
     */
    private List<String> getFiles(Tree<Node> tree, String regex) throws PatternSyntaxException {
        List<String> list = new ArrayList<>();
        Node node;
        
        for (Tree<Node> child : tree.children())
        {
            node = child.getData();
            if (node.getName().matches(regex)) {
                list.add(getAbsolutePath(child));
            }
            if (node.isDirectory()) {
                list.addAll(getFiles(child, regex));
            }
        }

        return list;
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
        String curr;
        Node node;
        int i = 0;

        if (path.isEmpty()) {
            return current;
        }
        if (path.startsWith("/")) {
            actual = root;
            i = 1;
        }
        
        for (; i < array.length; i++) {
            curr = array[i];
            changed = false;
            if (curr.equals("..")) {
                if (!actual.isRoot()) {
                    actual = actual.parent();
                }
                changed = true;
            }
            else {
                for (Tree<Node> child : actual.children()) {
                    node = child.getData();
                    if (node.getName().equals(curr)) {
                        actual = child;
                        changed = true;
                        break;
                    }
                }
            }
            if (!changed) {
                return null;
            }
        }

        return actual;
    }

    /**
     * Search a srcTree in the current directory or in the root. If the path starts
     * with '/' the search will start in the root, otherwise if will start in
     * the current directory.
     *
     * @param path The path to search.
     * @return The srcTree if found, otherwise null.
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
        if (!tree.isRoot()) {
            Tree<Node> parent = tree.parent();
            List<Sector> sectors = tree.getData().getSectors();
            markSectorsAsAvailable(sectors);
            parent.remove(tree.getData());
        }
    }
    
    private String getAbsolutePath(Tree<Node> tree) {
       if (tree.isRoot()) {
            return "/";
       }
       else {
            String absolute = "";
            while (!tree.isRoot()) {
                absolute = "/" + tree.getData().getName() + absolute;
                tree = tree.parent();
            }
            return absolute;
       }
   }

    /**
     * Calculate the amount of oldSectors required to store a string in disk.
     *
     * @param content The string to store.
     * @return The required oldSectors.
     */
    private int requiredSectors(String content) {
        if (content.isEmpty()) {
            return 0;
        }
        else {
            return (int) Math.ceil((double) content.length() / (double) sectorSize);
        }
    }

    /**
     * Remove and return n oldSectors from the list of available oldSectors.
     *
     * @param count The number of oldSectors to remove.
     * @return The oldSectors.
     */
    private List<Sector> getSectors(int count) {
        List<Sector> sectors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            sectors.add(availableSectors.remove(0));
        }
        return sectors;
    }
    
    /**
     * Wipe and add a sector list to the available oldSectors.
     * 
     * @param sectors The oldSectors.
     */
    private void markSectorsAsAvailable(List<Sector> sectors) {
        writeZeros(sectors);
        availableSectors.addAll(sectors);
        Collections.sort(availableSectors);
    }
    
    private String readSector(Sector sector) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            Reader reader = new InputStreamReader(in);
            reader.skip(sector.getIndex() * sectorSize);
            String content = "";
            char c;
            
            for (int i = 0; i < sectorSize; i++) {
                c = (char) reader.read();
                if (c != Disk.ZERO) {
                    content += c;
                }
            }
            
            return content;
        }
    }
    
    private String readSectors(List<Sector> sectors) throws IOException {
        String content = "";
        for (Sector sector : sectors) {
            content += readSector(sector);
        }
        return content;
    }

    /**
     * Write a string to a file in the given oldSectors.
     *
     * @param sectors The oldSectors.
     * @param content The content to write.
     * @return true if no errors occurs.
     */
    private boolean writeToSectors(List<Sector> sectors, String content) {
        java.io.File temp = new java.io.File("disk.temp");
        try (InputStream in = new FileInputStream(file)) {
            try (OutputStream out = new FileOutputStream(temp)) {
                Writer writer = new OutputStreamWriter(out);
                Reader reader = new InputStreamReader(in);
                String chunk = "";
                
                int c;     /* The current character */
                int i = 0; /* The current sector */
                int j = 0; /* The current chunk */
                
                while ((c = reader.read()) != -1) {
                    chunk += (char) c;
                    if (chunk.length() == sectorSize) {
                        if (sectors.contains(new Sector(i))) {
                            chunk = StringUtils.substring(content, j * sectorSize, (j+1) * sectorSize);
                            chunk = StringUtils.fill(chunk, ZERO, sectorSize);
                            j++;
                        }
                        writer.write(chunk);
                        chunk = "";
                        i++;
                    }
                }
                writer.flush();
            }
        } 
        catch (IOException ex) {
            return false;
        }

        file.delete();
        temp.renameTo(file);
        return true;
    }

    /**
     * Delete the content of the entire disk.
     */
    private boolean writeZeros() {
        String text = StringUtils.repeat(Disk.ZERO, sectorSize * sectorAmount);
        try (OutputStream out = new FileOutputStream(file)) {
            Writer writer = new OutputStreamWriter(out);
            writer.write(text);
            writer.flush();
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    /**
     * Delete the content of a sector.
     *
     * @param sector The sector.
     */
    private boolean writeZeros(Sector sector) {
        List<Sector> list = new ArrayList<>();
        list.add(sector);
        String text = StringUtils.repeat(Disk.ZERO, sectorSize);
        return writeToSectors(list, text);
    }

    /**
     * Delete the content of a list of oldSectors.
     *
     * @param sectors The oldSectors.
     */
    private boolean writeZeros(List<Sector> sectors) {
        String text = StringUtils.repeat(Disk.ZERO, sectorSize * sectors.size());
        return writeToSectors(sectors, text);
    }
    
    /**
     * Copies a file with real path to a virtual path.
     * 
     * @param origin The real path.
     * @param destination The virtual path.
     * @throws java.io.IOException
     */
    public void copyRealToVirtual(String origin, String destination) throws Exception
    {
        java.io.File originFile = new java.io.File(origin);
        if(!exists(destination))
        {
            throw new FileNotFoundException("Directory '" + destination + "' doesn't exist");
        }
        Tree<Node> node = searchTree(destination);
        if(originFile.isFile())
        {
            byte[] bytes = Files.readAllBytes(originFile.toPath());
            String content = new String(bytes);
            writeToSectors(node.getData().getSectors(), content);
            return;
        }
        if(!node.getData().isDirectory())
        {
            throw new FileNotFoundException("Can't copy a directory into a file.");
        }
        String[] nodesList = originFile.getPath().split(java.io.File.pathSeparator);
        for(String nodeName : nodesList)
        {
            Node child = new Directory(nodeName);
            node.add(child);
        }     
    }
    
    
    /**
     * Copies a file with virtual path to a real path.
     * 
     * @param origin The virtual path.
     * @param destination The real path.
     * @throws java.io.IOException
     */
    public void copyVirtualToReal(String origin, String destination) throws IOException
    {
        java.io.File fileOut = new java.io.File(destination);
        Tree<Node> tree = searchTree(origin);
        if(tree == null)
        {
            throw new FileNotFoundException("File '" + origin + "' doesn't exist");
        }
        String content = null;
        if(tree.getData().isDirectory())
        {
            if(!fileOut.isDirectory())
            {
                throw new FileNotFoundException("Can't copy a directory into a file.");
            }
            List<Tree<Node>> nodesList = tree.children();
            if(nodesList.isEmpty())
            {
                java.io.File newDir = new java.io.File(destination+tree.getData().getName());
                if(!newDir.exists())
                {
                    newDir.mkdir();
                    return;
                }
                else
                {
                    throw new FileNotFoundException("Failed to create directory.");
                }
            }
            for(Tree treeNode : nodesList)
            {
                Node node = (Node)treeNode.getData();
                if(node.isDirectory())
                {
                    origin += "/" + node.getName() + "/";
                }
                else
                {
                    content = readSectors(node.getSectors());
                    createRealFile(destination, content);
                } 
            }
        }
        else
        {
            content = readSectors(tree.getData().getSectors());
            createRealFile(destination, content);
        }
    }
    
    /**
     * Creates a file (can be a directory also) in the real file system.
     * 
     * @param destination The destination path,
     * @param content The content of the file.
     */
    private void createRealFile(String destination, String content) throws IOException
    {
        java.io.File fileOut = new java.io.File(destination);
        if(!fileOut.exists())
        {
            throw new FileNotFoundException("File '" + destination + "' doesn't exist");
        }
        FileWriter fw = new FileWriter(fileOut.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
    }
    
    /**
     * Copies a file from a virtual srcTree to another virtual srcTree.
     * 
     * @param origin The first virtual path.
     * @param destination The destination virtual path.
     * @throws java.io.IOException
     */
    public void copyVirtualToVirtual(String origin, String destination) throws IOException, Exception
    {
        Tree<Node> originNode = searchTree(origin);
        Tree<Node> destinationNode = searchTree(destination);
        Node copiedNode = null;
        if(originNode == null)
        {
            throw new FileNotFoundException("File '" + origin + "' doesn't exist.");
        }
        if(destinationNode == null)
        {
            throw new FileNotFoundException("File '" + destination + "' doesn't exist.");
        }
        if(originNode.getData().isDirectory())
        {
            createDirectory(destination);
        }
        else
        {
            if(exists(destination))
            {
                Node node = searchNode(destination);
                writeToSectors(node.getSectors(), getFileContent(origin));
            }
            else
            {
                throw new FileNotFoundException("File '" + destination + "' doesn't exist.");
            }
        }
        copiedNode = searchNode(destination);
        if(copiedNode != null)
        {
            copiedNode.setCreationDate(originNode.getData().getCreationDate());
            copiedNode.setLastModificationDate(originNode.getData().getLastModificationDate());
        }      
    }
}
