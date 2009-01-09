/*++++++++++
Schwimmbadsimulation

10.12.2008
Christian Danzmann
cdanzmann@gmail.com
##########*/

package schwimmbad.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Vector;

public class Statistics {
	// Unhappiness < time(added),amount(added),Point>
	LinkedList<Vector<Object>> unhappiness;
	// Unhappiness < time(added),Point>
	LinkedList<Vector<Object>> swimmer_added;
	LinkedList<Vector<Object>> unhappiness_rate10;
	long start_time;
	double unhappines_sum;
	public long simulation_time;
	public boolean paused = true;

	double max_uhr = 0.1;

	public Statistics() {
		initValues();

	}

	private void initValues() {
		unhappiness = new LinkedList<Vector<Object>>();
		unhappiness_rate10 = new LinkedList<Vector<Object>>();
		swimmer_added = new LinkedList<Vector<Object>>();
		start_time = 0;
		unhappines_sum = 0;
	}

	public synchronized void incGlobalUnHappiness(double by, Position p) {
		double now = simulation_time;
		Vector v = new Vector<Object>();
		v.add(now);
		v.add(by);
		v.add(new Position(p));
		unhappiness.add(v);
		unhappines_sum += by;
	}

	public synchronized void addSwimmer(Position p) {
		double now = simulation_time;
		Vector v = new Vector<Object>();
		v.add(now);
		v.add(new Position(p));
		swimmer_added.add(v);
	}

	public synchronized void addUnHappinessRate(double value) {
		double now = simulation_time;
		Vector v = new Vector<Object>();
		v.add(now);
		v.add(value);
		if (value > max_uhr) {
			max_uhr = value;
		}
		unhappiness_rate10.add(v);

	}

	public void setPaused(boolean isPaused) {
		paused = isPaused;
	}

