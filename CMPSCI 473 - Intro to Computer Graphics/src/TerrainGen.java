import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;


public class TerrainGen {
	
	private static int psize;
	private static int ssize;
	private static int max;
	static double r;
	private static byte[][] terrain;
	static Random rand = new Random();
	
	public static byte[] getRandomTerrain(int s, double roughness, int m) {

		
		//Checks if size is positive and a power of 2
	    if(s <=0){
            throw new IllegalArgumentException("Size must be positive.");
        }
        if ((s & -s) != s) {
        	 throw new IllegalArgumentException("Size must be a power of 2.");
        }
        if (roughness > 1 || roughness < 0) {
        	 throw new IllegalArgumentException("Roughness must be between 0 and 1.");
        }
        
		ssize = s;
		psize = ssize + 1;
        r = roughness;
		max = m;
		terrain = new byte[psize][psize];
		
		generate();

		return array2dto1d(terrain);
	}
	
	private static byte avgDiamond(int dist, int locx, int locy) {
		if (locx == 0) {
			if (locy == 0) {
				return (byte) ((terrain[dist][locy] + 
								terrain[locx][dist]) / 2);
			} else if (locy == ssize) {
				return (byte) ((terrain[dist][locy] + 
								terrain[locx][ssize-dist]) / 2);
			} else {
				return (byte) ((terrain[0][locy-dist] + 
								terrain[0][locy+dist] + 
								terrain[dist][locy]) / 3);
			}
		} else if (locx == ssize) {
			if (locy == 0) {
				return (byte) ((terrain[locx-dist][locy] + 
								terrain[locx][dist]) / 2);
			} else if (locy == ssize) {
				return (byte) ((terrain[locx-dist][locy] + 
								terrain[locx][locy-dist]) / 2);
			} else {
				return (byte) ((terrain[locx][locy-dist] +
						 		terrain[locx][locy+dist] +
						 		terrain[locx-dist][locy]) / 3);
			}
		} else if (locy == 0) {
			if (locx == 0) {
				return (byte) ((terrain[dist][locy] + 
								terrain[locx][dist]) / 2);
			} else if (locx == ssize) {
				return (byte) ((terrain[locx-dist][locy] + 
								terrain[locx][dist]) / 2);
			} else {
				return (byte) ((terrain[locx-dist][locy] +
						 		terrain[locx+dist][locy] +
						 		terrain[locx][dist]) / 3);
			}
		} else if (locy == ssize) {
			if (locx == 0) {
				return (byte) ((terrain[dist][locy] + 
								terrain[locx][ssize-dist]) / 2);
			} else if (locx == ssize) {
				return (byte) ((terrain[locx-dist][locy] + 
								terrain[locx][locy-dist]) / 2);
			} else {
				return (byte) ((terrain[locx-dist][locy] +
								terrain[locx+dist][locy] +
								terrain[locx][locy-dist]) / 3);
			}
		} else {
			return (byte) ((terrain[locx-dist][locy] +
					 		terrain[locx+dist][locy] +
					 		terrain[locx][locy-dist] +
					 		terrain[locx][locy+dist]) / 4);
		}
	}
	
	private static byte avgSquare(int dist, int locx, int locy) {
	    return (byte) ((terrain[locx-dist][locy-dist] +
	    				terrain[locx-dist][locy+dist] +
	    				terrain[locx+dist][locy-dist] +
	    				terrain[locx+dist][locy+dist]) / 4);
	}
	
	private static void generate() {
		int locx, locy;
	    int dist = ssize / 2;
	    boolean onOdd;
	    double startScale = 1;
	    double ratio = Math.pow(2, -r);
	    double scale = max;
	    terrain[0][0] = (byte) Math.abs((scale * (rand.nextDouble() - .5)));
	    terrain[0][ssize] = (byte) Math.abs((scale * (rand.nextDouble() - .5)));
	    terrain[ssize][0] = (byte) Math.abs((scale * (rand.nextDouble() - .5)));
	    terrain[ssize][ssize] = (byte) Math.abs((scale * (rand.nextDouble() - .5)));
	    
	    while (dist != 0) {
			for (locx = dist; locx < psize; locx += dist) {
				for (locy = dist; locy < psize; locy+= dist) {
					byte val = (byte) Math.abs((scale * (rand.nextDouble() - .5) + avgSquare(dist, locx, locy)));
					terrain[locx][locy] = val;
					locy += dist;
				}
				locx += dist;
			}
			
			onOdd = false;
			for (locx = 0; locx < psize; locx += dist) {
			    onOdd = (!onOdd);
				for (locy = 0; locy < psize; locy += dist) {
					if (onOdd && locy == 0) {locy += dist;}
					byte val = (byte) Math.abs((scale * (rand.nextDouble() - .5) + avgDiamond(dist, locx, locy)));
					terrain[locx][locy] = val;
				
					locy += dist;
				}
			}
			
			scale *= ratio;
			dist /= 2;
	    	
	    }
	}
	
	private static byte[] array2dto1d(byte[][] t) {
		byte[] converted = new byte[t.length * t[0].length];
		int n = 0;
		for (int a = 0; a < t.length; a++) {
			for (int b = 0; b < t[0].length; b++, n++) {
				converted[n] = t[a][b];
			}
		}
		return converted;
	}
	
}
