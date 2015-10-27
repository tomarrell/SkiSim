import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class SkiField implements Runnable {

	public Thread t;
	public int display = 1;
	public double stopProb;
	public int seatsOnLift;
	public int speedOfLift;
	public int maxSlopeTime;
	public int numberOfSkiers;
	public int skiersOnLift = 0;
	public SkiFieldPanel skiVis;
	private Random rand = new Random();
	private volatile Queue<Skier> lift; // Lift queue
	private volatile boolean running = false;
	public ArrayList skiersOnSlope = new ArrayList();
	private volatile Queue<Skier> queue = new LinkedList<Skier>(); // Lift waiting queue

	// Default constructor
	public SkiField(SkiFieldPanel skiVis) {
		
		this.skiVis 			= skiVis;
		this.seatsOnLift 		= 10;
		this.speedOfLift 		= 1;
		this.numberOfSkiers 	= 30;
		this.maxSlopeTime 		= 12;
		this.stopProb 			= 0.05;
		
		// Creates new ArrayBlockingQueue with maximum lift size and adds null
		// values to each.
		lift = new ArrayBlockingQueue<Skier>(10);
		for (int i = 0; i < 10; i++) {
			lift.add(new Skier(queue, "EMPTY"));
		}

		for (int i = 1; i <= 30; i++) {
			queue.add(new Skier(queue, Integer.toString(i), this.maxSlopeTime, this.skiersOnSlope));
		}
	}

	// Constructor method
	public SkiField(int seatsOnLift, int numberOfSkiers, int speedOfLift, int maxSlopeTime, double stopProb) {

		this.seatsOnLift 		= seatsOnLift;
		this.speedOfLift 		= speedOfLift;
		this.numberOfSkiers 	= numberOfSkiers;
		this.maxSlopeTime 		= maxSlopeTime;
		this.stopProb 			= stopProb;

		// Creates new ArrayBlockingQueue with maximum lift size and adds null
		// values to each.
		lift = new ArrayBlockingQueue<Skier>(seatsOnLift);
		for (int i = 0; i < seatsOnLift; i++) {
			lift.add(new Skier(queue, "EMPTY"));
		}

		for (int i = 1; i <= numberOfSkiers; i++) {
			queue.add(new Skier(queue, Integer.toString(i), this.maxSlopeTime, this.skiersOnSlope));
		}

	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public void reconstruct(int seatsOnLift, int numberOfSkiers, int speedOfLift, int maxSlopeTime, double stopProb, int display) {
		
		this.seatsOnLift 		= seatsOnLift;
		this.speedOfLift 		= speedOfLift;
		this.numberOfSkiers 	= numberOfSkiers;
		this.maxSlopeTime 		= maxSlopeTime;
		this.stopProb 			= stopProb;
		this.skiersOnLift 		= 0;
		this.display			= display;
		
		// Creates new ArrayBlockingQueue with maximum lift size and adds null
		// values to each.
		lift = new ArrayBlockingQueue<Skier>(seatsOnLift);
		queue = new LinkedList<Skier>(); // Lift waiting queue
		
		for (int i = 0; i < seatsOnLift; i++) {
			lift.add(new Skier(queue, "EMPTY"));
		}

		for (int i = 1; i <= numberOfSkiers; i++) {
			queue.add(new Skier(queue, Integer.toString(i), this.maxSlopeTime, this.skiersOnSlope));
		}
		
	}
	
	public void updateUI() {
		// Call to update UI
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				skiVis.updateSkiers(numberOfSkiers, lift, queue, skiersOnLift, skiersOnSlope, display);
				skiVis.repaint();
			}
		});
	}

	public void run() {

		while (true) {
			
			synchronized (this) {
			while(!running) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}}
			// Random chance
			if (rand.nextInt(100) < (this.stopProb * 100)) {

				int timeStopped = rand.nextInt(8001);
				System.out.println("Lift stops temporarily (for " + timeStopped + " milliseconds).");
				try {
					Thread.sleep(timeStopped);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Lift continues operation.\n");

			} else {
				
				// Call to update UI
				this.updateUI();
				
				// Logic
				Skier skierOff = lift.poll();
				if (!skierOff.toString().equals("EMPTY")) {
					skiersOnSlope.add(skierOff);
				}
				if (!skierOff.toString().equals("EMPTY")) {
					this.skiersOnLift = this.skiersOnLift - 1;
				}
				new Thread(skierOff, "Skier #" + skierOff.getId()).start();

				try {
					lift.add(queue.poll());
					this.skiersOnLift = this.skiersOnLift + 1;
				} catch (NullPointerException e) {
					lift.add(new Skier(queue, "EMPTY"));
				}

				// Logging to the console
				System.out.println("On Lift (" + this.skiersOnLift + "): " + lift);
				System.out.println("In Queue (" + queue.size() + "): " + queue + "\n");
				try {
					Thread.sleep(1000000/(1000 * this.speedOfLift));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				skiVis.updateSkiers(numberOfSkiers, lift, queue, skiersOnLift, skiersOnSlope, display);
				skiVis.repaint();

			}

		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, "Thread-Lift");
			t.start();
		}
	}
}
