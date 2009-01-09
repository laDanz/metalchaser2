/*++++++++++
Schwimmbadsimulation

10.12.2008
Christian Danzmann
cdanzmann@gmail.com
##########*/

package schwimmbad.main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Schwimmer {

	// Needed space as the radius of a circle in meters.
	double needed_space = 0.5;

	// The time for one lane (25 meters) in seconds.
	// 40 is fast
	// 60 is normal
	// 80 is lame
	double time_for_one_lane = 40;

	double happiness = 1.0;

	// The position of the swimmer in the bath.
	Position position;

	LinkedList<Position> myway;

	// Indicator in which direction the swimmer is moving.
	boolean direction;

	// Line count
	int line_count;

	public Schwimmer() {
		position = new Position();
		direction = true;
		setTimeForLane(40);
		line_count = 0;
		myway = new LinkedList<Position>();
	}

	public void setTimeForLane(double i) {
		this.time_for_one_lane = i;
	}

	public double getTimeForLane() {
		return time_for_one_lane;
	}

	/**
	 * Returns the velocity in meters/second. Cannot be set directly, only over
	 * time_for_lane.
	 * 
	 * @return The Velocity of the swimmer in m/s.
	 */
	public double getVelocity() {
		return (25. / getTimeForLane());
	}

	/**
	 * This method is invoked when the swimmer reaches one end of the lane.<br>
	 * It counts the lanes, pauses randomly and switches direction.
	 */
	public void onEndReached() {
		line_count++;
		setDirection(!isDirection());
	}

	public void onGettingPissedOff() {

	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
		try {
			if (myway.getLast() == null || myway.getLast().getX() != position.getX()) {
				myway.add(new Position(position));
			}
		} catch (NoSuchElementException e) {
			myway.add(new Position(position));
		}
	}

	public LinkedList<Position> getMyway() {
		return myway;
	}

	public boolean isDirection() {
		return direction;
	}

	public void setDirection(boolean direction) {
		this.direction = direction;
	}

	public double getNeeded_space() {
		return needed_space;
	}

	public int getLine_count() {
		return line_count;
	}

	/**
	 * Moves this swimmer and perform thinking.
	 * 
	 * @param schwimmbad
	 */
	public void tick(Schwimmbad schwimmbad) {
		double newy = getPosition().getY();
		if (this.direction) {
			newy += getVelocity() / Main.frame_rate;

		} else {
			newy -= getVelocity() / Main.frame_rate;
		}
		getPosition().setY(newy);

		// Someone in my way?

		LinkedList<Schwimmer> near_me = schwimmbad.getSchwimmerNearPosition(getPosition());
		for (Schwimmer s : near_me) {
			// Only different direction or same direction and i m faster
			if (!(this.isDirection() != s.isDirection() || this.getVelocity() > s.getVelocity())) {
				continue;
			}
			double dx = this.getPosition().getX() - s.getPosition().getX();
			double newx = this.getPosition().getX() + (getVelocity() / Main.frame_rate)
					* (dx == 0 ? (Math.random() > 0.5 ? 1 : -1) : (Math.abs(dx) / dx));
			// new x not allowed to be <0 or > Schwimmbad.width
			if (newx < 0 + getNeeded_space()) {
				newx = getNeeded_space();
			}
			if (newx > schwimmbad.width - getNeeded_space()) {
				newx = schwimmbad.width - getNeeded_space();
			}
			{
				double d = Math.abs(newx - getPosition().getX()) / 10;
				if (happiness > 0 && d > happiness) {
					onGettingPissedOff();
				}
				happiness -= d;
				Main.statistics.incGlobalUnHappiness(d, getPosition());
				Position p = new Position(newx, getPosition().getY());
				setPosition(p);
			}
		}
	}

	public double getHappiness() {
		return happiness;
	}

	public void draw(Graphics g) {

		int bias = (int) (getNeeded_space() * (10));

		// schwimmer_img = new BufferedImage(2 * bias + 6, 2 * bias + 6,
		// BufferedImage.TYPE_INT_RGB);
		Graphics gg = g;// schwimmer_img.createGraphics();

		// gg.setColor(Color.white);
		// gg.fillRect(0, 0, 2 * bias + 6, 2 * bias + 6);
		float temp_happ = Math.max((float) happiness, 0);
		gg.setColor(new Color(1 - temp_happ, temp_happ, 0));
		gg.fillOval(4, 4, 2 * bias - 2, 2 * bias - 2);
		gg.setColor(Color.BLUE);
		gg.drawOval(+3, +3, 2 * bias, 2 * bias);
		// draw lines
		gg.drawOval(bias - 1, 0, 2, 2);
		gg.drawOval(bias + 5, 2 * bias + 4, 2, 2);

	}

}
