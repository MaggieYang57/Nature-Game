import java.util.List;

import processing.core.PImage;

public class Quake extends Entity{
    
	public static final String QUAKE_ID = "quake";
    public static final int QUAKE_ACTION_PERIOD = 1100;
    public static final int QUAKE_ANIMATION_PERIOD = 100;
    public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

	public Quake(String id, Point position,
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
		return entity.accept(new QuakeVisitor());
	}
	
	public void executeQuakeActivity( WorldModel world,
            ImageStore imageStore, EventScheduler scheduler)
	{
		scheduler.unscheduleAllEvents( this);
		world.removeEntity(this);
	}
	
	public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
	{
		scheduler.scheduleEvent(this,
				Activity.createActivityAction(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent( this,
        		Animation.createAnimationAction(this, QUAKE_ANIMATION_REPEAT_COUNT),
                this.animationPeriod);        
	}
	
	public static Quake createQuake(Point position, List<PImage> images)
	   {
	      return new Quake(QUAKE_ID, position, images,
	              0, 0, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);
	   }
}
