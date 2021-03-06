import java.util.List;

import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

public class Vein extends Entity{

    private static final Random rand = new Random();
	public static final String ORE_ID_PREFIX = "ore -- ";
	public static final int ORE_CORRUPT_MIN = 20000;
	public static final int ORE_CORRUPT_MAX = 30000;
	public static final String ORE_KEY = "ore";


	public Vein(String id, Point position,
	      List<PImage> images, int resourceLimit, int resourceCount,
	      int actionPeriod, int animationPeriod)
	{
	      super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
	}
	
	public <R> R accept(EntityVisitor<R> visitor)
	{
	 return visitor.visit(this);
	}
	
	public boolean instanceToVisitor (Entity entity)
	{
		return entity.accept(new VeinVisitor());
	}
	
	public void executeVeinActivity( WorldModel world,
            ImageStore imageStore, EventScheduler scheduler)
	{
		Optional<Point> openPt = world.findOpenAround(this.position);
		
		if (openPt.isPresent())
		{
			Entity ore = Ore.createOre(ORE_ID_PREFIX + this.id,
			openPt.get(), ORE_CORRUPT_MIN +
			rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
			imageStore.getImageList(ORE_KEY));
			world.addEntity(ore);
			((Ore)ore).scheduleActions( scheduler, world, imageStore);
		}
		
		scheduler.scheduleEvent( this,
		Activity.createActivityAction(this, world, imageStore),
		this.actionPeriod);
	}
	
	public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
	{
		scheduler.scheduleEvent( this,
				Activity.createActivityAction(this, world, imageStore),
                this.actionPeriod);
	}
	
	public static Vein createVein(String id, Point position, int actionPeriod,
            List<PImage> images) 
	{
		return new Vein(id, position, images, 0, 0,
		actionPeriod, 0);
	}
}
