package gravity;

public abstract class Body extends MovingMass implements Runnable {

	private static final long serialVersionUID = 3601288502314572784L;

	protected float diameter; /* object limits, collision beyond */
	public static final long computeStep=30;

	public Body(float mass, float radius) {
		this(mass, radius, 0);
	}

	public Body(float mass, float radius, float spin) {
		this(mass, radius, spin, 0, 0);
	}
	
	public Body(float mass, float radius, float initSpin, float initSpeedX, float initSpeedY) {
		super(mass, initSpin, initSpeedX, initSpeedY);
		this.diameter = radius;
	}

	public float getDiameter() {
		return diameter;
	}

	public void setDiameter(float diameter) {
		this.diameter = diameter;
	}

	/**
	 * An impact occurred between this body and a smaller one,
	 * @param b another object
	 * @return true if this body is destroyed
	 */
	public boolean onImpact(Body b) {
		//Biggest body have to deal with the smallest body being absorbed
		if(b.getEnergyShield() >= this.getEnergyShield()) {
			return b.absorbEnergy(this);
		}
		return false;
	}

	/**
	 * The body must decide what to do with this incoming body
	 * @param m
	 * @return true if the incoming body must be destroyed
	 */
	protected boolean absorbEnergy(Body m) {
		return true;
	}
}