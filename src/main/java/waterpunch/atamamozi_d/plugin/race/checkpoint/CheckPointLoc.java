package waterpunch.atamamozi_d.plugin.race.checkpoint;

import org.bukkit.Location;
import waterpunch.atamamozi_d.plugin.tool.Loc_parts;

public class CheckPointLoc {

     private Loc_parts loc_parts;
     private int r;
     private double[] abcd;

     public CheckPointLoc(Location loc, int r) {
          this.loc_parts = new Loc_parts(loc);
          this.r = r;

          double PP = loc.getPitch() * Math.PI * 0.0055555;
          double YY = loc.getYaw() * Math.PI * 0.0055555;

          double a = -Math.cos(PP) * Math.sin(YY);
          double b = -Math.sin(PP);
          double c = Math.cos(PP) * Math.cos(YY);
          double d = -((a * loc.getX()) + (b * loc.getY()) + (c * loc.getZ()));

          this.abcd = new double[] { a, b, c, d };
     }

     public Location getLocation() {
          return this.loc_parts.getLocation();
     }

     public int getr() {
          return this.r;
     }

     public double[] getabcd() {
          return this.abcd;
     }
}
