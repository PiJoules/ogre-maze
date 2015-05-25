import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Exception;

public class Board {
	private static char OGRE = '@';
	private static char HOLE = 'O';
	private static char GOLD = '$';
	private static char FOOTPRINT = '&';

	public static void main(String[] args){
		/* Read from stdin */
		Scanner scan = new Scanner(System.in);
		ArrayList<String> map = new ArrayList<String>();
		while (scan.hasNextLine()){
			map.add(scan.nextLine().trim());
		}
		int w = map.get(0).length();
		int h = map.size();

		/* Find the initial ogre position. If not available, exit. */
		int initX = w;
		int initY = h;
		outerloop:
		for (int y = 0; y < h-1; y++){
			for (int x = 0; x < w-1; x++){
				if (map.get(y).charAt(x) == OGRE &&
					map.get(y).charAt(x+1) == OGRE &&
					map.get(y+1).charAt(x) == OGRE &&
					map.get(y+1).charAt(x+1) == OGRE){
					initX = x;
					initY = y;
					break outerloop;
				}
			}
		}
		if (initX == w && initY == h){
			System.out.println("Could not find the ogre.");
			return;
		}

		/* Exhaust all possible paths until a valid one is found */
		Ogre shrek = new Ogre(initX,initY);
		traverse(shrek, map);

		if (ogreFoundGold(shrek, map)){
			System.out.print("[ ");
			for (Direction d : shrek.getPath()){
				switch (d){
					case UP:
						System.out.print("up ");
						break;
					case RIGHT:
						System.out.print("right ");
						break;
					case DOWN:
						System.out.print("down ");
						break;
					case LEFT:
						System.out.print("left ");
						break;
				}
			}
			System.out.println("]");

			char[][] pathMap = getMapWithPath(map, shrek.getPath(), initX, initY);
			for (char[] row : pathMap){
				for (char c : row){
					System.out.print(c);
				}
				System.out.println("");
			}
		}
		else {
			System.out.println("No Path");
		}

		/* Print results */
		System.out.println(w);
		System.out.println(h);
		System.out.println(shrek.getX() + "," + shrek.getY());
	}

	/**
	 * Get all possible directions the ogre can move in,
	 * excluding the previous direction the ogre traveled from.
	 * @param  o   le ogre himself
	 * @param  map the map itself
	 * @return     the possible directions shrek can travel
	 */
	private static Direction[] getPossibleDirections(Ogre o, ArrayList<String> map){
		ArrayList<Direction> possibleDirections = new ArrayList<Direction>();
		Direction lastDir = o.getLastDirection();
		int x = o.getX(), y = o.getY();
		int w = map.get(0).length(), h = map.size();

		/* Up */
		if (lastDir != Direction.DOWN && y > 0){
			if (map.get(y-1).charAt(x) != HOLE && map.get(y-1).charAt(x+1) != HOLE &&
				!o.wasAtCoord(x, y-1) && !o.wasAtCoord(x+1, y-1)){
				possibleDirections.add(Direction.UP);
			}
		}

		/* Right */
		if (lastDir != Direction.LEFT && x < w-2){
			if (map.get(y).charAt(x+2) != HOLE && map.get(y+1).charAt(x+2) != HOLE &&
				!o.wasAtCoord(x+2, y) && !o.wasAtCoord(x+2, y+1)){
				possibleDirections.add(Direction.RIGHT);
			}
		}

		/* Down */
		if (lastDir != Direction.UP && y < h-2){
			System.out.println(x + "," + y + " below: " + map.get(y+2).charAt(x) + " " + map.get(y+2).charAt(x+1) + "");
			if (map.get(y+2).charAt(x) != HOLE && map.get(y+2).charAt(x+1) != HOLE &&
				!o.wasAtCoord(x, y+2) && !o.wasAtCoord(x+1, y+2)){
				possibleDirections.add(Direction.DOWN);
				System.out.println("added down");
			}
		}

		/* Left */
		if (lastDir != Direction.RIGHT && x > 0){
			if (map.get(y).charAt(x-1) != HOLE && map.get(y+1).charAt(x-1) != HOLE &&
				!o.wasAtCoord(x-1, y) && !o.wasAtCoord(x-1, y+1)){
				possibleDirections.add(Direction.LEFT);
			}
		}

		return possibleDirections.toArray(new Direction[possibleDirections.size()]);
	}

	/**
	 * Recursively navigate through the 
	 * @param  o   [description]
	 * @param  map [description]
	 * @return     [description]
	 */
	private static void traverse(Ogre o, ArrayList<String> map){
		Direction[] possibleDirections = getPossibleDirections(o, map);

		/* Move le ogre */
		for (Direction d : possibleDirections){
			o.move(d);
			if (ogreFoundGold(o, map)){
				return;
			}

			Direction[] nextPossibleDirections = getPossibleDirections(o, map);
			if (nextPossibleDirections.length > 0){
				traverse(o, map);
				if (ogreFoundGold(o, map)){
					return;
				}
			}
			else {
				System.out.println("won't traverse");
				o.moveBack();
			}
		}

	}

	/**
	 * Check if the ogre found the gold
	 * @param  o   le ogre himself
	 * @param  map le map itself
	 * @return     double
	 */
	private static boolean ogreFoundGold(Ogre o, ArrayList<String> map){
		int x1 = o.getX();
		int y1 = o.getY();
		int x2 = x1+1;
		int y2 = y1+1;
		return 	map.get(y1).charAt(x1) == GOLD ||
				map.get(y1).charAt(x2) == GOLD ||
				map.get(y2).charAt(x1) == GOLD ||
				map.get(y2).charAt(x2) == GOLD;
	}

	/**
	 * Return the updated map with the path drawn on it
	 * @param  map   [description]
	 * @param  path  [description]
	 * @param  initX [description]
	 * @param  initY [description]
	 * @return       [description]
	 */
	private static char[][] getMapWithPath(ArrayList<String> map, ArrayList<Direction> path, int initX, int initY){
		Ogre testShrek = new Ogre(initX, initY);
		int w = map.get(0).length();
		int h = map.size();
		char[][] updatedMap = new char[h][w];

		/* Setup the updated map */
		for (int y = 0; y < h; y++){
			for (int x = 0; x < w; x++){
				updatedMap[y][x] = map.get(y).charAt(x);
			}
		}

		/* Place testShrek */
		updatedMap[initY][initX] = FOOTPRINT;
		updatedMap[initY+1][initX] = FOOTPRINT;
		updatedMap[initY][initX+1] = FOOTPRINT;
		updatedMap[initY+1][initX+1] = FOOTPRINT;

		/* Go ogre go */
		for (Direction d : path){
			testShrek.move(d);
			updatedMap[testShrek.getY()][testShrek.getX()] = FOOTPRINT;
			updatedMap[testShrek.getY()+1][testShrek.getX()] = FOOTPRINT;
			updatedMap[testShrek.getY()][testShrek.getX()+1] = FOOTPRINT;
			updatedMap[testShrek.getY()+1][testShrek.getX()+1] = FOOTPRINT;
		}

		return updatedMap;
	}
}