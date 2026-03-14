package waterpunch.atamamozi_d.plugin.race.checkpoint;

import org.bukkit.Location;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import waterpunch.atamamozi_d.plugin.tool.Location.LocParts;

@Getter
public class CheckPoint {

     @SerializedName("loc_parts")
     private LocParts locParts;

     private int r;
     private double[] abcd;

     // no-arg constructor for Gson
     public CheckPoint() {
     }

     // convenience constructor used by code that builds from a Location
     public CheckPoint(Location loc, int r) {
          this.locParts = new LocParts(loc);
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
          return this.locParts == null ? null : this.locParts.getLocation();
     }

}
