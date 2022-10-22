package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.entity.Player;

public class Race_Core {

     public static HashMap<Player, Race> race_join_players = new HashMap<>();
     public static ArrayList<Race> Race_list = new ArrayList<>();

     public static void addPlayer(Player player, Race Race) {
          race_join_players.put(player, Race);
     }

     public static void refreshLocation(Player player, Race Race) {
          race_join_players.put(player, Race);
     }

     public static void delPlayer(Player player) {
          race_join_players.remove(player);
     }

     public static void clear() {
          race_join_players.clear();
          System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Memory clear");
     }
}
