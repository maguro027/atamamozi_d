package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import waterpunch.atamamozi_d.plugin.race.Race;

public class LocationViewer {

     private Location loc;
     private int r;
     private int CheckPoint;
     private Race Race;
     private Player player;

     public LocationViewer(Player player, Race Race, int CheckPoint) {
          this.player = player;
          this.loc = Race.getCheckPointLoc().get(CheckPoint).getLocation();
          this.r = Race.getCheckPointLoc().get(CheckPoint).getr();
          this.Race = Race;
          this.CheckPoint = CheckPoint;
     }

     public void DrawCircle() {
          Location particleLoc = loc;

          double[] abcd = Race.getCheckPointLoc().get(CheckPoint).getabcd();

          double[] v = GetVerticalVector(loc, r, abcd[0], abcd[1], abcd[2]);
          int n = 5 * r;
          double theta = 2 * Math.PI / (double) n;
          double Cos = Math.cos(theta * 0.5);
          double Sin = Math.sin(theta * 0.5);
          Quaternion q0 = new Quaternion(Cos, abcd[0] * Sin, abcd[1] * Sin, abcd[2] * Sin);
          Quaternion q1 = new Quaternion(0, v[0], v[1], v[2]);
          Quaternion q2 = new Quaternion(Cos, -abcd[0] * Sin, -abcd[1] * Sin, -abcd[2] * Sin);

          Location particleLoc1 = new Location(particleLoc.getWorld(), particleLoc.getX(), particleLoc.getY(), particleLoc.getZ());

          particleLoc1.setX(q1.x);
          particleLoc1.setY(q1.y);
          particleLoc1.setZ(q1.z);

          loc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc1, 1, new Particle.DustOptions(Color.RED, 1));
          for (int i = 1; i < n; i++) {
               Location particleLoc2 = new Location(particleLoc.getWorld(), particleLoc.getX(), particleLoc.getY(), particleLoc.getZ());

               q1 = Quaternion.Multiply(Quaternion.Multiply(q0, q1), q2);
               particleLoc2.setX(q1.x + loc.getX());
               particleLoc2.setY(q1.y + loc.getY());
               particleLoc2.setZ(q1.z + loc.getZ());
               player.spawnParticle(Particle.REDSTONE, particleLoc2, 1, new Particle.DustOptions(Color.RED, 1));
               // Packet<?> packet = new PacketPlayOutWorldParticles(Particle.BLOCK_CRACK, false, x, y, z, 0f, 0f, 0f, 0.5f, 10, type.getId() + data * 4096);
               // ((CraftPlayer) bukkitPlayer).getHandle().sendPacket(packet);
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
