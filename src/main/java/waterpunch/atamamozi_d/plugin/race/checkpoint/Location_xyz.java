package waterpunch.atamamozi_d.plugin.race.checkpoint;

import org.bukkit.Location;

public class Location_xyz {

	private double loc[];

	public Location_xyz(Location loc, int r) {
		this.loc[0] = loc.getX();
		this.loc[1] = loc.getY();
		this.loc[2] = loc.getZ();
		this.loc[3] = r;
		this.loc[4] = a(loc);
		this.loc[5] = b(loc);
		this.loc[6] = c(loc);
		this.loc[7] = d(loc);

	}

	public double[] getLoc() {
		return this.loc;
	}

	public double[] getLocation() {
		return this.loc;
	}

	double a(Location loc) {
		return -Math.cos(loc.getPitch() * Math.PI / 180) * Math.sin(loc.getYaw() * Math.PI / 180);
	}

	double b(Location loc) {
		return Math.sin(loc.getPitch() * Math.PI / 180);
	}

	double c(Location loc) {
		return Math.cos(loc.getPitch() * Math.PI / 180) * Math.cos(loc.getYaw() * Math.PI / 180);
	}

	double d(Location loc) {
		return -((a(loc) * loc.getX()) + (b(loc) * loc.getY()) + (c(loc) * loc.getZ()));
	}
}
