import java.util.List;

import processing.core.PImage;

public class Obstacle extends Entity{

	public Obstacle(String id, Point position,
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
		return entity.accept(new ObstacleVisitor());
	}
	
	public static Obstacle createObstacle(String id, Point position,
            List<PImage> images)
	{
		return new Obstacle(id, position, images,
		0, 0, 0, 0);
	}
	
	public void scheduleActions(EventScheduler scheduler,
            WorldModel world, ImageStore imageStore)
	{
		return;
	}
	
	
}
