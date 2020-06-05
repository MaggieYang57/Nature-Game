import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import processing.core.PImage;

public class MinerNotFull extends Entity{

	public MinerNotFull(String id, Point position,
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
		return entity.accept(new MinerNotFullVisitor());
	}
	
	public static MinerNotFull createMinerNotFull(String id, int resourceLimit,
            Point position, int actionPeriod, int animationPeriod,
            List<PImage> images)
	{
		return new MinerNotFull(id, position, images,
		resourceLimit, 0, actionPeriod, animationPeriod);
	}
	
	
	public void executeMinerNotFullActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler)
	   {
	      Optional<Entity> notFullTarget = world.findNearest( this.position, Ore.class);

	      if (!notFullTarget.isPresent() ||
	              !this.moveToNotFull(world, notFullTarget.get(), scheduler) ||
	              !transformNotFull( world, scheduler, imageStore))
	      {
	         scheduler.scheduleEvent(this,
	        			Activity.createActivityAction(this, world, imageStore),
	                 this.actionPeriod);
	      }
	   }
	
	public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
	{
		scheduler.scheduleEvent( this,
				Activity.createActivityAction(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent( this,
        		Animation.createAnimationAction(this, 0), this.animationPeriod);
	}
	
	public boolean transformNotFull( WorldModel world,
             EventScheduler scheduler, ImageStore imageStore)
	{
		if (this.resourceCount >= this.resourceLimit)
		{
			MinerFull miner = (MinerFull) MinerFull.createMinerFull(this.id, this.resourceLimit,
			this.position, this.actionPeriod, this.animationPeriod,
			this.images);
			
			world.removeEntity( this);
			scheduler.unscheduleAllEvents( this);
			
			world.addEntity( miner);
			miner.scheduleActions( scheduler, world, imageStore);
			
			return true;
		}
		
		return false;
	}
	
	public  boolean moveToNotFull(WorldModel world, Entity target, EventScheduler scheduler)
	   {

        if (this.getPosition().adjacent(target.getPosition()))
        {
            //CHECK
            this.resourceCount=this.resourceCount + 1;
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

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
