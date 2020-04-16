package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import objects.Asteroid;
import objects.Chips;
import objects.EvilChips;
import objects.LovePlanet;
import objects.Planet;
import objects.Satellite;
import graphics.Cross;
import graphics.Gauge;
import graphics.ImageCache;
import graphics.SpecialEffect;
import graphics.Sprite;
import gravity.Mass;
import gravity.Space;
import sounds.SoundEffect;
import tools.Pair;
import vehicles.Shuttle;
import vehicles.Tacos;
import vehicles.Vessel;

public class TacosGame extends Game {

	private static final long serialVersionUID = 4481168669906616997L;

	private static final int playersCount = 2;
    private static final int gaugesCount = 3;
	private Sprite background = null;
	
	protected float scaleTarget = 1.f;
	protected Pair cameraTarget;
	
	private SoundEffect se;
	
    private boolean gameOver = false;
	
	public TacosGame() {
		super();
		
		cameraTarget = new Pair();
		
		pilotable = new Vessel[playersCount];
		background = new Sprite(ImageCache.Sprites.BACKGROUND_PURPLE);
		
		gauges = new Gauge[playersCount][gaugesCount];
		gaugesLocations = new Pair[playersCount];

		for (int p=0; p<playersCount; p++) {
			gaugesLocations[p] = new Pair();
			gauges[p][0] = new Gauge(Color.GREEN, 0, "");
			gauges[p][1] = new Gauge(Color.BLUE, 0, "");
			gauges[p][2] = new Gauge(Color.RED, 0, "");
		}
		
		Space.getInstance().setDimensions(4096, 4096);
	}

