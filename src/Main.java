import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

// Notes:
// When the number of skiers is increased past a few thousand duplicates of output start to appear and formatting (lines blank lines) are lost. 

public class Main {
	public static void main(String[] args) {
		
		SkiFieldPanel skiVis = new SkiFieldPanel();
		
		SkiField field = new SkiField(skiVis); // ARGS: int seatsOnLift, int numberOfSkiers, int speedOfLift, int maxSlopeTime, double stopProb
		field.start();
		
		SwingGUI gui = new SwingGUI(field, skiVis);
		
	}
}

class SwingGUI {
	
	SkiField field;
	JPanel skiVis;
	
	JFrame frame = new JFrame("Ski Simulation");
	
	public SwingGUI(SkiField simField, JPanel skiVis) {
		this.field = simField;
		this.skiVis = skiVis;
		buildGUI(field);
	}
	
	public void buildGUI(SkiField field) {
		frame.setSize(750,600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel inputPanel = new JPanel(new GridBagLayout());
		frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
		Border padding = BorderFactory.createEmptyBorder(20, 0, 0, 0);
		
		inputPanel.setBorder(padding);
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = 1;
		c.gridx = 1; c.gridy = 1;
		JLabel seatsLabel = new JLabel("Seats on lift: ", SwingConstants.RIGHT);
		seatsLabel.setBorder(new EmptyBorder(0,10,0,10));
		inputPanel.add(seatsLabel, c);
		c.gridx = 2; c.gridy = 1;
		JTextField seatsInput = new JTextField(Integer.toString(field.seatsOnLift), 10);
		inputPanel.add(seatsInput, c);

		c.gridx = 1; c.gridy = 2;
		JLabel speedLabel = new JLabel("Speed of lift: ", SwingConstants.RIGHT);
		speedLabel.setBorder(new EmptyBorder(0,10,0,10));
		inputPanel.add(speedLabel, c);
		c.gridx = 2; c.gridy = 2;
		JTextField speedInput = new JTextField(Integer.toString(field.speedOfLift), 10);
		inputPanel.add(speedInput, c);
		
		c.gridx = 1; c.gridy = 3;
		JLabel numberOfSkiersLabel = new JLabel("Number of skiers: ", SwingConstants.RIGHT);
		numberOfSkiersLabel.setBorder(new EmptyBorder(0,10,0,10));
		inputPanel.add(numberOfSkiersLabel, c);
		c.gridx = 2; c.gridy = 3;
		JTextField numberOfSkiersInput = new JTextField(Integer.toString(field.numberOfSkiers), 10);
		inputPanel.add(numberOfSkiersInput, c);
		
		c.gridx = 4; c.gridy = 1;
		JLabel maxRunTimeLabel = new JLabel("Max run time: ", SwingConstants.RIGHT);
		maxRunTimeLabel.setBorder(new EmptyBorder(0,10,0,0));
		inputPanel.add(maxRunTimeLabel, c);
		c.gridx = 5; c.gridy = 1;
		JTextField maxRunTimeInput = new JTextField(Integer.toString(field.maxSlopeTime), 10);
		inputPanel.add(maxRunTimeInput, c);
		
		c.gridx = 4; c.gridy = 2;
		JLabel probabilityLabel = new JLabel("Probability of stop: ", SwingConstants.RIGHT);
		probabilityLabel.setBorder(new EmptyBorder(0,10,0,0));
		inputPanel.add(probabilityLabel, c);
		c.gridx = 5; c.gridy = 2;
		JTextField probabilityInput = new JTextField(Double.toString(field.stopProb), 10);
		inputPanel.add(probabilityInput, c);
		
		c.gridx = 4; c.gridy = 3;
		JLabel displayTypeLabel = new JLabel("Dots=0 Nums=1: ", SwingConstants.RIGHT);
		displayTypeLabel.setBorder(new EmptyBorder(0,10,0,0));
		inputPanel.add(displayTypeLabel, c);
		c.gridx = 5; c.gridy = 3;
		JTextField displayTypeInput = new JTextField(Integer.toString(field.display), 10);
		inputPanel.add(displayTypeInput, c);
		
		// ARGS: int seatsOnLift, int numberOfSkiers, int speedOfLift, int maxSlopeTime, double stopProb
		JButton setButton = new JButton("Set Values");
		setButton.setPreferredSize(new Dimension(100, 30));
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				int seats = Integer.parseInt(seatsInput.getText());
				int numberSkiers = Integer.parseInt(numberOfSkiersInput.getText());
				int speed = Integer.parseInt(speedInput.getText());
				int maxSlope = Integer.parseInt(maxRunTimeInput.getText());
				double stopProb = Double.parseDouble(probabilityInput.getText());
				int display = Integer.parseInt(displayTypeInput.getText());
				field.reconstruct(seats, numberSkiers, speed, maxSlope, stopProb, display);
				field.start();
				field.updateUI();
			}
		});
		JButton startButton = new JButton("Start");
		startButton.setPreferredSize(new Dimension(100, 30));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (field) {
					field.setRunning(true);
					field.notify();
				}
			}
		});
		JButton stopButton = new JButton("Stop");
		stopButton.setPreferredSize(new Dimension(100, 30));
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (field) {
					field.setRunning(false);
					field.notify();
				}
			}
		});
		
		c.insets = new Insets(10,10,10,10);
		c.gridx = 2; c.gridy = 4;
		inputPanel.add(setButton, c);
		c.gridx = 3; c.gridy = 4;
		inputPanel.add(startButton, c);
		c.gridx = 4; c.gridy = 4;
		inputPanel.add(stopButton, c);
		
//		JPanel textOutput = new JPanel(new GridLayout());
//		JTextArea outputArea = new JTextArea("Hello World");
//		textOutput.add(outputArea);
		
//		frame.add(textOutput);
		frame.add(skiVis);
		frame.setVisible(true);
		
	}

}





















