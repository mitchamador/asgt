package gbas.gtbch.util;

/**
 * crop string to N characters
 */
public class CropString {
    private final static int DEFAULT_CROP_LENGTH = 100;

    /**
     * crop string to default crop length
     * @param s
     * @return
     */
    public static String getCroppedString(String s) {
        return getCroppedString(s, DEFAULT_CROP_LENGTH);
    }

    /**
     * crop string to length characters
     * @param s
     * @param length
     * @return
     */
    public static String getCroppedString(String s, int length) {
        if (s != null) {
            s = s.replaceAll("(\n|\\s+)", " ");
            if (s.length() > length - 3) {
                s = s.substring(0, length - 3) + "...";
            }
        }
        return s;
    }

}
