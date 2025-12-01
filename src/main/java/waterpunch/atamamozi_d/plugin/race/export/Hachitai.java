package waterpunch.atamamozi_d.plugin.race.export;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

/**
 * 平面・チェックポイント計算クラス
 * チェックポイント通過判定と交点計算を行う
 * 
 * Plane and checkpoint calculation class
 * Handles checkpoint passage detection and intersection calculation
 */
public class Hachitai {

     /**
      * パーティクルで円を表示する
      * 
      * Displays a circle using particles
      * 
      * @param runner ランナー / Runner
      * @param loc 中心位置 / Center location
      * @param size 半径 / Radius
      */
     public static void setCircle(Race_Runner runner, Location loc, int size) {
          for (int d = 0; d <= 10; d += 1) {
               Location particleLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
               particleLoc.setYaw(loc.getYaw());
               particleLoc.setPitch(loc.getPitch());

               particleLoc.setX(loc.getX() + Math.cos(d) * size);
               particleLoc.setZ(loc.getZ() + Math.sin(d) * size);
               runner.getPlayer().spawnParticle(Particle.REDSTONE, particleLoc, 1, new Particle.DustOptions(Color.WHITE, 5));
          }
     }

     /**
      * 平面方程式の値を計算する
      * 
      * Calculates plane equation value
      * 
      * @param Runner ランナー / Runner
      * @param location 位置 / Location
      * @return 平面方程式の値 / Plane equation value
      */
     static float PCalc(Race_Runner Runner, Location location) {
          double[] abcd = Race_Core.getRace(Runner.getRaceID()).getCheckPointLoc().get(Runner.getCheckPoint()).getabcd();
          return (float) (((abcd[0] * location.getX()) + (abcd[1] * location.getY()) + (abcd[2] * location.getZ())) + abcd[3]);
     }

     /**
      * プレイヤーが平面を通過したかどうかを判定する
      * 
      * Checks if player passed through the plane
      * 
      * @param Runner ランナー / Runner
      * @param to 移動先位置 / Destination location
      * @param from 移動元位置 / Origin location
      * @return 通過したらtrue / True if passed
      */
     public static boolean CheckPlanePassed(Race_Runner Runner, Location to, Location from) {
          float C = PCalc(Runner, to);
          float P = PCalc(Runner, from);

          return (C * P <= 0) && (C != P);
     }

     /**
      * 内積を計算する
      * 
      * Calculates dot product
      */
     static double GetDot(double x1, double y1, double z1, double x2, double y2, double z2) {
          return (x1 * x2 + y1 * y2 + z1 * z2);
     }

     /**
      * 線分と平面の交点を計算する
      * 
      * Calculates intersection of line segment and plane
      * 
      * @param Runner ランナー / Runner
      * @param CheckPoint チェックポイント位置 / Checkpoint location
      * @param to 移動先位置 / Destination location
      * @param from 移動元位置 / Origin location
      * @return 交点座標の配列 [x, y, z] / Intersection coordinates [x, y, z]
      */
     public static double[] GetIntersection(Race_Runner Runner, Location CheckPoint, Location to, Location from) {
          double dirVecX = to.getX() - from.getX();
          double dirVecY = to.getY() - from.getY();
          double dirVecZ = to.getZ() - from.getZ();

          double[] abcd = Race_Core.getRace(Runner.getRaceID()).getCheckPointLoc().get(Runner.getCheckPoint()).getabcd();

          double length = (-abcd[3] - GetDot(abcd[0], abcd[1], abcd[2], to.getX(), to.getY(), to.getZ()));
          length /= GetDot(abcd[0], abcd[1], abcd[2], dirVecX, dirVecY, dirVecZ);

          return new double[] { to.getX() + dirVecX * length, to.getY() + dirVecY * length, to.getZ() + dirVecZ * length };
     }
}
