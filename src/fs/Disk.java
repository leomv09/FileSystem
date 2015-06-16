package fs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NotDirectoryException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


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
        Tree<Node> tree = current;
        if (tree.isRoot()) {
            return "/";
        }
        else {
            String path = "";
            while (!tree.isRoot()) {
                path = "/" + tree.getData().getName() + path;
                tree = tree.parent();
            }
            return path;
        }
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
        
        List<Sector> sectors = node.getSectors();
        int required = requiredSectors(content);
        int shortage = required - sectors.size();
        
        if (shortage > availableSectors.size()) {
            throw new IOException("Insufficient disk space");
        }
        
        if (shortage >= 0) {
            sectors.addAll( getSectors(shortage) );
        }
        else {
            Sector sector;
            for (int i = shortage; i < 0; i++) {
                sector = sectors.remove(0);
                availableSectors.add(sector);
                writeZeros(sector);
            }
        }
        
        writeToSectors(sectors, content);
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
     * @return A string object containing the file properties.
     * @throws IOException 
     */
    public String getFileProperties(String path) throws IOException
    {
        Node node = searchNode(path);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d k:m");
        StringBuilder props = new StringBuilder();
        props.append("Name: ").append(node.getName()).append("\n");
        props.append("Extension: ").append(node.getExtension()).append("\n");
        props.append("Creation date: ").append(sdf.format(node.getCreationDate())).append("\n");
        props.append("Last modification date: ").append(sdf.format(node.getLastModificationDate())).append("\n");
        props.append("Size: ").append(getFileSize(path)).append("\n");
        return props.toString();
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
     * @param oldPath The path of the file.
     * @param newPath The new path of the file.
     * @throws java.io.IOException if an I/O error occurs moving the file.
     */
    public void moveFile(String oldPath, String newPath) throws IOException {
        Tree<Node> node = searchTree(oldPath);
        
        if (node == null) {
            throw new FileNotFoundException("File doesn't exist");
        }
        
        String fileName = FileUtils.getFileName(newPath);
        String directory = FileUtils.getDirectory(newPath);
        Tree<Node> newDir = searchTree(directory);
        
        if (newDir == null) {
            throw new FileNotFoundException("Directory '" + directory + "' doesn't exist");
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
     * @return The list of children of the directory. Null if the node is not a directory.
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
     * Get the files that satisfies certain regex in a directory.
     *
     * @param directory The path of the directory.
     * @param regex The regular expression.
     * @return The list of children of the directory.
     * @throws java.io.IOException if an I/O error occurs reading the directory.
     */
    public List<Node> getFiles(String directory, String regex) throws IOException 
    {
        Tree<Node> treeNode = searchTree(directory);
        List<Node>  nodesList = new ArrayList();
        if(treeNode == null)
        {
            throw new FileNotFoundException("Directory not found.");
        }
        if(treeNode.getData().isDirectory())
        {
            for(Tree<Node> tree : treeNode.children())
            {
                if (tree.getData().getName().matches(regex))
                {
                    nodesList.add(tree.getData());
                }  
            }
            
            return nodesList;
        }
        return nodesList;
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
        if (!tree.isRoot()) {
            Tree<Node> parent = tree.parent();
            List<Sector> sectors = tree.getData().getSectors();
            writeZeros(sectors);
            parent.remove(tree.getData());
        }
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
        }
        else {
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
        List<Sector> sectors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            sectors.add(availableSectors.remove(0));
        }
        return sectors;
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
     * Write a string to a file in the given sectors.
     *
     * @param sectors The sectors.
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
     * Delete the content of a list of sectors.
     *
     * @param sectors The sectors.
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
     */
    public void copyRealToVirtual(String origin, String destination)
    {
        java.io.File originFile = new java.io.File(origin);
        try 
        {
            if(originFile.isDirectory())
            {
                Tree<Node> parent = searchTree(destination);
                if(parent == null || !parent.getData().isDirectory())
                {
                    throw new FileNotFoundException("Directory '" + destination + "' doesn't exist");
                }
                String[] nodesList = originFile.getPath().split(java.io.File.pathSeparator);
                for(String nodeName : nodesList)
                {
                    Node child = new Directory(nodeName);
                    parent.add(child);
                }
            }
            RandomAccessFile raf = new RandomAccessFile(originFile, "r");
            byte[] bytes = null;
            raf.readFully(bytes);
            String content = new String(bytes);
            
            createFile(destination, content);
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) 
        {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) 
        {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    
    /**
     * Copies a file with virtual path to a real path.
     * 
     * @param origin The virtual path.
     * @param destination The real path.
     */
    public void copyVirtualToReal(String origin, String destination) throws IOException
    {
        Tree<Node> tree = searchTree(origin);
        if(tree == null)
        {
            throw new FileNotFoundException("File '" + origin + "' doesn't exist");
        }
        byte[] content = null;
        if(tree.getData().isDirectory())
        {
            List<Tree<Node>> nodesList = tree.children();
            if(nodesList.isEmpty())
            {
                createRealFile(destination, null);
                return;
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
                    content = readSectors(node.getSectors()).getBytes();
                    createRealFile(destination + node.getName(), content);
                } 
            }
        }
        else
        {
            content = readSectors(tree.getData().getSectors()).getBytes();
            createRealFile(destination + tree.getData().getName(), content);
        }
    }
    
    /**
     * Creates a file (can be a directory also) in the real file system.
     * 
     * @param destination The destination path,
     * @param content The content of the file.
     */
    private void createRealFile(String destination, byte[] content)
    {
        try 
        {
            java.io.File fileOut = new java.io.File(destination);
            if(!fileOut.exists())
            {
                fileOut.mkdir();
            }
            FileOutputStream out = new FileOutputStream(fileOut);
            out.write(content);
            out.close();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Disk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Copies a file from a virtual node to another virtual node.
     * 
     * @param origin The first virtual path.
     * @param destination The destination virtual path.
     */
    public void copyVirtualToVirtual(String origin, String destination) throws Exception
    {
        Tree<Node> originNode = searchTree(origin);
        Tree<Node> destinationNode = searchTree(destination);
        
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
            if(!destinationNode.getData().isDirectory())
            {
                throw new IOException("Can't copy a directory into a file.");
            }
            List<Tree<Node>> nodesList = originNode.children();
            if(nodesList != null)
            {
                for(Tree<Node> treeNode : nodesList)
                {
                    if(treeNode.getData().isDirectory())
                    {
                        String newPath = destination + "/" + originNode.getData().getName() + "/" + treeNode.getData().getName();
                        copyVirtualNode(newPath, originNode.getData());
                        if(treeNode.hasChildren())
                        {
                            copyVirtualToVirtual(origin + "/" + treeNode.getData().getName(), newPath);
                        }
                    }
                    else
                    {
                        copyVirtualNode(destination + "/" + originNode.getData().getName() + "/" + treeNode.getData().getName(), treeNode.getData());
                    }
                }
            }
            else
            {
                Node node = searchNode(destination);
                if(node == null)
                {
                    copyVirtualNode(destination + "/" + originNode.getData().getName(), originNode.getData());
                }
            }
        }
        else
        {        
            copyVirtualNode(destination + "/" + originNode.getData().getName(), originNode.getData());
        }        
    }
    
    /**
     * Copies a node into another node. If the node is a file also copies its content to disk.
     * 
     * @param path The destination directory.
     * @param originNode The node to copy.
     * @return The new copied node, null otherwise.
     * @throws Exception 
     */
    private Node copyVirtualNode(String path, Node originNode) throws Exception
    {
        Node insertedNode = null;
        if(originNode.isDirectory())
        {
            createDirectory(path);
        }
        else
        {
            createFile(path, readSectors(originNode.getSectors()));
        }
        insertedNode = searchNode(path);
        if(insertedNode != null)
        {
            insertedNode.setCreationDate(originNode.getCreationDate());
            insertedNode.setLastModificationDate(originNode.getLastModificationDate());
            return insertedNode;
        }
        return null;
    }

}
