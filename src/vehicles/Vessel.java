package vehicles;

import java.awt.Color;
import java.util.ArrayList;

import objects.Heart;
import brains.BodyIA;
import tools.Pair;
import graphics.DebugInfo;
import gravity.*;

public abstract class Vessel extends Body {

	private static final long serialVersionUID = -8068577966332467103L;
	private static final float fuelDensity = .01f;
	public static boolean debug = false;
	
	private float inertiaCompensatorRatio = 1f;
	private boolean inertiaCompensatorSet = false;
	private boolean inertiaCompensatorActive = false;
	
	protected int inputTimer = 0;
	
	private DebugInfo debugMsg;
		
	private transient Thread travel;
	private ArrayList<VesselObserver> observers;
	
	public enum VesselEvents {
		VESSEL_REMOVE,
		VESSEL_NO_FUEL,
		VESSEL_DOCKED,
	};
	
	private ArrayList<BodyIA> IAs;
	
	private float fuelLevel = 0;
	private float shieldLevel = 0;
	private float damageLevel = 0;
	
	private float fuelCapacity = 0;
	private float damageCapacity = 0;
	private float shieldCapacity = 0;

	private float throttleX = 0;
	private float throttleY = 0;
	private float throttleZ = 0;
	private float throttleS = 0;
	private float maxThrottle = 1f;
	
	public Vessel(float mass, int radius, float orientation, float fuelC, float initSpeedX, float initSpeedY) {
		super(mass, radius, orientation, initSpeedX, initSpeedY);
		fuelCapacity = fuelC;

		if (debug) {
			addVisibleObject(debugMsg = new DebugInfo(1)).setVisible(true);
			debugMsg.setTextColor(Color.GREEN);
		}

		observers = new ArrayList<VesselObserver>();
		IAs = new ArrayList<BodyIA>();
		travel = null;
	}

	private void notifyObservers(VesselEvents e) {
		if (observers == null) return;
		
		for (VesselObserver o : observers) {
			o.onVesselEvent(this, e);
		}
	}
	
	public boolean addObserver(VesselObserver e) {
		return observers.add(e);
	}
	
	public Vessel(float mass, int radius, float orientation, float capacity) {
		this(mass, radius, orientation, capacity, 0, 0);
	}
	
	protected void onRemove() {
		if (travel != null) {
			travel.interrupt();
		}
		for (BodyIA ia : IAs) {
			unsetIA(ia);
		}
		IAs.clear();
		notifyObservers(VesselEvents.VESSEL_REMOVE);
	}

	protected void onDocked() {
		setSpeed(0,0);
		setSpin(0);
		setDocked(true);
		notifyObservers(VesselEvents.VESSEL_DOCKED);
	}

	/**
	 * Put all throttle triggers to zero
	 */
	protected void resetThrottle() {
		throttleX=0;
		throttleY=0;
		throttleZ=0;
		throttleS=0;
	}
	
	/**
	 * Increase horizontal throttle trigger value
	 * @param power
	 */
	protected void throttleX(float power) {
		throttleX += power;
	}
	
	/**
	 * Increase vertical throttle trigger value
	 * @param power
	 */
	protected void throttleY(float power) {
		throttleY += power;
	}
	
	/**
	 * Increase spin throttle trigger value
	 * @param power
	 */
	protected void throttleZ(float power) {
		throttleZ += power;
	}	

	/**
	 * Increase shield engine throttle trigger value
	 * @param power
	 */
	protected void throttleS(float power) {
		throttleS += power;
	}	
	
	/**
	 * Turn vessel's inertia compensator on/off
	 */
	public void toggleInertia() {
		if (inputTimer > 0) {
			return;
		}
		
		if (inertiaCompensatorSet) {
			System.out.println("toggleInertia:off");
			inertiaCompensatorSet = false;
			inertiaCompensatorActive = false;
			setInertia(1f);
		} else {
			System.out.println("toggleInertia:on");	
			inertiaCompensatorSet = true;
		}
		inputTimer += 15;
	}
	
	/**
	 * Fill the fuel tank, limited to the tank capacity
	 * @param fill quantity to add
	 */
	public void addFuel(float fill) {
		if (fuelLevel + fill > fuelCapacity) {
			fill = fuelCapacity - fuelLevel;
		}
		fuelLevel += fill;
		setMass(getMass()+(fill*fuelDensity));
	}

	/**
	 * Burn some Vessel's fuel, for some reason (like moving, or just for fun)
	 * @param fuel volume of fuel to burn
	 * @return whether there is still fuel in the tank after this operation
	 */
	protected boolean burnFuel(float fuel) {
		if(fuelLevel != 0) {
			if (fuelLevel > fuel) {
				fuelLevel -= fuel;
			} else {
				fuelLevel = 0;
			}
			setMass(getMass()-(fuel*fuelDensity));
		}
		return (fuelLevel!=0);
	}
	
	public float getFuelLevel() {
		return fuelLevel;
	}
	
	public float getThrottle() {
		return Math.min(Math.abs(throttleX) + Math.abs(throttleY) + Math.abs(throttleZ) + Math.abs(throttleS),
						maxThrottle);
	}

