package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import tools.Pair;
import vehicles.Vessel;
import vehicles.Vessel.VesselEvents;
import vehicles.VesselObserver;
import graphics.Gauge;
import gravity.Space;

/**
 * Empty game.
 * @author Vincent
 *
 */
public abstract class Game implements Serializable, VesselObserver, Runnable  {

    private static final long serialVersionUID = 4848344933582523392L;

    private float simulationSpeed = 1f;

	private boolean run = false;
	private boolean hasHud = true;
	
	protected int inputTimer = 30;
	
	protected Pair cameraOffset;
	protected Pair gaugesLocations[];

	public Vessel[] pilotable;
	public Gauge[][] gauges;
	
	protected float gameScale = 1f;
	protected Dimension gameDimensions;

	private static volatile Game instance = null;

	public static enum Controls {
		CONTROL_LEFT,
		CONTROL_RIGHT,
		CONTROL_FORWARD,
		CONTROL_BACKWARD,
		CONTROL_FIRE0,
		CONTROL_FIRE1,
		CONTROL_SWITCH,
		CONTROL_START,
		CONTROL_SELECT,
	};
	
	public static void toMenu() {
		synchronized (Space.class) {
		    Space.getInstance().removeAllNow();
			Space.getInstance().run();
			swapGame(new NoGame()).load();
		}
	}

	public Pair toSpaceCoordinates(Pair source) {
		Pair dest = new Pair(source);

		dest.add(-getWidth()/2, -getHeight()/2);
		dest.factor(1/getScale());
		dest.add(-getCameraOffset().fx, -getCameraOffset().fy);
		dest.add(getWidth()/2, getHeight()/2);
		
		return dest;
	}
	
	protected void centerCamera(Pair location) {
		setCameraOffset((int)((gameDimensions.width/2) - location.fx),
		        (int)((gameDimensions.height/2) - location.fy));
	}
	
	protected Game() {
		cameraOffset = new Pair();
		gameDimensions = new Dimension();
	}
	
	/**
	 * Initialize graphics and stuff when (re)loading game
	 */
	public void init() {
		/* loading background etc. */
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
				toMenu();
				inputTimer += 15;
				break;
			default:
				break;
		}
	}
	
	/**
	 * Handle given gamepad button
	 * @param padId
	 * @param controlUp
	 */
	public void handleButton(int padId, Controls controlUp) {
	}
	
	/**
	 * Pause/resume game
	 */
	public void togglePause() {
		setPauseState(!run);
	}
	public boolean isPaused() {
		return (run == false);
	}
	public void setPauseState(boolean pause) {
		run = pause;
	}
	
	public final static Game getInstance() {
		if (Game.instance == null) {
			synchronized(Game.class) {
				if (Game.instance == null) {
					Game.instance = new NoGame();
				}
			}
		}
		return Game.instance;
	}

	/** Here we should handle whether the player clicked on playable area or
	 * on some HUD buttons.
	 * @param ev
	 */
	public void onClick(MouseEvent ev) {
	}
	
	/**
	 * Load Game level
	 */
	public void load() {
	}
	
	/**
	 * Un-load Game level
	 */
	public void unload() {
	}
	
	public void reload() {
		unload();
		Space.getInstance().removeAllNow();
		load();
	}
	
	public final static Game swapGame(Game g) {
		synchronized(Game.class) {
			Dimension prevDimensions = null;
			if (instance != null) {
				instance.unload();
				prevDimensions = instance.gameDimensions;
			}
			instance = g;
			if (prevDimensions != null) {
				instance.setDimensions(prevDimensions);
			}
			g.init();
		}
		return g;
	}
	
	public void drawBackground(Graphics g) {
	}
	
	public synchronized void drawOverlay(Graphics g) {
	}
	
	public void setDimensions(Dimension dim) {
		gameDimensions = dim;
	}
	
	protected int getWidth() {
		return gameDimensions.width;
	}
	
	protected int getHeight() {
		return gameDimensions.height;
	}

	public float getSimulationSpeed() {
		return simulationSpeed;
	}

	public void incSimulationSpeed(float speed) {
		simulationSpeed += speed;
		Space.getInstance().setGameSpeed(simulationSpeed);
	}

    public float getScale() {
		return gameScale;
	}

	public void setScale(float scale) {
		this.gameScale = scale;
	}

	public Pair getCameraOffset() {
		return cameraOffset;
	}

	public void setCameraOffset(Pair cameraOffset) {
		this.cameraOffset = cameraOffset;
	}
	
	public void setCameraOffset(int i, int j) {
		setCameraOffset(new Pair(i, j));
	}
	
	public void incCameraOffsetX(int offset) {
		this.cameraOffset.fx += offset;
	}
	
	public void incCameraOffsetY(int offset) {
		this.cameraOffset.fy += offset;
	}
	
	public void onVesselEvent(Vessel v, VesselEvents e) {
		
	}

	public void run() {
		/* update input jigger */
		if (inputTimer > 0) {
			inputTimer--;
		}
	}

	public boolean hasHud() {
		return hasHud;
	}

	public void setHud(boolean hasHud) {
		this.hasHud = hasHud;
	}
}
