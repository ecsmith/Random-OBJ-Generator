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
		ssize = s;
		psize = ssize + 1;
		
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
        
        
        r = roughness;
		max = m;
		terrain = new byte[psize * psize];
		
		generate();

		return terrain;
	}
	
	private static byte avgDiamond(int dist, int locx, int locy) {
		if (locx == 0) {
			return (byte) ((terrain[(locx*psize) + locy-dist] +
					 terrain[(locx*psize) + locy+dist] +
					 terrain[((ssize-dist)*psize) + locy] +
					 terrain[((locx+dist)*psize) + locy]) / 4);
		} else if (locx == psize-1) {
			return (byte) ((terrain[(locx*psize) + locy-dist] +
					 terrain[(locx*psize) + locy+dist] +
					 terrain[((locx-dist)*psize) + locy] +
					 terrain[((0+dist)*psize) + locy]) / 4);
		} else if (locy == 0) {
			return (byte) ((terrain[((locx-dist)*psize) + locy] +
					 terrain[((locx+dist)*psize) + locy] +
					 terrain[(locx*psize) + locy+dist] +
					 terrain[(locx*psize) + ssize-dist]) / 4);
		} else if (locy == psize-1) {
			return (byte) ((terrain[((locx-dist)*psize) + locy] +
					 terrain[((locx+dist)*psize) + locy] +
					 terrain[(locx*psize) + locy-dist] +
					 terrain[(locx*psize) + 0+dist]) / 4);
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
	    int dist = ssize / 2;
	    double scale = max * r;
	    terrain[0] = (byte) (scale * (rand.nextDouble() - .5));
	    terrain[(ssize*psize)] = (byte) (scale * (rand.nextDouble() - .5));
	    terrain[(ssize*psize)+ssize] = (byte) (scale * (rand.nextDouble() - .5));
	    terrain[ssize] = (byte) (scale * (rand.nextDouble() - .5));
	    
	    while (dist != 0) {
			for (int locx = dist; locx < ssize; locx += dist) {
				for (int locy = dist; locy < ssize; locy+= dist) {
					terrain[(locx * psize) + locy] = (byte) (scale * (rand.nextDouble() - .5) + avgSquare(dist, locx, locy));
					locy += dist;
				}
				locx += dist;
			}
			
			boolean onOdd = false;
			for (int locx = 0; locx < ssize; locx += dist) {
			    onOdd = (!onOdd);
				for (int locy = 0; locy < ssize; locy += dist) {
					if (onOdd && locy == 0) {locy+=dist;}

					terrain[(locx * psize) + locy] = (byte) (scale * (rand.nextDouble() - .5) + avgDiamond(dist, locx, locy));

					if (locx == 0)
						terrain[(ssize*psize) + locy] = terrain[(locx * psize) + locy];
					if (locy == 0)
						terrain[(locy*psize) + ssize] = terrain[(locx * psize) + locy];
				
					locy += dist;
				}
			}
			
			scale *= r;
			dist /= 2;
	    	
	    }
	}
	
}
