package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class LocationViewer {

     private Race_Runner runner;

     public LocationViewer(Race_Runner runner) {
          this.runner = runner;
     }

     public void DrawCircle() {
          Location particleLoc = runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation();

          double PP = runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation().getPitch() * Math.PI * 0.0055555;
          double YY = runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation().getYaw() * Math.PI * 0.0055555;

          double a = -Math.cos(PP) * Math.sin(YY);
          double b = -Math.sin(PP);
          double c = Math.cos(PP) * Math.cos(YY);

          double[] v = GetVerticalVector(runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation(), runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getr(), a, b, c);
          int n = 10 * runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getr();
          double theta = 2 * Math.PI / (double) n;
          double Cos = Math.cos(theta * 0.5);
          double Sin = Math.sin(theta * 0.5);
          Quaternion q0 = new Quaternion(Cos, a * Sin, b * Sin, c * Sin);
          Quaternion q1 = new Quaternion(0, v[0], v[1], v[2]);
          Quaternion q2 = new Quaternion(Cos, -a * Sin, -b * Sin, -c * Sin);

          Location particleLoc1 = new Location(particleLoc.getWorld(), particleLoc.getX(), particleLoc.getY(), particleLoc.getZ());

          particleLoc1.setX(q1.x);
          particleLoc1.setY(q1.y);
          particleLoc1.setZ(q1.z);
          for (int i = 1; i < n; i++) {
               Location particleLoc2 = new Location(particleLoc.getWorld(), particleLoc.getX(), particleLoc.getY(), particleLoc.getZ());

               q1 = Quaternion.Multiply(Quaternion.Multiply(q0, q1), q2);
               particleLoc2.setX(q1.x + runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation().getX());
               particleLoc2.setY(q1.y + runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation().getY());
               particleLoc2.setZ(q1.z + runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation().getZ());
               runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation().getWorld().spawnParticle(Particle.REDSTONE, particleLoc2, 1, new Particle.DustOptions(Color.RED, 1));
          }
     }

     public static double[] GetVerticalVector(Location loc, int r, double a, double b, double c) {
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
