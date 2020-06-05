import java.util.List;

import processing.core.PImage;

final class Background
{
   public String id;
   public List<PImage> images;
   public int imageIndex;

   public Background(String id, List<PImage> images)
   {
      this.id = id;
      this.images = images;
   }
 
   public static PImage getCurrentImage(Object entity)
   {      
     return ((Background)entity).images.get(((Background)entity).imageIndex);
   }

}
