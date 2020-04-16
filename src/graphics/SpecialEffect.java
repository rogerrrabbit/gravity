package graphics;

import java.awt.Graphics;
import java.io.Serializable;

import tools.Pair;

public class SpecialEffect implements Serializable {
	
	private static final long serialVersionUID = -2560194557247546898L;

	private VisibleObject object;
	private Pair objectLocation;
	
	public void setObjectLocation(Pair objectLocation) {
		this.objectLocation = objectLocation;
	}

	public SpecialEffect(VisibleObject object, Pair location) {
		this.object = object;
		this.objectLocation = location;
	}
	
	public boolean paint(Graphics g) {
		return object.paintGraphics(g, objectLocation);
	}
	
	public void initGraphics() {
		object.init();
	}
	
	public void dispose() {
		object.setVisible(false);
	}
}
