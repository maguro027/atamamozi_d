package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.ChatColor;

public class CollarMessage {

	public static String setRace() {
		return ChatColor.GOLD + "[" + ChatColor.RED + "RACE" + ChatColor.GOLD + "]" + ChatColor.WHITE;
	}

	public static String setInfo() {
		return ChatColor.GOLD + "[" + ChatColor.BLUE + "INFO" + ChatColor.GOLD + "]" + ChatColor.WHITE;
	}

	public static String setWarning() {
		return ChatColor.GOLD + "[" + ChatColor.RED + "WARN" + ChatColor.GOLD + "]" + ChatColor.WHITE;
	}
}
