import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import processing.core.PImage;

public class Blob extends Entity {

	public static final String QUAKE_KEY = "quake";
	public static final String QUAKE_ID = "quake";


	public Blob(String id, Point position,
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
		return entity.accept(new BlobEntityVisitor());
	}
	
	public static Blob createOreBlob(String id, Point position,
            int actionPeriod, int animationPeriod, List<PImage> images)
	{
		return new Blob(id, position, images,
		0, 0, actionPeriod, animationPeriod);
	}	
	
	public void executeOreBlobActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler)
	   {
	      Optional<Entity> blobTarget = world.findNearest( this.position, Vein.class);
	      long nextPeriod = this.actionPeriod;

	      if (blobTarget.isPresent())
	      {
	         Point tgtPos = blobTarget.get().getPosition();

	         if (moveToOreBlob(world, blobTarget.get(), scheduler))
	         {
	            Entity quake = Quake.createQuake(tgtPos,
	                    imageStore.getImageList(QUAKE_KEY));

	            world.addEntity(quake);
	            nextPeriod += this.actionPeriod;
	            ((Quake) quake).scheduleActions(scheduler, world, imageStore);
	         }
	      }

	      scheduler.scheduleEvent(this,
	    			Activity.createActivityAction(this, world, imageStore),
	              nextPeriod);
	   }
	
	public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
	{
		scheduler.scheduleEvent( this,
				Activity.createActivityAction(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent( this,
        		Animation.createAnimationAction(this, 0), this.animationPeriod);
	}
	
	public boolean moveToOreBlob(WorldModel world,
            Entity target, EventScheduler scheduler)
	{
		AStarPathingStrategy singleStep = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = (point) -> world.withinBounds(point) && !world.isOccupied(point);
        BiPredicate<Point, Point> withinReach = (point1, point2) -> point1.adjacent(point2);

        if (this.position.adjacent(target.position))
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else
        {
            //if path is empty, nextPost is the currentPos. return false.
            List<Point> path = singleStep.computePath(this.getPosition(), target.getPosition(), canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
            Point nextPos = null;
            if (path.size() != 0) {
                nextPos = path.get(0);
            }

            //Point nextPos = nextPositionOreBlob(world, target.getPosition());
            //!this.getPosition().equals(nextPos)
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
	
	public Point nextPositionOreBlob( WorldModel world, Point destPos)
	{
	      int horiz = Integer.signum(destPos.x - this.position.x);
	      Point newPos = new Point(this.position.x + horiz,
	              this.position.y);

	      Optional<Entity> occupant = world.getOccupant( newPos);

	      if (horiz == 0 ||
	              (occupant.isPresent() && !(occupant.getClass().equals(Ore.class))))
	      {
	         int vert = Integer.signum(destPos.y - this.position.y);
	         newPos = new Point(this.position.x, this.position.y + vert);
	         occupant = world.getOccupant( newPos);

	         if (vert == 0 ||
	                 (occupant.isPresent() && !(occupant.getClass().equals(Ore.class))))
	         {
	            newPos = this.position;
	         }
	      }

	      return newPos;
	 }
	
}
