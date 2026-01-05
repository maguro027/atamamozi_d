package waterpunch.atamamozi_d.plugin.race.export;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import waterpunch.atamamozi_d.plugin.race.domain.Race;
import waterpunch.atamamozi_d.plugin.race.domain.RacePlayer;

/**
 * Hachitai: チェックポイント判定や簡易エフェクトのユーティリティ。
 *
 * <p>
 * 以前は `Race_Runner`/`Race_Core` に依存していたが、
 * 現在は `RacePlayer` と `Race` を直接受け取る形に変更。
 */
public class Hachitai {

     public static void setCircle(RacePlayer runner, Location loc, int size) {
          for (int d = 0; d <= 10; d += 1) {
               Location particleLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
               particleLoc.setYaw(loc.getYaw());
               particleLoc.setPitch(loc.getPitch());

               particleLoc.setX(loc.getX() + Math.cos(d) * size);
               particleLoc.setZ(loc.getZ() + Math.sin(d) * size);
               runner.getPlayer().spawnParticle(Particle.DUST, particleLoc, 1,
                         new Particle.DustOptions(Color.RED, 5));
          }
     }

     static float PCalc(RacePlayer runner, Location location, Race race) {
          if (runner == null || location == null || race == null) {
               return Float.NaN;
          }
          waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc cp = race
                    .getCheckPoint(runner.getCurrentCheckpoint());
          if (cp == null) {
               return Float.NaN;
          }
          double[] abcd = cp.getabcd();
          return (float) (((abcd[0] * location.getX()) + (abcd[1] * location.getY()) + (abcd[2] * location.getZ()))
                    + abcd[3]);
     }

     public static boolean CheckPlanePassed(RacePlayer runner, Location to, Location from, Race race) {
          float C = PCalc(runner, to, race);
          float P = PCalc(runner, from, race);

          return (C * P <= 0) && (C != P);
     }

     static double GetDot(double x1, double y1, double z1, double x2, double y2, double z2) {
          return (x1 * x2 + y1 * y2 + z1 * z2);
     }

     public static double[] GetIntersection(RacePlayer runner, Location CheckPoint, Location to, Location from,
               Race race) {
          if (runner == null || to == null || from == null || race == null) {
               return new double[] { 0, 0, 0 };
          }
          waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc cp = race
                    .getCheckPoint(runner.getCurrentCheckpoint());
          if (cp == null) {
               return new double[] { 0, 0, 0 };
          }

          double dirVecX = to.getX() - from.getX();
          double dirVecY = to.getY() - from.getY();
          double dirVecZ = to.getZ() - from.getZ();

          double[] abcd = cp.getabcd();

          double length = (-abcd[3] - GetDot(abcd[0], abcd[1], abcd[2], to.getX(), to.getY(), to.getZ()));
          length /= GetDot(abcd[0], abcd[1], abcd[2], dirVecX, dirVecY, dirVecZ);

          return new double[] { to.getX() + dirVecX * length, to.getY() + dirVecY * length,
                    to.getZ() + dirVecZ * length };
     }
}
