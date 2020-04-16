package graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageCache {

	public static enum Sprites {
		SPRITE_TACOS("/tacos.png"),
		SPRITE_TACOS_FULL("/tacos_full.png"),
		SPRITE_ASTEROID_1("/ba0.png"),
		SPRITE_ASTEROID_2("/va1.png"),
		SPRITE_ASTEROID_3("/oa1.png"),
		SPRITE_ASTEROID_4("/ba3.png"),
		SPRITE_ASTEROID_5("/ba2.png"),
		SPRITE_SHUTTLE ("/rocket3_darker.png"),
		SPRITE_SHUTTLE_THUMB("/rocket3_thumb.png"),
		SPRITE_SHUTTLE_IC ("/rocket3_ic.png"),
		SPRITE_PLANET ("/planet.png"),
		SPRITE_PLANET01 ("/spr_planet01_centered.png"),
		SPRITE_PLANET02 ("/spr_planet02_centered.png"),
		SPRITE_PLANET03 ("/spr_planet03_centered.png"),
		SPRITE_PLANET04 ("/spr_planet04_centered.png"),
		SPRITE_PLANET05 ("/spr_planet05_centered.png"),
		SPRITE_PLANET06 ("/spr_planet06_centered.png"),
		SPRITE_ROCKET ("/rocket1.png"),
		SPRITE_SATELLITE ("/satellite_alpha.png"),
		SPRITE_BLOB ("/blob.png"),
		SPRITE_BULLET ("/bullet.png"),
		SPRITE_HEART ("/heart_green.png"),
		SPRITE_CHIPS ("/tortilla.png"),
		SPRITE_CHIPS_EVIL ("/tortilla_evil.png"),
		SPRITE_DUEL ("/tortilla_duel.png"),
		SPRITE_BLACKHOLE ("/blackhole.png"),
		SPRITE_TITLE("/title_alpha.png"),
		BACKGROUND_GREEN_HEAVEN("/green_up.jpg"),
		BACKGROUND_HUBBLE ("/bg5.jpg"),
		BACKGROUND_PURPLE("/purple_up.jpg"),
		ANIMATION_LEM ("/rocket2.png;/rocket2_l.png;/rocket2_r.png;/rocket2_b.png"),
		ANIMATION_EXPLOSION ("/explosion0.png;/explosion1.png;/explosion2.png;/explosion3.png;/explosion4.png;/explosion5.png"),
		ANIMATION_SHUTLLE_SHIELDS ("/rocket3_shields1.png;/rocket3_shields2.png"),
		ANIMATION_SHUTTLE_REAR_ENGINE ("/rocket3_rear1.png;/rocket3_rear3.png;/rocket3_rear2.png"),
		ANIMATION_SHUTTLE_UP_ENGINE ("/rocket3_up1.png;/rocket3_up2.png"),
		ANIMATION_SHUTTLE_DOWN_ENGINE ("/rocket3_down1.png;/rocket3_down2.png"),
		ANIMATION_TACOS_DAMAGE("/tacos_damage.png;/tacos.png"),
		ANIMATION_TACOS_LEFT_ENGINE("/tacos_engine_left_mini.png;/tacos_engine_left_half.png;/tacos_engine_left.png"),
		ANIMATION_TACOS_RIGHT_ENGINE("/tacos_engine_right_mini.png;/tacos_engine_right_half.png;/tacos_engine_right.png");

		private transient BufferedImage[] images;
		private String[] sprites;

		Sprites (String n) {
			sprites = n.split(";");
			init();
		}
		
		private BufferedImage loadPicture(String filename) {
			BufferedImage picture = null;
			InputStream resource;
			
			try {
				System.out.println(String.format("Loading picture %s", filename));
				resource = getClass().getResourceAsStream(filename);
				if (resource == null) {
					System.err.println(String.format("Can't find %s picture", filename));
					System.exit(1);
				}
				if ((picture = ImageIO.read(resource)) == null) {
					System.err.println(String.format("Can't load %s picture", filename));
					System.exit(1);
				}

			} catch (IOException e) {
				System.err.println(String.format("Picture %s loading failed", filename));
				System.exit(1);
			}
			
			return picture;
		}
		
		public void init() {
			images = new BufferedImage[sprites.length];
			for (int i=0; i<sprites.length; i++) {
				images[i] = loadPicture(sprites[i]);
			}
		}
		
		public BufferedImage[] images() {
			return images;
		}
	};

}
