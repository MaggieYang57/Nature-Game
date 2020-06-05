
public class Activity extends Action 
{
	
	public Activity(Entity entity, WorldModel world, ImageStore imageStore, int repeatCount)
	{
		super(entity, world, imageStore, repeatCount);
	}
	
	public static Activity createActivityAction(Entity entity, WorldModel world,
            ImageStore imageStore)
	{
		return new Activity(entity, world, imageStore, 0);
	}
	
	public void executeAction(EventScheduler scheduler)
	{
		
		if (this.entity.accept( new MinerFullVisitor() ))
		{
			((MinerFull)this.entity).executeMinerFullActivity( this.world,
					this.imageStore, scheduler);
		}

		else if (this.entity.accept( new MinerNotFullVisitor() ))
	    {
	    	((MinerNotFull)this.entity).executeMinerNotFullActivity( this.world,
                    this.imageStore, scheduler);
	    }
		else if (this.entity.accept( new OreVisitor() ))
	    {
	    	((Ore)this.entity).executeOreActivity( this.world, this.imageStore,
                    scheduler);
	    }
		else if (this.entity.accept( new BlobEntityVisitor() ))
	    {
	    	((Blob)this.entity).executeOreBlobActivity( this.world,
                    this.imageStore, scheduler);
	    }
		else if (this.entity.accept( new QuakeVisitor() ))
	    {
	    	((Quake)this.entity).executeQuakeActivity( this.world, this.imageStore,
                    scheduler);
	    }
		else if (this.entity.accept( new VeinVisitor() ))
	    {
	    	((Vein)this.entity).executeVeinActivity( this.world, this.imageStore,
                    scheduler);
	    }
		else if (this.entity.accept( new RaccoonVisitor() ))
	    {
	    	((Raccoon)this.entity).executeRaccoonActivity( this.world, this.imageStore,
                    scheduler);
	    }
		else if (this.entity.accept( new SnailVisitor() ))
	    {
	    	((Snail)this.entity).executeSnailActivity( this.world, this.imageStore,
                    scheduler);
	    }
	   }
	
}
