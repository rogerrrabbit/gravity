package gravity;

import java.awt.Color;
import java.awt.Graphics;
import tools.Pair;
import graphics.DebugArrow;
import graphics.DebugInfo;
import graphics.VisibleObject;

public abstract class MovingMass extends Mass{	

	private static final long serialVersionUID = -7158309262723546692L;

	private DebugArrow debugGForceArrow;
	private DebugArrow debugHeadingArrow;
	private DebugArrow debugSpeedArrow;
	private DebugInfo debugInfo;

	/* X,Y,Z speeds */
	private Pair speed;			/* pixels/frame */
	private float spin = 0;		/* radian/frame*/
	
	/* last computed gravity field value at the object location */
	private Pair gforce;
	
	/* object orientation (radians) */
	private float heading = 0;

	/* how the object compensates its inertia */
	private float localInertia = 1f;
	
	protected void setInertia(float inertia) {
		localInertia = inertia;
	}
	
	public MovingMass(float mass) {
		this(mass, 0);
	}

	public MovingMass(float mass, float o) {
		this(mass, o, 0, 0);
	}
	
	public MovingMass(float mass, float orientation, float speedX, float speedY) {
		super(mass);
		speed = new Pair(speedX, speedY);
		gforce = new Pair(0, 0);
		heading = orientation;
		
		if (Space.debug) {
			addVisibleObject(debugGForceArrow = new DebugArrow(Color.GRAY, 1000));
			addVisibleObject(debugHeadingArrow = new DebugArrow(Color.BLUE, 30));
			addVisibleObject(debugSpeedArrow = new DebugArrow(Color.GREEN, 100));
			addVisibleObject(debugInfo = new DebugInfo(0));
		}
	}

	public synchronized void paintGraphics(Graphics g) {
		for (VisibleObject v : objects) {
			v.setOrientation(getHeading());
			v.paintGraphics(g, getLocation());
		}
	}
	
	public float getEnergy() {
		return getMass() + getMass()*getSpeed().getNorm();
	}
	
	protected float getEnergyShield() {
		return getEnergy();
	}

	protected void updateForces(float gameSpeed) {
		/* compute new gravity field */
		gforce = Space.getInstance().calculateGForce(this);
	}

	protected void updateLocation(float gameSpeed, float inertia) {
		/* apply the gravity field on the moving mass */
		speed.addFactor(gforce, gameSpeed);
		
		/* add friction */
		speed.factor(inertia*localInertia);
		spin *= inertia*localInertia;
		
		/* applying speed on object */
		getLocation().addFactor(speed, gameSpeed);
		incHeading(spin*gameSpeed);

		if (Space.debug) {
			debugHeadingArrow.setCoordinates(heading);
			debugSpeedArrow.setCoordinates(speed.fx, speed.fy);
			debugGForceArrow.setCoordinates(gforce.factor(10).getAngle());
			debugInfo.setInformation(String.format("\rE:%.2f;M:%.2f;G:%.2f;H:%.2f;V:%.2f(Vx:%.2f;Vy:%.2f;Vz:%.2f)",
					getEnergy(), getMass(), gforce.getNorm(), getHeading(), getSpeed().getNorm(), getSpeedX(), getSpeedY(), getSpin()));
		}
	}
	
	public Pair calculateOuterCoordinates(Pair p) {
		return p.rotateCoordinates(getHeading()).add(getLocation());
	}
	
	public Pair calculateInnerCoordinates(Pair p) {
		return p.rem(getLocation()).rotateCoordinates(-getHeading());
	}
	
	public Pair getSpeed() {
		return speed;
	}
	public float getSpeedX() {
		return speed.fx;
	}
	public float getSpeedY() {
		return speed.fy;
	}
	public float getHeading() {
		return heading;
	}
	public Pair getGforce() {
		return gforce;
	}
	protected float incHeading(float h) {
		return this.heading += h;
		
	}
	public float setHeading(float h) {
		return this.heading = h;
		
	}
	protected float getSpin() {
		return spin;
	}
	public void setSpin(float spin) {
		this.spin = spin;
	}
	
	public Pair setSpeed(float x, float y) {
		return speed.set(x, y);
	}
	public Pair setSpeed(Pair p) {
		return speed.set(p);
	}
	
	/* Including inertia:
	 * the faster and bigger you get, the slower you can move.
	 */
	public Pair incSpeed(float x, float y) {
		return speed.add(x/getEnergy(), y/getEnergy());
	}
	public Pair incSpeed(Pair p) {
		return incSpeed(p.fx, p.fy);
	}
	protected float incSpin(float spin) {
		return this.spin += spin/getEnergy();
	}
}
