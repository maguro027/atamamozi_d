package waterpunch.atamamozi_d.plugin.race.checkpoint;

import org.bukkit.Location;

import com.google.gson.annotations.SerializedName;

import waterpunch.atamamozi_d.plugin.tool.Location.LocParts;

public class CheckPointLoc {

     @SerializedName("loc_parts")
     private LocParts locParts;

     private int r;
     private double[] abcd;

     // no-arg constructor for Gson
     public CheckPointLoc() {
     }

     // convenience constructor used by code that builds from a Location
     public CheckPointLoc(Location loc, int r) {
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

     public LocParts getLocParts() {
          return locParts;
     }

     public Location getLocation() {
          return this.locParts == null ? null : this.locParts.getLocation();
     }

     public int getR() {
          return this.r;
     }

     public double[] getAbcd() {
          return this.abcd;
     }

     // legacy-style accessors
     public int getr() {
          return getR();
     }

     public double[] getabcd() {
          return getAbcd();
     }
}
