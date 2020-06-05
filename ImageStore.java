import java.util.HashMap;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PImage;

final class ImageStore
{

   public static final Random rand = new Random();
   public static final int COLOR_MASK = 0xffffff;
   public static final int KEYED_IMAGE_MIN = 5;
   private static final int KEYED_RED_IDX = 2;
   private static final int KEYED_GREEN_IDX = 3;
   private static final int KEYED_BLUE_IDX = 4;	
   
   public Map<String, List<PImage>> images;
   public List<PImage> defaultImages;

   public ImageStore(PImage defaultImage)
   {
      this.images = new HashMap<>();
      defaultImages = new LinkedList<>();
      defaultImages.add(defaultImage);
   }
   
   public List<PImage> getImageList(String key) {
	      return this.images.getOrDefault(key, this.defaultImages);
	   }
   
   public List<PImage> getImages(String key) 
   {
	   List<PImage> imgs = images.get(key);
	   if (imgs == null) {
		   imgs = new LinkedList<>();
		   images.put(key, imgs);
	   }
	   return imgs;
   }
   
   public void load(Scanner in, WorldModel world) {
	      int lineNumber = 0;
	      while (in.hasNextLine()) {
	         try {
	            if (!world.processLine(in.nextLine(), this)) {
	               System.err.println(String.format("invalid entry on line %d",
	                       lineNumber));
	            }
	         } catch (NumberFormatException e) {
	            System.err.println(String.format("invalid entry on line %d",
	                    lineNumber));
	         } catch (IllegalArgumentException e) {
	            System.err.println(String.format("issue on line %d: %s",
	                    lineNumber, e.getMessage()));
	         }
	         lineNumber++;
	      }
	   }

   public void processImageLine(String line, PApplet screen) {
      String[] attrs = line.split("\\s");
      if (attrs.length >= 2) {
         String key = attrs[0];
         PImage img = screen.loadImage(attrs[1]);
         if (img != null && img.width != -1) {
            List<PImage> imgs = getImages(key);
            imgs.add(img);

            if (attrs.length >= KEYED_IMAGE_MIN) {
               int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
               int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
               int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
               setAlpha(img, screen.color(r, g, b), 0);
            }
         }
      }
   }

   /*
     Called with color for which alpha should be set and alpha value.
     setAlpha(img, color(255, 255, 255), 0));
   */
   public void setAlpha(PImage img, int maskColor, int alpha) 
   {
      int alphaValue = alpha << 24;
      int nonAlpha = maskColor & COLOR_MASK;
      img.format = PApplet.ARGB;
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++) {
         if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
            img.pixels[i] = alphaValue | nonAlpha;
         }
      }
      img.updatePixels();
   }
}