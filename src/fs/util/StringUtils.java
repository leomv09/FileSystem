package fs.util;

/**
 *
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 */
public class StringUtils {

    /**
     * Index safe substring operation.
     * 
     * @param str The string.
     * @param beginIndex The begin index.
     * @param endIndex The end index.
     * @return The substring.
     */
    public static String substring(String str, int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            beginIndex = 0;
        }
        if (endIndex > str.length()) {
            endIndex = str.length();
        }
        return str.substring(beginIndex, endIndex);
    }
    
    /**
     * Fill a string with a padding character until a specific string length is reached.
     * Only pad character if string length is less than size.
     * 
     * @param text The string
     * @param padding The padding
     * @param size The desired length
     * @return The filled string.
     */
    public static String fill(String text, char padding, int size) {
        for (int i = text.length(); i < size; i++) {
            text += padding;
        }
        return text;
    }

    /**
     * Repeat a text n times.
     *
     * @param text The text to repeat.
     * @param n The number of times text will be repeated.
     * @return The string.
     */
    public static String repeat(String text, int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result += text;
        }
        return result;
    }
    
    /**
     * Repeat a character n times.
     *
     * @param text The text to repeat.
     * @param n The number of times text will be repeated.
     * @return The string.
     */
    public static String repeat(char text, int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result += text;
        }
        return result;
    }

}
