package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class Race_Core {

     public static HashMap<Race, ArrayList<Race_Runner>> Race_Run = new HashMap<>();
     public static ArrayList<Race> Race_list = new ArrayList<>();
     public static ArrayList<Race_Runner> Race_Runner_list = new ArrayList<>();

     public static void joinRace(Race Race, int Rap, Player player) {
          if (Race_Run.isEmpty()) {
               Race.setRap(Rap);
               Race_Run.put(Race, new ArrayList<Race_Runner>());
               Race_Runner Runner = new Race_Runner(player, Race);
               Race_Runner_list.add(Runner);
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Join Race : " + Race.getRace_name());
               return;
          }

          for (Race key : Race_Run.keySet()) {
               if (Race.getRace_name().equals(key.getRace_name()) && Rap == key.getRap()) {
                    Race_Runner Runner = new Race_Runner(player, Race);
                    Race_Run.get(Race).add(Runner);
                    Race_Runner_list.add(Runner);
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Join Race : " + Race.getRace_name());
                    return;
               } else {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + Race.getRace_name() + " is Active Race Please wait");
                    return;
               }
          }
          Race.setRap(Rap);
          Race_Run.put(Race, new ArrayList<Race_Runner>());
          Race_Runner Runner = new Race_Runner(player, Race);
          Race_Run.get(Race).add(Runner);
          Race_Runner_list.add(Runner);
          player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Join Race : " + Race.getRace_name());
     }

     public static void removeRunner(Player player) {
          player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
          for (Race_Runner val : Race_Runner_list) if (val.getPlayer() == player) {
               Race_Runner_list.remove(val);
               Race_Run.get(val.getRace()).remove(val);
               for (Entry<Race, ArrayList<Race_Runner>> entry : Race_Run.entrySet()) if (entry.getValue().size() == 0) Race_Run.remove(entry.getKey());

               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "leave race");
               return;
          }
          player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "You not join Race");
     }

     public static boolean isJoin(Player player) {
          for (Race_Runner val : Race_Runner_list) {
               if (val.getPlayer() == player) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Already join race");
                    return false;
               }
          }

          return true;
     }

     public static Race getRace(String race) {
          for (int i = 0; i < waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.size(); i++) if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i).getRace_name().equals(race)) {
               return waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i);
          }
          return null;
     }

     public static void clear() {
          Race_Run.clear();
          Race_Runner_list.clear();
          System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Memory clear");
     }
}
