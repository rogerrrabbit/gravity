package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import brains.BasicDefenseIA;
import objects.Asteroid;
import graphics.DebugInfo;
import graphics.Gauge;
import graphics.ImageCache;
import graphics.VesselSelector;
import graphics.Sprite;
import gravity.Body;
import gravity.Mass;
import gravity.Space;
import tools.Pair;
import vehicles.Vessel;

public class SandboxGame extends Game {

	private static final long serialVersionUID = 4481168669906616997L;

	private static final int playersCount = 2;
    private static final int gaugesCount = 3;
	public DebugInfo[] player_info;
	
	public VesselSelector selector;
    private Pair selectorLocation;
	private Sprite background;
	
	public SandboxGame() {
		super();
		
		pilotable = new Vessel[playersCount];
		player_info = new DebugInfo[playersCount];
		background = new Sprite(ImageCache.Sprites.BACKGROUND_PURPLE);
		
		gauges = new Gauge[playersCount][gaugesCount];
		gaugesLocations = new Pair[playersCount];

		for (int p=0; p<playersCount; p++) {
			gaugesLocations[p] = new Pair();
			gauges[p][0] = new Gauge(Color.GREEN, 0, "");
			gauges[p][1] = new Gauge(Color.BLUE, 0, "");
			gauges[p][2] = new Gauge(Color.RED, 0, "");
		}
		
		selector = new VesselSelector(Color.WHITE);
		selector.setVisible(true);
		selectorLocation = new Pair();

		Space.getInstance().setDimensions(1908, 974);
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
		for (int i=0; i<1200; ++i) {
			Asteroid a = new Asteroid((int)(Math.random()*dim.getWidth()), (int)(Math.random()*dim.getHeight()));
			a.setSpin(((float)Math.random()-0.5f)*0.025f);
			a.incSpeed((float)(Math.random()-0.5f)*2, ((float)Math.random()-0.5f)*2);
			Space.getInstance().insertMass(a);
		}
		/*Space.getInstance().insertMass(new SpaceDock(getWidth()/2, getHeight()/2, 0.01f));*/
		//Space.getInstance().insertMass(new Planet((int)(getWidth()/1.5), getHeight()/2));
		/*Space.getInstance().insertMass(new BlackHole(getWidth()/2, getHeight()/2));
		Space.getInstance().insertMass(new Asteroid(getWidth(), getWidth(), 30));
		*/
		setPauseState(true);
	}
	
	public void onVesselEvent(Vessel vessel, Vessel.VesselEvents e) {
		/* Gauges */
		if (e == Vessel.VesselEvents.VESSEL_REMOVE) {
			System.out.println("Vessel removed.");
			int pilotableIndex=0;
			synchronized (pilotable) {
				for (Vessel v : pilotable) {
					if (v == vessel) {
						gauges[pilotableIndex][0].setVisible(false);
						gauges[pilotableIndex][1].setVisible(false);
						pilotable[pilotableIndex] = null;
					}
					pilotableIndex++;
				}
			}
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
				
		/* Object Selector */
		selector.paintGraphics(g, selectorLocation);
	}
	
	private void nextSelectedObject() {
		selector.next();
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
		/* initializing selector pictures */
		selector.init();
	}
	
	public void handleKey(int key) {
		switch (key) {
			case(KeyEvent.VK_N):
				nextSelectedObject();
				break;
			case(KeyEvent.VK_ESCAPE):
				toMenu();
		}
	}
	
	public void handleButton(int padId, Controls buttonId) {
		if (padId >= playersCount) {
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
		
		Mass m = selector.insertSelected(clic.fx, clic.fy);

		switch (ev.getButton()) {
			case MouseEvent.BUTTON1:
				if (m instanceof Vessel) {
					((Vessel)m).addIA(new BasicDefenseIA((Body)m));
					attachVessel(0, (Vessel)m);
				}
				break;
			case MouseEvent.BUTTON3:
				if (m instanceof Vessel) {
					((Vessel)m).addIA(new BasicDefenseIA((Body)m));
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
		selectorLocation.set(dim.width-65, 15);
	}
}