	public synchronized void paintPositionUH(Graphics gg) {
		// Matrix zum schwimmbad rastern
		// Aufloesung in metern
		double aufloesung = 1;
		int sizex = (int) (Main.schwimmbad.width / aufloesung);
		int sizey = (int) (Main.schwimmbad.length / aufloesung);
		int size = (sizex * sizey);
		int max_value = 0;
		int[] treffmatrix = new int[size];
		// treffer matrix bilden
		for (Vector<Object> v : unhappiness) {
			Position p = (Position) v.get(2);
			int posx = (int) (p.getX() / Main.schwimmbad.width * sizex);
			int posy = (int) (p.getY() / Main.schwimmbad.length * sizey);
			treffmatrix[posx + (posy) * sizex]++;
			if (treffmatrix[posx + (posy) * sizex] > max_value) {
				max_value = treffmatrix[posx + (posy) * sizex];
			}
		}
		// treffer matrix zeichnen
		BufferedImage img = new BufferedImage(Main.schwimmbad.width * 20, Main.schwimmbad.length * 20,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(new Color(238, 238, 238));
		g.fillRect(0, 0, Main.schwimmbad.width * 20, Main.schwimmbad.length * 20);
		for (int i = 0; i < size; i++) {
			float v = treffmatrix[i];
			int posx = i % sizex;
			int posy = i / sizex;
			Color c = new Color(v / max_value, 1 - v / max_value, 0);
			g.setColor(c);
			g.fillRect(posx * 20, posy * 20, (int) (aufloesung * 20), (int) (aufloesung * 20));
			g.setColor(Color.black);
			if (v != 0)
				g.drawString(v + "", (int) ((posx + 0.) * 20), (int) ((posy + 0.5) * 20));
		}
		gg.drawImage(img, 0, 0, null);

	}

	public synchronized void paintUnHappinessGraph(Graphics gg) {

		BufferedImage img = new BufferedImage(400, 430, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(new Color(238, 238, 238));
		g.fillRect(0, 0, 400, 430);
		g.translate(0, 20);
		// axis
		g.setColor(Color.black);
		int width = 200;
		int height = 200;
		g.drawLine(0, height, width, height);
		g.drawLine(0, 0, 0, height);

		long now = simulation_time;
		long start = start_time;
		double loc_unhappiness_sum = 0;
		double loc_unhappiness_sum_last10 = 0;
		Point last_point = new Point(0, height);
		long last10_time = (long) (200);
		for (Vector<Object> v : unhappiness) {
			if (now - (Double) v.get(0) < last10_time) {
				loc_unhappiness_sum_last10 += (Double) v.get(1);
			}
			loc_unhappiness_sum += (Double) v.get(1);
			int x = (int) (width * ((Double) v.get(0) - start) / (now - start));
			int y = (int) (height - height * (loc_unhappiness_sum / unhappines_sum));
			Point p = new Point(x, y);
			g.drawLine(last_point.x, last_point.y, p.x, p.y);
			last_point = p;

		}
		int x = (int) (width);
		int y = (int) (height - height * (loc_unhappiness_sum / unhappines_sum));
		Point p = new Point(x, y);
		g.drawLine(last_point.x, last_point.y, p.x, p.y);

		g.setColor(Color.CYAN);
		// UHR
		last_point = new Point(0, height);
		for (Vector<Object> v : unhappiness_rate10) {

			x = (int) (width * ((Double) v.get(0) - start) / (now - start));
			y = (int) (height - height * ((Double) v.get(1) / max_uhr));
			p = new Point(x, y);
			g.drawLine(last_point.x, last_point.y, p.x, p.y);
			last_point = p;

		}
		g.setColor(Color.LIGHT_GRAY);
		for (Vector<Object> v : swimmer_added) {
			x = (int) (width * ((Double) v.get(0) - start) / (now - start));
			y = (int) (0);
			p = new Point(x, y);
			g.drawLine(x, y, x, y + height);
		}
		// x = (int) (width);
		// y = (int) (height - height * (loc_unhappiness_sum / unhappines_sum));
		// Point p = new Point(x, y);
		// g.drawLine(last_point.x, last_point.y, p.x, p.y);
		g.setColor(Color.black);
		char[] c = ("Global Unhappines: " + ((int) (unhappines_sum * 100) / 100.)).toCharArray();
		g.drawChars(c, 0, c.length, 210, 00);

		c = ("UH Rate (global): " + ((int) (unhappines_sum / simulation_time * 100000) / 1000.)).toCharArray();
		g.drawChars(c, 0, c.length, 210, 30);

		double unhappiness_rate10 = ((int) (loc_unhappiness_sum_last10 / last10_time * 100000) / 1000.);
		c = ("UH Rate (now): " + unhappiness_rate10).toCharArray();
		g.drawChars(c, 0, c.length, 210, 60);
		addUnHappinessRate(unhappiness_rate10);
		c = ("#swimmers: " + (Main.schwimmbad.getSchwimmerCount())).toCharArray();
		g.drawChars(c, 0, c.length, 210, 90);
		c = ("time: " + time2str(simulation_time / Main.frame_rate)).toCharArray();
		g.drawChars(c, 0, c.length, 210, 120);
		gg.drawImage(img, 0, 0, null);
	}

	private String time2str(long seconds) {
		if (seconds < 60) {
			return zeroIfSmaller10(seconds) + seconds + " sec";
		} else if (seconds < 600) {
			return zeroIfSmaller10(seconds / 60) + seconds / 60 + ":" + zeroIfSmaller10(seconds % 60) + seconds % 60
					+ " min";
		} else {
			return zeroIfSmaller10(seconds / 3600) + seconds / 3600 + ":" + zeroIfSmaller10(seconds / 60 % 60)
					+ seconds / 60 % 60 + " h";
		}

	}

	private String zeroIfSmaller10(long seconds) {
		if (seconds < 10)
			return "0";
		else
			return "";
	}
}
