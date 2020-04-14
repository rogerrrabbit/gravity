package graphics;

import game.DuelGame;
import game.Game;
import game.PlanetGame;
import game.SandboxGame;
import game.TacosGame;
import graphics.ImageCache.Sprites;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class GameSelector extends VisibleObject implements ImageObserver {

	private static final long serialVersionUID = 3143774829044027211L;
	private Games[] gameList = {
		Games.TACOS2000,
		Games.PLANETSIDE,
		Games.SANDBOX,
		Games.DUEL,
	};
	
	public static final int selectorHeight = 100;
	public static final int selectorWidth = 100;
	private static final int selectorThickness = 2;
	
	enum Games {
		PLANETSIDE(PlanetGame.class, Sprites.SPRITE_PLANET, "Love Planet"),
		SANDBOX(SandboxGame.class, Sprites.SPRITE_SHUTTLE_THUMB, "Sandbox"),
		TACOS2000(TacosGame.class, Sprites.SPRITE_TACOS_FULL, "Tacos Adventures"),
		DUEL(DuelGame.class, Sprites.SPRITE_DUEL, "Duel");
		
		private Class<?> gameClass;
		private transient BufferedImage thumbnail;
		private Sprites sprite;
		private String name;
				
		Games(Class<?> c, Sprites spr, String str) {
			gameClass = c;
			sprite = spr;
			thumbnail = spr.images()[0];
			name = str;
		}
		
		public BufferedImage thumbnail() {
			if (thumbnail != null) {
				thumbnail = sprite.images()[0];
			}
			return thumbnail;
		}
		
		public String title() {
			return name;
		}
		
		public Game instance() {
			try {
				return (Game)gameClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	private Games selectedGame;
	private int selected = 0;
	private Color color;

	public GameSelector(Color color) {
		this.color = color;
		setVisible(true);	
		init();
	}

	public boolean init() {
		selectedGame = gameList[selected];
		return (selectedGame.thumbnail() != null);
	}
	
	public void next() {
		selected=(selected+1)%gameList.length;
		selectedGame = gameList[selected];
	}
	
	public void prev() {
		if (selected == 0) {
			selected = gameList.length - 1;
		} else {
			selected--;
		}
		selectedGame = gameList[selected];
	}
	
	public Game getGame() {
		return gameList[selected].instance();
	}

	public boolean draw(Graphics g, int x, int y) {
		if (selectedGame == null) {
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
				selectedGame.thumbnail(),
				x + 2*selectorThickness, y + 2*selectorThickness,
				selectorWidth - 3*selectorThickness,
				selectorHeight - 3*selectorThickness,
				this);
		g2.setStroke(prevStroke);
		g2.drawString(selectedGame.title(), x + (3 * selectorWidth/2), y + (selectorHeight/2));

		return true;
	}

	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		return false;
	}
}
