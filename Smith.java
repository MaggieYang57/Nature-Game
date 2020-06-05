import java.util.List;

import processing.core.PImage;

public class Smith extends Entity{

	public Smith(String id, Point position,
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
		return entity.accept(new SmithVisitor());
	}
	
	public static Smith createBlacksmith(String id, Point position,
            List<PImage> images)
	{
		return new Smith(id, position, images,
		0, 0, 0, 0);
	}
	
	public void scheduleActions(EventScheduler scheduler,
            WorldModel world, ImageStore imageStore)
	{
		return;
	}

}
