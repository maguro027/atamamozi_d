package waterpunch.atamamozi_d.plugin.race.export;

import org.bukkit.Location;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Hachitai {

     static float PCalc(Race_Runner Runner, Location location) {
          Location CheckPoint = Runner.getRace().getCheckPointLoc().get(Runner.getCheckPoint()).getLocation();

          double PP = CheckPoint.getPitch() * Math.PI * 0.0055555;
          double YY = CheckPoint.getYaw() * Math.PI * 0.0055555;

          double a = -Math.cos(PP) * Math.sin(YY);
          double b = Math.cos(PP) * Math.cos(YY);
          double c = Math.sin(PP);

          return (float) ((a * location.getX()) + (b * location.getY()) + (c + location.getZ()) + (-(a * CheckPoint.getX()) + (b * CheckPoint.getY()) + (c + CheckPoint.getZ())));
     }

     public static boolean CheckPlanePassed(Race_Runner Runner, Location to, Location from) {
          float C = PCalc(Runner, from);
          float P = PCalc(Runner, to);
          return (C * P <= 0) && (C != P);
     }
}
