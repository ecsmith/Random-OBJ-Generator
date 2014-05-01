import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.vecmath.*;

import com.sun.opengl.util.GLUT;
public class Camera extends JFrame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	
	private final GLCanvas canvas;
	private int lastx, lasty;
	private int winW, winH;
	private int speed;
	private int fwd, right;
	private float xangle, yangle;
	Vector3f currentPos;
	
	private GL gl;
	private final GLU glu = new GLU();
	private final GLUT glut = new GLUT();
	
	public Camera() {
		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		getContentPane().add(canvas);
		winW = 860;
		winH = 320;
		setSize(winW, winH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		canvas.requestFocus();
		lastx = winW/2;
		lasty = winH/2;
		xangle = 0;
		yangle = 0;
		speed = 1;
		currentPos = new Vector3f(0,0,0);
	}
	
	public static void main(String args[]) {
		new Camera();
	}

	public FloatBuffer camTransform(Vector3f eye, float pitch, float yaw) {
		pitch = (float) Math.toRadians(pitch);
		yaw = (float) Math.toRadians(yaw);
		
		float cospitch = (float) Math.cos(pitch);
		float sinpitch = (float) Math.sin(pitch);
		float cosyaw = (float) Math.cos(yaw);
		float sinyaw = (float) Math.sin(yaw);
		
		Vector3f xax = new Vector3f(cosyaw, 0, -sinyaw);
		Vector3f yax = new Vector3f(sinyaw * sinpitch, cospitch, cosyaw * sinpitch);
		Vector3f zax = new Vector3f(sinyaw * cospitch, -sinpitch, cospitch * cosyaw);
		
		FloatBuffer viewTransform = null;
		float[] v = {xax.x, yax.x, zax.x, 0, xax.y, yax.y, zax.y, 0, xax.z, yax.z, zax.z, 0, -xax.dot(eye), -yax.dot(eye), -zax.dot(eye), 1};
		viewTransform.put(v);
		return viewTransform;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int dx = e.getX() - lastx;
		int dy = e.getY() - lasty;
		xangle += dx;
		yangle += dy;
		if (xangle < -90) {
			xangle = -90;
		} else if (xangle > 90) {
			xangle = 90;
		}
		
		if (yangle > 360) {
			yangle = yangle - 360;
		} else if (yangle < 0) {
			yangle = yangle + 360;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			System.exit(0);
			break;
		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:
			fwd += speed;
			break;
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			right -= speed;
			break;
		case KeyEvent.VK_S:
		case KeyEvent.VK_DOWN:
			fwd -= speed;
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:
			right += speed;
			break;
		}
		
		Matrix4f cam = new Matrix4f();
		cam.setIdentity();
		cam.rotX(xangle);
		cam.rotZ(yangle);
		cam.invert();
		cam.transform(new Vector3f(right, fwd, 0));
		
		currentPos.add(new Vector3f(right, fwd, 0));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		FloatBuffer viewMatrix = camTransform(currentPos, xangle, yangle);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadMatrixf(viewMatrix);
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
		
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		winW = width;
		winH = height;

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(30.0f, (float) width / (float) height, 0.01f, 100.0f);
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_MODELVIEW);	
		
	}
}
