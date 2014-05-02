
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

// kelas yang digunakan untuk pembacaan transparan
// pada berkas  dari current directori atau dari classpath
public class ResourceGrabber
{
    public static URL getResource(final String filename) throws IOException 
    {
        // berusaha untuk load resource dari file jar
        URL url = ClassLoader.getSystemResource(filename);
        // Jika tidak didapatkan dari file jar, maka load dari disk
        if (url == null)
            return new URL("file", "localhost", filename);
        else
            return url;
    }

    public static InputStream getResourceAsStream(final String filename) throws IOException
    {
        // berusaha untuk load resource dari file jar
        InputStream stream = ClassLoader.getSystemResourceAsStream(filename);
        // Jika tidak didapatkan dari file jar, maka load dari disk
        if (stream == null)
            return new FileInputStream(filename);
        else
            return stream;
    }
}
