package waterpunch.atamamozi_d.plugin.race.export;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import waterpunch.atamamozi_d.plugin.race.domain.Race;
import waterpunch.atamamozi_d.plugin.race.domain.RacePlayer;
import waterpunch.atamamozi_d.plugin.race.domain.RaceSession;

/**
 * Hachitai: チェックポイント判定や簡易エフェクトのユーティリティ。
 *
 * <p>
 * 以前は `Race_Runner`/`Race_Core` に依存していたが、
 * 現在は `RacePlayer` と `RaceSession` を直接受け取る形に変更。
 */
public class Hachitai {

     private static final Map<UUID, Long> particleCooldown = new HashMap<>();
     private static long particleInterval = 500; // ミリ秒（デフォルト0.5秒）

     /**
      * パーティクル表示間隔を設定（ミリ秒）
      */
     public static void setParticleInterval(long intervalMillis) {
          particleInterval = intervalMillis;
     }

     public static void setCircle(RacePlayer runner, RaceSession session, Location loc, int size) {
          UUID playerId = runner.getPlayer().getUniqueId();
          long now = System.currentTimeMillis();

          // クールタイムチェック
          if (particleCooldown.containsKey(playerId)) {
               long lastTime = particleCooldown.get(playerId);
               if (now - lastTime < particleInterval) {
                    return; // まだクールタイム中
               }
          }

          // パーティクル表示
          particleCooldown.put(playerId, now);
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

     static float PCalc(RacePlayer runner, RaceSession session, Location location, Race race) {
          int currentCheckpoint = session.getCurrentCheckpoint(runner.getPlayer());
          double[] abcd = race.getCheckPoint(currentCheckpoint).getabcd();
          return (float) (((abcd[0] * location.getX()) + (abcd[1] * location.getY()) + (abcd[2] * location.getZ()))
                    + abcd[3]);
     }

     public static boolean CheckPlanePassed(RacePlayer runner, RaceSession session, Location to, Location from,
               Race race) {
          float C = PCalc(runner, session, to, race);
          float P = PCalc(runner, session, from, race);

          return (C * P <= 0) && (C != P);
     }

     static double GetDot(double x1, double y1, double z1, double x2, double y2, double z2) {
          return (x1 * x2 + y1 * y2 + z1 * z2);
     }

     public static double[] GetIntersection(RacePlayer runner, RaceSession session, Location CheckPoint, Location to,
               Location from,
               Race race) {
          double dirVecX = to.getX() - from.getX();
          double dirVecY = to.getY() - from.getY();
          double dirVecZ = to.getZ() - from.getZ();

          int currentCheckpoint = session.getCurrentCheckpoint(runner.getPlayer());
          double[] abcd = race.getCheckPoint(currentCheckpoint).getabcd();

          double length = (-abcd[3] - GetDot(abcd[0], abcd[1], abcd[2], to.getX(), to.getY(), to.getZ()));
          length /= GetDot(abcd[0], abcd[1], abcd[2], dirVecX, dirVecY, dirVecZ);

          return new double[] { to.getX() + dirVecX * length, to.getY() + dirVecY * length,
                    to.getZ() + dirVecZ * length };
     }
}
