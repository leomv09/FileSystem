package fs;

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
            return path.substring(0, index);
        }
    }

}
