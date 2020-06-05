import processing.core.PImage;

import java.util.*;

final class WorldModel
{
   public int numRows;
   public int numCols;
   public Background background[][];
   public Entity occupancy[][];
   public Set<Entity> entities;
     
   public static final int PROPERTY_KEY = 0;

   public static final String BGND_KEY = "background";
   public static final int BGND_NUM_PROPERTIES = 4;
   public static final int BGND_ID = 1;
   public static final int BGND_COL = 2;
   public static final int BGND_ROW = 3;

   public static final String MINER_KEY = "miner";
   public static final int MINER_NUM_PROPERTIES = 7;
   public static final int MINER_ID = 1;
   public static final int MINER_COL = 2;
   public static final int MINER_ROW = 3;
   public static final int MINER_LIMIT = 4;
   public static final int MINER_ACTION_PERIOD = 5;
   public static final int MINER_ANIMATION_PERIOD = 6;

   public static final String OBSTACLE_KEY = "obstacle";
   public static final int OBSTACLE_NUM_PROPERTIES = 4;
   public static final int OBSTACLE_ID = 1;
   public static final int OBSTACLE_COL = 2;
   public static final int OBSTACLE_ROW = 3;

   public static final String ORE_KEY = "ore";
   public static final int ORE_NUM_PROPERTIES = 5;
   public static final int ORE_ID = 1;
   public static final int ORE_COL = 2;
   public static final int ORE_ROW = 3;
   public static final int ORE_ACTION_PERIOD = 4;
   public static final int ORE_REACH = 1;

   public static final String SMITH_KEY = "blacksmith";
   public static final int SMITH_NUM_PROPERTIES = 4;
   public static final int SMITH_ID = 1;
   public static final int SMITH_COL = 2;
   public static final int SMITH_ROW = 3;

