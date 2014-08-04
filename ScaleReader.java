package jhyry.scale.reader;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public final class ScaleReader extends Observable{
	
	private static ScaleReaderFrame scale_gui;
	private static Timer scaleGuiUpdateTimer = new Timer();
	private static TimerTask task;
	private static double unitMultiplier = 1.0;
	private static String unit = "oz";
	private static ScaleReader reader;
	
	enum Unit {
		
		GRAMS(28.0, "g"),
		KILOGRAMS(28.0/1000.0, "kg"),
		OUNCES(1.0, "oz"),
		POUNDS(1.0/16.0, "lb");
		
		private double unitMultiplier;
		private String unit;
		
		Unit(double multiplier, String unit) {
			this.unitMultiplier = multiplier;
			this.unit = unit;
		}
		
		double getMultiplier() {
			return unitMultiplier;
		}
		
		public String toString() {
			return unit;
		}
	}
	
	enum TimeInterval {
		
		THOUSANDTH(1),
		HUNDREDTH(10),
		TENTH(100),
		QUARTER(250),
		HALF(500),
		THREE_QUARTERS(750),
		FULL(1000);
		
		private long interval;
		
		TimeInterval(long intervalMs) {
			this.interval = intervalMs;
		}
		
		long getInterval() {
			return this.interval;
		}
	}
	
	public static void main(String[] args) {
		new ScaleReader();
	}
	
	private ScaleReader() {
		
		scale_gui = ScaleReaderFrame.getReaderGui();
		this.addObserver(scale_gui);
		scale_gui.setVisible(true);
		
		System.out.println("Using default half-second update interval.");
		scaleGuiUpdateTimer.schedule(task = new ScaleReadUpdateTimerTask(reader = this), 1, TimeInterval.HALF.getInterval());
	}

	private static class ScaleReaderFrame extends JFrame implements Observer {
		
		private static final long serialVersionUID = 2413067983807038444L;
		private static final String aboutMessage = "Unlicensed Public Domain Software - The Unlicense";
		private static final String bugsMessage = "Please send bug reports to jon@ttcwenatchee.com";
		private JLabel lblScaleWeight = new JLabel("No scale readings yet.", SwingConstants.CENTER);
		
		static ScaleReaderFrame getReaderGui() {
			return new ScaleReaderFrame();
		}
		
		private ScaleReaderFrame() // the frame constructor method
		{
			super("Scale Reader - HID Scale");
			
			setBounds(100, 100, 350, 150);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			getContentPane().add(lblScaleWeight, BorderLayout.CENTER);
			
			buildMenus();
			
			setVisible(true); // display this frame
		}

		private void buildMenus() {
			//Where the GUI is created:
			JMenuBar menuBar;
			JMenu menu, submenu;
			JMenuItem menuItem;

			//Create the menu bar.
			menuBar = new JMenuBar();

			//Build the first menu.
			menu = new JMenu("Scale Menu");
			menu.setMnemonic(KeyEvent.VK_A);
			menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
			menuBar.add(menu);

			//a submenu
			submenu = new JMenu("Unit");
			menuItem = new JMenuItem("Grams");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					unit = Unit.GRAMS.toString();
					unitMultiplier = Unit.GRAMS.getMultiplier();
				}
			});
			submenu.add(menuItem);
			menuItem = new JMenuItem("Kilograms");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					unit = Unit.KILOGRAMS.toString();
					unitMultiplier = Unit.KILOGRAMS.getMultiplier();
				}
			});
			submenu.add(menuItem);
			menuItem = new JMenuItem("Ounces");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					unit = Unit.OUNCES.toString();
					unitMultiplier = Unit.OUNCES.getMultiplier();
				}
			});
			submenu.add(menuItem);
			menuItem = new JMenuItem("Pounds");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					unit = Unit.POUNDS.toString();
					unitMultiplier = Unit.POUNDS.getMultiplier();
				}
			});
			submenu.add(menuItem);
			
			menu.add(submenu);
			
			menu.addSeparator();
			
			submenu = new JMenu("Update Interval");
			menuItem = new JMenuItem("1/1000s");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					task.cancel();
					scaleGuiUpdateTimer.purge();
					scaleGuiUpdateTimer.schedule(task = ScaleReadUpdateTimerTask.getTimerTask(reader), 1, TimeInterval.THOUSANDTH.getInterval());
				}
			});
			submenu.add(menuItem);
			menuItem = new JMenuItem("1/100s");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					task.cancel();
					scaleGuiUpdateTimer.purge();
					scaleGuiUpdateTimer.schedule(task = ScaleReadUpdateTimerTask.getTimerTask(reader), 1, TimeInterval.HUNDREDTH.getInterval());
				}
			});
			submenu.add(menuItem);
			menuItem = new JMenuItem("1/10s");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					task.cancel();
					scaleGuiUpdateTimer.purge();
					scaleGuiUpdateTimer.schedule(task = ScaleReadUpdateTimerTask.getTimerTask(reader), 1, TimeInterval.TENTH.getInterval());
				}
			});
			submenu.add(menuItem);
			menuItem = new JMenuItem("1/4s");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					task.cancel();
					scaleGuiUpdateTimer.purge();
					scaleGuiUpdateTimer.schedule(task = ScaleReadUpdateTimerTask.getTimerTask(reader), 1, TimeInterval.QUARTER.getInterval());
				}
			});
			submenu.add(menuItem);
			menuItem = new JMenuItem("1/2s");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					task.cancel();
					scaleGuiUpdateTimer.purge();
					scaleGuiUpdateTimer.schedule(task = ScaleReadUpdateTimerTask.getTimerTask(reader), 1, TimeInterval.HALF.getInterval());
				}
			});
			submenu.add(menuItem);
			menuItem = new JMenuItem("3/4s");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					task.cancel();
					scaleGuiUpdateTimer.purge();
					scaleGuiUpdateTimer.schedule(task = ScaleReadUpdateTimerTask.getTimerTask(reader), 1, TimeInterval.THREE_QUARTERS.getInterval());
				}
			});
			submenu.add(menuItem);
			menuItem = new JMenuItem("1s");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					task.cancel();
					scaleGuiUpdateTimer.purge();
					scaleGuiUpdateTimer.schedule(task = ScaleReadUpdateTimerTask.getTimerTask(reader), 1, TimeInterval.FULL.getInterval());
				}
			});
			submenu.add(menuItem);
			
			menu.add(submenu);
			
			menu.addSeparator();
			
			menuItem = new JMenuItem("Quit");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					task.cancel();
					scaleGuiUpdateTimer.cancel();
					scaleGuiUpdateTimer.purge();
					scale_gui.dispose();
					System.exit(0);
				}
			});
			menu.add(menuItem);
			
			menu = new JMenu("About");
			menuItem = new JMenuItem("About ScaleReader");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFrame aboutFrame = new JFrame();
					JTextArea aboutText = new JTextArea("\n\n" + aboutMessage + "\n\n" + bugsMessage, 4, 1);
					
					aboutText.setLineWrap(true);
					aboutText.setEditable(false);
					aboutText.setMargin(new Insets(2, 10, 2, 10));
					aboutText.setWrapStyleWord(true);

					aboutFrame.setBounds(100, 100, 250, 150);
					aboutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					aboutFrame.getContentPane().add(aboutText, BorderLayout.CENTER);
					
					aboutFrame.setVisible(true);
				}
			});
			
			menu.add(menuItem);
			
			menuBar.add(menu);
			
			setJMenuBar(menuBar);
		}

		@Override
		public void update(Observable o, Object arg) {
			if(arg.equals(Double.MIN_VALUE)) {
				lblScaleWeight.setText("Error when reading scale.");
			}
			else {
				lblScaleWeight.setText("Weight: " + arg + " " + unit);
			}
		}
	}
	
	private static class ScaleReadUpdateTimerTask extends TimerTask {

		private final int size = 6;
		private final String file = "/dev/hidraw3";

		private int bytesRead;
		private byte[] b = new byte[size];
		private double lastRead = -1.0;
		
		private ScaleReader reader;
		
		static ScaleReadUpdateTimerTask getTimerTask(ScaleReader reader) {
			return ScaleReadUpdateTimerTask.getTimerTask(reader);
		}
		
		private ScaleReadUpdateTimerTask(ScaleReader reader) {
			this.reader = reader;
		}
		
		@Override
		public void run() {
			
			final String updateMessageFormat = "Updated Scale read, %d bytes: 1:%h 2:%h 3:%h 4:%h 5:%h 6:%h\n";
			
			try {
				
				FileInputStream scalereader = new FileInputStream(new File(file));
				bytesRead = scalereader.read(b);
				
				double weight = (((float) b[5] * 25.5)
						+ ((float) b[4] / 10.0)) * unitMultiplier;
				
				if(weight != lastRead) {
					
					System.out.printf(updateMessageFormat, bytesRead, b[0], b[1], b[2], b[3], b[4], b[5]);
					System.out.println("Weight: " + weight);
					
					lastRead = weight;
					this.reader.setChanged();
					this.reader.notifyObservers(weight);
					
					System.out.println("Observers notified of change.");
				}
				
				scalereader.close();
			}
			catch (FileNotFoundException e) {
				
				lastRead = Double.MIN_VALUE;
				
				this.reader.setChanged();
				this.reader.notifyObservers(lastRead);
				System.err.println("Scale not available.");
				
				this.cancel();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
