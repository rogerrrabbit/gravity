package objects;

import tools.Pair;
import graphics.ImageCache.Sprites;
import graphics.Sprite;
import gravity.*;

public class Chips extends Body{

	private static final long serialVersionUID = -4427894404095486155L;

	private Sprite body;

	private static final float chipsDiameter = 15;
	private static final float chipsDensity = .015f;

	public Chips(Pair location) {
		this(location, chipsDiameter, 0, 0);
	}
	
	public Chips(Pair location, float radius) {
		this(location, radius, 0, 0);
	}

	public Chips(float x, float y) {
		this(x, y, chipsDiameter);
	}

	public Chips(float x, float y, float diameter) {
		this(x, y, diameter, 0, 0);
	}

	public Chips(Pair location, float radius, float initSpeedX, float initSpeedY) {
		this(location.fx, location.fy, radius, initSpeedX, initSpeedY);
	}
	
	public Chips(float x, float y, float diameter, float initSpeedX, float initSpeedY) {
		super(chipsDensity*diameter*diameter, diameter, 0, initSpeedX, initSpeedY);
		setLocation(x, y);

		addVisibleObject(body = new Sprite(Sprites.SPRITE_CHIPS));
		body.setVisible(true);

		updateDiameter();
	}

	private int updateDiameter() {
		int newDiameter = (int)Math.sqrt(getMass()/chipsDensity);
		setDiameter(newDiameter);
		body.setDiameter(newDiameter);
		return newDiameter;
	}

	/**
	 * When hit by a smaller object, the object increases its own mass and speed
	 * @param m absorbed Mass
	 * @return true so the smaller object is destroyed
	 */
	public boolean absorbEnergy(Body m) {
		incSpeed(	(m.getSpeedX() - this.getSpeedX()) * m.getMass(),
					(m.getSpeedY() - this.getSpeedY()) * m.getMass());
		
		if (updateDiameter() > EvilChips.evilDiameterLimit) {
			EvilChips evil = new EvilChips(getLocation(), getDiameter(), getSpeedX(), getSpeedY());
			evil.setSpin(getSpin());
			Space.getInstance().removeMass(this);
			Space.getInstance().insertMass(evil);
		} else {
			setMass(getMass() + m.getMass());
		}

		return true;
	}

	public void run() {
	}
}