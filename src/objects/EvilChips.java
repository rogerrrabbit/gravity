package objects;

import brains.BasicDefenseIA;
import brains.BodyIA;
import tools.Pair;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.*;

public class EvilChips extends Body{

	private static final long serialVersionUID = -4427894404095486155L;

	private Sprite body;
	private BodyIA ia;
	private int hitCount = 0;

	public static final float evilDiameterLimit = 50;
	private static final float chipsRadius = 50;
	private static final float chipsDensity = .25f;

	public EvilChips(Pair location) {
		this(location, chipsRadius, 0, 0);
	}
	
	public EvilChips(float x, float y) {
		this(x, y, chipsRadius);
	}

	public EvilChips(float x, float y, float radius) {
		this(x, y, radius, 0, 0);
	}

	public EvilChips(Pair location, float radius, float initSpeedX, float initSpeedY) {
		this(location.fx, location.fy, radius, initSpeedX, initSpeedY);
	}
	
	public EvilChips(float x, float y, float radius, float initSpeedX, float initSpeedY) {
		super(chipsDensity*radius*radius, radius, 0, initSpeedX, initSpeedY);
		setLocation(x, y);

		addVisibleObject(body = new Sprite(Sprites.SPRITE_CHIPS_EVIL));
		body.setVisible(true);

		ia = new BasicDefenseIA(this);
		ia.start();

		setDiameter((int)Math.sqrt(getMass()/chipsDensity));
		body.setDiameter(getDiameter());
	}

	/**
	 * When hit by a smaller object, the object increases its own mass and speed
	 * @param m absorbed Mass
	 * @return true so the smaller object is destroyed
	 */
	public boolean absorbEnergy(Body m) {
		if (++hitCount > 2) {
			Space.getInstance().removeMass(this);
			
			/* chips explosion */
			for (int count = 0; count < 8; count++) {
				Pair coord = new Pair((float)Math.cos(Math.PI * count / 4),
									  (float)Math.sin(Math.PI * count / 4));
	
				/* initial speed of the object */
				Pair initSpeed = new Pair(4, 0).rotateCoordinates(coord.getAngle());
				initSpeed.add(getSpeedX(), getSpeedY());

				coord.factor(getDiameter()/2 + 6);
				coord.add(getLocation());
				
				/* the object */
				Chips c = new Chips(coord.fx, coord.fy, 8);
				c.setSpin(.1f);
				c.setSpeed(initSpeed);
	
				Space.getInstance().insertMass(c);
			}
		}
		return true;
	}

	public void run() {
	}

	protected void onRemove() {
		ia.stop();
	}
}