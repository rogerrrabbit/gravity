package graphics;

import graphics.ImageCache.Sprites;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class Sprite extends VisibleObject implements ImageObserver {

	private static final long serialVersionUID = -8444935745538427421L;

	private float orientation;
	private float orientation_offset;

	private Sprites spriteId;
	private transient BufferedImage picture;
	
	private float height;
	private float width;
	
	public float getWidth() {
		return width;
	}

	public void setSize(Dimension d) {
		this.width = d.width;
		this.height = d.height;
	}
	
	public boolean init() {
		picture = spriteId.images()[0];
		return (picture != null);
	}

	public Sprite(Sprites spriteId) {
		this(spriteId, 0);
	}
	
	public Sprite(Sprites spriteId, float theta) {
		orientation = 0;
		orientation_offset = theta;
		this.spriteId = spriteId;

		if (init()) {
			width = picture.getWidth();
			height = picture.getHeight();	
		} else {
			System.err.println(String.format("Sprite loading failed:%s", spriteId.name()));
		}
	}

	public boolean draw(Graphics g, int x, int y) {
		if (picture == null) {
			return false;
		}
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform old = g2d.getTransform();
		g2d.rotate(orientation + orientation_offset, x, y);
		g2d.drawImage(picture, x - (int)(width/2), y - (int)(height/2), (int)width, (int)height, this);
		g2d.setTransform(old);
		return true;
	}

	public float getOrientation() {
		return orientation;
	}

	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}

	public void setPicture(BufferedImage picture) {
		this.picture = picture;
	}

	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setDiameter(float diameter) {
		float aspectRatio = width/height;
		if (width>height) {
			width = diameter;
			height = diameter/aspectRatio;
		} else {
			height = diameter;
			width = diameter/aspectRatio;
		}
	}
}
