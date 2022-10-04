package waterpunch.atamamozi_d.plugin.race;

import java.util.HashMap;

import org.bukkit.entity.Player;

import waterpunch.atamamozi_d.plugin.race.export.SET_RACE_JSON;

public class Race_Core {

	public static HashMap<Player, SET_RACE_JSON> race_join_players = new HashMap<>();

	public static HashMap<Player, SET_RACE_JSON> race_join_Players() {
		return race_join_players;
	}

	public static SET_RACE_JSON getRace(Player player) {
		return race_join_players.get(player);
	}

	public static void addPlayer(Player player, SET_RACE_JSON SET_RACE_JSON) {
		race_join_players.put(player, SET_RACE_JSON);
	}

	public static void refreshLocation(Player player, SET_RACE_JSON SET_RACE_JSON) {
		race_join_players.put(player, SET_RACE_JSON);
	}

	public static void delPlayer(Player player) {
		race_join_players.remove(player);
	}

	public static void clear() {
		race_join_players.clear();
		System.out.println("[RACE]Memory clear");
	}

}
