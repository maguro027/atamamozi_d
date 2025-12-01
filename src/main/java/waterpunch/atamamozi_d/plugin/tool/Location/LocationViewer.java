package waterpunch.atamamozi_d.plugin.tool.Location;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

/**
 * 位置表示クラス
 * チェックポイントをパーティクルで円形に表示する
 * 
 * Location viewer class
 * Displays checkpoints as circular particles
 */
public class LocationViewer {

     /** 関連するランナー / Associated runner */
     private Race_Runner runner;
     
     /** パーティクル描画スロットリング用タイムスタンプ / Timestamp for particle rendering throttling */
     // throttle particle rendering to avoid heavy per-tick overhead (ms)
     private long lastDrawTime = 0L;

     /**
      * ロケーションビューワーを作成する
      * 
      * Creates a location viewer
      * 
      * @param runner ランナー / Runner
      */
     public LocationViewer(Race_Runner runner) {
          this.runner = runner;
     }

     /**
      * チェックポイントを円形のパーティクルで描画する
      * 250msごとに最大4回/秒で描画
      * 
      * Draws checkpoint as circular particles
      * Draws at most ~4 times/sec with 250ms interval
      * 
      * @param checkNo チェックポイント番号 / Checkpoint number
      */
     public void DrawCircle(int checkNo) {
          // Avoid drawing particles too often — this method can be invoked very
          // frequently
          final long now = System.currentTimeMillis();
          if (now - lastDrawTime < 250)
               return; // draw at most ~4 times/sec
          lastDrawTime = now;
          Race RACE = Race_Core.getRace(runner.getRaceID());
          Location particleLoc = RACE.getCheckPointLoc().get(checkNo).getLocation();
          double PP = RACE.getCheckPointLoc().get(checkNo).getLocation().getPitch() * Math.PI * 0.0055555;
          double YY = RACE.getCheckPointLoc().get(checkNo).getLocation().getYaw() * Math.PI * 0.0055555;

          double a = RACE.getCheckPointLoc().get(checkNo).getabcd()[0];
          double b = RACE.getCheckPointLoc().get(checkNo).getabcd()[1];
          double c = RACE.getCheckPointLoc().get(checkNo).getabcd()[2];

          double[] v = GetVerticalVector(RACE.getCheckPointLoc().get(checkNo).getLocation(),
                    RACE.getCheckPointLoc().get(checkNo).getr(), a, b, c);
          double[] u = new double[3];

          u[0] = v[1] * c - v[2] * b;
          u[1] = v[2] * a - v[0] * c;
          u[2] = v[0] * b - v[1] * a;

          double theta = 2 * Math.PI / (double) (10 * RACE.getCheckPointLoc().get(checkNo).getr());
          double cos = 1;
          double sin = 0;
          double cosDelta = Math.cos(theta);
          double sinDelta = Math.sin(theta);

          Location particleLoc0 = new Location(particleLoc.getWorld(), particleLoc.getX(), particleLoc.getY(),
                    particleLoc.getZ());

          for (int i = 0; i < 10 * RACE.getCheckPointLoc().get(checkNo).getr(); i++) {
               particleLoc0.setX(cos * u[0] + sin * v[0] + particleLoc.getX());
               particleLoc0.setY(cos * u[1] + sin * v[1] + particleLoc.getY());
               particleLoc0.setZ(cos * u[2] + sin * v[2] + particleLoc.getZ());
               runner.getPlayer().spawnParticle(Particle.REDSTONE, particleLoc0, 1,
                         new Particle.DustOptions(Color.RED, 1));

               PP = cos * cosDelta - sin * sinDelta;
               YY = cos * sinDelta + sin * cosDelta;
               cos = PP;
               sin = YY;
          }
     }

     /**
      * 垂直ベクトルを取得する
      * 
      * Gets vertical vector
      * 
      * @param loc 位置 / Location
      * @param r 半径 / Radius
      * @param a 平面係数a / Plane coefficient a
      * @param b 平面係数b / Plane coefficient b
      * @param c 平面係数c / Plane coefficient c
      * @return 垂直ベクトル / Vertical vector
      */
     public double[] GetVerticalVector(Location loc, int r, double a, double b, double c) {
          double[] rtn = new double[3];
          if (a == 0) {
               rtn[0] = r;
          } else {
               double s = (b + c) / a;
               s *= s;
               s += 2;
               s = (float) (r / Math.sqrt(s));
               rtn[0] = s * (b + c) / a;
               rtn[1] = -s;
               rtn[2] = -s;
          }
          return rtn;
     }
}
