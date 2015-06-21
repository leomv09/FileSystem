package fs.util;

import fs.App;
import fs.Disk;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class FileUtils {

    /**
     * Check if a file or directory name is valid.
     *
     * @param name The name.
     * @return true if the name is valid.
     */
    public static boolean isValidName(String name) {
        return !name.isEmpty() && !name.contains("/") && !name.contains("\\");
    }

    /**
     * Check if a path is valid.
     *
     * @param path The path.
     * @return true if the path is valid.
     */
    public static boolean isValidPath(String path) {
        String[] array = path.split("/");
        for (String curr : array) {
            if (!isValidName(curr)) {
                return false;
            }
        }
        return true;
    }

    public static String getFileName(String path) {
        String[] array = path.split("/");
        return array[array.length - 1];
    }

    public static String getDirectory(String path) {
        int index = path.lastIndexOf("/");
        if (index == -1) {
            return "";
        } else {
            return path.substring(0, index + 1);
        }
    }
    
    public static String appendPath(String parent, String child) {
        if (!parent.endsWith("/")) {
            parent += "/";
        }
        return parent + child;
    }

    public static void delete(java.io.File f) {
        if (f.isDirectory()) {
            for (java.io.File c : f.listFiles()) {
                delete(c);
            }
        }
        f.delete();
    }
    
    public static boolean promptForVirtualOverride(String path) {
        App app = App.getInstance();
        Disk disk = app.getDisk();
        
        try {
            System.out.print("File \"" + disk.getAbsolutePath(path)  + "\" already exists. Do you want to override it? [y/n] ");
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            String line = input.readLine();
            if (!line.equalsIgnoreCase("y")) {
                return false;
            }
            disk.delete(path);
            return true;
        } 
        catch (IOException ex) {
            return false;
        }
    }
    
    public static boolean promptForRealOverride(String path) {
        java.io.File file = new java.io.File(path);
        
        try {
            System.out.print("File \"" + file.getAbsolutePath() + "\" already exists. Do you want to override it? [y/n] ");
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            String line = input.readLine();
            if (!line.equalsIgnoreCase("y")) {
                return false;
            }
            FileUtils.delete(file);
            return true;
        } 
        catch (IOException ex) {
            return false;
        }
    }
    
}
