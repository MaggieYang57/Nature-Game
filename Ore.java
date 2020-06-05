import java.util.List;
import java.util.Random;

import processing.core.PImage;

public class Ore extends Entity{
    
    private static final Random rand = new Random();
    public static final String BLOB_KEY = "blob";
    public static final String BLOB_ID_SUFFIX = " -- blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;

	public Ore(String id, Point position,
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
		return entity.accept(new OreVisitor());
	}
	
	public static Ore createOre(String id, Point position, int actionPeriod,
            List<PImage> images)
	{
		return new Ore(id, position, images, 0, 0,
		actionPeriod, 0);
	}
	
	public void executeOreActivity(WorldModel world,
            ImageStore imageStore, EventScheduler scheduler)
	{
		Point pos = this.position;  // store current position before removing
		
		world.removeEntity(this);
		scheduler.unscheduleAllEvents(this);
		
		Entity blob = Blob.createOreBlob(this.id + BLOB_ID_SUFFIX,
		pos, this.actionPeriod / BLOB_PERIOD_SCALE,
		BLOB_ANIMATION_MIN +
		rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN),
		imageStore.getImageList(BLOB_KEY));
		
		world.addEntity( blob);
		((Blob) blob).scheduleActions(scheduler, world, imageStore);
	}
	
	public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
	{
		scheduler.scheduleEvent( this,
				Activity.createActivityAction(this, world, imageStore),
                this.actionPeriod);
	}
	
}
