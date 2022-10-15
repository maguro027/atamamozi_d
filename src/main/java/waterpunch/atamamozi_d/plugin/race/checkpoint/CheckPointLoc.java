package waterpunch.atamamozi_d.plugin.race.checkpoint;

import org.bukkit.Location;
import waterpunch.atamamozi_d.plugin.tool.Loc_parts;

public class CheckPointLoc {

     private Loc_parts loc_parts;
     private int r;

     public CheckPointLoc(Location loc, int r) {
          this.loc_parts = new Loc_parts(loc);
          this.r = r;
     }

     public Location getLocation() {
          return loc_parts.getLocation();
     }

     public int getr() {
          return r;
     }
}
