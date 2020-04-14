package game;

/** 
 * Spawns a big Planet and centers the camera between the active vessel and the planet
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import objects.Asteroid;
import objects.LovePlanet;
import objects.Planet;
import objects.Satellite;
import graphics.DebugInfo;
import graphics.DialogBox;
import graphics.Gauge;
import graphics.ImageCache;
import graphics.SpecialEffect;
import graphics.Sprite;
import gravity.Body;
import gravity.Mass;
import gravity.Space;
import sounds.SoundEffect;
import tools.Pair;
import vehicles.Tacos;
import vehicles.Vessel;

public class DuelGame extends Game {

	private static final long serialVersionUID = -8124825021825078767L;

	private Dimension dim;
	
	private static final int playersCount = 2;
    private static final int gaugesCount = 3;
    private Sprite background = null;
	public DebugInfo[] player_info;

    private Planet planet;
    
    private boolean gameOver = false;
    
	protected Pair cameraTarget;
	private SoundEffect se;
	
	public DuelGame() {
		super();
		dim = new Dimension(4000, 4000);
		
		cameraTarget = new Pair();
		
		pilotable = new Vessel[playersCount];
		player_info = new DebugInfo[playersCount];
		
		background = new Sprite(ImageCache.Sprites.BACKGROUND_HUBBLE);
				
		gauges = new Gauge[playersCount][gaugesCount];
		gaugesLocations = new Pair[gaugesCount];

		for (int p=0; p<playersCount; p++) {
			gaugesLocations[p] = new Pair();
			gauges[p][0] = new Gauge(Color.GREEN, 0, "");
			gauges[p][1] = new Gauge(Color.BLUE, 0, "");
			gauges[p][2] = new Gauge(Color.RED, 0, "");
		}
		
		Space.getInstance().setDimensions(dim);
		setScale(1.75f);
	}

	public void drawBackground(Graphics g) {
		background.draw(g,
				Space.getInstance().getDimensions().width/2,
				Space.getInstance().getDimensions().height/2);
	}
	
	public void load() {
		/* adding the planet */
		planet = new LovePlanet(dim.width/2, dim.height/2);
		planet.setDiameter(100);
		Space.getInstance().insertMass(planet);

		/* adding some dust */
		for (int i=0; i<700; ++i) {
			Body a;
			if (Math.random() > 0.99f) {
				a = new Satellite(
						(int)(Math.random()*Space.getInstance().getDimensions().width),
						(int)(Math.random()*Space.getInstance().getDimensions().height));
				a.setSpin(((float)Math.random()-0.5f)*0.050f);
				a.incSpeed(((float)Math.random()-0.5f)*12, ((float)Math.random()-0.5f)*12);
			} else {
				a = new Asteroid(
						(int)(Math.random()*Space.getInstance().getDimensions().width),
						(int)(Math.random()*Space.getInstance().getDimensions().height),
						(float)(-Math.log(Math.random())*10));
				a.setSpin(((float)Math.random()-0.5f)*0.025f);
				a.incSpeed(((float)Math.random()-0.5f)*2, ((float)Math.random()-0.5f)*2);
			}
			Space.getInstance().insertMass(a);
		}
		
		cameraTarget.add(cameraOffset);
		centerCamera(planet.getLocation());
			
		/* adding two players */
		Mass m1 = new Tacos(dim.width/2 - 300 , dim.height/2);
		Mass m2 = new Tacos(dim.width/2 + 300, dim.height/2);
		Space.getInstance().insertMass(m1);
		Space.getInstance().insertMass(m2);
		attachVessel(0, (Vessel)m1);
		attachVessel(1, (Vessel)m2);
		
		/* playing some music */
		se = new SoundEffect("/havok_main.mp3");
		se.setLoop(true);
		se.play();

		setPauseState(true);
		gameOver = false;
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
						SpecialEffect se = new SpecialEffect(new DialogBox(
								Color.WHITE,
								String.format("Player %d died", pilotableIndex + 1))
							.setTimeOut(180), new Pair(dim.width/2, dim.height/2));
						Space.getInstance().attachVisibleObject(se);
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
			pilotable[slot].removeVisibleObject(player_info[slot]);
		}
		pilotable[slot] = v;
		v.addFuel(1000);
		v.addVisibleObject(player_info[slot] = new DebugInfo(-2, String.format("%d", slot+1), Color.RED));
		
		gauges[slot][0].setVisible(true);
		gauges[slot][1].setVisible(true);
		gauges[slot][2].setVisible(true);
		gauges[slot][0].setMaxValue(v.getFuelCapacity());
		gauges[slot][1].setMaxValue(v.getShieldCapacity());
		gauges[slot][2].setMaxValue(v.getMaxThrottle());
		
		v.addObserver(this);
		v.start();
	}
	
	public void init() {
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
				gameOver = true;
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
		Pair clic = toSpaceCoordinates(new Pair(ev.getX(),ev.getY()));
		Mass m = new Tacos((int)clic.fx, (int)clic.fy);
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
	
	/*public Pair getCameraOffset() {
		// center on the center of the two pilots
		if (pilotable[0] != null && pilotable[1] != null) {
			centerCamera(new Pair(
					(pilotable[1].getLocationX() + pilotable[0].getLocationX())/2,
					(pilotable[1].getLocationY() + pilotable[0].getLocationY())/2));
		} else if (pilotable[0] != null) {
			centerCamera(pilotable[0].getLocation());
		} else if (pilotable[1] != null) {
			centerCamera(pilotable[1].getLocation());
		}
		
		cameraTarget.add(
				(cameraOffset.fx - cameraTarget.fx)/10,
				(cameraOffset.fy - cameraTarget.fy)/10);
		
		return cameraTarget;
	}*/
	
	public void unload() {
		se.stop();
	}
	
	public void setDimensions(Dimension dim) {
		gameDimensions = dim;

		/* updating HUD elements location */
		gaugesLocations[0].set(15, dim.height-15);
		gaugesLocations[1].set(dim.width-45, dim.height-15);
	}
	
	public void run() {
		Space thisSpace = Space.getInstance();
		Dimension dim = thisSpace.getDimensions();
		Body b = null;

		if (gameOver) {
			reload();
		}

		Pair loc = new Pair((int)(Math.random()*dim.getWidth()), (int)(Math.random()*dim.getHeight()));
		if ((loc.fx < 500.f || loc.fx > (dim.width - 500.f)) &&
			(loc.fy < 500.f || loc.fy > (dim.height - 500.f))) {
			b = new Asteroid(loc.fx, loc.fy);
			b.setSpin(((float)Math.random()-0.5f)*0.025f);
			b.incSpeed((float)(Math.random()-0.5f)*10, ((float)Math.random()-0.5f)*10);
			Space.getInstance().insertMass(b);
		}
		
		if (inputTimer > 0) {
			inputTimer--;
		}
	}
}
