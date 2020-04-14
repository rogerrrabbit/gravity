package graphics;

import java.awt.*;
import java.net.URL;

import javax.swing.*;

public class SimpleSplash extends JWindow implements Runnable {
	private static final long serialVersionUID = -652618173170999438L;
	private volatile boolean showSplash = true;
	private int duration;
	Thread splashThread;
	
	private static SimpleSplash instance;
	
	public static SimpleSplash getSplash() {
		return instance;
	}
	
	public SimpleSplash() {
		this(0);
	}
	
	public SimpleSplash(int d) {
		duration = d;
		splashThread = new Thread(this);
		splashThread.start();
		
		instance = this;
	}

	public void hide() {
		showSplash = false;
	}
	
	public void run() {
		JPanel content = (JPanel)getContentPane();
		content.setBackground(Color.black);

		int width = 450;
		int height = 300;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width-width)/2;
		int y = (screen.height-height)/2;
		setBounds(x,y,width,height);

		URL resource = getClass().getResource("/tacos_full.png");
		if (resource == null) {
			System.out.println("Can't find splash screen image!");
			System.exit(1);
		}
		
		JLabel label = new JLabel(new ImageIcon(resource));
		JLabel copyright = new JLabel
				("Valentine Edition", JLabel.CENTER);
		copyright.setFont(new Font("Sans-Serif", Font.BOLD, 12));
		content.add(label, BorderLayout.CENTER);
		content.add(copyright, BorderLayout.SOUTH);
		Color oraRed = new Color(156, 20, 20,  255);
		content.setBorder(BorderFactory.createLineBorder(oraRed, 10));

		setVisible(true);
		
		if (duration != 0) {
			try {
				Thread.sleep(duration);
			} catch (Exception e) {
				
			}
		} else while (showSplash) {}
		
		dispose();
	}
}