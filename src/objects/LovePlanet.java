package objects;

import gravity.Space;
import tools.Pair;

public class LovePlanet extends Planet {

	private static final long serialVersionUID = 8926479396144149553L;
	
	private static final int heartDiameter = 5;
	private static final int securityGap = 5;
	
	private transient Thread love;
	
	public LovePlanet(int x, int y) {
		super(x, y);
		
		love = new Thread(this);
		love.start();
	}

	public void run() {
		while(true) {
			try {
				Thread.sleep(640);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}

			float angle = (float) (Math.random() * 2.f * Math.PI);
			Pair location = new Pair((float)Math.cos(angle),
					                 (float)Math.sin(angle));

			/* where the object is launched from */
			location.factor(getDiameter()/2 + heartDiameter/2 + securityGap);
			location.add(getLocation());

			/* initial speed of the object */
			Pair initSpeed = new Pair(1, 0).rotateCoordinates(angle);
			initSpeed.add(getSpeedX(), getSpeedY());

			/* the object */
			Heart c = new Heart(location.fx, location.fy, heartDiameter);
			c.setSpin(.1f);
			c.setSpeed(initSpeed);

			Space.getInstance().insertMass(c);
		}
	}

	protected void onRemove() {
		love.interrupt();
	}
}
