/*++++++++++
Schwimmbadsimulation

10.12.2008
Christian Danzmann
cdanzmann@gmail.com
##########*/

package schwimmbad.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

import javax.swing.JPanel;

import schwimmbad.main.Position;
import schwimmbad.main.Schwimmer;


public class SchwimmbadGUI extends JPanel implements MouseListener {
	Image schwimmer_img;
	boolean mouse_clicked = false;

	public SchwimmbadGUI() {
		super();
		setBounds(0, 0, 400, 600);
		setDoubleBuffered(true);
		setLayout(null);
		this.setBackground(Color.LIGHT_GRAY);
		addMouseListener(this);
		paintSchwimmbad();

	}

	public void mouseClicked(MouseEvent e) {
		mouse_clicked = true;
		merk_schwimmer = null;
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void paintSchwimmbad() {
		paintSchwimmbad(true, this.getGraphics());
	}

	public void paintSchwimmbad(boolean draw_background, Graphics g) {

		if (g == null) {
			return;
		}
		if (draw_background) {
			g.setColor(Color.white);
			g.fill3DRect(15, 15, 200, 500, true);
		}
		g.setColor(Color.BLUE);
		g.draw3DRect(15, 15, 200, 500, true);
		for (int x = 65; x < 200; x += 50) {
			for (int y = 15; y <= 500; y += 20) {
				g.drawLine(x, y, x, y + 10);
			}
		}

	}

	@Override
	public void paint(Graphics g) {
		if (g == null) {
			return;
		}
		// super.paint(g);

	}

	Schwimmer merk_schwimmer = null;

	public synchronized void drawSchwimmer(LinkedList<Schwimmer> schwimmer) throws ConcurrentModificationException {
		BufferedImage buffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g = buffer.createGraphics();
		paintSchwimmbad(true, g);

		if (g == null) {
			return;
		}
		paint(g);
		Point p = MouseInfo.getPointerInfo().getLocation();
		Point px;
		try {
			px = this.getParent().getLocationOnScreen();
		} catch (IllegalComponentStateException e) {
			return;
		}

		p.translate(-px.x, -px.y);

		boolean had_one = false;
		for (Schwimmer s : schwimmer) {
			int x = 15 + (int) (s.getPosition().getX() * 20);
			int y = 15 + (int) (s.getPosition().getY() * 20);
			int bias = (int) (s.getNeeded_space() * (10));

			g.translate(x - bias, y - bias);
			s.draw(g);
			g.translate(-x + bias, -y + bias);
			if (p.x > x - bias * 2 && p.x < x + bias * 2 && p.y > y - bias * 2 && p.y < y + bias * 2) {
				drawSchwimmerInfo(g, s);
				if (mouse_clicked) {
					merk_schwimmer = s;
				}
				had_one = true;
			}

		}
		if (!had_one && merk_schwimmer == null) {
			drawSchwimmerInfoClear(g);
		}
		if (merk_schwimmer != null) {
			drawSchwimmerInfo(g, merk_schwimmer);
		}
		mouse_clicked = false;
		this.getGraphics().drawImage(buffer, 0, 0, null);
	}

	private void drawSchwimmerInfoClear(Graphics g) {
		g.clearRect(240, 30, 400, 100);

	}

	private void drawSchwimmerInfo(Graphics g, Schwimmer s) {
		char[] c = "Schwimmer:".toCharArray();
		g.drawChars(c, 0, c.length, 240, 00);
		c = ("Bahnen: " + s.getLine_count()).toCharArray();
		g.drawChars(c, 0, c.length, 240, 60);
		c = ("Happiness: " + ((int) (s.getHappiness() * 1000) / 1000.)).toCharArray();
		g.drawChars(c, 0, c.length, 240, 90);
		g.setColor(Color.black);
		Position p = s.getMyway().getFirst();
		int x1 = 15 + (int) (p.getX() * 20);
		int y1 = 15 + (int) (p.getY() * 20);
		for (Position p_ : s.getMyway()) {

			int x2 = 15 + (int) (p_.getX() * 20);
			int y2 = 15 + (int) (p_.getY() * 20);
			g.drawLine(x1, y1, x2, y2);
			x1 = x2;
			y1 = y2;

		}

	}

}
