import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import processing.core.PImage;

public class Snail extends Entity{

	public Snail(String id, Point position,
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
		return entity.accept(new SnailVisitor());
	}
	
	public static Snail createSnail(String id, int resourceLimit,
            Point position, int actionPeriod, int animationPeriod,
            List<PImage> images)
	{
		return new Snail(id, position, images,
		resourceLimit, 0, actionPeriod, animationPeriod);
	}
	
	
	public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
	{
		scheduler.scheduleEvent(this,
				Activity.createActivityAction(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this, Animation.createAnimationAction(this, 0), this.animationPeriod);
	}
	
	public void executeSnailActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler)
	   {
		Optional<Entity> fullTarget = world.findNearest(this.getPosition(), Ore.class);
	      long nextPeriod = this.actionPeriod;

	        if (fullTarget.isPresent())
	        {
	            Point tgtPos = fullTarget.get().getPosition();

	            if (this.moveToSnail(world, fullTarget.get(), scheduler))
	            {
	            	world.removeEntityAt(tgtPos);
	            }
	        }
	        scheduler.scheduleEvent(this,
	                new Activity(this, world, imageStore, 0),
	                nextPeriod);
	   }

	
	public  boolean moveToSnail(WorldModel world, Entity target, EventScheduler scheduler)
	   {
		AStarPathingStrategy singleStep = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = (point) -> world.withinBounds(point) && !world.isOccupied(point);
        BiPredicate<Point, Point> withinReach = (point1, point2) -> point1.adjacent(point2);

        if (this.position.adjacent(target.getPosition()))
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else
        {
            List<Point> path = singleStep.computePath(this.getPosition(), target.getPosition(), canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
            Point nextPos = null;
            if (path.size() != 0) {
                nextPos = path.get(0);
            }

            if (nextPos != null)
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }
                world.moveEntity(this, nextPos);
            }
            return false;

        }
	   }
}
