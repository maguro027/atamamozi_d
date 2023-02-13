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

          double a = runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getabcd()[0];
          double b = runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getabcd()[1];
          double c = runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getabcd()[2];

          double[] v = GetVerticalVector(runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getLocation(), runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getr(), a, b, c);
          double[] u = new double[3];

          u[0] = v[1] * c - v[2] * b;
          u[1] = v[2] * a - v[0] * c;
          u[2] = v[0] * b - v[1] * a;

          double theta = 2 * Math.PI / (double) (10 * runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getr());
          // 2 * Math.PI / (double) 10 * runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getr();
          double cos = 1;
          double sin = 0;
          double cosDelta = Math.cos(theta);
          double sinDelta = Math.sin(theta);

          Location particleLoc0 = new Location(particleLoc.getWorld(), particleLoc.getX(), particleLoc.getY(), particleLoc.getZ());

          for (int i = 0; i < 10 * runner.getRace().getCheckPointLoc().get(runner.getCheckPoint()).getr(); i++) {
               particleLoc0.setX(cos * u[0] + sin * v[0] + particleLoc.getX());
               particleLoc0.setY(cos * u[1] + sin * v[1] + particleLoc.getY());
               particleLoc0.setZ(cos * u[2] + sin * v[2] + particleLoc.getZ());
               particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc0, 1, new Particle.DustOptions(Color.RED, 1));

               PP = cos * cosDelta - sin * sinDelta;
               YY = cos * sinDelta + sin * cosDelta;
               cos = PP;
               sin = YY;
          }
     }

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
