package waterpunch.atamamozi_d.plugin.race.export;

import org.bukkit.Location;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Hachitai {

     static float PCalc(Race_Runner Runner, Location location) {
          Location CheckPoint = Runner.getRace().getCheckPointLoc().get(Runner.getCheckPoint()).getLocation();

          double PP = CheckPoint.getPitch() * Math.PI * 0.0055555;
          double YY = CheckPoint.getYaw() * Math.PI * 0.0055555;

          double a = -Math.cos(PP) * Math.sin(YY);
          double b = -Math.sin(PP);
          double c = Math.cos(PP) * Math.cos(YY);

          return (float) ((a * location.getX()) + (b * location.getY()) + (c * location.getZ()) - ((a * CheckPoint.getX()) + (b * CheckPoint.getY()) + (c * CheckPoint.getZ())));
     }

     public static boolean CheckPlanePassed(Race_Runner Runner, Location to, Location from) {
          float C = PCalc(Runner, to);
          float P = PCalc(Runner, from);

          return (C * P <= 0) && (C != P);
     }

     static float GetDot(float x1, float y1, float z1, float x2, float y2, float z2) {
          return (x1 * x2 + y1 * y2 + z1 * z2);
     }

     public static float[] GetIntersection(Location CheckPoint, Location to, Location from) {
          float dirVecX = (float) (to.getX() - from.getX());
          float dirVecY = (float) (to.getY() - from.getY());
          float dirVecZ = (float) (to.getZ() - from.getZ());

          double PP = CheckPoint.getPitch() * Math.PI * 0.0055555;
          double YY = CheckPoint.getYaw() * Math.PI * 0.0055555;

          double a = -Math.cos(PP) * Math.sin(YY);
          double b = -Math.sin(PP);
          double c = Math.cos(PP) * Math.cos(YY);

          double d = -((a * CheckPoint.getX()) + (b * CheckPoint.getY()) + (c * CheckPoint.getZ()));

          float length = ((float) -d - GetDot((float) a, (float) b, (float) c, (float) to.getX(), (float) to.getY(), (float) to.getZ()));
          length /= GetDot((float) a, (float) b, (float) c, dirVecX, dirVecY, dirVecZ);

          float[] rtn = new float[] { (float) to.getX() + dirVecX * length, (float) to.getY() + dirVecY * length, (float) to.getZ() + dirVecZ * length };
          return rtn;
     }
}
