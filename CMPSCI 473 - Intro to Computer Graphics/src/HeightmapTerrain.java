
import com.sun.opengl.util.Animator;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import javax.media.opengl.GLCanvas;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;


public class HeightmapTerrain extends JFrame implements KeyListener, MouseMotionListener {

	public static File file;
	private Animator animator;
	private GLRenderer renderer;
	private static byte[] input_bytes = null;
	private static Robot robot;
	private static int canvasheight, canvaswidth;
	private boolean drawTex = true;
	private boolean renderAnim = true;
	private boolean useNormals = true;

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

					@Override
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
		JOptionPane.showMessageDialog(this, "Use WASD to move back, forth, and sideways.\n" +
											"Use the mouse or arrow keys to change camera direction.\n" +
											"Q and E move up and down.\n" +
											"Z and X rotate around the Z axis.\n" +
											"Press 1 to toggle wireframe/textured.\n" +
											"Press 2 to toggle animation, and 3 to toggle surface normals.\n" +
											"Press 4 to generate a new random terrain.");

	}

	@Override
	public void setVisible(boolean show){
		if(!show) {
			animator.stop();
		}
		super.setVisible(show);
		if(!show) {
			animator.start();
		}
	}


	@SuppressWarnings("unchecked")
	private void initComponents() {

		heightmapCanvas = new GLCanvas();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Heightmap Landscape");
		addKeyListener(this);
		heightmapCanvas.addMouseMotionListener(this);
		heightmapCanvas.addKeyListener(this);


		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
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

	public static void main(String args[]) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					file = new File(System.getProperty("user.dir"));
					JFileChooser fc = new JFileChooser("");
					fc.setCurrentDirectory(file);
					fc.setAcceptAllFileFilterUsed(false);
					fc.addChoosableFileFilter(new imgFilter());
					int returnVal = fc.showOpenDialog(null);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						file = fc.getSelectedFile();

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
							System.exit(0);
						}

					} else {
						System.out.println("Open command cancelled by user.\n");
						System.exit(0);
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
			@Override
			public void run() {

				HeightmapTerrain frame = new HeightmapTerrain();

				frame.setVisible(true);
				hideCursor(frame);
				curX = canvaswidth/2;
				curY = canvasheight/2;
			}
		});
	}
	private GLCanvas heightmapCanvas;

	private static void hideCursor(JFrame frame) {
		byte[]imageByte=new byte[0];

		Cursor emptyCursor;
		Point myPoint=new Point(0,0);

		Image cursorImage=Toolkit.getDefaultToolkit().createImage(imageByte);
		emptyCursor=Toolkit.getDefaultToolkit().createCustomCursor(cursorImage,myPoint,"cursor");
		frame.setCursor(emptyCursor);
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		evt.getKeyCode();
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
			renderer.camera.moveForward(1.0f);
		break;
		case (KeyEvent.VK_SEMICOLON):
		case (KeyEvent.VK_A):
			renderer.camera.strafeRight(-1.0f);
		break;
		case (KeyEvent.VK_PERIOD):
		case (KeyEvent.VK_W):
			renderer.camera.moveForward(-2.0f);
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
			if (drawTex) {
				renderer.setRenderType(RenderType.LINE);	
			} else {
				renderer.setRenderType(RenderType.TEXTURED);
			}
			drawTex = !drawTex;	
		break;
		case (KeyEvent.VK_2):
			if (renderAnim) {
				renderer.setAnimationType(AnimationType.NONE);
			} else {
				renderer.setAnimationType(AnimationType.ANIMATED);
			}
			renderAnim = !renderAnim;	
		break;
		case (KeyEvent.VK_3):
			if (useNormals) {
				renderer.setNormalType(NormalType.NONE);	
			} else {
				renderer.setNormalType(NormalType.NORMALS);
			}
			useNormals = !useNormals;
		break;
		case (KeyEvent.VK_4):
			renderer.newHeightmap();
		break;
		case (KeyEvent.VK_ESCAPE):
			System.exit(0);
		break;
		}


	}

	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
	@Override
	public void mouseDragged(MouseEvent arg0) {}

	static int curX;
	static int curY;

	private boolean ignoreRobotMove = false;

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!ignoreRobotMove) {

			int dx = MouseInfo.getPointerInfo().getLocation().x - (this.getX() + canvaswidth/2);
			int dy = MouseInfo.getPointerInfo().getLocation().y - (this.getY() + canvasheight/2);
			if(dx != 0) {
				float yrot = -(float)dx/100;
				renderer.camera.rotateY(yrot);
			}

			if(dy != 0) {
				float xrot = -(float)dy/100;
				renderer.camera.rotateX(xrot);
			}

			robot.mouseMove(this.getX() + canvaswidth/2, this.getY() + canvasheight/2);
			ignoreRobotMove = true;
		} else {
			ignoreRobotMove = false;
		}
	}
		
		public static class imgFilter extends FileFilter {
			
		public boolean accept(File f) {
	        if (f.isDirectory()) {
	            return true;
	        }
	 
	        String ename = f.getName();
	        int i = ename.lastIndexOf(".");
	        if (i > 0 && i < ename.length() - 1)
	        	ename = ename.substring(i+1).toLowerCase();
	        
	        if (ename != null) {
	            if (ename.equals("gif") ||
	                ename.equals("jpeg") ||
	                ename.equals("jpg") ||
	                ename.equals("png")) {
	                    return true;
	            } else {
	                return false;
	            }
	        }
	 
	        return false;
		}

		public String getDescription() {
			return "Only images";
		}
		
	}
}
