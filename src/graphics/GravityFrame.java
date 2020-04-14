package graphics;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import main.Main;
import game.Game;
import game.SaveState;
import gravity.*;

public class GravityFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 2070522217020052924L;
	private UniversePanel universePanel;

	public GravityFrame()
	{	
		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		//screenDimensions.setSize(1280, 720);
		
		universePanel = new UniversePanel(screenDimensions, 2);
		setUndecorated(true);
		
		addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent arg0) {
				
				switch(arg0.getKeyCode()) {
					/*case(KeyEvent.VK_ESCAPE):
						System.exit(0);
						break;*/
					case(KeyEvent.VK_M):
						ObjectOutputStream oos;
						universePanel.setStatus("Memorizing state...");
						universePanel.repaint();
						Game.getInstance().togglePause();
						SaveState save = new SaveState(Game.getInstance(), Space.getInstance());
						synchronized (Space.getInstance()) {
							try {
								oos = new ObjectOutputStream(new FileOutputStream(Main.quickSaveState));
								oos.writeObject(save);
								oos.close();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						universePanel.togglePause();
						break;
					case(KeyEvent.VK_L):
						ObjectInputStream ois;
						universePanel.setStatus("Loading state...");
						universePanel.repaint();
						Game.getInstance().togglePause();
						SaveState s = null;
						try {
							ois = new ObjectInputStream(new FileInputStream(Main.quickSaveState));
							s = (SaveState)ois.readObject();	
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						
						if (s != null) {
							Space.getInstance().removeAllNow();
							Space.swapSpace(s.getSpaceState());
							Game.swapGame(s.getGameState());
							Game.getInstance().setDimensions(universePanel.getSize());
						}
						universePanel.togglePause();
						break;
					case(KeyEvent.VK_HOME):
						Game.toMenu();
						break;
					/* simulation/debug */
					case(KeyEvent.VK_PAUSE):
					case(KeyEvent.VK_P):
						universePanel.togglePause();
						break;
					/*case(KeyEvent.VK_ENTER):
						Space.getInstance().run();
						break;*/
					case(KeyEvent.VK_C):
						Space.getInstance().toggleCollisions();
						break;
					case(KeyEvent.VK_G):
						Space.getInstance().toggleGravity();
						break;
					case(KeyEvent.VK_DELETE):
						/* flush the entire universe */
						Space.getInstance().removeAll();
						break;
					default:
						Game.getInstance().handleKey(arg0.getKeyCode());
				}
			}
		});

		setTitle("Gravity");
		setContentPane(universePanel);

		setSize(screenDimensions);
	    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
 
		universePanel.init();
		universePanel.start();

		Game.getInstance().setDimensions(universePanel.getSize());
		Game.getInstance().load();
		
		setVisible(true);
		SimpleSplash.getSplash().hide();
	}

	public void actionPerformed(ActionEvent arg0) {
	}
}
