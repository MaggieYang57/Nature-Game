import processing.core.PImage;

import java.util.List;

public class Coin extends Entity {

	public Coin(String id, Point position,
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
			return entity.accept(new CoinVisitor());
		}
		
		public static Coin createCoin(String id, Point position,
	            List<PImage> images)
		{
			return new Coin(id, position, images,
			0, 0, 0, 0);
		}
		
		public void scheduleActions(EventScheduler scheduler,
	            WorldModel world, ImageStore imageStore)
		{
			return;
		}

}