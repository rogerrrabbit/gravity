package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import tools.Pair;
import game.Game;
import gravity.*;

public class UniversePanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private static final long frameStep = 16;
	//private static final long computeStep = 16;
	
	private Thread bigBang;
	//private Thread computing;

	private DebugInfo gameObjectsInfo;
	private DebugInfo gameStatusInfo;
	private DebugInfo gameFPSInfo;
	
	private Pair gameStatusInfoLocation;
	
	public UniversePanel(Dimension dim, int players_count) {
		super();
		setSize(dim);

		gameStatusInfo  = new DebugInfo(0, "", Color.WHITE);
		gameObjectsInfo = new DebugInfo(1, "", Color.WHITE);
		gameFPSInfo = new DebugInfo(2, "", Color.WHITE);
		gameStatusInfoLocation = new Pair(5, 10);
				
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Dimension newSize = e.getComponent().getSize();
				setSize(newSize);
				Game.getInstance().setDimensions(newSize);
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				Game.getInstance().onClick(ev);
			}
		});
	}

	public void init() {
		System.out.println("Loading engine...");
		Space.getInstance().setGameSpeed(Game.getInstance().getSimulationSpeed());
		
		/*computing = new Thread() {
		long now = System.currentTimeMillis(), delta = 0;
		
		public void run() {
			for(;;) {
				delta = -now + (now = System.currentTimeMillis());
				if (run) {
					//Space.getInstance().run(Game.getInstance().simulationSpeed, Game.getInstance().friction);
					repaint();
				}
				
			    if(delta < computeStep) {
			        try {
			            Thread.sleep(computeStep-delta);
			            now += computeStep-delta;
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
			    }
			}
		}*/
		
		bigBang = new Thread(this);
		repaint();
	}
	
	public void paintComponent(Graphics g)
	{
		Game currentGame = Game.getInstance();
		Graphics2D g2 = (Graphics2D) g;
		
		super.paintComponent(g);

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		AffineTransform af = g2.getTransform();
		
		/* scaling space graphics */
		g2.translate(getWidth()/2, getHeight()/2);
		g2.scale(currentGame.getScale(), currentGame.getScale());
		g2.translate(-getWidth()/2, -getHeight()/2);
		
		/* shifting space graphics */
		g2.translate(currentGame.getCameraOffset().fx, currentGame.getCameraOffset().fy);
		
		/* drawing space graphics */
		currentGame.drawBackground(g);
		Space.getInstance().drawSpace(g);
		
		g2.setTransform(af);
		
		/* NOT scaled overlay */
		currentGame.drawOverlay(g);
		
		/* DEBUG Information */
		if (currentGame.hasHud()) {
			gameObjectsInfo.setInformation(String.format("%d objects, %d special effects (x%.1f speed), x%.1f zoom", Space.getInstance().getObjectCount(), Space.getInstance().getSpecialEffectsCount(), currentGame.getSimulationSpeed(), currentGame.getScale()));
			gameObjectsInfo.paintGraphics(g,gameStatusInfoLocation);
			gameStatusInfo.paintGraphics(g, gameStatusInfoLocation);
			gameFPSInfo.paintGraphics(g, gameStatusInfoLocation);
		}
	}

	public void start()
	{
		bigBang.start();
		//computing.start();
		togglePause();
	}
	
	public void setStatus(String information) {
		gameStatusInfo.setInformation(information);
	}
	
	public void updateStatus () {
		if (!Game.getInstance().isPaused()) {
			gameStatusInfo.setInformation("Playing");
		} else {
			gameStatusInfo.setInformation("Ready");
		}
	}

	public void togglePause() {
		Game.getInstance().togglePause();
		updateStatus();
	}

	public void run() 
	{
		long now = System.currentTimeMillis(), delta = 0;
		long lastFPS = now;
		int FPS = 0;
		
		for(;;) {
			delta = -now + (now = System.currentTimeMillis());
			if (!Game.getInstance().isPaused()) {
				Space.getInstance().run();
				Game.getInstance().run();
			}
			if (now - lastFPS > 1000) {
				gameFPSInfo.setInformation(String.format("%d FPS", FPS*1000/(now - lastFPS)));
				lastFPS = now;
				FPS = 0;
			}
			FPS++;

			repaint();
			
		    if(delta < frameStep) {
		        try {
		            Thread.sleep(frameStep-delta);
		            now += frameStep-delta;
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}
}
