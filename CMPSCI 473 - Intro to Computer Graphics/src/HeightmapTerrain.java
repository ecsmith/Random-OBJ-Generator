
import com.sun.opengl.util.Animator;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.WindowConstants;


public class HeightmapTerrain extends JFrame implements KeyListener, MouseMotionListener {

    public static File file;
	private Animator animator;          
    private GLRenderer renderer;        
    private static byte[] input_bytes = null;
    private static Robot robot;
    private static int canvasheight, canvaswidth;

    public ThreadHeightmap repainter;   

    
    public HeightmapTerrain()
    {
        initComponents();
        
        renderer = new GLRenderer();
        heightmapCanvas.addGLEventListener(renderer);
        animator = new Animator(heightmapCanvas);
        
        try {
			robot = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
        
        heightmapCanvas.requestFocus();

        this.addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });

        this.repainter = new ThreadHeightmap(heightmapCanvas);
        this.repainter.setPriority(Thread.MIN_PRIORITY);
        this.repainter.start();

        renderer.camera.moveForward(-50.0f);
        renderer.camera.moveUpward(-4.0f);
    }

    @Override
    public void setVisible(boolean show){
        if(!show)
            animator.stop();
        super.setVisible(show);
        if(!show)
            animator.start();
    }


    private void resetTerrain(String filename)
    {

        renderer = new GLRenderer(filename);
        heightmapCanvas.addGLEventListener(renderer);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        heightmapCanvas = new GLCanvas();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Heightmap Landscape");
        addKeyListener(this);

        heightmapCanvas.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent evt) {
                heightmapCanvasMouseWheelMoved(evt);
            }
        });
        heightmapCanvas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                heightmapCanvasMouseClicked(evt);
            }
        });
        heightmapCanvas.addMouseMotionListener(this);
        heightmapCanvas.addKeyListener(this);


        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(heightmapCanvas, GroupLayout.DEFAULT_SIZE, 837, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                    .addComponent(heightmapCanvas, GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE))            
        );

        pack();
        canvasheight = heightmapCanvas.getHeight();
        canvaswidth = heightmapCanvas.getWidth();

    }

    private void formMouseDragged(MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void formKeyPressed(KeyEvent evt) {
        // TODO add your handling code here:
    }

    private void heightmapCanvasMouseWheelMoved(MouseWheelEvent evt) {

    }

    private void heightmapCanvasMouseClicked(MouseEvent evt) {
        RenderType renderType = renderer.getRenderType();

        if(renderType == RenderType.LINE)
            renderer.setRenderType(RenderType.SOLID);
        else if(renderType == RenderType.SOLID)
            renderer.setRenderType(RenderType.MULTICOLOR);
        else if(renderType == RenderType.MULTICOLOR)
            renderer.setRenderType(RenderType.LINE);
    }

   
    public static void main(String args[]) {
    	try {
			SwingUtilities.invokeAndWait(new Runnable() {

			    @Override
			    public void run() {
			    	file = new File(System.getProperty("user.dir"));
			        JFileChooser fc = new JFileChooser("");
			        fc.setCurrentDirectory(file);
			        int returnVal = fc.showOpenDialog(null);

			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            file = fc.getSelectedFile();

			            //This is where a real application would open the file.
			            System.out.println("Opening: " + file.getName() + ".\n");
			            System.out.println("Size in Bytes: " + file.getFreeSpace() + ".\n");
			            try{
			                RandomAccessFile rand_file = new RandomAccessFile(file.getAbsoluteFile(), "rw");
			                System.out.println("Size in Bytes: " + rand_file.length() + ".\n");
			                rand_file.seek(0);
			                input_bytes = new byte[(int)rand_file.length()];
			                rand_file.readFully(input_bytes);
			                System.out.println("Output: " + input_bytes.toString() + ".\n");

			                
			                rand_file.close(); 
			                
			               } catch (IOException e) {//If the file is not found
			       			e.printStackTrace();  //Print trace the error
			       		}

			        } else {
			            System.out.println("Open command cancelled by user.\n");
			        }
			    
			    }
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                // switch to system l&f for native font rendering etc.
                try{
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }catch(Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.INFO, "can not enable system look and feel", ex);
                }
                
                HeightmapTerrain frame = new HeightmapTerrain();
                
                //height = frame.getHeight();
                //width = frame.getWidth();
                frame.setVisible(true);
                hideCursor(frame);
                curX = canvaswidth/2;
            	curY = canvasheight/2;
            	//robot.mouseMove(width/2, height/2);
            }
        });
    }
    private GLCanvas heightmapCanvas;

    private static void hideCursor(JFrame frame) {
    	//Create an empty byte array  
    	byte[]imageByte=new byte[0];  
    	  
    	Cursor emptyCursor;  
    	Point myPoint=new Point(0,0);  
    	  
    	//Create image for cursor using empty array  
    	Image cursorImage=Toolkit.getDefaultToolkit().createImage(imageByte); 
    	emptyCursor=Toolkit.getDefaultToolkit().createCustomCursor(cursorImage,myPoint,"cursor");
    	frame.setCursor(emptyCursor);
    }

	@Override
	public void keyPressed(KeyEvent evt) {
        int button = evt.getKeyCode();
        float scale = renderer.getScaleValue();

        switch (evt.getKeyCode()) {
        case (KeyEvent.VK_UP):               
            renderer.camera.rotateX(2.0f);
        	break;
        case (KeyEvent.VK_DOWN):         
            renderer.camera.rotateX(-2.0f);
        	break;
        case (KeyEvent.VK_RIGHT):      
            renderer.camera.rotateY(-2.0f);
        	break;
        case (KeyEvent.VK_LEFT):       
            renderer.camera.rotateY(2.0f);
        	break;
        case (KeyEvent.VK_COMMA):
        case (KeyEvent.VK_S):         
            renderer.camera.moveForward(-1.0f);
        	break;
        case (KeyEvent.VK_SEMICOLON):
        case (KeyEvent.VK_A):
        	renderer.camera.strafeRight(-1.0f);
        	break;
        case (KeyEvent.VK_PERIOD):
        case (KeyEvent.VK_W): 
            renderer.camera.moveForward(2.0f);
        	break;
        case (KeyEvent.VK_QUOTE):
        case (KeyEvent.VK_D): 
            renderer.camera.strafeRight(1.0f);
        	break;
        case (KeyEvent.VK_Q):           
            renderer.camera.moveUpward(1.0f);
        	break;
        case (KeyEvent.VK_E):           
            renderer.camera.moveUpward(-1.0f);
        	break;
        case (KeyEvent.VK_Z):        
            renderer.camera.rotateZ(-2.0f);
         	break;
        case (KeyEvent.VK_X):          
            renderer.camera.rotateZ(2.0f);
        	break;
        case (KeyEvent.VK_PAGE_UP):     
            renderer.setScaleValue(scale + 0.01f);
        	break;
        case (KeyEvent.VK_PAGE_DOWN):   
            renderer.setScaleValue(scale - 0.01f);
        	break;
        case (KeyEvent.VK_1):               
            renderer.setRenderType(RenderType.LINE);
        	break;
        case (KeyEvent.VK_2):
            renderer.setRenderType(RenderType.TEXTURED);
        	break;
        case (KeyEvent.VK_ESCAPE):
            System.exit(0);
        	break;
        case (KeyEvent.VK_R):
        	try {Runtime.getRuntime().exec("java HeightmapTerrain");}
			catch (IOException e) { e.printStackTrace() ;}
        	System.exit(0);
        	break;
        }
        
		
	}


	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	static int curX;
	static int curY;

	private boolean ignoreRobotMove = false;
	
	public void mouseMoved(MouseEvent e) {
		if (!ignoreRobotMove) {
			
			int dx = MouseInfo.getPointerInfo().getLocation().x - (canvaswidth/2);
			int dy = MouseInfo.getPointerInfo().getLocation().y - (canvasheight/2);
			if(dx != 0) {     
				float yrot = -(float)dx/100;
	            renderer.camera.rotateY(yrot);
			}
			
	        if(dy != 0) {  
	        	float xrot = -(float)dy/100;
	            renderer.camera.rotateX(xrot);
	        }
	        
	        robot.mouseMove(canvaswidth/2, canvasheight/2);
	        ignoreRobotMove = true;
		} else {
			ignoreRobotMove = false;
		}
	}
}
