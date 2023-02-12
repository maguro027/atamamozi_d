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
          double[] u = new double[3];

          // ﾍﾞｸﾄﾙ(a,b,c)とvの外積
          u[0] = v[1] * c - v[2] * b;
          u[1] = v[2] * a - v[0] * c;
          u[2] = v[0] * b - v[1] * a;

          int n = 10 * runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getr();
          double theta = 2 * Math.PI / (double) n;
          double cos = 1;
          double sin = 0;
          double cosDelta = Math.cos(theta);
          double sinDelta = Math.sin(theta);

          Location particleLoc0 = new Location(particleLoc.getWorld(), particleLoc.getX(), particleLoc.getY(), particleLoc.getZ());

          for (int i = 1; i < n; i++) {
               particleLoc0.setX(cos * u[0] + sin * v[0] + runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation().getX());
               particleLoc0.setY(cos * u[1] + sin * v[1] + runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation().getY());
               particleLoc0.setZ(cos * u[2] + sin * v[2] + runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation().getZ());
               runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation().getWorld().spawnParticle(Particle.REDSTONE, runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation(), 1, new Particle.DustOptions(Color.RED, 5));

               PP = cos * cosDelta - sin * sinDelta;
               YY = cos * sinDelta + sin * cosDelta;
               cos = PP;
               sin = YY;
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
