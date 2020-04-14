package brains;

import java.awt.Color;
import java.util.ArrayList;

import objects.Chips;
import tools.Pair;
import graphics.DebugArrow;
import graphics.Sphere;
import gravity.Body;
import gravity.Mass;
import gravity.Space;

public class BasicDefenseIA extends BodyIA {
	 
	private static final long serialVersionUID = 1155026793999882064L;

	Body body;
	Mass target;

	protected int inputTimer = 0;
	private DebugArrow threatDirection;
	private Sphere radarCircle;

	private static int radarRangeRatio = 3;

	public static int getRadarRadius() {
		return radarRangeRatio;
	}

	public static void setRadarRadius(int r) {
		BasicDefenseIA.radarRangeRatio = r;
	}

	public BasicDefenseIA(Body b) {
		super(b);
		body = b;
		
		threatDirection = new DebugArrow(Color.RED);
		radarCircle = new Sphere(radarRangeRatio*body.getDiameter());
		radarCircle.setVisible(true);

		b.addVisibleObject(radarCircle);
		b.addVisibleObject(threatDirection);
	}

	public void performActions() {
		if (inputTimer > 0) {
			inputTimer--;
			return;
		}

		ArrayList<Mass> intruders = Space.getInstance().detectBodiesCircleExternal(body, radarRangeRatio*body.getDiameter());
		
		if(!intruders.isEmpty()) {
			
			/* find a vector pointing to the average center of dangerous masses around */
			/* we will throw an object to that center */ 
			Pair threatLocation = Space.calculateGCenter(intruders, body).normalized();
			float objectDiameter = 12;
			float securityGap = 2;
			
			/* debug */
			threatDirection.setCoordinates(threatLocation.fx*100, threatLocation.fy*100);
			threatDirection.setVisible(true);
			radarCircle.setDiameter(radarRangeRatio*body.getDiameter());
			radarCircle.setBorderColor(Color.RED);
			
			/* where the object is launched from */
			Pair coord = new Pair(threatLocation).factor(body.getDiameter()/2 + objectDiameter/2 + securityGap);
			coord.add(body.getLocation());

			/* initial speed of the object */
			Pair initSpeed = new Pair(4, 0).rotateCoordinates(threatLocation.getAngle());
			initSpeed.add(body.getSpeedX(), body.getSpeedY());

			/* the object */
			Chips c = new Chips(coord.fx, coord.fy, objectDiameter);
			c.setSpin(.1f);
			c.setSpeed(initSpeed);
			c.setParent(body);

			Space.getInstance().insertMass(c);
			inputTimer += 5;

		} else {
			radarCircle.setBorderColor(Color.GREEN);
			threatDirection.setVisible(false);
		}
	}

	public void run() {
		while(true) {
			try {
				Thread.sleep(computeStep);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			performActions();
		}
	}
}