	public void drawBackground(Graphics g) {
		if (background != null) {
			background.draw(g,
					Space.getInstance().getDimensions().width/2,
					Space.getInstance().getDimensions().height/2);
		} else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
	
	public void load() {
		Dimension dim = Space.getInstance().getDimensions();
		Game.getInstance().setScale(0.5f);

		Space.getInstance().attachVisibleObject(new SpecialEffect(
				new Cross(Color.RED),
				new Pair(0, 0)));
		Space.getInstance().attachVisibleObject(new SpecialEffect(
				new Cross(Color.RED),
				new Pair(dim.width, dim.height)));
		Space.getInstance().attachVisibleObject(new SpecialEffect(
				new Cross(Color.RED),
				new Pair(0, dim.height)));
		Space.getInstance().attachVisibleObject(new SpecialEffect(
				new Cross(Color.RED),
				new Pair(dim.width, 0)));
		
		for (int i=0; i<400; ++i) {
			Asteroid a = new Asteroid((int)(Math.random()*dim.getWidth()), (int)(Math.random()*dim.getHeight()));
			a.setSpin(((float)Math.random()-0.25f)*0.025f);
			a.incSpeed((float)(Math.random()-0.5f), ((float)Math.random()-0.5f));
			Space.getInstance().insertMass(a);
		}
		for (int i=0; i<5; ++i) {
			Planet a = new LovePlanet((int)(Math.random()*dim.getWidth()), (int)(Math.random()*dim.getHeight()));
			a.setDiameter((int)(Math.random()*884));
			a.incSpeed((float)(Math.random()-0.5f), ((float)Math.random()-0.5f));
			Space.getInstance().insertMass(a);
		}
		for (int i=0; i<10; ++i) {
			Satellite a = new Satellite((int)(Math.random()*dim.getWidth()), (int)(Math.random()*dim.getHeight()));
			a.setSpin(((float)Math.random()-0.25f)*0.025f);
			a.incSpeed((float)(Math.random()-0.5f), ((float)Math.random()-0.5f));
			Space.getInstance().insertMass(a);
		}
		for (int i=0; i<50; ++i) {
			Chips a = new Chips((int)(Math.random()*dim.getWidth()), (int)(Math.random()*dim.getHeight()));
			a.setSpin(((float)Math.random()-0.25f)*0.025f);
			a.incSpeed((float)(Math.random()-0.5f), ((float)Math.random()-0.5f));
			Space.getInstance().insertMass(a);
		}
		for (int i=0; i<10; ++i) {
			EvilChips a = new EvilChips((int)(Math.random()*dim.getWidth()), (int)(Math.random()*dim.getHeight()));
			a.setSpin(((float)Math.random()-0.25f)*0.025f);
			a.incSpeed((float)(Math.random()-0.5f), ((float)Math.random()-0.5f));
			Space.getInstance().insertMass(a);
		}
		for (int i=0; i<4; ++i) {
			Shuttle a = new Shuttle((int)(Math.random()*dim.getWidth()), (int)(Math.random()*dim.getHeight()));
			a.setSpin(((float)Math.random()-0.25f)*0.025f);
			a.incSpeed((float)(Math.random()-0.5f), ((float)Math.random()-0.5f));
			Space.getInstance().insertMass(a);
		}
		
		/* play music */
		se = new SoundEffect("/havok_main.mp3");
		se.setLoop(true);
		se.play();
		
		/* adding the tacos */
		Mass m1 = new Tacos(dim.width/2, dim.height/2);
		Space.getInstance().insertMass(m1);
		attachVessel(0, (Vessel)m1);

		cameraTarget.add(cameraOffset);
		setPauseState(true);
		gameOver = false;
	}

	public Pair getCameraOffset() {
		// center on pilot(s)
		if (pilotable[0] != null && pilotable[1] != null) {
			centerCamera(new Pair(
					(pilotable[1].getLocationX() + pilotable[0].getLocationX())/2,
					(pilotable[1].getLocationY() + pilotable[0].getLocationY())/2));
		} else if (pilotable[0] != null) {
			centerCamera(pilotable[0].getLocation());
		} else if (pilotable[1] != null) {
			centerCamera(pilotable[1].getLocation());
		}

		setScale(getScale() + (scaleTarget - getScale())/10);
		
		cameraTarget.add(
				(cameraOffset.fx - cameraTarget.fx)/10,
				(cameraOffset.fy - cameraTarget.fy)/10);
		
		return cameraTarget;
	}

	public void unload() {
		se.stop();
	}

	public void onVesselEvent(Vessel vessel, Vessel.VesselEvents e) {
		/* Gauges */
		if (e == Vessel.VesselEvents.VESSEL_REMOVE && !gameOver) {
			int pilotableIndex=0;
			synchronized (pilotable) {
				for (Vessel v : pilotable) {
					if (v == vessel) {
						gauges[pilotableIndex][0].setVisible(false);
						gauges[pilotableIndex][1].setVisible(false);
						pilotable[pilotableIndex] = null;
						break;
					}
					pilotableIndex++;
				}
			}
			gameOver = true;
		}
	}
	
	public synchronized void drawOverlay(Graphics g) {
		/* Gauges */
		for (int player=0; player<playersCount; player++) {
			synchronized (pilotable) {
				if (pilotable[player] != null) {
					gauges[player][0].setCurValue(pilotable[player].getFuelLevel());
					gauges[player][0].paintGraphics(g, (int)gaugesLocations[player].fx, (int)gaugesLocations[player].fy);
					gauges[player][1].setCurValue(pilotable[player].getShieldLevel());
					gauges[player][1].paintGraphics(g, (int)gaugesLocations[player].fx+15, (int)gaugesLocations[player].fy);
					gauges[player][2].setCurValue(pilotable[player].getThrottle());
					gauges[player][2].paintGraphics(g, (int)gaugesLocations[player].fx+30, (int)gaugesLocations[player].fy);
				}
			}
		}
	}
	
	private void attachVessel(int slot, Vessel v) {
		if (v==null) return;

		if (pilotable[slot] != null) {
			pilotable[slot].stop();
		}
		pilotable[slot] = v;
		v.addFuel(1000);
		
		gauges[slot][0].setVisible(true);
		gauges[slot][1].setVisible(true);
		gauges[slot][2].setVisible(true);
		gauges[slot][0].setMaxValue(v.getFuelCapacity());
		gauges[slot][1].setMaxValue(v.getShieldCapacity());
		gauges[slot][2].setMaxValue(v.getMaxThrottle());
		
		v.addObserver(this);
		v.start();
		
		System.out.println(String.format("Attached vessel to player slot %d", slot));
	}
	
	public void init() {
		/* loading background picture */
		if (background != null) {
			background.init();
		}
		/* starting player threads */
		for (int player=0; player<playersCount; player++) {
			if (pilotable[player] != null) {
				pilotable[player].start();
			}
		}
	}
	
	public void handleButton(int padId, Controls buttonId) {
		if (padId >= playersCount) {
			return;
		}
		
		if (inputTimer > 0) {
			return;
		}
		
		if (pilotable[padId] != null) {
			switch (buttonId) {
			case CONTROL_FORWARD:
    			pilotable[padId].goForward(1);
    			break;
			case CONTROL_BACKWARD:
				pilotable[padId].goBackward(1);
				break;
			case CONTROL_FIRE0:
				pilotable[padId].fire(0);
				break;
			case CONTROL_FIRE1:
				pilotable[padId].fire(1);
				break;
			case CONTROL_LEFT:
				pilotable[padId].goLeft(1);
				break;
			case CONTROL_RIGHT:
				pilotable[padId].goRight(1);
				break;
			case CONTROL_SWITCH:
				pilotable[padId].toggleInertia();
				break;
			case CONTROL_START:
				reload();
				inputTimer += 30;
				break;
			case CONTROL_SELECT:
				toMenu();
				inputTimer += 15;
				break;
			default:
				break;
			}
    	} else {
    		switch (buttonId) {
			case CONTROL_START:
				reload();
				inputTimer += 30;
				break;
			case CONTROL_SELECT:
				toMenu();
				inputTimer += 15;
				break;
			default:
				break;
			}
    	}
	}
	
	public void onClick(MouseEvent ev) {
		Pair clic = new Pair(ev.getX(),ev.getY());
		
		clic.add(-getWidth()/2, -getHeight()/2);
		clic.factor(1/getScale());
		clic.add(-getCameraOffset().fx, -getCameraOffset().fy);
		clic.add(getWidth()/2, getHeight()/2);
		
		Mass m = new Tacos();
		m.setLocation(clic);
		Space.getInstance().insertMass(m);
		
		switch (ev.getButton()) {
			case MouseEvent.BUTTON1:
				if (m instanceof Vessel) {
					attachVessel(0, (Vessel)m);
				}
				break;
			case MouseEvent.BUTTON3:
				if (m instanceof Vessel) {
					attachVessel(1, (Vessel)m);
				}
				break;
		}
	}
	
	public void setDimensions(Dimension dim) {
		gameDimensions = dim;
		
		/* centering display on space center */
		setCameraOffset((int)(getScale()*(dim.width - Space.getInstance().getDimensions().width)/2),
				 		(int)(getScale()*(dim.height - Space.getInstance().getDimensions().height)/2));
		
		/* updating HUD elements location */
		gaugesLocations[0].set(15, dim.height-15);
		gaugesLocations[1].set(dim.width-45, dim.height-15);
	}
	
	public void run() {
		if (gameOver) {
			reload();
		}
		
		if (inputTimer > 0) {
			inputTimer--;
		}
	}
}
