package waterpunch.atamamozi_d.plugin.race.export;

import org.bukkit.Location;

public class Hachitai {

     static double PCalc(Location location) {
          double PP = location.getPitch() * Math.PI * 0.0055555;
          double YY = location.getYaw() * Math.PI * 0.0055555;

          double a = -Math.cos(PP) * Math.sin(YY);
          double b = -Math.sin(PP);
          double c = Math.cos(PP) * Math.cos(YY);

          return (a * location.getX()) + (b * location.getY()) + (c + location.getZ()) + -((a * location.getX()) + (b * location.getY()) + (c + location.getZ()));
     }

     public static boolean CheckPlanePassed(Location nowloc, Location oldLoc) {
          float C = (float) PCalc(nowloc);
          float P = (float) PCalc(oldLoc);
          return (C * P <= 0) && (C != P);
     }
}
