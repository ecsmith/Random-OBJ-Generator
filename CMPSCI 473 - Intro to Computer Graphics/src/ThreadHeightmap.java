import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLCanvas;


public class ThreadHeightmap extends Thread
{

    GLCanvas obj;  

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
                Thread.sleep(100);
                this.obj.repaint();
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(ThreadHeightmap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


}
