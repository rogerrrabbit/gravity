package graphics;

import graphics.ImageCache.Sprites;
import gravity.Mass;
import gravity.Space;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.lang.reflect.InvocationTargetException;

import objects.BlackHole;
import objects.Satellite;
import vehicles.*;

public class VesselSelector extends VisibleObject implements ImageObserver {

	private Selectables[] selectableList = {
		Selectables.SHUTTLE,
		Selectables.ROCKET,
		Selectables.LANDINGMODULE,
		Selectables.BLOB,
		Selectables.BLACKHOLE,
		Selectables.TACOS,
		Selectables.SATELLITE,
	};
	
	enum Selectables {
		SHUTTLE(Shuttle.class, Sprites.SPRITE_SHUTTLE_THUMB),
		ROCKET(Rocket.class, Sprites.SPRITE_ROCKET),
		LANDINGMODULE(LandingModule.class, Sprites.ANIMATION_LEM),
		BLOB(Blob.class, Sprites.SPRITE_BLOB),
		BLACKHOLE(BlackHole.class, Sprites.SPRITE_BLACKHOLE),
		TACOS(Tacos.class, Sprites.SPRITE_TACOS_FULL),
		SATELLITE(Satellite.class, Sprites.SPRITE_SATELLITE),
		SPACEDOCK(SpaceDock.class, Sprites.SPRITE_BLOB);
		
		private Class<?> selectableClass;
		private transient BufferedImage thumbnail;
		private Sprites sprite;
		
		Selectables(Class<?> c, Sprites spr) {
			selectableClass = c;
			sprite = spr;
			thumbnail = spr.images()[0];
		}

		private BufferedImage thumbnail() {
			if (thumbnail != null) {
				thumbnail = sprite.images()[0];
			}
			return thumbnail;
		}
		
		public Mass instance() {
			try {
				return (Mass)selectableClass.getDeclaredConstructor().newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return null;
			} catch (SecurityException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	private int selected = 0;
	private transient BufferedImage selectedThumbnail;
	
	private static final long serialVersionUID = 2508215373328405809L;

	private static final int selectorHeight = 50;
	private static final int selectorWidth = 50;
	private static final int selectorThickness = 2;

	private Color color;

	public VesselSelector(Color color) {
		this.color = color;
		setVisible(true);	
		init();
	}

	public boolean init() {
		selectedThumbnail = selectableList[selected].thumbnail();
		return (selectedThumbnail != null);
	}
	
	public void next() {
		selected=(selected+1)%selectableList.length;
		selectedThumbnail = selectableList[selected].thumbnail();
	}
	
	public Mass insertSelected(float x, float y) {
		Mass newMass = selectableList[selected].instance();
		newMass.setLocation(x, y);
		Space.getInstance().insertMass(newMass);
		return newMass;
	}

	public boolean draw(Graphics g, int x, int y) {
		if (selectedThumbnail == null) {
			return false;
		}
		Graphics2D g2 = (Graphics2D) g;
		Stroke prevStroke = g2.getStroke();
		
		Stroke aspect = new BasicStroke(selectorThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f);
		Shape box = new java.awt.geom.Rectangle2D.Float(x, y, selectorWidth, selectorHeight);

		g2.setStroke(aspect); 
		g2.setColor(color);
		g2.draw(box);
		g2.drawImage(
				selectedThumbnail,
				x + 2*selectorThickness, y + 2*selectorThickness,
				selectorWidth - 3*selectorThickness,
				selectorHeight - 3*selectorThickness, this);
		g2.setStroke(prevStroke);

		return true;
	}

	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		return false;
	}
}
