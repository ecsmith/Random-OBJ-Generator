import java.util.Random;


public class TerrainGen {

	private static int psize;
	private static int ssize;
	private static int max;
	private static int stopAfter;
	static double r;
	private static int[][] terrain;
	static Random rand = new Random();

	public static int[][] getRandomTerrain(int s, double roughness, int m) {

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
		terrain = new int[psize][psize];

		generate();

		return terrain;
	}



	private static void generate() {
		int locx, locy;
		int dist = ssize / 2;
		boolean onOdd;
		double ratio = Math.pow(2, -r);
		double scale = max;
		terrain[0][0] = (int) (scale * (rand.nextDouble() - .5));
		terrain[0][ssize] = (int) (scale * (rand.nextDouble() - .5));
		terrain[ssize][0] = (int) (scale * (rand.nextDouble() - .5));
		terrain[ssize][ssize] = (int) (scale * (rand.nextDouble() - .5));

		while (dist != 0) {
			if (dist <= Math.pow(2, stopAfter)) {
			}
			for (locx = dist; locx < psize; locx += dist) {
				for (locy = dist; locy < psize; locy+= dist) {
					terrain[locx][locy] = avgSquare(dist, locx, locy) + (int) (scale * (rand.nextDouble() - .5));
					locy += dist;
				}
				locx += dist;
			}

			onOdd = false;
			for (locx = 0; locx < psize; locx += dist) {
				onOdd = (!onOdd);
				for (locy = 0; locy < psize; locy += dist) {
					if (onOdd && locy == 0) {locy += dist;}
					terrain[locx][locy] = avgDiamond(dist, locx, locy) + (int) (scale * (rand.nextDouble() - .5));;
					locy += dist;
				}
			}

			scale *= ratio;
			dist /= 2;

		}
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

}
