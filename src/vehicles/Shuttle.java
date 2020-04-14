package vehicles;

import objects.LightBulb;
import objects.Plasma;
import tools.Pair;
import graphics.Animation;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.Body;
import gravity.Space;

public class Shuttle extends Vessel {

	private static final long serialVersionUID = 5702798293004040184L;

	private Animation rearEngine;
	private Animation frontUpEngine;
	private Animation frontDownEngine;
	private Animation shields;
	
	private Sprite inertiaCompensators;

	private static final int shuttleRadius = 35;
	private static final float shuttleDensity = .005f;
	private static final float shuttleMaxThrottle = 1f;
	private static final float shuttleFuelTankSize = 1000;
	private static final float throttleAbs = -.1f;
	private static final float throttleStepZ = .0015f;
	private static final float throttleStepY = .01f;
	private static final float throttleStepX = .1f;
	private static final float throttleStepS = .01f;
	private static final float throttleMin = .001f;
	private static final float shuttleShieldCapacity = 100;
	private static final float shuttleInertiaCompensatorRatio = 0.99f;
	
	
	public Shuttle() {
		this(0, 0);
	}
	
	public Shuttle(int x, int y) {
		this(x, y, 0, 0);
	}

	public Shuttle(int x, int y, float initSpeedX, float initSpeedY) {
		super(shuttleDensity*shuttleRadius*shuttleRadius, shuttleRadius, (float)Math.PI, shuttleFuelTankSize, initSpeedX, initSpeedY);
		
		/* adding sprites and animations */
		addVisibleObject(new Sprite(Sprites.SPRITE_SHUTTLE, (float)Math.PI)).setVisible(true);
		addVisibleObject(rearEngine = new Animation(Sprites.ANIMATION_SHUTTLE_REAR_ENGINE, (float)Math.PI));
		addVisibleObject(frontUpEngine = new Animation(Sprites.ANIMATION_SHUTTLE_UP_ENGINE, (float)Math.PI));
		addVisibleObject(frontDownEngine = new Animation(Sprites.ANIMATION_SHUTTLE_DOWN_ENGINE, (float)Math.PI));
		addVisibleObject(shields = new Animation(Sprites.ANIMATION_SHUTLLE_SHIELDS, (float)Math.PI));
		addVisibleObject(inertiaCompensators = new Sprite(Sprites.SPRITE_SHUTTLE_IC, (float)Math.PI));
		
		/* setting up animations */
		shields.setAnimationStep(2);
		shields.setLoopStep(2);
		rearEngine.setAnimation(Animation.AnimationStatus.PLAY_LOOP);
		rearEngine.setAnimationStep(2);
		rearEngine.setVisible(false);
		frontUpEngine.setAnimation(Animation.AnimationStatus.PLAY_LOOP);
		frontUpEngine.setAnimationStep(2);
		frontUpEngine.setVisible(false);
		frontDownEngine.setAnimation(Animation.AnimationStatus.PLAY_LOOP);
		frontDownEngine.setAnimationStep(2);
		frontDownEngine.setVisible(false);
		
		/* shield setting */
		setShieldCapacity(shuttleShieldCapacity);

		/* setting up inertia compensator */
		setInertiaCompensatorRatio(shuttleInertiaCompensatorRatio);
		
		setLocation(x, y);
	}

	private void updateThrottleValues(float y, float x, float z) {
		if (Math.abs(getThrottleX())+Math.abs(getThrottleY())+Math.abs(getThrottleZ()) < shuttleMaxThrottle) {
			throttleX(x*throttleStepX);
			throttleY(y*throttleStepY);
			throttleZ(z*throttleStepZ);
		}
	}

	public void goLeft(int units) {
		updateThrottleValues(0, 0, -units);
	}

	public void goRight(int units) {
		updateThrottleValues(0, 0, units);
	}

	public void goForward(int units) {
		updateThrottleValues(0, units, 0);
	}

	public void goBackward(int units) {
		updateThrottleValues(0, -units, 0);
	}
	
	protected void onFreeze() {
		resetThrottle();
	}

	protected void onTankEmpty(){
		rearEngine.setVisible(false);
		frontUpEngine.setVisible(false);
		frontDownEngine.setVisible(false);
	}

	protected void onTimeCycle(){
		/* consume fuel for inertia compensator usage */
		if (isInertiaCompensatorActive()) {
			inertiaCompensators.setVisible(true);
		} else {
			inertiaCompensators.setVisible(false);
		}
		
		/* automatically reduce throttle when not used */
		throttleX(getThrottleX()*throttleAbs);
		throttleY(getThrottleY()*throttleAbs);
		throttleZ(getThrottleZ()*throttleAbs);

		/* shields up!! */
		if (getShieldLevel() < shuttleShieldCapacity) {
			setThrottleS(throttleStepS*(shuttleShieldCapacity-getShieldLevel()));
		}

		/* light the engines if used */
		if (getFuelLevel()>0) {
			rearEngine.setVisible((getThrottleX()>throttleMin));
			frontDownEngine.setVisible((getThrottleZ()>=throttleMin || getThrottleX()<-throttleMin));
			frontUpEngine.setVisible((getThrottleZ()<=-throttleMin || getThrottleX()<-throttleMin));
		}
	}

	protected void onTakeDamage() {
		if (getShieldLevel()>0) {
			shields.setAnimation(Animation.AnimationStatus.PLAY_LOOP);
		}
	}

	public Vessel dropVessel() {
		return new LandingModule(
				calculateOuterCoordinates(new Pair(-2*getDiameter(), 0)),
				getSpeedX()/2, getSpeedY()/2);
	}
	
	public boolean fire(int id) {
		float speed = 0;
		Body b = null;
		
		if (inputTimer > 0) {
			return false;
		}
		
		switch (id) {
		case 0:
			if (!burnFuel(1)) return false;
			speed = 2;
			b = new Plasma(calculateOuterCoordinates(new Pair(getDiameter()/1.5f, 0)));
			break;
		case 1:
			if (!burnFuel(20)) return false;
			speed = 4;
			b = new LightBulb(calculateOuterCoordinates(new Pair(getDiameter()/1.5f, 0)));
			inputTimer += 30;
			break;
		default:
		}
		
		Pair initSpeed = new Pair(speed, 0).rotateCoordinates(getHeading());
		initSpeed.add(getSpeedX(), getSpeedY());
		b.setSpeed(initSpeed);
		b.setParent(this);
		
		if (burnFuel(0.1f)) {
			Space.getInstance().insertMass(b);
			return true;
		}
		
		return false;
	}
}
