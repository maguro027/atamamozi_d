package waterpunch.atamamozi_d.plugin.tool.Location;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * 位置情報パーツクラス
 * 位置情報をシリアライズ可能な形式で保存する
 * 
 * Location parts class
 * Stores location information in a serializable format
 */
public class Loc_parts {

     /** X座標 / X coordinate */
     private Double x;
     
     /** Y座標 / Y coordinate */
     private Double y;
     
     /** Z座標 / Z coordinate */
     private Double z;
     
     /** ヨー角 / Yaw angle */
     private Float yaw;
     
     /** ピッチ角 / Pitch angle */
     private Float pitch;
     
     /** ワールド名 / World name */
     private String world_name;

     /**
      * 位置から新しいLoc_partsを作成する
      * 
      * Creates a new Loc_parts from a location
      * 
      * @param location 位置 / Location
      */
     public Loc_parts(Location location) {
          this.x = location.getX();
          this.y = location.getY();
          this.z = location.getZ();
          this.yaw = location.getYaw();
          this.pitch = location.getPitch();
          this.world_name = location.getWorld().getName();

          updata(location);
     }

     /**
      * 位置情報を更新する
      * 
      * Updates location information
      * 
      * @param location 新しい位置 / New location
      */
     public void updata(Location location) {
          this.x = location.getX();
          this.y = location.getY();
          this.z = location.getZ();
          this.yaw = location.getYaw();
          this.pitch = location.getPitch();
          this.world_name = location.getWorld().getName();
     }

     /**
      * Bukkit Locationオブジェクトを取得する
      * 
      * Gets Bukkit Location object
      * 
      * @return 位置オブジェクト / Location object
      */
     public Location getLocation() {
          return new Location(Bukkit.getWorld(world_name), x, y, z, yaw, pitch);
     }
}
