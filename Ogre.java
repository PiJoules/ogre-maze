import java.util.ArrayList;
import java.awt.Point;

public class Ogre {
	private int x, y;
	private ArrayList<Direction> path;
	private ArrayList<Point> traversedCoords;

	public Ogre(int x, int y){
		this.x = x;
		this.y = y;
		path = new ArrayList<Direction>();
		traversedCoords = new ArrayList<Point>();
		traversedCoords.add(new Point(x, y));
		traversedCoords.add(new Point(x+1, y));
		traversedCoords.add(new Point(x, y+1));
		traversedCoords.add(new Point(x+1, y+1));
	}

	public Ogre(int x, int y, ArrayList<Direction> path){
		this.x = x;
		this.y = y;
		this.path = path;
	}

	public Ogre clone(){
		return new Ogre(this.x, this.y, this.clonedPath());
	}

	private ArrayList<Direction> clonedPath(){
		ArrayList<Direction> clone = new ArrayList<Direction>();
		for (Direction d : this.path){
			clone.add(d);
		}
		return clone;
	}

	/**
	 * Move the orge in a given direction
	 * @param d the direction to move
	 */
	public void move(Direction d){
		switch (d){
			case UP:
				this.y--;
				traversedCoords.add(new Point(this.x, this.y));
				traversedCoords.add(new Point(this.x+1, this.y));
				break;
			case RIGHT:
				this.x++;
				traversedCoords.add(new Point(this.x, this.y));
				traversedCoords.add(new Point(this.x, this.y+1));
				break;
			case DOWN:
				this.y++;
				traversedCoords.add(new Point(this.x, this.y));
				traversedCoords.add(new Point(this.x+1, this.y));
				break;
			case LEFT:
				this.x--;
				traversedCoords.add(new Point(this.x, this.y));
				traversedCoords.add(new Point(this.x, this.y+1));
				break;
		}
		this.path.add(d);
	}

	public boolean wasAtCoord(int x, int y){
		return traversedCoords.contains(new Point(x,y));
	}

	/**
	 * Retract the previous move
	 */
	public void moveBack(){
		Direction d = this.path.get(this.path.size()-1);
		switch (d){
			case UP:
				this.y++;
				break;
			case RIGHT:
				this.x--;
				break;
			case DOWN:
				this.y--;
				break;
			case LEFT:
				this.x++;
				break;
		}
		this.path.remove(this.path.size()-1);
	}

	/* Return the coordinates */
	public int getX(){
		return this.x;
	}
	public int getY(){
		return this.y;
	}

	/* Return other stuff */
	public Direction getLastDirection(){
		if (this.path.size() > 0)
			return this.path.get(this.path.size()-1);
		else
			return null;
	}
	public ArrayList<Direction> getPath(){
		return this.path;
	}
}