package ssmith.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;

import javax.swing.JFrame;

/**
 * RotateImage45Degrees.java - 1. scales an image's dimensions by a factor of
 * two 2. rotates it 45 degrees around the image center 3. displays the
 * processed image
 */
public class ImageFunctions extends JFrame {

	private static final long serialVersionUID = 1L;

	public static Image rotateImage(Image inputImage, float ang_deg,  ImageObserver ob) {
		BufferedImage sourceBI = new BufferedImage(inputImage.getWidth(ob), inputImage.getHeight(ob), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = (Graphics2D) sourceBI.getGraphics();
		g.drawImage(inputImage, 0, 0, null);

		AffineTransform at = new AffineTransform();

		// scale image
		//at.scale(2.0, 2.0);

		// rotate 45 degrees around image center
		at.rotate(ang_deg * Math.PI / 180.0, sourceBI.getWidth()/2, sourceBI.getHeight()/2);

		// instantiate and apply affine transformation filter
		BufferedImageOp bio;
		bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

		BufferedImage destinationBI = bio.filter(sourceBI, null);

		return destinationBI;
	}

	public static Image scaleImage(Image inputImage, int w, int h,  ImageObserver ob) {
		BufferedImage sourceBI = new BufferedImage(inputImage.getWidth(ob), inputImage.getHeight(ob), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = (Graphics2D) sourceBI.getGraphics();
		g.drawImage(inputImage, 0, 0, null);

		AffineTransform at = new AffineTransform();

		// scale image
		float sx = (float)w / (float)inputImage.getWidth(ob);
		float sy = (float)h / (float)inputImage.getHeight(ob);
		//at.scale(w / inputImage.getWidth(ob), h / inputImage.getHeight(ob));
		at.scale(sx, sy);

		//at.rotate(45 * Math.PI / 180.0, sourceBI.getWidth()/2, sourceBI.getHeight()/2);

		// instantiate and apply affine transformation filter
		BufferedImageOp bio;
		bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

		BufferedImage destinationBI = bio.filter(sourceBI, null);

		return destinationBI;
	}

	
	public static BufferedImage[] ExtractGraphics(Image img, int cols, int rows, ImageObserver ob) {  
		int w = img.getWidth(ob)/cols;			  
		int h = img.getHeight(ob)/rows;  
		int num = 0;  
		BufferedImage imgs[] = new BufferedImage[cols*rows];  
		for(int y = 0; y < rows; y++) {  
			for(int x = 0; x < cols; x++) {  
				imgs[num] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);  
				// Tell the graphics to draw only one block of the image  
				Graphics2D g = imgs[num].createGraphics();  
				g.drawImage(img, 0, 0, w, h, w*x, h*y, w*x+w, h*y+h, null);  
				g.dispose();  
				num++;  
			}  
		}  
		return imgs;  
	}  

	
	public static BufferedImage[] ExtractGraphics(Image img, int cols, int rows, int sz, int gap_px, ImageObserver ob) {  
		int w = sz;//(img.getWidth(ob)/cols) - gap_px;			  
		int h = sz;//(img.getHeight(ob)/rows) - gap_px;  
		int num = 0;  
		BufferedImage imgs[] = new BufferedImage[cols*rows];  
		for(int y = 0; y < rows; y++) {  
			for(int x = 0; x < cols; x++) {  
				imgs[num] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);  
				// Tell the graphics to draw only one block of the image  
				Graphics2D g = imgs[num].createGraphics();  
				int x2 = (w+gap_px)*x;
				int y2 = (h+gap_px)*y;
				g.drawImage(img, 0, 0, w, h, x2, y2, x2+w, y2+h, null);  
				g.dispose();  
				num++;  
			}  
		}  
		return imgs;  
	}  

}

