import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;
import processing.core.*;

public final class VirtualWorld
   extends PApplet
{
   public static final int TIMER_ACTION_PERIOD = 100;

   public static final int VIEW_WIDTH = 640;
   public static final int VIEW_HEIGHT = 480;
   public static final int TILE_WIDTH = 32;
   public static final int TILE_HEIGHT = 32;
   public static final int WORLD_WIDTH_SCALE = 2;
   public static final int WORLD_HEIGHT_SCALE = 2;

   public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   public static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   public static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   public static final String IMAGE_LIST_FILE_NAME = "imagelist";
   public static final String DEFAULT_IMAGE_NAME = "background_default";
   public static final int DEFAULT_IMAGE_COLOR = 0x808080;

   public static final String LOAD_FILE_NAME = "gaia.sav";

   public static final String FAST_FLAG = "-fast";
   public static final String FASTER_FLAG = "-faster";
   public static final String FASTEST_FLAG = "-fastest";
   public static final double FAST_SCALE = 0.5;
   public static final double FASTER_SCALE = 0.25;
   public static final double FASTEST_SCALE = 0.10;

   public static double timeScale = 1.0;
   
   public ImageStore imageStore;
   public WorldModel world;
   public WorldView view;
   public EventScheduler scheduler;

   public long next_time;

   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */
   public void setup()
   {
      this.imageStore = new ImageStore(
         createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
         createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
         TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, LOAD_FILE_NAME, imageStore);

      scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   public void draw()
   {
      long time = System.currentTimeMillis();
      if (time >= next_time)
      {
         this.scheduler.updateOnTime( time);
         next_time = time + TIMER_ACTION_PERIOD;
      }

      this.view.drawViewport();
   }

   public void keyPressed()
   {
      if (key == CODED)
      {
         int dx = 0;
         int dy = 0;

         switch (keyCode)
         {
            case UP:
               dy = -1;
               break;
            case DOWN:
               dy = 1;
               break;
            case LEFT:
               dx = -1;
               break;
            case RIGHT:
               dx = 1;
               break;
         }
         
         this.view.shiftView(dx,dy);
      }
   }
   
   public void mouseClicked() {
	      int tileX = (int)mouseX/TILE_WIDTH;
	      int tileY = (int)mouseY/TILE_HEIGHT;
	      int currentTileX = tileX + view.viewport.col;
	      int currentTileY = tileY + view.viewport.row;
	      Point tile = new Point(currentTileX, currentTileY);
	      String dirt = "dirt";
	      Background dirtBackground = new Background(dirt, imageStore.getImageList(dirt));
	      world.setBackground(tile, dirtBackground);
	      world.setBackground(new Point(tile.x + 1, tile.y), dirtBackground);
	      world.setBackground(new Point(tile.x - 1, tile.y), dirtBackground);
	      world.setBackground(new Point(tile.x, tile.y + 1), dirtBackground);
	      world.setBackground(new Point(tile.x, tile.y - 1), dirtBackground);

	      world.setBackground(new Point(tile.x + 2, tile.y + 2), dirtBackground);
	      world.setBackground(new Point(tile.x + 2 , tile.y - 2), dirtBackground);
	      world.setBackground(new Point(tile.x - 2, tile.y + 2), dirtBackground);
	      world.setBackground(new Point(tile.x - 2, tile.y - 2), dirtBackground);
	      
	      world.removeEntityAt(tile);
	      String raccoon = "raccoon";
	      Raccoon raccoonSpawn = new Raccoon(raccoon, tile, imageStore.getImageList(raccoon), 0, 0, 0, 2);
	      world.addEntity(raccoonSpawn);
	      raccoonSpawn.scheduleActions(scheduler, world, imageStore);
	      
	      Optional<Entity> movingSnail = world.findNearest(tile, Smith.class);
	      
	      long nextPeriod = raccoonSpawn.actionPeriod;

	      if (movingSnail.isPresent())
	        {
	            Point tgtPos = movingSnail.get().getPosition();

	            Snail snail = new Snail("snail", tgtPos, imageStore.getImageList("snail"), 0, 0, 0, 0);
	             world.removeEntityAt(tgtPos);
	             world.addEntity(snail);
	            snail.scheduleActions(scheduler, world, imageStore);
	           
	        }
	        scheduler.scheduleEvent(raccoonSpawn,
	                new Activity(raccoonSpawn, world, imageStore, 0),
	                nextPeriod);

	   }

   public static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
         imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   public static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   public static void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         loadImages(in, imageStore, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }
   
   public static void loadImages(Scanner in, ImageStore imageStore,
           PApplet screen)
   {
	   int lineNumber = 0;
	   while (in.hasNextLine()) {
		   try 
		   {
			   imageStore.processImageLine(in.nextLine(), screen);
		   } 
		   catch (NumberFormatException e) 
		   {
			   System.out.println(String.format("Image format error on line %d", lineNumber));
		   }
		   lineNumber++;
	}
	} 

   public static void loadWorld(WorldModel world, String filename,
      ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.load(in, world);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public void scheduleActions(WorldModel world,
      EventScheduler scheduler, ImageStore imageStore)
   {
      for (Entity entity : world.entities)
      {
          entity.scheduleActions(scheduler, world, imageStore);
      }
   }

   public static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}