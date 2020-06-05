import java.util.List;
import java.util.Optional;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import processing.core.PImage;

public class Raccoon extends Entity {

    public Raccoon(String id, Point position,
  	      List<PImage> images, int resourceLimit, int resourceCount,
  	      int actionPeriod, int animationPeriod)
  	{
  	      super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
  	}
    
    public static Raccoon createRaccoon(String id, int resourceLimit,
            Point position, int actionPeriod, int animationPeriod,
            List<PImage> images)
	{
		return new Raccoon(id, position, images,
		resourceLimit, 0, actionPeriod, animationPeriod);
	}

    public void executeRaccoonActivity(WorldModel world,
                                ImageStore imageStore, EventScheduler scheduler)
    {
	      
	      Optional<Entity> raccoonTarget = world.findNearest(this.getPosition(), MinerNotFull.class);
	      long nextPeriod = this.actionPeriod;

	        if (raccoonTarget.isPresent())
	        {
	            Point tgtPos = raccoonTarget.get().getPosition();

	            if (this.moveToRaccoon(world, raccoonTarget.get(), scheduler))
	            {

	                Coin coin = new Coin("coin", tgtPos, imageStore.getImageList("coin"), 0, 0, 0, 0);
	                world.addEntity(coin);
	                coin.scheduleActions(scheduler, world, imageStore);
	            }
	        }
	        scheduler.scheduleEvent(this,
	                new Activity(this, world, imageStore, 0),
	                nextPeriod);
    }

    public boolean moveToRaccoon(WorldModel world,
                                 Entity target, EventScheduler scheduler)
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
    
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
	{
		scheduler.scheduleEvent(this,
				Activity.createActivityAction(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this, Animation.createAnimationAction(this, 0), this.animationPeriod);

	}

    public <R> R accept(EntityVisitor<R> visitor)
	{
	 return visitor.visit(this);
	}
	
	public boolean instanceToVisitor (Entity entity)
	{
		return entity.accept(new RaccoonVisitor());
	}


}