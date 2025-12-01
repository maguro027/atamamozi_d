package waterpunch.atamamozi_d.plugin.tool.Scoreboaed;

import org.bukkit.Location;

/**
 * スピードメータークラス
 * プレイヤーの移動速度を計算する
 * 
 * Speed meter class
 * Calculates player movement speed
 */
public class Speed_Meter {

     /**
      * 2点間の水平距離からスピードを計算する
      * 
      * Calculates speed from horizontal distance between two points
      * 
      * @param loc1 位置1 / Location 1
      * @param loc2 位置2 / Location 2
      * @return 計算されたスピード / Calculated speed
      */
     public static double Meter(Location loc1, Location loc2) {
          double X = Math.pow((double) loc1.getX() - (double) loc2.getX(), 2);
          double Z = Math.pow((double) loc1.getZ() - (double) loc2.getZ(), 2);
          return Math.sqrt(X + Z) * 1000;
     }
}
