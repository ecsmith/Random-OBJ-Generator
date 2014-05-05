import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;


public class TerrainGen {
	public BufferedImage getRandomTerrain() {
		int width = 800, height = 600;
		int numhighs = 4, numlows = 3;
		int maxterrainheight = 100, minterrainheight = 0;
		Point[] highs = new Point[numhighs];
		Point[] lows = new Point[numlows];
		BufferedImage terrainmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for (Point h : highs) {
			h.x = (int) Math.random() * width;
			h.y = (int) Math.random() * height;
		}
		
		for (Point l : lows) {
			l.x = (int) Math.random() * width;
			l.y = (int) Math.random() * height;
			for (Point h : highs) {
				while (Math.abs(h.x - l.x) < 10 && Math.abs(h.y - l.y) < 10) {
					l.x = (int) Math.random() * width;
					l.y = (int) Math.random() * height;
				}
			}
		}
		
		for (Point h : highs) {
			terrainmap.setRGB(h.x, h.y, Color.black.getRGB());
		}
		
		for (Point l : lows) {
			terrainmap.setRGB(l.x, l.y, Color.white.getRGB());
		}
		
		for (int y = 0; y < terrainmap.getHeight(); y++) {
			for (int x = 0; x < terrainmap.getWidth(); x++) {
				
			}
		}
		return null;
	}
}
