//import java.awt.Canvas;
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Point;
//import java.util.Random;
//
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//
//class Quaternion {
//	private double w, x, y, z;
//	private Quaternion inverse;
//	public Quaternion (double w, double x, double y, double z) {
//		this.w = w;
//		this.x = x;
//		this.y = y;
//		this.z = z;
//		this.inverse = null;
//	}
//	public Quaternion inverse () {
//		double scale = 1.0 / (x * x + y * y + z * z + w * w);
//		return new Quaternion (w * scale, - x * scale, - y * scale, - z * scale);
//	}
//	public Quaternion multiply (Quaternion q) {
//		double qx = q.x, qy = q.y, qz = q.z, qw = q.w;
//		double rw = w * qw - x * qx - y * qy - z * qz;
//		double rx = w * qx + x * qw + y * qz - z * qy;
//		double ry = w * qy + y * qw + z * qx - x * qz;
//		double rz = w * qz + z * qw + x * qy - y * qx;
//		return new Quaternion (rw, rx, ry, rz);
//	}
//	public Triple rotate (Triple t) {
//		if (inverse == null)
//			inverse = inverse ();
//		double iw = inverse.w, ix = inverse.x, iy = inverse.y, iz = inverse.z;
//		double tx = t.x, ty = t.y, tz = t.z;
//		double aw = - x * tx - y * ty - z * tz;
//		double ax = w * tx + y * tz - z * ty;
//		double ay = w * ty + z * tx - x * tz;
//		double az = w * tz + x * ty - y * tx;
//		double bx = aw * ix + ax * iw + ay * iz - az * iy;
//		double by = aw * iy + ay * iw + az * ix - ax * iz;
//		double bz = aw * iz + az * iw + ax * iy - ay * ix;
//		return new Triple (bx, by, bz);
//	}
//	public static Quaternion newRotation (double r, double x, double y, double z) {
//		double len = Math.sqrt (x * x + y * y + z * z);
//		double sin = Math.sin (r / 2.0);
//		double cos = Math.cos (r / 2.0);
//		double tmp = sin / len;
//		return new Quaternion (cos, x * tmp, y * tmp, z * tmp);
//	}
//}
//class Triple {
//	double x;
//	double y;
//	double z;
//	public Triple (double x, double y, double z) {
//		this.x = x;
//		this.y = y;
//		this.z = z;
//	}
//	public Triple add (Triple t) {
//		return new Triple (x + t.x, y + t.y, z + t.z);
//	}
//	public Triple subtract (Triple t) {
//		return new Triple (x - t.x, y - t.y, z - t.z);
//	}
//	public Triple cross (Triple t) {
//		return new Triple (y * t.z - z * t.y, z * t.x - x * t.z,
//				x * t.y - y * t.x);
//	}
//	public double dot (Triple t) {
//		return x * t.x + y * t.y + z * t.z;
//	}
//	public double length2 () {
//		return dot (this);
//	}
//	public Triple normalize () {
//		return scale (1.0 / Math.sqrt (length2 ()));
//	}
//	public Triple scale (double scale) {
//		return new Triple (x * scale, y * scale, z * scale);
//	}
//}
//class Tri {
//	int[] i = new int[3];
//	int[] j = new int[3];
//	Triple n;
//	RGB[] rgb = new RGB[3];
//	Color color;
//	public Tri (int i0, int j0, int i1, int j1, int i2, int j2) {
//		i[0] = i0;
//		i[1] = i1;
//		i[2] = i2;
//		j[0] = j0;
//		j[1] = j1;
//		j[2] = j2;
//	}
//}
//public class ObjectGenerator extends JPanel
//{
//	public ObjectGenerator()                       // set up graphics window
//	{
//		super();
//		setBackground(Color.WHITE);
//	}
//
//	public void paintComponent(Graphics g)  // draw graphics in the panel
//	{
////		int width = getWidth();             // width of window in pixels
////		int height = getHeight();           // height of window in pixels
//
//		super.paintComponent(g);            // call superclass to make panel display correctly
//		//
//		double exaggeration = 10;
//		int lod = 5;
//		int steps = 1 << lod;
//		Triple[][] map = new Triple[steps + 1][steps + 1];
//		RGB[][] colors = new RGB[steps + 1][steps + 1];
//		Terrain terrain = new FractalTerrain (lod, .5);
//		for (int i = 0; i <= steps; ++ i) {
//			for (int j = 0; j <= steps; ++ j) {
//				double x = 1.0 * i / steps, z = 1.0 * j / steps;
//				double altitude = terrain.getAltitude (x, z);
//				map[i][j] = new Triple (x, altitude * exaggeration, z);       
//				colors[i][j] = terrain.getColor (x, z);
//			}
//		}
//		//
//		int numTriangles = (steps * steps * 2);
//		Tri[] triangles = new Tri[numTriangles];
//		int triangle = 0;
//		for (int i = 0; i < steps; ++ i) {
//			for (int j = 0; j < steps; ++ j) {
//				triangles[triangle ++] = new Tri (i, j, i + 1, j, i, j + 1);
//				triangles[triangle ++] = new Tri (i + 1, j, i + 1, j + 1, i, j + 1);
//			}
//		}
//		//
//		double ambient = .3;
//		double diffuse = 4.0;
//		Triple[][] normals = new Triple[numTriangles][3];
//		Triple sun = new Triple (3.6, 3.9, 0.6);
//		for (int i = 0; i < numTriangles; ++ i)
//			for (int j = 0; j < 3; ++ j)
//				normals[i][j] = new Triple (0.0, 0.0, 0.0);
//		/* compute triangle normals and vertex averaged normals */
//		for (int i = 0; i < numTriangles; ++ i) {
//			Triple v0 = map[triangles[i].i[0]][triangles[i].j[0]],
//					v1 = map[triangles[i].i[1]][triangles[i].j[1]],
//					v2 = map[triangles[i].i[2]][triangles[i].j[2]];
//			Triple normal = v0.subtract (v1).cross (v2.subtract (v1)).normalize ();
//			triangles[i].n = normal;
//			for (int j = 0; j < 3; ++ j) {
//				int ii = triangles[i].i[j];
//				int jj = triangles[i].j[j];
//				normals[ii][j] = normals[ii][j].add (normal);
//			}
//		}
//		/* compute vertex colors and triangle average colors */
//		for (int i = 0; i < numTriangles; ++ i) {
//			RGB avg = new RGB (0.0, 0.0, 0.0);
//			for (int j = 0; j < 3; ++ j) {
//				int k = triangles[i].i[j], l = triangles[i].j[j];
//				Triple vertex = map[k][j];
//				RGB color = colors[k][j];
//				Triple normal = normals[k][j].normalize ();
//				Triple light = vertex.subtract (sun);
//				double distance2 = light.length2 ();
//				double dot = light.normalize ().dot (normal);
//				double lighting = ambient + diffuse * ((dot < 0.0) ? - dot : 0.0) / distance2;
//				color = color.scale (lighting);
//				triangles[i].rgb[j] = color;
//				avg = avg.add (color);
//			}
//			triangles[i].color = new Color (avg.scale (1.0 / 3.0).toRGB ());
//		}
//		//
//		double[][] shade = new double[steps + 1][steps + 1];
//		for (int i = 0; i <= steps; ++ i) {
//			for (int j = 0; j <= steps; ++ j) {
//				shade[i][j] = 1.0;
//				Triple vertex = map[i][j];
//				Triple ray = sun.subtract (vertex);
//				double distance = steps * Math.sqrt (ray.x * ray.x + ray.z * ray.z);
//				/* step along ray in horizontal units of grid width */
//				for (double place = 1.0; place < distance; place += 1.0) {
//					Triple sample = vertex.add (ray.scale (place / distance));
//					double sx = sample.x, sy = sample.y, sz = sample.z;
//					if ((sx < 0.0) || (sx > 1.0) || (sz < 0.0) || (sz > 1.0))
//						break; /* steppd off terrain */
//					double ground = exaggeration * terrain.getAltitude (sx, sz);
//					if (ground >= sy) {
//						shade[i][j] = 0.0;
//						break;
//					}
//				}
//			}
//		}
//		/* modified lighting computation:
//            ...
//            double shadow = shade[k][l];
//            double lighting = ambient + diffuse * ((dot < 0.0) ? - dot : 0.0) /
//                                          distance2 * shadow;
//		 */
//		Triple loc = new Triple (0.5, 3.0, -2.0);
//		Quaternion rot = Quaternion.newRotation (-.82, 1.0, 0.0, 0.0);
//		Triple[][] eyeMap = new Triple[steps + 1][steps + 1];
//		for (int i = 0; i <= steps; ++ i) {
//			for (int j = 0; j <= steps; ++ j) {
//				Triple p = map[i][j];
//				Triple t = p.subtract (loc);
//				Triple r = rot.rotate (t);
//				eyeMap[i][j] = r;
//			}
//		}
//		//
//		double hither = .1;
//		double fov = Math.PI / 3;
//		Point[][] srcMap = new Point[steps + 1][steps + 1];
//		int widt = srcMap.length; int heigh = srcMap[1].length;
//		double scale = widt / 2; /// Math.tan (fov / 2);
//		for (int i = 0; i <= steps; ++ i) {
//			for (int j = 0; j <= steps; ++ j) {
//				Triple p = eyeMap[i][j];
//				double x = p.x, y = p.y, z = p.z;
//				if (z >= hither) {
//					double tmp = scale / z;
//					srcMap[i][j] = new  Point(widt / 2 + (int) (x * tmp), heigh / 2 - (int) (y * tmp));
//				} else {
//					srcMap[i][j] = null;
//				}
//			}
//		}
//		//
//		for (int i = 0; i < numTriangles; ++ i) {
//			Point xy0 = srcMap[triangles[i].i[0]][triangles[i].j[0]];
//			Point xy1 = srcMap[triangles[i].i[1]][triangles[i].j[1]];
//			Point xy2 = srcMap[triangles[i].i[2]][triangles[i].j[2]];
//			double dot = - map[triangles[i].i[0]][triangles[i].j[0]]
//					.subtract (loc).normalize ().dot (triangles[i].n);
//			if ((dot > 0.0) && (xy0 != null) && (xy1 != null) && (xy2 != null)) {
//				int[] x = { xy0.x+400, xy1.x+400, xy2.x+400 }, y = { xy0.y+400, xy1.y+400, xy2.y+400 };
//				g.setColor (new Color(0,0,0)/*triangles[i].color*/);
//				g.fillPolygon (x, y, 3);
//			}
//		}
//	}
//
//	public static void main(String[] args)
//	{
//		ObjectGenerator panel = new ObjectGenerator();                            // window for drawing
//		JFrame application = new JFrame();                            // the program itself
//
//		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // set frame to exit
//		// when it is closed
//		application.add(panel);           
//
//
//		application.setSize(1000, 600);         // window is 500 pixels wide, 400 high
//		application.setVisible(true);          
//	}
//}
//
//interface Terrain {
//	public double getAltitude (double i, double j);
//	public RGB getColor (double i, double j);
//}
//class RGB {
//	private double r, g, b;
//	public RGB (double r, double g, double b) {
//		this.r = r;
//		this.g = g;
//		this.b = b;
//	}
//	public RGB add (RGB rgb) {
//		return new RGB (r + rgb.r, g + rgb.g, b + rgb.b);
//	}
//	public RGB subtract (RGB rgb) {
//		return new RGB (r - rgb.r, g - rgb.g, b - rgb.b);
//	}
//	public RGB scale (double scale) {
//		return new RGB (r * scale, g * scale, b * scale);
//	}
//	private int toInt (double value) {
//		return (value < 0.0) ? 0 : (value > 1.0) ? 255 :
//			(int) (value * 255.0);
//	}
//	public int toRGB () {
//		return (0xff << 24) | (toInt (r) << 16) |
//				(toInt (g) << 8) | toInt (b);
//	}
//}
//class FractalTerrain implements Terrain {
//	private double[][] terrain;
//	private double roughness, min, max;
//	private int divisions;
//	private Random rng;
//	public FractalTerrain (int lod, double roughness) {
//		this.roughness = roughness;
//		this.divisions = 1 << lod;
//		terrain = new double[divisions + 1][divisions + 1];
//		rng = new Random ();
//		terrain[0][0] = rnd ();
//		terrain[0][divisions] = rnd ();
//		terrain[divisions][divisions] = rnd ();
//		terrain[divisions][0] = rnd ();
//		double rough = roughness;
//		for (int i = 0; i < lod; ++ i) {
//			int q = 1 << i, r = 1 << (lod - i), s = r >> 1;
//			for (int j = 0; j < divisions; j += r)
//				for (int k = 0; k < divisions; k += r)
//					diamond (j, k, r, rough);
//			if (s > 0)
//				for (int j = 0; j <= divisions; j += s)
//					for (int k = (j + s) % r; k <= divisions; k += r)
//						square (j - s, k - s, r, rough);
//			rough *= roughness;
//		}
//		min = max = terrain[0][0];
//		for (int i = 0; i <= divisions; ++ i)
//			for (int j = 0; j <= divisions; ++ j)
//				if (terrain[i][j] < min) min = terrain[i][j];
//				else if (terrain[i][j] > max) max = terrain[i][j];
//	}
//	private void diamond (int x, int y, int side, double scale) {
//		if (side > 1) {
//			int half = side / 2;
//			double avg = (terrain[x][y] + terrain[x + side][y] +
//					terrain[x + side][y + side] + terrain[x][y + side]) * 0.25;
//			terrain[x + half][y + half] = avg + rnd () * scale;
//		}
//	}
//	private void square (int x, int y, int side, double scale) {
//		int half = side / 2;
//		double avg = 0.0, sum = 0.0;
//		if (x >= 0)
//		{ avg += terrain[x][y + half]; sum += 1.0; }
//		if (y >= 0)
//		{ avg += terrain[x + half][y]; sum += 1.0; }
//		if (x + side <= divisions)
//		{ avg += terrain[x + side][y + half]; sum += 1.0; }
//		if (y + side <= divisions)
//		{ avg += terrain[x + half][y + side]; sum += 1.0; }
//		terrain[x + half][y + half] = avg / sum + rnd () * scale;
//	}
//	private double rnd () {
//		return 2. * rng.nextDouble() - 1.0;
//	}
//	public double getAltitude (double i, double j) {
//		double alt = terrain[(int) (i * divisions)][(int) (j * divisions)];
//		return (alt - min) / (max - min);
//	}
//	private RGB blue = new RGB (0.0, 0.0, 1.0);
//	private RGB green = new RGB (0.0, 1.0, 0.0);
//	private RGB white = new RGB (1.0, 1.0, 1.0);
//	public RGB getColor (double i, double j) {
//		double a = getAltitude (i, j);
//		if (a < .5)
//			return blue.add (green.subtract (blue).scale ((a - 0.0) / 0.5));
//		else
//			return green.add (white.subtract (green).scale ((a - 0.5) / 0.5));
//	}
//
//}
//
