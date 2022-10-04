package waterpunch.atamamozi_d.plugin.race;

import org.bukkit.entity.Player;

public class Race_Runner {
	private Player player;
	private Race race;
	private int CheckPoint;

	public Race_Runner(Player player, Race race) {
		this.player = player;
		this.race = race;
	}

	public Player getPlayer() {
		return player;
	}

	public void getRace(Race race) {
		this.race = race;
	}

	public Race setRace() {
		return race;
	}

	public int setCheckPoint() {
		return CheckPoint;
	}

	public void addCheckPoint() {
		CheckPoint++;
	}
}
