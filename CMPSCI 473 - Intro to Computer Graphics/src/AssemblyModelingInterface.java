
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import javax.swing.JFrame;
import com.sun.opengl.util.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
//import framework.base.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.zip.*;
import javax.media.opengl.*;
//import com.jogamp.opengl.util.*;
//import static javax.media.opengl.GL2.*;

public class AssemblyModelingInterface extends JFrame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, ActionListener{

	// mouse control variables
	private final GLCanvas canvas;
	private int winW = 1024, winH = 1024;
	private int mouseX, mouseY;
	private int mouseButton;
	private boolean mouseClick = false;
	private boolean clickedOnShape = false;

	// gl shading/transformation variables
	private float tx = 0.0f, ty = 0.0f;
	private float scale = 1.0f;
	private float angleV = 0.0f;
	private float angleH = 0.0f;
	private boolean drawWireframe = false;
	private float lightPos[] = { -5.0f, 10.0f, 5.0f, 1.0f };
	
    private int PolyCount = 0;                                      //the model polygon count
	private ArrayList<int[]> fv = new ArrayList<int[]>();           //face vertex indices
    private ArrayList<int[]> ft = new ArrayList<int[]>();           //face texture indices
    private ArrayList<int[]> fn = new ArrayList<int[]>(); 
    private ArrayList<float[]> vData = new ArrayList<float[]>();    //list of vertex coordinates
    private ArrayList<float[]> vtData = new ArrayList<float[]>();   //list of texture coordinates
    private ArrayList<float[]> vnData = new ArrayList<float[]>(); 
    private int FaceFormat;                                         //format of the faces triangles or quads
    private int FaceMultiplier;                                     //number of possible coordinates per face
    private FloatBuffer modeldata;                                  //buffer which will contain data of the vertices
    private boolean initialize  = true;

	// a set of shapes
	private static final int Triangle = 0, Torus = 1, Sphere = 2, Icosahedron = 3, Teapot = 4, Cube = 5, BigCone = 6;
	private static final int NumShapes = 7;
	// initial shape is a triangle
	private int shape = Triangle;

	// gl context/variables
	private GL gl;
	private final GLU glu = new GLU();
	private final GLUT glut = new GLUT();

	public static void main(String args[]) {
		new AssemblyModelingInterface();
	}

