package gravity;

public abstract class Singularity extends Mass {

	private static final long serialVersionUID = 4184344288726798099L;
	private int radius; /* horizon of the singularity */

	public Singularity(float mass, int radius) {
		super(mass);
		this.radius = radius;
	}
	
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
}