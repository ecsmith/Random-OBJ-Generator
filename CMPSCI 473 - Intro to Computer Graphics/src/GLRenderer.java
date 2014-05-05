import java.io.IOException;
import java.io.InputStream;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class GLRenderer implements GLEventListener
{
    private RenderType renderType;                            
    private static final int MAP_SIZE = 1024;                 
    private static final int STEP_SIZE = 4;                     
    private byte[] heightMap = new byte[MAP_SIZE * MAP_SIZE];  
    private float scaleValue = 0.10f;                          
    private float HEIGHT_RATIO = 1.0f;                          
    private float skyMovCounter = 0.0f;                       
    private float[] lightAmbient = {1.0f, 1.0f, 1.0f, 0.5f};       
    private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 0.5f};        
    private float[] lightPosition = {-50.0f, 200.0f, -50.0f, 1.0f}; 
    public int terrainFilter = 3;                               
    public int textureFilter = 2;                               
    private int[] textures = new int[12];                     
    private int skyTexture;                                    
    private boolean cullingMode = false;                       
    private GLU glu = new GLU();                            
    private GLUquadric quadric;                               
    private GL _gl;                                            
    public String filename = null;                           
    public Camera camera = new Camera();                     

    public GLRenderer(String file)
    {
        filename = file;
        renderType = RenderType.TEXTURED;
    }

    public GLRenderer()
    {
        renderType = RenderType.TEXTURED;
    }

  
    public void init(GLAutoDrawable drawable)
    {

        GL gl = drawable.getGL();
        this._gl = gl;
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        gl.setSwapInterval(1);

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClearDepth(1.0f);                     
        gl.glEnable(GL.GL_DEPTH_TEST);             
        gl.glDepthFunc(GL.GL_LEQUAL);           
        gl.glShadeModel(GL.GL_SMOOTH);              
        

     
       
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        if(filename == null)
            loadFile("Heightfield.bmp");    
        else
            loadFile(filename);

        setTexture();  

        setLightning(gl);  

        quadric = glu.gluNewQuadric();                 
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH); 
        glu.gluQuadricTexture(quadric, true);  
        loadSkyTexture();  
    }

    public void loadFile(String filename)
    {

        try { loadRawFile(filename, heightMap); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        GL gl = drawable.getGL();        

        height = (height == 0) ? 1 : height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45, (float) width / height, 1, 10000);  
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

    }

    public void display(GLAutoDrawable drawable)
    {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        enableCullingMode(!cullingMode);

        gl.glLoadIdentity();
        Vector3 camPosition = camera.getCameraPosition();
        Vector3 camTarget = camera.getCameraTarget();
        Vector3 upVector = camera.getUpVector();
        glu.gluLookAt(camPosition.X, 
                camPosition.Y,
                camPosition.Z,
                camTarget.X,
                camTarget.Y,
                camTarget.Z,
                upVector.X,
                upVector.Y,
                upVector.Z);

        
        gl.glScalef(scaleValue, scaleValue * HEIGHT_RATIO, scaleValue);    // scaling
        setLightning(gl);  
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[terrainFilter * 3 + textureFilter]);

        gl.glEnable(GL.GL_LIGHT0); 
        gl.glEnable(GL.GL_LIGHTING);
        renderHeightMap(gl, heightMap);
        gl.glDisable(GL.GL_LIGHT0); 
        gl.glDisable(GL.GL_LIGHTING);

        drawSky();  
        
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) 
    {
    }


    private void renderHeightMap(GL gl, byte[] pHeightMap) 
    {
        if(renderType == RenderType.LINE)
            gl.glBegin(gl.GL_LINES);
        else
            gl.glBegin(gl.GL_QUADS);

        for (int X = 0; X < (MAP_SIZE - STEP_SIZE); X += STEP_SIZE)
            for (int Y = 0; Y < (MAP_SIZE - STEP_SIZE); Y += STEP_SIZE) 
            {
                int x = X;
                int y = height(pHeightMap, X, Y);
                int z = Y;
                if(renderType == RenderType.TEXTURED)
                    gl.glTexCoord2f((float)x / (float)MAP_SIZE, (float)z / (float)MAP_SIZE);  
                else
                    setVertexColor(gl, pHeightMap, x, z); 
                gl.glVertex3i(x, y, z);                          

                x = X;
                y = height(pHeightMap, X, Y + STEP_SIZE);
                z = Y + STEP_SIZE;
                if(renderType == RenderType.TEXTURED)
                    gl.glTexCoord2f((float)x / (float)MAP_SIZE, (float)(z + 1) / (float)MAP_SIZE);
                else
                    setVertexColor(gl, pHeightMap, x, z);
                gl.glVertex3i(x, y, z);
                x = X + STEP_SIZE;
                y = height(pHeightMap, X + STEP_SIZE, Y + STEP_SIZE);
                z = Y + STEP_SIZE;
                if(renderType == RenderType.TEXTURED)
                    gl.glTexCoord2f((float)(x + 1) / (float)MAP_SIZE, (float)(z + 1) / (float)MAP_SIZE);
                else
                    setVertexColor(gl, pHeightMap, x, z);
                gl.glVertex3i(x, y, z);

                x = X + STEP_SIZE;
                y = height(pHeightMap, X + STEP_SIZE, Y);
                z = Y;
                if(renderType == RenderType.TEXTURED)
                    gl.glTexCoord2f((float)(x + 1) / (float)MAP_SIZE, (float)z / (float)MAP_SIZE);
                else
                    setVertexColor(gl, pHeightMap, x, z);
                gl.glVertex3i(x, y, z);
            }
        
        gl.glEnd();
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // reset
    }
    
        private void setVertexColor(GL gl, byte[] pHeightMap, int x, int y) 
    {
        int height = height(pHeightMap, x, y);
        if(renderType != RenderType.MULTICOLOR)
        {            
            float fColor = 0.1f + (height / 256.0f);
            gl.glColor3f(0, 0, fColor);
        }
        else
        {
            if(height >= 250)
                gl.glColor3f(0.984313f, 0.3019607f, 0.3019607f);
            else if(height > 200)
                gl.glColor3f(1.0f, 0.5607843f, 0.243147f);
            else if(height > 150)
                gl.glColor3f(0.50196f, 1.0f, 0.50196f);
            else if(height > 100)
                gl.glColor3f(0.011764f, 0.55294117f, 0.1490196f);
            else if(height > 50)
                gl.glColor3f(0.011764f, 0.6862745f, 0.945098f);
            else
                gl.glColor3f(0.011764f, 0.270588f, 0.945098f);
        }
    }
    
    
    private int height(byte[] pHeightMap, int X, int Y) 
    { 
        int x = X % MAP_SIZE; 
        int y = Y % MAP_SIZE;
        return pHeightMap[x + (y * MAP_SIZE)] & 0xFF; 
    }

    private void loadRawFile(String strName, byte[] pHeightMap) throws IOException
    {
        InputStream input = ResourceGrabber.getResourceAsStream(strName);  
        readBuffer(input, pHeightMap); 
        input.close(); 

        for (int i = 0; i < pHeightMap.length; i++)
            pHeightMap[i] &= 0xFF; 
    }

    private static void readBuffer(InputStream in, byte[] buffer) throws IOException
    {
        int bytesRead = 0;
        int bytesToRead = buffer.length;
        while (bytesToRead > 0) {
            int read = in.read(buffer, bytesRead, bytesToRead);
            bytesRead += read;
            bytesToRead -= read;
        }
    }

    private void makeRGBTexture(GL gl,
            GLU glu,
            TextureReader.Texture img,
            int target,
            boolean mipmapped)
    {

        if (mipmapped)
            glu.gluBuild2DMipmaps(target, GL.GL_RGB8, img.getWidth(),
                    img.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
        else
            gl.glTexImage2D(target, 0, GL.GL_RGB, img.getWidth(),
                    img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());

    }

    private void setLightning(GL gl)
    {
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, this.lightAmbient, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, this.lightDiffuse, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, this.lightPosition, 0);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_LIGHTING);
    }

    private void loadSkyTexture()
    {
        String textureName = "sky.png";

        TextureReader.Texture texture;
        try { texture = TextureReader.readTexture(textureName); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e); }

        _gl.glBindTexture(GL.GL_TEXTURE_2D, skyTexture);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGB8, texture.getWidth(),
                    texture.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.getPixels());
    }


    private void drawSky()
    {
        Vector3 camPosition = camera.getCameraPosition();
        _gl.glBindTexture(GL.GL_TEXTURE_2D, skyTexture);
        enableCullingMode(false);

        _gl.glPushMatrix(); 
        _gl.glRotatef(-91.7f, 1.0f, 0.0f, 0.0f);
        _gl.glRotatef(skyMovCounter, 0.0f, 0.0f, 1.0f); 
        _gl.glTranslatef(camPosition.X, camPosition.Y, camPosition.Z - MAP_SIZE * scaleValue * 0.5f);
        double[] clipPlane1 = {0.0f, 0.0f, 1.0f, 0.5f};
        _gl.glClipPlane(GL.GL_CLIP_PLANE1, clipPlane1, 0); 
        _gl.glEnable(GL.GL_CLIP_PLANE1);
        glu.gluSphere(quadric, 5000, 50, 5);
        _gl.glDisable(GL.GL_CLIP_PLANE1);
        _gl.glPopMatrix();  
        _gl.glPushMatrix(); 
        _gl.glRotatef(-91.7f, 1.0f, 0.0f, 0.0f);
        _gl.glTranslatef(camPosition.X, camPosition.Y, camPosition.Z - MAP_SIZE * scaleValue * 0.5f);
        double[] clipPlane2 = {0.0f, 0.0f, -1.0f, 0.5f};
        _gl.glClipPlane(GL.GL_CLIP_PLANE2, clipPlane2, 0); 
        _gl.glEnable(GL.GL_CLIP_PLANE2);       
        glu.gluSphere(quadric, 5000, 50, 5);
        _gl.glDisable(GL.GL_CLIP_PLANE2);
        _gl.glPopMatrix();  

        skyMovCounter += 0.05f; 
    }

    /*
     * 0 - Acker Rock
     * 1 - Terrain 1
     * 2 - Terrain 2
     * 3 - Heighfield
     * 
     */
    private void setTexture()
    {
        textures = new int[12];

        _gl.glEnable(GL.GL_TEXTURE_2D);

        TextureReader.Texture texture = null;

        // ACKER ROCK //////////////////////////////////////////////////////////
        try { texture = TextureReader.readTexture("Acker_Rock.png"); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e); }
        
        _gl.glGenTextures(12, this.textures, 0);

        // Nearest Filtered Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, false);

        // Linear Filtered Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[1]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, false);

        // Mipmapped Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[2]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, true);
        
        texture = null;

        // TERRAIN I ///////////////////////////////////////////////////////////
        try { texture = TextureReader.readTexture("Terrain1.png"); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e); }

        // Nearest Filtered Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[3]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, false);

        // Linear Filtered Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[4]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, false);

        // Mipmapped Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[5]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, true);

        texture = null;

        // TERRAIN II //////////////////////////////////////////////////////////
        try { texture = TextureReader.readTexture("Terrain2.png"); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e); }

        // Nearest Filtered Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[6]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, false);

        // Linear Filtered Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[7]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, false);

        // Mipmapped Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[8]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, true);

        texture = null;

        // HEIGHTFIELD /////////////////////////////////////////////////////////

        try { texture = TextureReader.readTexture("Heightfield.png"); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e); }

        // Nearest Filtered Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[9]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, false);

        // Linear Filtered Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[10]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, false);

        // Mipmapped Texture
        _gl.glBindTexture(GL.GL_TEXTURE_2D, textures[11]);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        _gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D, true);

    }

    // mendapatkan tipe render
    public RenderType getRenderType()
    {
        return renderType;
    }

    // mengatur tipe render
    public void setRenderType(RenderType renderType)
    {
        this.renderType = renderType;
    }

    // medapatkan skala terrain
    public float getScaleValue()
    {
        return scaleValue;
    }

    public void setScaleValue(float scaleValue) 
    {
        if(scaleValue < 0.1f || scaleValue > 0.4f)
            return;

        this.scaleValue = scaleValue;
    }

    private void enableCullingMode(boolean value)
    {
        if(value)
        {
            _gl.glCullFace(_gl.GL_BACK);
            _gl.glEnable(_gl.GL_CULL_FACE);
            _gl.glFrontFace(_gl.GL_CCW);
        }
        else
            _gl.glDisable(_gl.GL_CULL_FACE);
    }

    public boolean isCullingMode()
    {
        return cullingMode;
    }

    public void setCullingMode(boolean cullingMode)
    {
        this.cullingMode = cullingMode;
    }
}

