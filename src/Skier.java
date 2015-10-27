import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Skier implements Runnable {

	public String skierID;
	private Queue<Skier> waitQueue;
	private Random rand = new Random();
	private int maxSlopeTime;
	private ArrayList skiersOnSlope;

	public Skier(Queue<Skier> waitQueue, String id) {
		this.skierID = id;
		this.waitQueue = waitQueue;
		this.maxSlopeTime = 12;
	}

	public Skier(Queue<Skier> waitQueue, String id, int maxSlopeTime, ArrayList skiersOnSlope) {
		this.skierID = id;
		this.waitQueue = waitQueue;
		this.maxSlopeTime = maxSlopeTime;
		this.skiersOnSlope = skiersOnSlope;
	}

	private void removeFromSlope() {
		skiersOnSlope.remove(this);
	}
	
	public void run() {
		if (!this.skierID.equals("EMPTY")) {
			int skiTime = 2000 + (rand.nextInt((this.maxSlopeTime * 1000) - 1999)); // -1999 is necessary to be inclusive of maximum slope time
			//int skiTime = rand.nextInt(10001) + 2000;
			try {
				Thread.sleep(skiTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.removeFromSlope();
			waitQueue.add(this);
		}
	}

	public String toString() {
		return skierID;
	}

	public String getId() {
		return skierID;
	}

}
