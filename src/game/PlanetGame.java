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
import graphics.Cross;
import graphics.DebugInfo;
import graphics.Gauge;
import graphics.SpecialEffect;
import gravity.Mass;
import gravity.Space;
import tools.Pair;
import vehicles.Shuttle;
import vehicles.Vessel;

public class PlanetGame extends Game {

	private static final long serialVersionUID = -8124825021825078767L;

	private static final int playersCount = 1;
    private static final int gaugesCount = 3;
	public DebugInfo[] player_info;
    
    private Planet planet;
    
	protected Pair cameraTarget;
	
	public PlanetGame() {
		super();
		Dimension dim = new Dimension(32000, 32000);
		
		cameraTarget = new Pair();
		
		pilotable = new Vessel[playersCount];
		player_info = new DebugInfo[playersCount];
				
		gauges = new Gauge[playersCount][gaugesCount];
		gaugesLocations = new Pair[playersCount];

		for (int p=0; p<playersCount; p++) {
			gaugesLocations[p] = new Pair();
			gauges[p][0] = new Gauge(Color.GREEN, 0, "");
			gauges[p][1] = new Gauge(Color.BLUE, 0, "");
			gauges[p][2] = new Gauge(Color.RED, 0, "");
		}
		
		planet = new LovePlanet(dim.width/2, dim.height/2);
		planet.setDiameter(800);
		
		Space.getInstance().setDimensions(dim);
		Space.getInstance().attachVisibleObject(new SpecialEffect(
				new Cross(Color.RED),
				new Pair(0, 0)));
		Space.getInstance().attachVisibleObject(new SpecialEffect(
				new Cross(Color.WHITE, 10),
				new Pair(dim.width/2, dim.height/2)));
		Space.getInstance().attachVisibleObject(new SpecialEffect(
				new Cross(Color.RED),
				new Pair(dim.width, dim.height)));
	}

	public void drawBackground(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Space.getInstance().getDimensions().width,
				   		 Space.getInstance().getDimensions().height);
	}
	
	public void load() {
		Space.getInstance().insertMass(planet);
		
		for (int i=0; i<1200; ++i) {
			Asteroid a = new Asteroid(
					(int)(Math.random()*Space.getInstance().getDimensions().width),
					(int)(Math.random()*Space.getInstance().getDimensions().height),
					(float)(-Math.log(Math.random())*10));
			a.setSpin(((float)Math.random()-0.5f)*0.025f);
			a.incSpeed(((float)Math.random()-0.5f)*2, ((float)Math.random()-0.5f)*2);
			Space.getInstance().insertMass(a);
		}
		
		cameraTarget.add(cameraOffset);
		setPauseState(true);
	}
	
	public void onVesselEvent(Vessel vessel, Vessel.VesselEvents e) {
		/* Gauges */
		if (e == Vessel.VesselEvents.VESSEL_REMOVE) {
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
		
		Mass m = new Shuttle((int)clic.fx, (int)clic.fy);
		Space.getInstance().insertMass(m);
		
		switch (ev.getButton()) {
			case MouseEvent.BUTTON1:
				if (m instanceof Vessel) {
					attachVessel(0, (Vessel)m);
				}
				break;
		}
	}
	
	public Pair getCameraOffset() {
		// center on planet
		if (pilotable[0] != null) {
			centerCamera(new Pair(
					(planet.getLocationX() + pilotable[0].getLocationX())/2,
					(planet.getLocationY() + pilotable[0].getLocationY())/2));
		} else {
			centerCamera(planet.getLocation());
		}
		
		cameraTarget.add(
				(cameraOffset.fx - cameraTarget.fx)/10,
				(cameraOffset.fy - cameraTarget.fy)/10);
		
		return cameraTarget;
	}
	
	public void setDimensions(Dimension dim) {
		gameDimensions = dim;

		/* updating HUD elements location */
		gaugesLocations[0].set(15, dim.height-15);
	}
}
