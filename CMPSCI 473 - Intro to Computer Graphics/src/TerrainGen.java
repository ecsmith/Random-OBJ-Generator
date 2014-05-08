import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;


public class TerrainGen {
	
	private static int psize;
	private static int ssize;
	private static int max;
	private static int stopAfter;
	static double r;
	private static int[][] terrain;
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
        int numDivs = (int) (Math.log(s)/Math.log(2));
       	stopAfter = 0;
        
		max = 128;
		terrain = new int[psize][psize];
		
		generate();

		return array2dto1d(terrain);
	}
	
	private static int avgDiamond(int dist, int locx, int locy) {
		if (locx == 0) {
			if (locy == 0) {
				return (terrain[dist][locy] + 
						terrain[locx][dist]) / 2;
			} else if (locy == ssize) {
				
				return ((terrain[dist][locy] + 
						terrain[locx][locy-dist]) / 2);
			} else {
				return ((terrain[locx][locy-dist] + 
						terrain[locx][locy+dist] + 
								terrain[dist][locy]) / 3);
			}
		} else if (locx == ssize) {
			if (locy == 0) {
				return ((terrain[locx-dist][locy] + 
								terrain[locx][dist]) / 2);
			} else if (locy == ssize) {
				return ((terrain[locx-dist][locy] + 
								terrain[locx][locy-dist]) / 2);
			} else {

				return ((terrain[locx][locy-dist] +
						 		terrain[locx][locy+dist] +
						 		terrain[locx-dist][locy]) / 3);
			}
		} else if (locy == 0) {
			if (locx == 0) {
				return ((terrain[dist][locy] + 
								terrain[locx][dist]) / 2);
			} else if (locx == ssize) {
				return ((terrain[locx-dist][locy] + 
								terrain[locx][dist]) / 2);
			} else {

				return ((terrain[locx-dist][locy] +
						 		terrain[locx+dist][locy] +
						 		terrain[locx][dist]) / 3);
			}
		} else if (locy == ssize) {
			if (locx == 0) {
				return ((terrain[dist][locy] + 
								terrain[locx][locy-dist]) / 2);
			} else if (locx == ssize) {
				return ((terrain[locx-dist][locy] + 
								terrain[locx][locy-dist]) / 2);
			} else {

				return ((terrain[locx-dist][locy] +
								terrain[locx+dist][locy] +
								terrain[locx][locy-dist]) / 3);
			}
		} else {
			return ((terrain[locx-dist][locy] +
					 		terrain[locx+dist][locy] +
					 		terrain[locx][locy-dist] +
					 		terrain[locx][locy+dist]) / 4);
		}
	}
	
	private static int avgSquare(int dist, int locx, int locy) {
	    return ((terrain[locx-dist][locy-dist] +
	    		terrain[locx-dist][locy+dist] +
	    		terrain[locx+dist][locy-dist] +
	    		terrain[locx+dist][locy+dist]) / 4);
	}
	
	private static void generate() {
		int locx, locy;
	    int dist = ssize / 2;
	    boolean onOdd, flux = true;
	    double startScale = 1;
	    double ratio = Math.pow(2, -r);
	    double scale = max;
	    terrain[0][0] = (int) (scale * (rand.nextDouble() - .5));
	    terrain[0][ssize] = (int) (scale * (rand.nextDouble() - .5));
	    terrain[ssize][0] = (int) (scale * (rand.nextDouble() - .5));
	    terrain[ssize][ssize] = (int) (scale * (rand.nextDouble() - .5));
	    
	    while (dist != 0) {
	    	if (dist <= Math.pow(2, stopAfter)) {
	    		flux = false;
	    	}
			for (locy = dist; locy < ssize; locy += dist) {
				for (locx = dist; locx < ssize; locx+= dist) {
					int val = avgSquare(dist, locx, locy);
					if (flux) {
						val += (int) (scale * (rand.nextDouble() - .5));
					}
					terrain[locx][locy] = val;
					locx += dist;
				}
				locy += dist;
			}
			
			onOdd = false;
			for (locy = 0; locy < ssize; locy += dist) {
			    onOdd = (!onOdd);
				for (locx = 0; locx < ssize; locx += dist) {
					if (onOdd && locx == 0) {locx += dist;}
					int val = avgDiamond(dist, locx, locy);
					if (flux) {
						val += (int) (scale * (rand.nextDouble() - .5));
					}
					terrain[locx][locy] = val;
				
					locx += dist;
				}
			}
			
			scale *= ratio;
			dist /= 2;
	    	
	    }
	}
	
	private static byte[] array2dto1d(int[][] t) {
		byte[] converted = new byte[t.length * t[0].length];
		int n = 0;
		for (int a = 0; a < t.length; a++) {
			for (int b = 0; b < t[0].length; b++, n++) {
				converted[n] = (byte) Math.abs(t[a][b]);
			}
		}
		return converted;
	}
	
}
