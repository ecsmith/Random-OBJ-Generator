import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLCanvas;


/**
 * Kelas untuk me-refresh otomatis GLJPanel
 */
public class ThreadHeightmap extends Thread
{

    GLCanvas obj;   // objek GLJPanel

    public ThreadHeightmap(GLCanvas canvas)
    {
        this.obj = canvas;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                // refresh rate -60 per detik
                Thread.sleep(17);
                this.obj.repaint();
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(ThreadHeightmap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


}
