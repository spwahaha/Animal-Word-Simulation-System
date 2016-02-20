package view;

/**
 * Record the position in the map
 * @author pang
 *
 */
public class Position {
	protected double x;
	protected double y;
	
	/**
	 * Construct a position at (x,y)
	 * @param x
	 * @param y
	 */
	public Position(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Calculate the distance between position and point (x,y)
	 * @param x the x position of the point
	 * @param y the y position of the point
	 * @return the distance between two points
	 */
	double dist(double x, double y){
		double dis2 = Math.pow((this.x - x),2) + Math.pow((this.y - y), 2);
		return Math.sqrt(dis2);
	}
	
}
