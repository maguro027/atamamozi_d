package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Loc_parts {

     private Double x, y, z;
     private Float yaw, pitch;
     private String world_name;

     public Loc_parts(Location location) {
          this.x = location.getX();
          this.y = location.getY();
          this.z = location.getZ();
          this.yaw = location.getYaw();
          this.pitch = location.getPitch();
          this.world_name = location.getWorld().getName();

          updata(location);
     }

     public void updata(Location location) {
          this.x = location.getX();
          this.y = location.getY();
          this.z = location.getZ();
          this.yaw = location.getYaw();
          this.pitch = location.getPitch();
          this.world_name = location.getWorld().getName();
     }

     public Location getLocation() {
          return new Location(Bukkit.getWorld(world_name), x, y, z, yaw, pitch);
     }
}
