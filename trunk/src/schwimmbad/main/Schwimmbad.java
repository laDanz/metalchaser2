/*++++++++++
Schwimmbadsimulation

10.12.2008
Christian Danzmann
cdanzmann@gmail.com
##########*/

package schwimmbad.main;

import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

public class Schwimmbad {

	// Bounds in meters.
	int length = 25;
	int width = 10;
	int schwimmer_count = 0;

	public static final byte ADDMETHOD_MODELL = 0;
	public static final byte ADDMETHOD_R1 = 1;
	public static final byte ADDMETHOD_R2 = 2;
	public byte addmethod = ADDMETHOD_MODELL;

	LinkedList<Schwimmer> schwimmer;

	public Schwimmbad() {
		schwimmer = new LinkedList<Schwimmer>();
		schwimmer_count = 0;
	}

	public void setAddmethod(byte addmethod) {
		this.addmethod = addmethod;
	}

	public byte getAddmethod() {
		return addmethod;
	}

	public Schwimmbad(int length, int width) {
		this();
		this.length = length;
		this.width = width;
	}

	public synchronized void addSchwimmer(Schwimmer schwimmer) {
		switch (addmethod) {
		case ADDMETHOD_MODELL:
			addSchwimmerMethod0(schwimmer);
			break;
		case ADDMETHOD_R1:
			addSchwimmerMethod1(schwimmer);
			break;
		case ADDMETHOD_R2:
			addSchwimmerMethod2(schwimmer);
			break;

		default:
			break;
		}
	}

	public void addSchwimmerMethod0(Schwimmer schwimmer) {
		// bahnen
		double best_x = 1.25;
		double place_at_that_lane = 0;

		// warten auf andere Threads
		boolean error = true;
		while (error) {
			error = true;
			try {
				for (double x = 1.25; x < 10; x += 2.5) {
					double nearest_swimmer = 1000;
					for (Schwimmer s : this.schwimmer) {
						double delta = s.getPosition().getSqrDistanceWithWeights(new Position(x, 0), 10, 1. / 3);
						if (delta < nearest_swimmer) {
							nearest_swimmer = delta;
						}
					}
					if (nearest_swimmer > place_at_that_lane) {
						place_at_that_lane = nearest_swimmer;
						best_x = x;
					}
				}
				schwimmer.setPosition(new Position(best_x, 0.6));
				System.out.println("Neuer Schwimmer: " + schwimmer.getTimeForLane());
				this.schwimmer.add(schwimmer);
				error = false;
			} catch (ConcurrentModificationException e) {
				error = true;
			}

		}
		Main.statistics.addSwimmer(new Position(best_x, 0.6));
		schwimmer_count++;
		notifyAll();
	}

	public void addSchwimmerMethod1(Schwimmer schwimmer) {
		// warten auf andere Threads
		boolean error = true;
		double best_x = 0;
		while (error) {
			error = true;
			try {
				// Abstandsmatrix
				// v[x]=Platz in der Luecke zwischen Schwimmer x-1 und x
				double[] am = new double[this.schwimmer_count + 2];
				Schwimmer[] sa = this.schwimmer.toArray(new Schwimmer[0]);
				// Schwimmer nach X sortieren
				Arrays.sort(sa, getSchwimmerXComparator());

				// die xe mit rändern in am schreiben
				am[0] = 0.;
				am[this.schwimmer_count + 1] = this.width - 0.;
				int i = 1;
				for (Schwimmer s : sa) {
					am[i] = s.position.getX();
					i++;
				}

				// groeste luecke ermitteln
				double groesster_abstand = 0;
				best_x = 0;
				for (i = 0; i < am.length - 1; i++) {
					double d = am[i + 1] - am[i];
					if (d > groesster_abstand) {
						groesster_abstand = d;
						best_x = am[i] + d / 2;
					}
				}
				schwimmer.setPosition(new Position(best_x, 0.6));
				System.out.println("Neuer Schwimmer: " + schwimmer.getTimeForLane());
				this.schwimmer.add(schwimmer);
				error = false;
			} catch (ConcurrentModificationException e) {
				error = true;
			}

		}
		Main.statistics.addSwimmer(new Position(best_x, 0.6));
		schwimmer_count++;
		notifyAll();
	}

	public void addSchwimmerMethod2(Schwimmer schwimmer) {
		// warten auf andere Threads
		boolean error = true;
		double best_x = 0;
		while (error) {
			error = true;
			try {
				// Abstandsmatrix
				// v[x]=Platz in der Luecke zwischen Schwimmer x-1 und x
				double[] am = new double[this.schwimmer_count + 2];
				Schwimmer[] sa = this.schwimmer.toArray(new Schwimmer[0]);
				// Schwimmer nach X sortieren
				Arrays.sort(sa, getSchwimmerXComparator());

				// die xe mit rändern in am schreiben
				am[0] = 0.;
				am[this.schwimmer_count + 1] = this.width - 0.;
				int i = 1;
				for (Schwimmer s : sa) {
					am[i] = s.position.getX();
					i++;
				}

				// groeste luecke ermitteln
				double groesster_abstand = 0;
				best_x = 0;
				for (i = 0; i < am.length - 1; i++) {
					double d = am[i + 1] - am[i];
					if (d > groesster_abstand) {
						groesster_abstand = d;
						best_x = am[i] + 1;
					}
				}
				schwimmer.setPosition(new Position(best_x, 0.6));
				System.out.println("Neuer Schwimmer: " + schwimmer.getTimeForLane());
				this.schwimmer.add(schwimmer);
				error = false;
			} catch (ConcurrentModificationException e) {
				error = true;
			}

		}
		Main.statistics.addSwimmer(new Position(best_x, 0.6));
		schwimmer_count++;
		notifyAll();
	}

	private Comparator<? super Schwimmer> getSchwimmerXComparator() {
		// TODO Auto-generated method stub
		return new Comparator<Schwimmer>() {
			public int compare(Schwimmer o1, Schwimmer o2) {

				return (int) ((o1.position.getX() - o2.position.getX()) * 100);
			}
		};
	}

	public int getSchwimmerCount() {
		return schwimmer_count;
	}

	/**
	 * Perform a Simulation step.
	 */
	public synchronized void tick() {
		for (Schwimmer s : schwimmer) {
			s.tick(this);
			double y = s.getPosition().getY();
			if ((y < s.needed_space && !s.isDirection()) || (y > this.length - s.needed_space && s.isDirection())) {
				s.onEndReached();
			}
		}
	}

	public synchronized LinkedList<Schwimmer> getSchwimmer() {

		return schwimmer;
	}

	public synchronized LinkedList<Schwimmer> getSchwimmerNearPosition(Position position) {
		LinkedList<Schwimmer> result = new LinkedList<Schwimmer>();

		for (Schwimmer s : schwimmer) {
			if (s.getPosition().getSqrDistance(position) < ((s.getNeeded_space() * 2) * (s.getNeeded_space() * 2))) {
				result.add(s);
			}
		}

		return result;
	}
}