	// constructor
	public AssemblyModelingInterface() {
		super("Intro Geometric Modeling Assignment 1");
		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		getContentPane().add(canvas);
		setSize(winW, winH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		canvas.requestFocus();
	}

	// gl display function
	public void display(GLAutoDrawable drawable) {

		// if mouse is clicked, we need to detect whether it's clicked on the shape
		if (mouseClick) {
			ByteBuffer pixel = ByteBuffer.allocateDirect(1);

			gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glDisable( GL.GL_LIGHTING );
			drawShape();
			gl.glReadPixels(mouseX, (winH-1-mouseY), 1, 1, GL.GL_RED, GL.GL_UNSIGNED_BYTE, pixel);

			if (pixel.get(0) == (byte)255) {
				// mouse clicked on the shape, set clickedOnShape to true
				clickedOnShape = true;
			}
			// set mouseClick to false to avoid detecting again
			mouseClick = false;
		}

		// shade the current shape
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_LIGHTING);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, drawWireframe ? GL.GL_LINE : GL.GL_FILL);
		gl.glColor3f(1.0f, 0.3f, 0.1f);
		drawShape();
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		// TODO Auto-generated method stub
	}

	// draw the current shape
	public void drawShape() {
		gl.glLoadIdentity();
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPos, 0);
		gl.glTranslatef(tx, ty, -10.0f);
		gl.glScalef(scale, scale, scale);
		gl.glRotatef(angleV, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(angleH, 0.0f, 1.0f, 0.0f);

		switch(shape) {
		case Triangle:
			gl.glBegin(GL.GL_TRIANGLES);
			gl.glVertex3f(0.0f, 1.0f, 0.0f);
			gl.glVertex3f(-1.0f, -0.5f, 0.0f);
			gl.glVertex3f(1.0f, -0.5f, 0.0f);
			gl.glEnd();
			break;
		case Torus:
			glut.glutSolidTorus(0.5f, 1.0f, 32, 32);
			break;
		case Sphere:
			glut.glutSolidSphere(1.0f, 32, 32);
			break;
		case Icosahedron:
			glut.glutSolidIcosahedron();
			break;
		case Teapot:
			gl.glFrontFace(GL.GL_CW);
			glut.glutSolidTeapot(1.0f);
			gl.glFrontFace(GL.GL_CCW);
			break;
		case Cube:
			glut.glutSolidCube(1.0f);
			break;
		case BigCone:
			glut.glutSolidCone(2.0f, 1.0f, 32, 32);
			break;
		}

	}
	private float[] ProcessData(String read) {
        final String s[] = read.split("\\s+");
        return (ProcessFloatData(s)); //returns an array of processed float data
    }
	
	private float[] ProcessFloatData(String sdata[]) {
        float data[] = new float[sdata.length - 1];
        for (int loop = 0; loop < data.length; loop++) {
            data[loop] = Float.parseFloat(sdata[loop + 1]);
        }
        return data; //return an array of floats
    }
	
	private void ProcessFaceData(String fread) {
        PolyCount++;
        String s[] = fread.split("\\s+");
        if (fread.contains("//")) { //pattern is present if obj has only v and vn in face data
            for (int loop = 1; loop < s.length; loop++) {
                s[loop] = s[loop].replaceAll("//", "/0/"); //insert a zero for missing vt data
            }
        }
        ProcessFaceIntData(s); //pass in face data
    }
	private void ProcessFaceIntData(String sdata[]) {
        int vdata[] = new int[sdata.length - 1];
        int vtdata[] = new int[sdata.length - 1];
        int vndata[] = new int[sdata.length - 1];
        for (int loop = 1; loop < sdata.length; loop++) {
            String s = sdata[loop];
            String[] temp = s.split("/");
            vdata[loop - 1] = Integer.valueOf(temp[0]);         //always add vertex indices
            if (temp.length > 1) {                              //we have v and vt data
                vtdata[loop - 1] = Integer.valueOf(temp[1]);    //add in vt indices
            } else {
                vtdata[loop - 1] = 0;                           //if no vt data is present fill in zeros
            }
            if (temp.length > 2) {                              //we have v, vt, and vn data
                vndata[loop - 1] = Integer.valueOf(temp[2]);    //add in vn indices
            } else {
                vndata[loop - 1] = 0;                           //if no vn data is present fill in zeros
            }
        }
        fv.add(vdata);
        ft.add(vtdata);
        fn.add(vndata);
    }
	private void SetFaceRenderType() {
        final int temp[] = (int[]) fv.get(0);
        if (temp.length == 3) {
            FaceFormat = gl.GL_TRIANGLES; 	//the faces come in sets of 3 so we have triangular faces
            FaceMultiplier = 3;
        } else if (temp.length == 4) {
            FaceFormat = gl.GL_QUADS; 		//the faces come in sets of 4 so we have quadrilateral faces
            FaceMultiplier = 4;
        } else {
            FaceFormat = gl.GL_POLYGON; 	//fall back to render as free form polygons
        }
    }
	
	private void loadOBJModel(String modelPath) {
        try {
        InputStream inStream = new FileInputStream(modelPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {         //read any descriptor data in the file
                    // ignore it...
                } else if (line.equals("")) {
                    // Ignore whitespace data
                } else if (line.startsWith("v ")) {  //read in vertex data
                    vData.add(ProcessData(line));
                } else if (line.startsWith("vt ")) { //read texture coordinates
                    vtData.add(ProcessData(line));
                } else if (line.startsWith("vn ")) { //read normal coordinates
                    vnData.add(ProcessData(line));
                } else if (line.startsWith("f ")) {  //read face data
                    ProcessFaceData(line);
                }
            }
            br.close();
        }
        catch(IOException ex){
            System.out.println (ex.toString());
            System.out.println("Could not find file");
        }
    }
	private void ConstructInterleavedArray(GL inGL) {
        final int tv[] = (int[]) fv.get(0);
        final int tt[] = (int[]) ft.get(0);
        final int tn[] = (int[]) fn.get(0);
        //if a value of zero is found that it tells us we don't have that type of data
        if ((tv[0] != 0) && (tt[0] != 0) && (tn[0] != 0)) {
            ConstructTNV(); //we have vertex, 2D texture, and normal Data
            inGL.glInterleavedArrays(gl.GL_T2F_N3F_V3F, 0, modeldata);
        } else if ((tv[0] != 0) && (tt[0] != 0) && (tn[0] == 0)) {
            ConstructTV(); //we have just vertex and 2D texture Data
            inGL.glInterleavedArrays(gl.GL_T2F_V3F, 0, modeldata);
        } else if ((tv[0] != 0) && (tt[0] == 0) && (tn[0] != 0)) {
            ConstructNV(); //we have just vertex and normal Data
            inGL.glInterleavedArrays(gl.GL_N3F_V3F, 0, modeldata);
        } else if ((tv[0] != 0) && (tt[0] == 0) && (tn[0] == 0)) {
            ConstructV();
            inGL.glInterleavedArrays(gl.GL_V3F, 0, modeldata);
        }
    }
	private void ConstructTNV() {
        int[] v, t, n;
        float tcoords[] = new float[2]; //only T2F is supported in interLeavedArrays!!
        float coords[] = new float[3];
        int fbSize = PolyCount * (FaceMultiplier * 8); //3v per poly, 2vt per poly, 3vn per poly
        modeldata = FloatBuffer.allocate(2 * fbSize);
        modeldata.position(0);
        for (int oloop = 0; oloop < fv.size(); oloop++) {
            v = (int[]) (fv.get(oloop));
            t = (int[]) (ft.get(oloop));
            n = (int[]) (fn.get(oloop));
            for (int iloop = 0; iloop < v.length; iloop++) {
                //fill in the texture coordinate data
                for (int tloop = 0; tloop < tcoords.length; tloop++)
                    //only T2F is supported in interleavedarrays!!
                    tcoords[tloop] = ((float[]) vtData.get(t[iloop] - 1))[tloop];
                modeldata.put(tcoords);
                //fill in the normal coordinate data
                for (int vnloop = 0; vnloop < coords.length; vnloop++)
                    coords[vnloop] = ((float[]) vnData.get(n[iloop] - 1))[vnloop];
                modeldata.put(coords);
                //fill in the vertex coordinate data
                for (int vloop = 0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[]) vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }

    private void ConstructTV() {
        int[] v, t;
        float tcoords[] = new float[2]; //only T2F is supported in interLeavedArrays!!
        float coords[] = new float[3];
        int fbSize = PolyCount * (FaceMultiplier * 5); //3v per poly, 2vt per poly
        modeldata = FloatBuffer.allocate(fbSize);
        modeldata.position(0);
        for (int oloop = 0; oloop < fv.size(); oloop++) {
            v = (int[]) (fv.get(oloop));
            t = (int[]) (ft.get(oloop));
            for (int iloop = 0; iloop < v.length; iloop++) {
                //fill in the texture coordinate data
                for (int tloop = 0; tloop < tcoords.length; tloop++)
                    //only T2F is supported in interleavedarrays!!
                    tcoords[tloop] = ((float[]) vtData.get(t[iloop] - 1))[tloop];
                modeldata.put(tcoords);
                //fill in the vertex coordinate data
                for (int vloop = 0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[]) vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }

    private void ConstructNV() {
        int[] v, n;
        float coords[] = new float[3];
        int fbSize = PolyCount * (FaceMultiplier * 6); //3v per poly, 3vn per poly
        modeldata = FloatBuffer.allocate(fbSize);
        modeldata.position(0);
        for (int oloop = 0; oloop < fv.size(); oloop++) {
            v = (int[]) (fv.get(oloop));
            n = (int[]) (fn.get(oloop));
            for (int iloop = 0; iloop < v.length; iloop++) {
                //fill in the normal coordinate data
                for (int vnloop = 0; vnloop < coords.length; vnloop++)
                    coords[vnloop] = ((float[]) vnData.get(n[iloop] - 1))[vnloop];
                modeldata.put(coords);
                //fill in the vertex coordinate data
                for (int vloop = 0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[]) vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }

    private void ConstructV() {
        int[] v;
        float coords[] = new float[3];
        int fbSize = PolyCount * (FaceMultiplier * 3); //3v per poly
        modeldata = FloatBuffer.allocate(fbSize);
        modeldata.position(0);
        for (int oloop = 0; oloop < fv.size(); oloop++) {
            v = (int[]) (fv.get(oloop));
            for (int iloop = 0; iloop < v.length; iloop++) {
                //fill in the vertex coordinate data
                for (int vloop = 0; vloop < coords.length; vloop++)
                    coords[vloop] = ((float[]) vData.get(v[iloop] - 1))[vloop];
                modeldata.put(coords);
            }
        }
        modeldata.position(0);
    }

    public void drawModel(GL xGL) {
        if (initialize) {
            ConstructInterleavedArray(xGL);
            cleanup();
            initialize = false;
        }
        xGL.glDrawArrays(FaceFormat, 0, PolyCount * FaceMultiplier);
    }
    
    private void cleanup() {
        vData.clear();
        vtData.clear();
        vnData.clear();
        fv.clear();
        ft.clear();
        fn.clear();
        modeldata.clear();
    }
	
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		gl = drawable.getGL();
		gl.setSwapInterval(1);

		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
		gl.glEnable( GL.GL_COLOR_MATERIAL ) ;
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_NORMALIZE);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glCullFace(GL.GL_BACK);
		gl.glEnable(GL.GL_CULL_FACE);

		// set clear color: this determines the background color (which is dark gray)
		gl.glClearColor(.3f, .3f, .3f, 1f);
		gl.glClearDepth(1.0f);
	}

	// reshape callback function: called when the size of the window changes
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		winW = width;
		winH = height;

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(30.0f, (float) width / (float) height, 0.01f, 100.0f);
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_MODELVIEW);		
	}

	// mouse pressed even callback function
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseClick = true;
		mouseX = e.getX();
		mouseY = e.getY();
		mouseButton = e.getButton();
		canvas.display();
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		clickedOnShape = false;
		canvas.display();
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if (!clickedOnShape)	return;

		int x = e.getX();
		int y = e.getY();
		if (mouseButton == MouseEvent.BUTTON3) {
			// right button scales
			scale += (y - mouseY) * 0.01f;
		}
		else if (mouseButton == MouseEvent.BUTTON2) {
			// middle button translates
			tx += (x - mouseX) * 0.01f;
			ty -= (y - mouseY) * 0.01f;
		}
		else if (mouseButton == MouseEvent.BUTTON1) {
			// left button rotates
			// update: rotates object horizontally
			angleV += (y - mouseY);
			angleH += (x - mouseX);
		}
		mouseX = x;
		mouseY = y;
		canvas.display();

	}
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch(e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			System.exit(0);
			break;
		case KeyEvent.VK_O:
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "OBJ Files","obj");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(null);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String path = chooser.getSelectedFile().getPath();
		            System.out.println(path);
		            loadOBJModel(path);
		            SetFaceRenderType();
		            drawModel(gl);
		    }
		 
		case KeyEvent.VK_W:
			drawWireframe = !drawWireframe;
			break;
		case KeyEvent.VK_SPACE:
			shape = (shape + 1) % NumShapes;
			break;
		}
		canvas.display();		
	}

	// these event functions are not used for this assignment
	// but may be useful in the future
	public void keyTyped(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void actionPerformed(ActionEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) {	}

}


