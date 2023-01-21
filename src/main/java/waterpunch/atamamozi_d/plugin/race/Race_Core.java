package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Race_Core {

     public static HashMap<Race, ArrayList<Race_Runner>> Race_Run = new HashMap<>();
     public static HashMap<Race, ArrayList<Race_Runner>> Race_Wait = new HashMap<>();
     public static ArrayList<Race_Runner> Race_Runner_list = new ArrayList<>();
     public static ArrayList<Race_Runner> Race_Runner_Wait_list = new ArrayList<>();
     public static ArrayList<Race> Race_list = new ArrayList<>();

     public static void joinRace(Race Race, int Rap, Player player) {
          if (!isJoin(player)) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Already join race");
               return;
          }
          if (Race_Wait.isEmpty()) {
               Race.setRap(Rap);
               Race_Runner Runner = new Race_Runner(player, Race);
               Race_Runner_Wait_list.add(Runner);
               Race_Wait.put(Race, new ArrayList<Race_Runner>());
               Race_Wait.get(Race).add(Runner);

               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Join Race!! : " + Race.getRace_name());
               return;
          }

          for (Race key : Race_Wait.keySet()) {
               if (Race.getRace_name().equals(key.getRace_name())) {
                    if (!(key.getRap() == Race.getRap())) {
                         player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + key.getRace_name() + " is Active Race Please wait");
                         return;
                    }
                    if (key.getJoin_Amount() == Race_Wait.get(Race).size()) {
                         player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Sorry Max Player");
                         return;
                    }
                    Race_Runner Runner = new Race_Runner(player, Race);
                    Race_Wait.get(Race).add(Runner);
                    Race_Runner_Wait_list.add(Runner);
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Join Race! : " + Race.getRace_name());
                    return;
               }
          }
          Race.setRap(Rap);
          Race_Runner Runner = new Race_Runner(player, Race);
          Race_Wait.put(Race, new ArrayList<Race_Runner>());
          Race_Wait.get(Race).add(Runner);
          Race_Runner_list.add(Runner);
          player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Join Race : " + Race.getRace_name());
     }

     public static void removeRunner(Player player) {
          if (!waterpunch.atamamozi_d.plugin.race.Race_Core.isJoin(player)) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Not join the race");
               return;
          }
          player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
          if (!Race_Runner_list.isEmpty()) for (Race_Runner val : Race_Runner_list) if (val.getPlayer() == player) {
               Race_Runner_list.remove(val);
               Race_Run.get(val.getRace()).remove(val);
               for (Entry<Race, ArrayList<Race_Runner>> entry : Race_Run.entrySet()) if (entry.getValue().size() == 0) Race_Run.remove(entry.getKey());
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "leave race");
               return;
          }
          if (!Race_Runner_Wait_list.isEmpty()) for (Race_Runner val : Race_Runner_Wait_list) if (val.getPlayer() == player) {
               Race_Runner_list.remove(val);
               Race_Wait.get(val.getRace()).remove(val);
               for (Entry<Race, ArrayList<Race_Runner>> entry : Race_Wait.entrySet()) if (entry.getValue().size() == 0) Race_Wait.remove(entry.getKey());
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "leave race");
               return;
          }
     }

     public static boolean isJoin(Player player) {
          for (Race_Runner val : Race_Runner_list) if (val.getPlayer() == player) return false;
          for (Race_Runner val : Race_Runner_Wait_list) if (val.getPlayer() == player) return false;
          return true;
     }

     public static Race getRace(String race) {
          for (int i = 0; i < Race_list.size(); i++) if (Race_list.get(i).getRace_name().equals(race)) return waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i);
          return null;
     }

     public static void Race_Start(Race race) {
          Race_Run.put(race, Race_Wait.get(race));
          Race_Wait.remove(race);

          int count = 0;

          for (Race_Runner runner : Race_Run.get(race)) {
               runner.getPlayer().teleport(race.getStartPointLoc().get(count).getLocation());
               if (race.getRace_Type() == Race_Type.BOAT) runner.getPlayer().getLocation().getWorld().spawnEntity(runner.getPlayer().getLocation(), EntityType.BOAT).addPassenger(runner.getPlayer());

               runner.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "START");

               Race_Runner_list.add(runner);
               Race_Runner_Wait_list.remove(runner);
               count++;
               //count down
          }
          return;
     }

     public static void clear() {
          Race_Run.clear();
          Race_Runner_list.clear();
          Race_Runner_Wait_list.clear();
          Race_Runner_list.clear();
          System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Atamamozi_D Memory clear");
     }

     public static void HashmapDebug(HashMap<Race, ArrayList<Race_Runner>> map) {
          if (map.isEmpty()) {
               System.out.println("Empty");
          }
          for (HashMap.Entry<Race, ArrayList<Race_Runner>> entry : map.entrySet()) {
               System.out.println(entry.getKey().toString() + ":");
               for (Race_Runner runner : map.get(entry.getKey())) {
                    System.out.println(runner.getPlayer().getDisplayName());
               }
          }
     }
}
