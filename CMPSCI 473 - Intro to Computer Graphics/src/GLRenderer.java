import java.io.IOException;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

import com.sun.opengl.util.GLUT;

public class GLRenderer implements GLEventListener {

	private RenderType renderType;
	private static final int MAP_SIZE = 256;
	private int[][] heightMap = new int[MAP_SIZE][MAP_SIZE];
	private Vector3f[][] normal = new Vector3f[MAP_SIZE][MAP_SIZE];
	private float scaleValue = 0.10f;
	private float HEIGHT_RATIO = 1.0f;
	private float[] lightAmbient = {1.0f, 1.0f, 1.0f, 0.5f};
	private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 0.5f};
	private float[] lightPosition = {-50.0f, 200.0f, -50.0f, 1.0f};
	private int[] textures = new int[1];
	private int skyTexture;
	private GLU glu = new GLU();
	private GLUT glut;
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


	@Override
	public void init(GLAutoDrawable drawable)
	{
		glut = new GLUT();
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

		heightMap = TerrainGen.getRandomTerrain(MAP_SIZE, .9, 600);

		setTexture();
		calcNorms(gl, heightMap);
		setLightning(gl);
		loadSphereTexture();
		camera.move(new CustomVector3f(-60, -55, -70));

	}

	@Override
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

	@Override
	public void display(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();
		CustomVector3f camPosition = camera.getCameraPosition();
		CustomVector3f camTarget = camera.getCameraTarget();
		CustomVector3f upVector = camera.getUpVector();
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
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_LIGHTING);
		renderHeightMap(gl, heightMap);
		gl.glDisable(GL.GL_LIGHT0);
		gl.glDisable(GL.GL_LIGHTING);

		drawSphere();
		animateColors();
		gl.glFlush();
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}



	private void renderHeightMap(GL gl, int[][] pHeightMap) {
		if(renderType == RenderType.LINE) {
			gl.glBegin(GL.GL_LINES);
		} else {
			gl.glBegin(GL.GL_QUADS);
		}


		for (int X = 0; X < (MAP_SIZE-1); X += 1) {
			for (int Y = 0; Y < (MAP_SIZE-1); Y += 1) {
				int x = X;
				int y = pHeightMap[X][Y];
				int z = Y;
				if(renderType == RenderType.TEXTURED) {
					gl.glTexCoord2f((float)x / (float)MAP_SIZE, (float)z / (float)MAP_SIZE);
				} else {
					setVertexColor(gl, pHeightMap, x, z);
				}
				gl.glVertex3i(x, y, z);
				gl.glNormal3f(normal[X][Y].x, normal[X][Y].y, normal[X][Y].z);

				x = X;
				y = pHeightMap[X][Y + 1];
				z = Y + 1;
				if(renderType == RenderType.TEXTURED) {
					gl.glTexCoord2f((float)x / (float)MAP_SIZE, (float)(z + 1) / (float)MAP_SIZE);
				} else {
					setVertexColor(gl, pHeightMap, x, z);
				}
				gl.glVertex3i(x, y, z);
				gl.glNormal3f(normal[X][Y+1].x, normal[X][Y+1].y, normal[X][Y+1].z);

				x = X + 1;
				y = pHeightMap[X + 1][Y + 1];
				z = Y + 1;
				if(renderType == RenderType.TEXTURED) {
					gl.glTexCoord2f((float)(x + 1) / (float)MAP_SIZE, (float)(z + 1) / (float)MAP_SIZE);
				} else {
					setVertexColor(gl, pHeightMap, x, z);
				}
				gl.glVertex3i(x, y, z);
				gl.glNormal3f(normal[X+1][Y+1].x, normal[X+1][Y+1].y, normal[X+1][Y+1].z);

				x = X + 1;
				y = pHeightMap[X + 1][Y];
				z = Y;
				if(renderType == RenderType.TEXTURED) {
					gl.glTexCoord2f((float)(x + 1) / (float)MAP_SIZE, (float)z / (float)MAP_SIZE);
				} else {
					setVertexColor(gl, pHeightMap, x, z);
				}
				gl.glVertex3i(x, y, z);
				gl.glNormal3f(normal[X+1][Y].x, normal[X+1][Y].y, normal[X+1][Y].z);
			}
		}

		gl.glEnd();
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // reset
	}

	private void setVertexColor(GL gl, int[][] pHeightMap, int x, int y)
	{
		int height = pHeightMap[x][y];
		if(renderType != RenderType.MULTICOLOR)
		{
			float fColor = 0.1f + (height / 256.0f);
			gl.glColor3f(0, 0, fColor);
		}
		else
		{
			if(height >= 250) {
				gl.glColor3f(0.984313f, 0.3019607f, 0.3019607f);
			} else if(height > 200) {
				gl.glColor3f(1.0f, 0.5607843f, 0.243147f);
			} else if(height > 150) {
				gl.glColor3f(0.50196f, 1.0f, 0.50196f);
			} else if(height > 100) {
				gl.glColor3f(0.011764f, 0.55294117f, 0.1490196f);
			} else if(height > 50) {
				gl.glColor3f(0.011764f, 0.6862745f, 0.945098f);
			} else {
				gl.glColor3f(0.011764f, 0.270588f, 0.945098f);
			}
		}
	}

	private void makeRGBTexture(GL gl, GLU glu,TextureReader.Texture img, int target)
	{

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

	private void loadSphereTexture()
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


	private void drawSphere()
	{
		CustomVector3f camPosition = camera.getCameraPosition();
		_gl.glBindTexture(GL.GL_TEXTURE_2D, skyTexture);

		_gl.glPushMatrix();
		_gl.glTranslatef(camPosition.X, camPosition.Y, camPosition.Z - MAP_SIZE * scaleValue * 0.5f);
		double[] clipPlane1 = {0.0f, 0.0f, 0.0f, 0.0f};
		_gl.glClipPlane(GL.GL_CLIP_PLANE1, clipPlane1, 0);
		_gl.glEnable(GL.GL_CLIP_PLANE1);
		glut.glutWireSphere(5000, 50, 5);
		_gl.glDisable(GL.GL_CLIP_PLANE1);
		_gl.glPopMatrix();
		_gl.glPushMatrix();
		_gl.glTranslatef(camPosition.X, camPosition.Y, camPosition.Z - MAP_SIZE * scaleValue * 0.5f);
		double[] clipPlane2 = {0.0f, 0.0f, -1.0f, 0.5f};
		_gl.glClipPlane(GL.GL_CLIP_PLANE2, clipPlane2, 0);
		_gl.glEnable(GL.GL_CLIP_PLANE2);
		glut.glutWireSphere(5000, 50, 5);
		_gl.glDisable(GL.GL_CLIP_PLANE2);
		_gl.glPopMatrix();

	}

	private void calcNorms(GL gl, int[][] pHeightMap)
	{
		for (int x = 0; x < MAP_SIZE; x++) {
			for (int y = 0; y < MAP_SIZE; y++) {
				normal[x][y] = new Vector3f(0,0,0);
			}
		}

		for (int X = 0; X < MAP_SIZE-1; X ++) {
			for (int Y = 0; Y < MAP_SIZE-1; Y ++) {
				Vector3f a = new Vector3f(X, pHeightMap[X][Y], Y);
				Vector3f b = new Vector3f(X + 1, pHeightMap[X + 1][Y], Y);
				Vector3f c = new Vector3f(X, pHeightMap[X][Y + 1], Y + 1);
				Vector3f d = new Vector3f(X + 1, pHeightMap[X + 1][Y + 1], Y + 1);


				Vector3f n1 = new Vector3f();
				Vector3f ba = new Vector3f();
				ba.sub(b, a);
				Vector3f ca = new Vector3f();
				ca.sub(c, a);
				n1.cross(ba, ca);

				Vector3f n2 = new Vector3f();
				Vector3f bd = new Vector3f();
				ba.sub(b, d);
				Vector3f cd = new Vector3f();
				ca.sub(c, d);
				n2.cross(bd, cd);

				normal[X][Y].add(new Vector3f(n1));

				normal[X+1][Y].add(new Vector3f(n1));
				normal[X+1][Y].add(new Vector3f(n2));

				normal[X][Y+1].add(new Vector3f(n1));
				normal[X][Y+1].add(new Vector3f(n2));

				normal[X+1][Y+1].add(new Vector3f(n2));

			}
		}

		for (Vector3f[] l1 : normal) {
			for (Vector3f l2 : l1) {
				l2.normalize();
			}
		}
	}

	private void animateColors()
	{
		_gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
		_gl.glPushMatrix();
		TextureReader.Texture texture;
		try {
			texture = TextureReader.animateTexture((HeightmapTerrain.file.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		_gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
		_gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		_gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D);
		_gl.glPopMatrix();
	}


	private void setTexture()
	{
		textures = new int[1];

		_gl.glEnable(GL.GL_TEXTURE_2D);

		TextureReader.Texture texture;

		try { texture = TextureReader.readTexture(HeightmapTerrain.file.getAbsolutePath()); }
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e); }

		_gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
		_gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		_gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		makeRGBTexture(_gl, glu, texture, GL.GL_TEXTURE_2D);

	}

	public RenderType getRenderType()
	{
		return renderType;
	}

	public void setRenderType(RenderType renderType)
	{
		this.renderType = renderType;
	}

	public float getScaleValue()
	{
		return scaleValue;
	}

	public void setScaleValue(float scaleValue)
	{
		if(scaleValue < 0.1f || scaleValue > 0.4f) {
			return;
		}

		this.scaleValue = scaleValue;
	}

}
