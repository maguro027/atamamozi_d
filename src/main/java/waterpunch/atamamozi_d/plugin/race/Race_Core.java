package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class Race_Core {

     public static HashMap<Race, ArrayList<Race_Runner>> Race_Run = new HashMap<>();
     public static ArrayList<Race> Race_list = new ArrayList<>();

     public static void joinRace(Race Race, Player player) {
          if (!Race_Run.containsKey(Race)) Race_Run.put(Race, new ArrayList<Race_Runner>());
          Race_Run.get(Race).add(new Race_Runner(player, Race));
     }

     public static void joinRace(String race, Player player) {
          if (isJoin(player)) for (int i = 0; i < waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.size(); i++) {
               if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i).getRace_name().equals(race)) {
                    // waterpunch.atamamozi_d.plugin.race.Race_Core.race_join_players.put(player, waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i));
                    joinRace(waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i), player);
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Join Race : " + waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i).getRace_name());
               }
          }
     }

     public static boolean isJoin(Player player) {
          for (ArrayList<Race_Runner> val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.values()) for (Race_Runner run : val) if (run.getPlayer() == player) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Already join race");
               return false;
          }
          return true;
     }

     // public static void refreshLocation(Player player, Race Race) {
     //      Race_Run.put(player, Race);
     // }

     public static void removeRunner(Player player) {
          for (ArrayList<Race_Runner> val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.values()) for (Race_Runner run : val) if (run.getPlayer() == player) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "leave race");
               player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               val.remove(run);
               return;
          }
          player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "You not join Race");
     }

     public static void clear() {
          Race_Run.clear();
          System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Memory clear");
     }
}
