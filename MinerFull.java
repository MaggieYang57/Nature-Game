import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import processing.core.PImage;

public class MinerFull extends Entity{


	public MinerFull(String id, Point position,
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
		return entity.accept(new MinerFullVisitor());
	}
	
	public static MinerFull createMinerFull(String id, int resourceLimit,
            Point position, int actionPeriod, int animationPeriod,
            List<PImage> images)
	{
		return new MinerFull(id, position, images,
		resourceLimit, resourceLimit, actionPeriod, animationPeriod);
	}
	
	
	public void executeMinerFullActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler)
	   {
	      Optional<Entity> fullTarget = world.findNearest(this.position, Smith.class);

	      if (fullTarget.isPresent() &&
	              this.moveToFull(world, fullTarget.get(), scheduler))
	      {
	         this.transformFull(world, scheduler, imageStore);
	      }
	      else
	      {
	         scheduler.scheduleEvent(this,
	        			Activity.createActivityAction(this, world, imageStore),
	                 this.actionPeriod);
	      }
	   }
	
	public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
	{
		scheduler.scheduleEvent(this,
				Activity.createActivityAction(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this, Animation.createAnimationAction(this, 0), this.animationPeriod);

	}
	
	public void transformFull( WorldModel world,
            EventScheduler scheduler, ImageStore imageStore)
	{
		MinerNotFull miner = MinerNotFull.createMinerNotFull(this.id, this.resourceLimit,
		this.position, this.actionPeriod, this.animationPeriod,
		this.images);
		
		world.removeEntity( this);
		scheduler.unscheduleAllEvents( this);
		
		world.addEntity(miner);
		miner.scheduleActions(scheduler, world, imageStore);
	}
	
	public boolean moveToFull(WorldModel world, Entity target, EventScheduler scheduler)
	   {
        if (this.position.adjacent(target.position))
        {
            return true;
        }
        else
        {
            AStarPathingStrategy singleStep = new AStarPathingStrategy();
            Predicate<Point> canPassThrough = (point) -> world.withinBounds(point) && !world.isOccupied(point);
            BiPredicate<Point, Point> withinReach = (point1, point2) -> point1.adjacent(point2);

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