	/**
	 * Use fuel to thrust the Vessel according to the current throttle values
	 * @return whether there was enough fuel left to thrust the Vessel 
	 */
	protected boolean powerVessel() {
		float excess = 0;
		float throttle3D = Math.abs(throttleX) + Math.abs(throttleY) + Math.abs(throttleZ);
		float fuel = throttle3D + Math.abs(throttleS);

		if (isInertiaCompensatorActive()) {
			fuel+=0.25f;
		}
		
		if (fuel > maxThrottle) {
			excess = fuel - maxThrottle;
		}
		
		if (fuel == 0) {
			return false;
		}
		
		if (burnFuel(fuel)) {
			if (isInertiaCompensatorSet()) {
				if (throttle3D > 0.01f) {
					inertiaCompensatorActive = false;
					setInertia(1f);
				} else {
					inertiaCompensatorActive = true;
					setInertia(inertiaCompensatorRatio);
				}
			}
			shieldLevel += throttleS - (throttleS/fuel)*excess;
			incSpin(throttleZ - (throttleZ/fuel)*excess);
			incSpeed(new Pair(throttleX - (throttleX/fuel)*excess,
					          throttleY - (throttleY/fuel)*excess).rotateCoordinates(getHeading()));
			return true;
		} else {
			if (debug) {
				debugMsg.setTextColor(Color.GRAY);
			}
			inertiaCompensatorActive = false;
			setInertia(1f);
			onTankEmpty();
		}

		return false;
	}
	
	public void run() {
		while(true) {
			/* perform all the things that need power */
			if(powerVessel() && debug) {
				debugMsg.setInformation(String.format("F:%.2f;S:%.2f;Tx:%.4f;Ty:%.4f;Tz:%.4f;Ts:%.4f",
					getFuelLevel(), getShieldLevel(), getThrottleX(), getThrottleY(), getThrottleZ(), getThrottleS()));
			}

			/* IA processing */
			/*for (BodyIA ia : IAs) {
				ia.performActions();
			}*/
			
			/* vessel specific routines */
			onTimeCycle();
			
			try {
				Thread.sleep(computeStep);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			
			/* update input jigger */
			if (inputTimer > 0) {
				inputTimer--;
			}
		}
	}

	protected boolean absorbEnergy(Body m) {
		if (m instanceof SpaceDock) {
			return false;

		} else if (m instanceof Heart) {
			/* restores life/fuel */
			if (fuelLevel < fuelCapacity) {
				fuelLevel += Math.min(100.f, fuelCapacity - fuelLevel);
			}

		} else {
			/* Can this mass be absorbed by the shield (if any) ? */
			/* Or will the crew die in the vessel explosion */
			float inboundEnergy = m.getEnergy();
			if (inboundEnergy <= shieldLevel) {
				shieldLevel -= inboundEnergy;
				onTakeDamage();
			} else if (damageLevel >= damageCapacity) {
				Space.getInstance().removeMass(this);
			}
		}

		return true;
	}

	protected abstract void onFreeze();
	protected abstract void onTankEmpty();
	protected abstract void onTimeCycle();

	protected void onTakeDamage() {
	}

	public void start() {
		if (travel == null) {
			travel = new Thread(this);
		}
		travel.start();
		for (BodyIA ia : IAs) {
			ia.start();
		}
	}

	public void stop() {
		if (debug) {
			debugMsg.setTextColor(Color.GRAY);
		}
		for (BodyIA ia : IAs) {
			unsetIA(ia);
		}
		onFreeze();
	}

	public abstract void goLeft(int units);
	public abstract void goRight(int units);
	public abstract void goForward(int units);
	public abstract void goBackward(int units);
	
	protected float getThrottleX() {
		return throttleX;
	}

	protected float getThrottleY() {
		return throttleY;
	}

	protected float getThrottleZ() {
		return throttleZ;
	}
	
	protected float getThrottleS() {
		return throttleS;
	}
	
	public float getShieldLevel() {
		return shieldLevel;
	}

	protected void setShieldLevel(float shieldLevel) {
		this.shieldLevel = shieldLevel;
	}
	
	public Vessel dropVessel() {
		return null;
	}
	
	public boolean fire(int id) {
		return false;
	}
	
	protected float getEnergyShield() {
		return Math.max(shieldLevel, getMass());
	}
	
	protected void setThrottleS(float throttleS) {
		this.throttleS = throttleS;
	}

	public float getFuelCapacity() {
		return fuelCapacity;
	}

	public float getShieldCapacity() {
		return shieldCapacity;
	}

	protected void setShieldCapacity(float shieldCapacity) {
		this.shieldCapacity = shieldCapacity;
	}

	protected float getDamageLevel() {
		return damageLevel;
	}

	protected void setDamageLevel(float damageLevel) {
		this.damageLevel = damageLevel;
	}

	protected float getDamageCapacity() {
		return damageCapacity;
	}

	protected void setDamageCapacity(float damageCapacity) {
		this.damageCapacity = damageCapacity;
	}

	public synchronized void addIA(BodyIA iA) {
		IAs.add(iA);
		iA.start();
	}
	
	public synchronized void unsetIA(BodyIA iA) {
		iA.stop();
	}

	public void setInertiaCompensatorRatio(float inertiaCompensatorRatio) {
		this.inertiaCompensatorRatio = inertiaCompensatorRatio;
	}
	
	public float getMaxThrottle() {
		return maxThrottle;
	}

	public void setMaxThrottle(float maxThrottle) {
		this.maxThrottle = maxThrottle;
	}
	
	public boolean isInertiaCompensatorSet() {
		return inertiaCompensatorSet;
	}
	
	public boolean isInertiaCompensatorActive() {
		return inertiaCompensatorActive;
	}
}
