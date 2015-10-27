import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JPanel;

public class SkiFieldPanel extends JPanel {
	
//	SkiField field;
	int display = 1; 					// Display int: 0 = dots, 1 = numbers
	volatile Queue lift = new ArrayBlockingQueue<Skier>(10);
	volatile Queue queue = new LinkedList<Skier>();
	volatile int skiersOnSlope = 0;
	volatile int skiersOnLift;
	volatile int totalSkiers = 30;
	volatile ArrayList cleansedLift = new ArrayList();
	volatile ArrayList onSlope;
	
//	volatile int skiersInQueue 	= 0;
//	volatile int skiersOnLift 	= 0;
//	volatile int skiersOnSlope 	= 0;

	SkiFieldPanel() {
		this.setBackground(Color.WHITE);
	}

	public void updateSkiers(int numberSkiers, Queue lift, Queue queue, int skiersOnLift, ArrayList onSlope, int display) {
		this.totalSkiers = numberSkiers;
		this.lift = lift;
		this.queue = queue;
		this.skiersOnSlope = numberSkiers - skiersOnLift - queue.size();
		cleansedLift = new ArrayList(lift);
		this.onSlope = onSlope;
		this.display = display;
	}

	@Override
	public void paintComponent(Graphics g) {

		int drawnInQueue = 10;
		int drawnOnLift = 10;
		int drawnOnSlope = 10;
		int width = this.getWidth();
		int height = this.getHeight();

		super.paintComponent(g);

		Polygon poly = new Polygon(new int[] { 0, 60, -60 }, new int[] { -70, 30, 30 }, 3);
		poly.translate(this.getWidth() / 2, this.getHeight() / 2);

		g.drawString("On Lift:", width / 2 - 220, height / 2 - 100);
		g.drawString("On Slope:", width / 2 + 180, height / 2 - 100);
		g.drawString("In Queue:", width / 2 - 25, height / 2 + 70);
		g.drawPolygon(poly);

		// QUEUE SKIER DISPLAY
		for (int i = 0; i < queue.size(); i++, drawnInQueue++) {
			if (display == 1) {
				// Draw skiers as numbers
				g.drawString(
					queue.toArray()[i].toString(),
					(width / 2) + ((drawnInQueue % 10) * 20) - 95,
					(height / 2 + 80) + 20 * (drawnInQueue / 10) - 10
				);
			} else {
				// Draw skiers as ovals
				g.fillOval(
					(width / 2) + ((drawnInQueue % 10) * 20) - 95,
					(height / 2 + 80) + 20 * (drawnInQueue / 10) - 10,
					10,
					10
				);
			}
			
		}
		
		// LIFT DISPLAY
		for (int i = 0; i <= cleansedLift.size(); i++, drawnOnLift++) {
			try {
				if (cleansedLift.get(i) != null && !cleansedLift.get(i).toString().equals("EMPTY")) {
					if (display == 1) {
						// Draw skiers as numbers
						g.drawString(
							cleansedLift.get(i).toString(),
							(width / 2) + ((drawnOnLift % 10) * 20) - 298,
							(height / 2 + 80) + 20 * (drawnOnLift / 10) - 180
						);
					} else {
						// Draw skiers as ovals
						g.fillOval(
							(width / 2) + ((drawnOnLift % 10) * 20) - 298,
							(height / 2 + 80) + 20 * (drawnOnLift / 10) - 180,
							10,
							10
						);
					}
				}
			} catch (IndexOutOfBoundsException e) {}
		} 
		
		// SLOPE DISPLAY
		for (int i = 0; i < skiersOnSlope; i++, drawnOnSlope++) {

			if (display == 1) {
				// Draw skiers as numbers
				g.drawString(
					onSlope.get(i).toString(),
					(width / 2) + ((drawnOnSlope % 10) * 20) + 109,
					(height / 2 + 80) + 20 * (drawnOnSlope / 10) - 180
				);
			} else {
				// Draw skiers as ovals
				g.fillOval(
					(width / 2) + ((drawnOnSlope % 10) * 20) + 109,
					(height / 2 + 80) + 20 * (drawnOnSlope / 10) - 180,
					10,
					10
				);
			}

		}

	}

}