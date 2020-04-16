package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import objects.Chips;
import objects.LovePlanet;
import objects.Planet;
import objects.Satellite;
import sounds.SoundEffect;
import tools.Pair;
import graphics.GameSelector;
//import graphics.ImageCache;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.Body;
import gravity.Space;

/**
 * Empty game
 * @author Vincent
 *
 */
public class NoGame extends Game {

	private static final long serialVersionUID = 450287947476849893L;
	
	private GameSelector selector;
    private Pair selectorLocation;
    private Pair titleLocation;
	private Sprite background;
	private Sprite title;
	private SoundEffect se;
	
	public NoGame() {
		background = new Sprite(Sprites.BACKGROUND_GREEN_HEAVEN);
		title = new Sprite(Sprites.SPRITE_TITLE);
		cameraOffset = new Pair();
		gameDimensions = new Dimension();
		
		Space.getInstance().setDimensions(2048, 2048);
		
		selector = new GameSelector(Color.WHITE);
		selector.setVisible(true);
		selectorLocation = new Pair();
		titleLocation = new Pair();
		title.setVisible(true);
		setHud(false);
	}

	/**
	 * Initialize graphics and stuff when (re)loading game
	 */
	public void init() {
		/* loading background picture */
		if (background != null) {
			background.init();
		}
		/* initializing pictures */
		selector.init();
		title.init();
	}
	
	private void nextSelectedObject() {
		selector.next();
	}
	
	private void prevSelectedObject() {
		selector.prev();
	}
	
	/**
	 * Handle specific keyboard key
	 * @param key
	 */
	public void handleKey(int key) {
		if (inputTimer > 0) {
			return;
		}
		
		switch (key) {
			case(KeyEvent.VK_ESCAPE):
				System.exit(0);
			case(KeyEvent.VK_ENTER):
				synchronized(Space.class) {
					Space.getInstance().removeAll();
					Game.swapGame(selector.getGame()).load();	
				}
				break;
			case(KeyEvent.VK_RIGHT):
			case(KeyEvent.VK_UP):
				nextSelectedObject();
				break;
			case(KeyEvent.VK_LEFT):
			case(KeyEvent.VK_DOWN):
				prevSelectedObject();
				break;
		}
		
		inputTimer += 15;
	}
	
	public void handleButton(int padId, Controls buttonId) {
		if (inputTimer > 0) {
			return;
		}
		
		switch (buttonId) {
			case CONTROL_FORWARD:
			case CONTROL_RIGHT:
				nextSelectedObject();
    			break;
			case CONTROL_BACKWARD:
			case CONTROL_LEFT:
				prevSelectedObject();
				break;
			case CONTROL_FIRE0:
			case CONTROL_START:
				synchronized(Space.class) {
					Space.getInstance().removeAll();
					Game.swapGame(selector.getGame()).load();	
				}
				break;
			case CONTROL_SELECT:
				System.exit(0);
			default:
				break;
    	}
		
		inputTimer += 15;
	}
	
	public void onClick(MouseEvent ev) {
		switch (ev.getButton()) {
		}
	}
	
	public void drawBackground(Graphics g) {
		background.draw(g, Space.getInstance().getDimensions().width/2,
						   Space.getInstance().getDimensions().height/2);
	}
	
	public synchronized void drawOverlay(Graphics g) {
		/* Game Selector */
		selector.paintGraphics(g, selectorLocation);
		
		/* Title */
		title.paintGraphics(g, titleLocation);
	}
	
	public void setDimensions(Dimension dim) {
		selectorLocation.set(dim.width/2 - GameSelector.selectorWidth/2,
						     dim.height/2 - GameSelector.selectorHeight/2);
		titleLocation.set(dim.width/4, dim.height/2);
		gameDimensions = dim;
	}
	
	public void load() {
		Dimension dim = Space.getInstance().getDimensions();
		for (int i=0; i<3; ++i) {
			Planet a = new LovePlanet((int)(Math.random()*dim.width), (int)(Math.random()*dim.height));
			a.setDiameter((int)(Math.random()*884));
			a.incSpeed((float)(Math.random()-0.5f), ((float)Math.random()-0.5f));
			Space.getInstance().insertMass(a);
		}
		setPauseState(true);
		
		/* centering display on space center once */
		setCameraOffset((int)(getScale()*(gameDimensions.width - Space.getInstance().getDimensions().width)/2),
				 		(int)(getScale()*(gameDimensions.height - Space.getInstance().getDimensions().height)/2));
		
		/* play intro music */
		se = new SoundEffect("/havok_intro.mp3");
		se.setLoop(true);
		se.play();
	}
	
	public void unload() {
		se.stop();
	}
	
	public void run() {
		Space thisSpace = Space.getInstance();
		Dimension dim = thisSpace.getDimensions();
		Body b = null;
		
		Pair loc = new Pair((int)(Math.random()*dim.getWidth()), (int)(Math.random()*dim.getHeight()));
		if ((loc.fx < 500.f || loc.fx > (dim.width - 500.f)) &&
			(loc.fy < 500.f || loc.fy > (dim.height - 500.f))) {
			double rnd = Math.random();
			if (rnd > 0.95f) {
				b = new Satellite(loc);
				b.incSpeed((float)(Math.random()-0.5f)*10, ((float)Math.random()-0.5f)*10);
			} else if (rnd > 0.90f){
				b = new Chips(loc);
				b.incSpeed((float)(Math.random()-0.5f)*2, ((float)Math.random()-0.5f)*2);
			}
			
			if (b != null) {
				b.setSpin(((float)Math.random()-0.5f)*0.025f);
				Space.getInstance().insertMass(b);
			}
		}
		
		/* update input jigger */
		if (inputTimer > 0) {
			inputTimer--;
		}
	}
}
