import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;


public class TerrainGen {
	
	private static int psize;
	private static int ssize;
	private static int max;
	static double r;
	private static byte[] terrain;
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
		terrain = new byte[psize * psize];
		
		generate();

		return terrain;
	}
	
	private static byte avgDiamond(int dist, int locx, int locy) {
		if (locx == 0) {
			if (locy == 0) {
				return (byte) ((terrain[dist] + terrain[dist*psize]) / 2);
			} else if (locy == psize - 1) {
				return (byte) ((terrain[((ssize-dist)*psize) + locy] + terrain[dist*psize]) / 2);
			} else {
				return (byte) ((terrain[locy-dist] + terrain[locy+dist] + terrain[((ssize-dist)*psize) + locy] + terrain[(dist*psize) + locy]) / 4);
			}
		} else if (locx == psize-1) {
			if (locy == 0) {
				
			} else if (locy == psize - 1) {
				
			} else {
				return (byte) ((terrain[(locx*psize) + locy-dist] +
						 		terrain[(locx*psize) + locy+dist] +
						 		terrain[((locx-dist)*psize) + locy] +
						 		terrain[((dist)*psize) + locy]) / 4);
			}
		} else if (locy == 0) {
			if (locx == 0) {
				return (byte) ((terrain[dist] + terrain[dist*psize]) / 2);
			} else if (locx == psize - 1) {
				
			} else {
				return (byte) ((terrain[((locx-dist)*psize)] +
						 		terrain[((locx+dist)*psize)] +
						 		terrain[(locx*psize) + dist] +
						 		terrain[(locx*psize) + ssize-dist]) / 4);
			}
		} else if (locy == psize-1) {
			if (locx == 0) {
				
			} else if (locx == psize - 1) {
				
			} else {
				return (byte) ((terrain[((locx-dist)*psize) + locy] +
								terrain[((locx+dist)*psize) + locy] +
								terrain[(locx*psize) + locy-dist] +
								terrain[(locx*psize) + dist]) / 4);
			}
		} else {
			return (byte) ((terrain[((locx-dist)*psize) + locy] +
					 		terrain[((locx+dist)*psize) + locy] +
					 		terrain[(locx*psize) + locy-dist] +
					 		terrain[(locx*psize) + locy+dist]) / 4);
		}
	}
	
	private static byte avgSquare(int dist, int locx, int locy) {
	    return (byte) ((terrain[((locx-dist)*psize) + locy-dist] +
			     terrain[((locx-dist)*psize) + locy+dist] +
			     terrain[((locx+dist)*psize) + locy-dist] +
			     terrain[((locx+dist)*psize) + locy+dist]) / 4);
	}
	
	private static void generate() {
		int locx, locy;
	    int dist = ssize / 2;
	    boolean onOdd;
	    double startScale = 1;
	    double ratio = Math.pow(2, -r);
	    double scale = max;
	    terrain[0] = (byte) (scale * (rand.nextDouble() - .5));
	    terrain[(ssize*psize)] = (byte) (scale * (rand.nextDouble() - .5));
	    terrain[(ssize*psize)+ssize] = (byte) (scale * (rand.nextDouble() - .5));
	    terrain[ssize] = (byte) (scale * (rand.nextDouble() - .5));
	    
	    while (dist != 0) {
			for (locx = dist; locx < ssize; locx += dist) {
				for (locy = dist; locy < ssize; locy+= dist) {
					
					terrain[(locx * psize) + locy] = (byte) (scale * (rand.nextDouble() - .5) + avgSquare(dist, locx, locy));
					locy += dist;
				}
				locx += dist;
			}
			
			onOdd = false;
			for (locx = 0; locx < ssize; locx += dist) {
			    onOdd = (!onOdd);
				for (locy = 0; locy < ssize; locy += dist) {
					if (onOdd && locy == 0) {locy += dist;}
					
					terrain[(locx * psize) + locy] = (byte) (scale * (rand.nextDouble() - .5) + avgDiamond(dist, locx, locy));
				
					locy += dist;
				}
			}
			
			scale *= ratio;
			dist /= 2;
	    	
	    }
	}
	
}
