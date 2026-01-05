package waterpunch.atamamozi_d.plugin.tool.Location;

import org.bukkit.Bukkit;
import org.bukkit.Location;

//Location 情報を分割して保持するクラス
//update()は不要になったため削除

public class LocParts {

    private final Double x, y, z;
    private final Float yaw, pitch;
    private final String worldName;

    public LocParts(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        org.bukkit.World w = location.getWorld();
        this.worldName = (w != null) ? w.getName() : "world";
    }

    public Location getLocation() {
        org.bukkit.World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        }
        if (world == null) {
            java.util.logging.Logger.getLogger(LocParts.class.getName())
                    .log(java.util.logging.Level.WARNING, "Could not find world: {0}", worldName);
            return null;
        }
        return new Location(world, x, y, z, yaw, pitch);
    }
}
