package waterpunch.atamamozi_d.plugin.tool.Scoreboaed;

import org.bukkit.Location;

public class Speed_Meter {

     public static double Meter(Location loc1, Location loc2) {
          double X = Math.pow((double) loc1.getX() - (double) loc2.getX(), 2);
          double Z = Math.pow((double) loc1.getZ() - (double) loc2.getZ(), 2);
          return Math.sqrt(X + Z) * 1000;
     }
}
