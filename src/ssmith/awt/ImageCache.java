package ssmith.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Hashtable;

public class ImageCache extends Hashtable<String, Image> {

	private static final long serialVersionUID = 1L;

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private MediaTracker mt;
	private Component c;

	public ImageCache(Component _c) {
		super();

		c = _c;
		mt = new MediaTracker(c);
	}


	public Image getImage(String filename, int w, int h, boolean shade) {
		String key = filename + "_" + w + "_" + h + "_" + (shade?"s":"n");
		Image img = get(key);
		if (img == null) {
			String res_filename = filename;
			if (res_filename.startsWith(".")) {
				res_filename = res_filename.substring(2);
			}
			ClassLoader cl = this.getClass().getClassLoader();
			URL url = cl.getResource(res_filename);
			if (url != null) {
				//System.out.println("Loading image from URL: " + url);
				img = tk.getImage(url);
			} else {
				if (new File(filename).canRead() == false) {
					//throw new FileNotFoundException(filename);
					System.err.println("Unable to Load image " + filename);
				}
				//System.out.println("Loading image from file: " + filename);
				img = tk.getImage(filename);
			}
			mt.addImage(img, 1);
			try {
				mt.waitForID(1);
			}
			catch (InterruptedException e) {
				System.err.println("Error loading images: " + e.getMessage());
			}
			mt.removeImage(img);

			if (shade) {
				BufferedImage buffered = new BufferedImage(img.getWidth(c), img.getHeight(c), BufferedImage.TYPE_INT_ARGB);
				buffered.getGraphics().drawImage(img, 0, 0, c);

				for (int j = buffered.getHeight()/4; j < buffered.getHeight(); j++) {                    
					for (int i = 0; i < buffered.getWidth(); i++) {
						int rgb = buffered.getRGB(i, j);
						Color c = new Color(rgb);
						buffered.setRGB(i, j, c.darker().darker().getRGB());
					}
				}
				img = buffered;
			}
			img = img.getScaledInstance(w, h, Image.SCALE_DEFAULT);
			put(key, img);
		}
		return img;
	}

}

