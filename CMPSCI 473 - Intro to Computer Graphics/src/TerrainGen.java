import java.awt.Color;
import java.util.Random;

public class TerrainGen {
  private double[][] terrain;
  private double roughness, min, max;
  private int divisions;
  private Random rng;
  public TerrainGen (int lod, double roughness) {
    this.roughness = roughness;
    this.divisions = 1 << lod;
    terrain = new double[divisions + 1][divisions + 1];
    rng = new Random ();
    terrain[0][0] = rnd ();
    terrain[0][divisions] = rnd ();
    terrain[divisions][divisions] = rnd ();
    terrain[divisions][0] = rnd ();
    double rough = roughness;
    for (int i = 0; i < lod; ++ i) {
      int q = 1 << i, r = 1 << (lod - i), s = r >> 1;
      for (int j = 0; j < divisions; j += r)
        for (int k = 0; k < divisions; k += r)
          diamond (j, k, r, rough);
      if (s > 0)
        for (int j = 0; j <= divisions; j += s)
          for (int k = (j + s) % r; k <= divisions; k += r)
            square (j - s, k - s, r, rough);
      rough *= roughness;
    }
    

    
    min = max = terrain[0][0];
    for (int i = 0; i <= divisions; ++ i)
      for (int j = 0; j <= divisions; ++ j)
        if (terrain[i][j] < min) min = terrain[i][j];
        else if (terrain[i][j] > max) max = terrain[i][j];
  }
  
  public Byte[] getTerrain(int detail, double roughnessfactor, int size) {
  	Byte[] terr = new Byte[size * size];
  	new TerrainGen(detail, roughness);
  	
  	return terr;
  }
  
  private void diamond (int x, int y, int side, double scale) {
    if (side > 1) {
      int half = side / 2;
      double avg = (terrain[x][y] + terrain[x + side][y] +
        terrain[x + side][y + side] + terrain[x][y + side]) * 0.25;
      terrain[x + half][y + half] = avg + rnd () * scale;
    }
  }
  private void square (int x, int y, int side, double scale) {
    int half = side / 2;
    double avg = 0.0, sum = 0.0;
    if (x >= 0)
    { avg += terrain[x][y + half]; sum += 1.0; }
    if (y >= 0)
    { avg += terrain[x + half][y]; sum += 1.0; }
    if (x + side <= divisions)
    { avg += terrain[x + side][y + half]; sum += 1.0; }
    if (y + side <= divisions)
    { avg += terrain[x + half][y + side]; sum += 1.0; }
    terrain[x + half][y + half] = avg / sum + rnd () * scale;
  }
  private double rnd () {
    return 2. * rng.nextDouble () - 1.0;
  }
  public double getAltitude (double i, double j) {
    double alt = terrain[(int) (i * divisions)][(int) (j * divisions)];
    return (alt - min) / (max - min);
  }
  
  private Color blue = new Color(0, 0, 255);
  private Color green = new Color(0, 255, 0);
  private Color white = new Color(255, 255, 255);
  public Color getColor (double i, double j) {
    double a = getAltitude (i, j);
    if (a < .5)
      return addColors(blue, (subtractColors(green, scaleColor(blue, ((a - 0.0) / 0.5)))));
    else
      return addColors(green, (subtractColors(white, scaleColor(green, ((a - 0.5) / 0.5)))));
  }
  
	  private Color subtractColors(Color a, Color b) {
		  return new Color(a.getRed() - b.getRed(), a.getGreen() - b.getGreen(), a.getBlue() - b.getBlue());
	  }
	  
	  private Color addColors(Color a, Color b) {
		  return new Color(a.getRed() + b.getRed(), a.getGreen() + b.getGreen(), a.getBlue() + b.getBlue());
	  }
	  
	  private Color scaleColor(Color a, double s) {
		  int r = (int) (a.getRed()*(s*100));
		  int g = (int) (a.getGreen()*(s*100));
		  int b = (int) (a.getBlue()*(s*100));
		  if (r > 255) {r = 255;}
		  if (g > 255) {g = 255;}
		  if (b > 255) {b = 255;}
		  
		  return new Color(r, g, b);
	  }


}
