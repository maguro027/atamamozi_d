package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

public class LocationViewer {

     private Location loc;
     int r;

     public void setLoc(Location loc) {
          this.loc = loc;
     }

     public void setr(int r) {
          this.r = r;
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

     public void DrawCircle() {
          Location particleLoc = loc;

          double PP = loc.getPitch() * Math.PI * 0.0055555;
          double YY = loc.getYaw() * Math.PI * 0.0055555;

          double a = -Math.cos(PP) * Math.sin(YY);
          double b = -Math.sin(PP);
          double c = Math.cos(PP) * Math.cos(YY);

          double[] v = GetVerticalVector(loc, r, a, b, c);
          int n = 10 * r;
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

          loc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc1, 1, new Particle.DustOptions(Color.WHITE, 5));
          for (int i = 1; i < n; i++) {
               Location particleLoc2 = new Location(particleLoc.getWorld(), particleLoc.getX(), particleLoc.getY(), particleLoc.getZ());

               q1 = Quaternion.Multiply(Quaternion.Multiply(q0, q1), q2);
               particleLoc2.setX(q1.x + loc.getX());
               particleLoc2.setY(q1.y + loc.getY());
               particleLoc2.setZ(q1.z + loc.getZ());
               loc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc2, 1, new Particle.DustOptions(Color.RED, 5));
          }
     }
}
