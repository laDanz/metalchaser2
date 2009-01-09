/*++++++++++
Schwimmbadsimulation

10.12.2008
Christian Danzmann
cdanzmann@gmail.com
##########*/

package schwimmbad.main;

public class Position {

	private double x;
	private double y;

	public Position() {
		this(0, 0);
	}

	public Position(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Position(Position p) {
		this(p.x, p.y);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getSqrDistance(Position p) {
		return ((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
	}

	public double getSqrDistanceWithWeights(Position p, double x_weight, double y_weight) {
		return ((this.x - p.x) * (this.x - p.x) * x_weight + (this.y - p.y) * (this.y - p.y) * y_weight);
	}

}
