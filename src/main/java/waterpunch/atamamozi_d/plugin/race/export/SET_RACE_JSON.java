package waterpunch.atamamozi_d.plugin.race.export;

import java.util.ArrayList;

import org.bukkit.Location;

import waterpunch.atamamozi_d.plugin.race.checkpoint.Location_xyz;

public class SET_RACE_JSON {

//	private int RACE_ID;
	private ArrayList<Location_xyz> CheckPoint = new ArrayList<>();

	public SET_RACE_JSON(Location loc, int r) {
		CheckPoint.add(new Location_xyz(loc, r));
	}

	public String toString() {
		return CheckPoint.toString();
	}

	public Location_xyz getLocation_xyz(int i) {
		return CheckPoint.get(i);
	}

}
