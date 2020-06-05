import java.util.List;

import processing.core.PImage;

abstract public class Entity {
	
	protected String id;
	protected Point position;
	protected List<PImage> images;
	protected int imageIndex;
	protected int resourceLimit;
	protected int resourceCount;
	protected int actionPeriod;
	protected int animationPeriod;
	
	public abstract <R> R accept(EntityVisitor<R> visitor);
	
	protected abstract boolean instanceToVisitor(Entity e);
	
	public abstract void scheduleActions(EventScheduler scheduler,
            WorldModel world, ImageStore imageStore);

	public Entity(String id, Point position,
	      List<PImage> images, int resourceLimit, int resourceCount,
	      int actionPeriod, int animationPeriod)
   {
		this.id = id;
	    this.position = position;
	    this.images = images;
	    this.imageIndex = 0;
	    this.resourceLimit = resourceLimit;
	    this.resourceCount = resourceCount;
	    this.actionPeriod = actionPeriod;
	    this.animationPeriod = animationPeriod;    
   }

	public int getAnimationPeriod()
	{
		return animationPeriod;
	}
	
	public void nextImage()
	{
	      this.imageIndex = (this.imageIndex+ 1) % this.images.size();    
	}	
	
	public static PImage getCurrentImage(Object entity)
	{
		return ((Entity)entity).images.get(((Entity)entity).imageIndex);
	}
	
	public Point getPosition()
	{
		return this.position;
	}
	
	public void setPosition(Point p1)
	{
		this.position = p1;
	}
}

