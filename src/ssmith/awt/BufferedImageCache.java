package ssmith.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class BufferedImageCache extends Hashtable<String, BufferedImage> {

	public enum ShadeType {
		NONE, BOTTOM_HALF;//, BOTTOM_RIGHT, TOP_LEFT, ALL;
	}

	private static final long serialVersionUID = 1L;

	private Component c;

	public BufferedImageCache(Component _c) {
		super();

		c = _c;
	}


	public BufferedImage getImage(String filename, int w, int h, ShadeType shade) {
		if (filename != null && filename.length() > 0 && !filename.endsWith("/") && !filename.endsWith("\\")) {
			String key = filename + "_" + w + "_" + h + "_" + shade.ordinal();
			BufferedImage img = get(key);
			if (img == null) {
				try {
					String res_filename = filename;
					if (res_filename.startsWith(".")) {
						res_filename = res_filename.substring(2);
					}
					ClassLoader cl = this.getClass().getClassLoader();
					URL url = cl.getResource(res_filename);
					if (url != null) {
						img = ImageIO.read(url);
					} else {
						File f = new File(filename);
						if (f.canRead() == false) {
							throw new FileNotFoundException(filename);
						}
						//System.out.println("Loading image from file: " + filename);
						img = ImageIO.read(f);
					}
					switch (shade) {
					case NONE:
						break;

					case BOTTOM_HALF:
						for (int j = img.getHeight()/4; j < img.getHeight(); j++) {                    
							for (int i = 0; i < img.getWidth(); i++) {
								int rgb = img.getRGB(i, j);
								Color c = new Color(rgb);
								img.setRGB(i, j, c.darker().darker().getRGB());
							}
						}
						break;
						
					default:
						throw new RuntimeException("Unknown shade type: " + shade);
					}

					// Resize it
					BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
					scaled.getGraphics().drawImage(img, 0, 0, w, h, c);
					img = scaled;
					put(key, img);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			return img;			
		} else {
			return null;
		}
	}

}

