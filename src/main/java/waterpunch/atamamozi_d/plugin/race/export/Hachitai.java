package waterpunch.atamamozi_d.plugin.race.export;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Hachitai {

	static double PCalc(Player player, double x, double y, double z) {

		double PP = player.getLocation().getPitch() * Math.PI * 0.0055555;
		double YY = player.getLocation().getYaw() * Math.PI * 0.0055555;

		double a = -Math.cos(PP) * Math.sin(YY);
		double b = -Math.sin(PP);
		double c = Math.cos(PP) * Math.cos(YY);

		return ((a * x) + (b * y) + (c + z)) + -(a * player.getLocation().getX()) + (b * player.getLocation().getY())
				+ (c + player.getLocation().getZ());
	}

	public static boolean CheckPlanePassed(Player player, Location oldLoc) {
		float C = (float) PCalc(player, player.getLocation().getX(), player.getLocation().getY(),
				player.getLocation().getZ());
		float P = (float) PCalc(player, oldLoc.getX(), oldLoc.getY(), oldLoc.getZ());
		return (C * P <= 0) && (C != P);
	}
}
