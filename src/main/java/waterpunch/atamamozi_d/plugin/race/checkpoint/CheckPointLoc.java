package waterpunch.atamamozi_d.plugin.race.checkpoint;

import org.bukkit.Location;
import waterpunch.atamamozi_d.plugin.tool.Location.Loc_parts;

/**
 * チェックポイント位置クラス
 * チェックポイントの位置、半径、平面方程式係数を保持する
 * 
 * Checkpoint location class
 * Holds checkpoint location, radius, and plane equation coefficients
 */
public class CheckPointLoc {

     /** 位置情報 / Location parts */
     private Loc_parts loc_parts;
     
     /** チェックポイント半径 / Checkpoint radius */
     private int r;
     
     /** 平面方程式係数 [a, b, c, d] / Plane equation coefficients [a, b, c, d] */
     private double[] abcd;

     /**
      * 新しいチェックポイントを作成する
      * 位置と向きから平面方程式係数を計算する
      * 
      * Creates a new checkpoint
      * Calculates plane equation coefficients from location and orientation
      * 
      * @param loc 位置 / Location
      * @param r 半径 / Radius
      */
     public CheckPointLoc(Location loc, int r) {
          this.loc_parts = new Loc_parts(loc);
          this.r = r;

          // ピッチとヨーから平面の法線ベクトルを計算
          // Calculate plane normal vector from pitch and yaw
          double PP = loc.getPitch() * Math.PI * 0.0055555;
          double YY = loc.getYaw() * Math.PI * 0.0055555;

          double a = -Math.cos(PP) * Math.sin(YY);
          double b = -Math.sin(PP);
          double c = Math.cos(PP) * Math.cos(YY);
          double d = -((a * loc.getX()) + (b * loc.getY()) + (c * loc.getZ()));

          this.abcd = new double[] { a, b, c, d };
     }

     /**
      * チェックポイントの位置を取得する
      * 
      * Gets the checkpoint location
      * 
      * @return 位置 / Location
      */
     public Location getLocation() {
          return this.loc_parts.getLocation();
     }

     /**
      * チェックポイントの半径を取得する
      * 
      * Gets the checkpoint radius
      * 
      * @return 半径 / Radius
      */
     public int getr() {
          return this.r;
     }

     /**
      * 平面方程式係数を取得する
      * ax + by + cz + d = 0
      * 
      * Gets plane equation coefficients
      * ax + by + cz + d = 0
      * 
      * @return 係数配列 [a, b, c, d] / Coefficient array [a, b, c, d]
      */
     public double[] getabcd() {
          return this.abcd;
     }
}
