package vehicles;

import objects.Chips;
import objects.LightBulb;
import tools.Pair;
import graphics.ImageCache.Sprites;
import graphics.Animation;
import graphics.Sprite;
import gravity.Body;
import gravity.Space;

public class Tacos extends Vessel {

	private static final long serialVersionUID = 5702798293004040184L;

	/* thrust engines */
	private Sprite tacos;
	private Animation damage;
	private Animation leftEngine;
	private Animation rightEngine;
	
	private static final int tacosRadius = 50;
	private static final float tacosDensity = .001f;
	private static final float tacosMaxThrottle = 5f;
	private static final float tacosFuelTankSize = 1000;
	private static final float tacosShieldCapacity = 100;
	private static final float throttleAbs = -.1f;
	private static final float throttleStepZ = .002f;
	private static final float throttleStepY = .01f;
	private static final float throttleStepX = .1f;
	private static final float throttleStepS = .01f;
	private static final float throttleMin = .001f;
	
	public Tacos() {
		this(0, 0);
	}
	
	public Tacos(Pair p) {
		this((int)p.fx, (int)p.fy);
	}
	
	public Tacos(int x, int y) {
		this(x, y, 0, 0);
	}

	public Tacos(int x, int y, float initSpeedX, float initSpeedY) {
		super(tacosDensity*tacosRadius*tacosRadius, tacosRadius, -(float)Math.PI/2, tacosFuelTankSize, initSpeedX, initSpeedY);
		
		tacos = new Sprite(Sprites.SPRITE_TACOS, (float)Math.PI/2);
		leftEngine = new Animation(Sprites.ANIMATION_TACOS_LEFT_ENGINE, (float)Math.PI/2);
		rightEngine = new Animation(Sprites.ANIMATION_TACOS_RIGHT_ENGINE, (float)Math.PI/2);
		damage = new Animation(Sprites.ANIMATION_TACOS_DAMAGE, (float)Math.PI/2);
		
		tacos.setDiameter(tacosRadius);
		damage.setDiameter(tacosRadius);
		leftEngine.setDiameter(tacosRadius);
		rightEngine.setDiameter(tacosRadius);
		
		damage.setAnimationStep(4);
		damage.setLoopStep(4);
		leftEngine.setAnimation(Animation.AnimationStatus.PLAY_LOOP);
		leftEngine.setAnimationStep(3);
		rightEngine.setAnimation(Animation.AnimationStatus.PLAY_LOOP);
		rightEngine.setAnimationStep(3);
		
		/* adding sprites and animations */
		addVisibleObject(tacos).setVisible(true);
		addVisibleObject(leftEngine);
		addVisibleObject(rightEngine);
		addVisibleObject(damage);
		
		/* shield setting */
		setShieldCapacity(tacosShieldCapacity);
		
		setLocation(x, y);
	}

	private void updateThrottleValues(float y, float x, float z) {
		if (Math.abs(getThrottleX())+Math.abs(getThrottleY())+Math.abs(getThrottleZ()) < tacosMaxThrottle) {
			throttleX(x*throttleStepX);
			throttleY(y*throttleStepY);
			throttleZ(z*throttleStepZ);
		}
	}

	public void goLeft(int units) {
		updateThrottleValues(-1, 0, -units);
	}

	public void goRight(int units) {
		updateThrottleValues(1, 0, units);
	}

	public void goForward(int units) {
		updateThrottleValues(0, 1, 0);
	}

	public void goBackward(int units) {
	}

	protected void onFreeze() {
		resetThrottle();
	}

	protected void onTankEmpty(){
		leftEngine.setVisible(false);
		rightEngine.setVisible(false);
	}

	protected void onTimeCycle(){
		/* automatically reduce throttle when not used */
		throttleX(getThrottleX()*throttleAbs);
		throttleY(getThrottleY()*throttleAbs);
		throttleZ(getThrottleZ()*throttleAbs);

		/* shields up!! */
		if (getShieldLevel() < tacosShieldCapacity) {
			setThrottleS(throttleStepS*(tacosShieldCapacity-getShieldLevel()));
		}
		
		/* light the engines if used */
		if (getFuelLevel()>0) {
			if (getThrottleX()>throttleMin) {
				leftEngine.setVisible(true);
				rightEngine.setVisible(true);
			} else {
				leftEngine.setVisible(Math.abs(getThrottleY())>throttleMin && getThrottleY() > 0);
				rightEngine.setVisible(Math.abs(getThrottleY())>throttleMin && getThrottleY() < 0);
			}
		}
	}

	protected void onTakeDamage() {
		if (getShieldLevel()>0) {
			damage.setAnimation(Animation.AnimationStatus.PLAY_LOOP);
		}
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
			speed = 3f;
			b = new Chips(calculateOuterCoordinates(new Pair(getDiameter()/1.5f, 0)), 12.f);
			b.setSpin(.05f);
			inputTimer += 5;
			break;
		case 1:
			if (!burnFuel(20)) return false;
			speed = 4f;
			b = new LightBulb(calculateOuterCoordinates(new Pair(getDiameter()/1.5f, 0)));
			inputTimer += 35;
			break;
		default:
		}
		
		Pair initSpeed = new Pair(speed, 0).rotateCoordinates(getHeading());
		initSpeed.add(getSpeedX(), getSpeedY());
		b.setSpeed(initSpeed);
		
		if (burnFuel(0.1f)) {
			Space.getInstance().insertMass(b);
			return true;
		}
		
		return false;
	}
}