   public static final String VEIN_KEY = "vein";
   public static final int VEIN_NUM_PROPERTIES = 5;
   public static final int VEIN_ID = 1;
   public static final int VEIN_COL = 2;
   public static final int VEIN_ROW = 3;
   public static final int VEIN_ACTION_PERIOD = 4;


   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }
   
   public void tryAddEntity( Entity entity)
   {
      if (isOccupied( entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity(entity);
   }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < this.numRows &&
              pos.x >= 0 && pos.x < this.numCols;
   }

   public boolean isOccupied( Point pos)
   {
      return withinBounds( pos) &&
              getOccupancyCell( pos) != null;
   }


   public Optional<Entity> nearestEntity(List<Entity> entities, Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         double nearestDistance = distanceSquared(nearest.getPosition(), pos);

         for (Entity other : entities)
         {
            double otherDistance = distanceSquared(other.getPosition(), pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   public  int distanceSquared(Point p1, Point p2)
   {
      int deltaX = p1.x - p2.x;
      int deltaY = p1.y - p2.y;

      return deltaX * deltaX + deltaY * deltaY;
   }

   public Optional<Entity> findNearest(  Point pos, Class kind)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : entities)
      {
         if (entity.getClass() == kind)
         {
            ofType.add(entity);
         }
      }

      return nearestEntity(ofType, pos);
   }

   /*
      Assumes that there is no entity currently occupying the
      intended destination cell.
   */
   public void addEntity( Entity entity)
   {
      if (withinBounds( entity.getPosition()))
      {
         setOccupancyCell( entity.getPosition(), entity);
         entities.add(entity);
      }
   }

   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds( pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell( pos, entity);
         entity.setPosition(pos);
      }
   }

   public void removeEntity(Entity entity)
   {
      removeEntityAt( entity.getPosition());
   }

   public void removeEntityAt( Point pos)
   {
      if (withinBounds( pos)
              && getOccupancyCell( pos) != null)
      {
         Entity entity = getOccupancyCell( pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */

         entity.setPosition(new Point(-1, -1));
         this.entities.remove(entity);
         setOccupancyCell( pos, null);
      }
   }

//   public PImage getCurrentImage(Object entity)
//   {
//      if (entity instanceof Background)
//      {
//         return ((Background)entity).images
//            .get(((Background)entity).imageIndex);
//      }
//      else if (entity instanceof Entity)
//		{
//			return ((Entity)entity).images.get(((Entity)entity).imageIndex);
//		}
//		else if (entity instanceof MinerNotFull)
//	    {
//	    	return ((MinerNotFull)entity).images.get(((MinerNotFull)entity).imageIndex);
//	    }
//		else if (entity instanceof Ore)
//	    {
//	    	return ((Ore)entity).images.get(((Ore)entity).imageIndex);
//	    }
//		else if (entity instanceof Blob)
//	    {
//	    	return ((Blob)entity).images.get(((Blob)entity).imageIndex);
//	    }
//		else if (entity instanceof Smith)
//	    {
//	    	return ((Smith)entity).images.get(((Smith)entity).imageIndex);
//	    }
//		else if (entity instanceof Quake)
//	    {
//	    	return ((Quake)entity).images.get(((Quake)entity).imageIndex);
//	    }
//		else if (entity instanceof Obstacle)
//	    {
//	    	return ((Obstacle)entity).images.get(((Obstacle)entity).imageIndex);
//	    }
//		else if (entity instanceof Vein)
//	    {
//	    	return ((Vein)entity).images.get(((Vein)entity).imageIndex);
//	    }
//		else
//	      {
//	         throw new UnsupportedOperationException(
//	            String.format("getCurrentImage not supported for %s",
//	            entity));
//	      }
//   }
   
   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds( pos))
      {
         return Optional.of(Background.getCurrentImage(getBackgroundCell( pos)));
      }
      else
      {
         return Optional.empty();
      }
   }

   public void setBackground(Point pos, Background background)
   {
      if (withinBounds( pos))
      {
         setBackgroundCell( pos, background);
      }
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied( pos))
      {
         return Optional.of(getOccupancyCell( pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   public Entity getOccupancyCell(Point pos)
   {
      return occupancy[pos.y][pos.x];
   }

   public void setOccupancyCell(Point pos,
                                       Entity entity)
   {
      occupancy[pos.y][pos.x] = entity;
   }

   public Background getBackgroundCell( Point pos)
   {
      return background[pos.y][pos.x];
   }

   public void setBackgroundCell( Point pos, Background background)
   {
      this.background[pos.y][pos.x] = background;
   }


   public Optional<Point> findOpenAround( Point pos)
   {
      for (int dy = -ORE_REACH; dy <= ORE_REACH; dy++)
      {
         for (int dx = -ORE_REACH; dx <= ORE_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds( newPt) &&
                    !this.isOccupied( newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }
   
   public boolean processLine(String line, ImageStore imageStore) 
   {
		String[] properties = line.split("\\s");
		if (properties.length > 0) 
		{
			switch (properties[PROPERTY_KEY]) 
			{
				case BGND_KEY:
					return parseBackground(properties, imageStore);
				case MINER_KEY:
					return parseMiner(properties, imageStore);
				case OBSTACLE_KEY:
					return parseObstacle(properties,imageStore);
				case ORE_KEY:
					return parseOre(properties,imageStore);
				case SMITH_KEY:
					return parseSmith(properties, imageStore);
				case VEIN_KEY:
					return parseVein(properties, imageStore);
			}
		}
		return false;
   }
   
   public boolean parseBackground(String[] properties,
           ImageStore imageStore) 
	{
		if (properties.length == BGND_NUM_PROPERTIES) 
		{
			Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
			Integer.parseInt(properties[BGND_ROW]));
			String id = properties[BGND_ID];
			setBackground(pt, new Background(id, imageStore.getImageList(id)));
		}	
	return properties.length == BGND_NUM_PROPERTIES;
	}

   public boolean parseMiner(String[] properties,
      ImageStore imageStore) 
   {
		if (properties.length == MINER_NUM_PROPERTIES) 
		{
			Point pt = new Point(Integer.parseInt(properties[MINER_COL]),
			Integer.parseInt(properties[MINER_ROW]));
			Entity entity = MinerNotFull.createMinerNotFull(properties[MINER_ID],
			Integer.parseInt(properties[MINER_LIMIT]),
			pt,
			Integer.parseInt(properties[MINER_ACTION_PERIOD]),
			Integer.parseInt(properties[MINER_ANIMATION_PERIOD]),
			imageStore.getImageList(MINER_KEY));
			tryAddEntity(entity);
		}

	return properties.length == MINER_NUM_PROPERTIES;
	}

	public boolean parseObstacle(String[] properties,
	         ImageStore imageStore) 
	{
		if (properties.length == OBSTACLE_NUM_PROPERTIES) 
		{
			Point pt = new Point(
			Integer.parseInt(properties[OBSTACLE_COL]),
			Integer.parseInt(properties[OBSTACLE_ROW]));
			Entity entity = Obstacle.createObstacle(properties[OBSTACLE_ID],
			pt, imageStore.getImageList(OBSTACLE_KEY));
			tryAddEntity(entity);
		}
	return properties.length == OBSTACLE_NUM_PROPERTIES;
	}

	public boolean parseOre(String[] properties,
	    ImageStore imageStore) 
	{
		if (properties.length == ORE_NUM_PROPERTIES) {
			Point pt = new Point(Integer.parseInt(properties[ORE_COL]),
			Integer.parseInt(properties[ORE_ROW]));
			Entity entity = Ore.createOre(properties[ORE_ID],
			pt, Integer.parseInt(properties[ORE_ACTION_PERIOD]),
			imageStore.getImageList(ORE_KEY));
			tryAddEntity(entity);
		}
	return properties.length == ORE_NUM_PROPERTIES;
	}

	public boolean parseSmith(String[] properties,
	      ImageStore imageStore) 
	{
		if (properties.length == SMITH_NUM_PROPERTIES) 
		{
			Point pt = new Point(Integer.parseInt(properties[SMITH_COL]),
			Integer.parseInt(properties[SMITH_ROW]));
			Entity entity = Smith.createBlacksmith(properties[SMITH_ID],
			pt, imageStore.getImageList(SMITH_KEY));
			tryAddEntity(entity);
		}
	
	return properties.length == SMITH_NUM_PROPERTIES;
	}

	public boolean parseVein(String[] properties,
	     ImageStore imageStore) 
	{
		if (properties.length == VEIN_NUM_PROPERTIES) 
		{
			Point pt = new Point(Integer.parseInt(properties[VEIN_COL]),
			Integer.parseInt(properties[VEIN_ROW]));
			Entity entity = Vein.createVein(properties[VEIN_ID],
			pt,
			Integer.parseInt(properties[VEIN_ACTION_PERIOD]),
			imageStore.getImageList(VEIN_KEY));
			
			tryAddEntity(entity);
		}
	return properties.length == VEIN_NUM_PROPERTIES;
	}
   
   
		   


}