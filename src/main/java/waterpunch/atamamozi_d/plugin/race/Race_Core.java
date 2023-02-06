package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import waterpunch.atamamozi_d.plugin.tool.Race_Mode;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Race_Core {

     public static HashMap<Race, ArrayList<Race_Runner>> Race_Run = new HashMap<>();
     public static ArrayList<Player> Race_Runner_Onetime = new ArrayList<>();
     public static ArrayList<Race_Runner> Race_Runner_List = new ArrayList<>();
     public static ArrayList<Race> Race_list = new ArrayList<>();

     public static void joinRace(Race Race, int Rap, Player player) {
          if (isJoin(player)) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Already join race");
               return;
          }
          switch (Race.getMode()) {
               case WAIT:
                    if (Race_Run.isEmpty()) {
                         Race.setRap(Rap);
                         if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getName().equals(player.getName())) {
                              val.setMode(Race_Mode.WAIT);
                              Race_Run.put(Race, new ArrayList<Race_Runner>());
                              Race_Run.get(Race).add(val);
                              val.UPDate(Race, 0);
                              JoinMesseage(Race, player);
                              return;
                         }
                         Race_Runner Runner = new Race_Runner(player, Race, 0);
                         Race_Run.put(Race, new ArrayList<Race_Runner>());
                         Race_Run.get(Race).add(Runner);
                         JoinMesseage(Race, player);
                         return;
                    }

                    for (Race key : Race_Run.keySet()) if (Race.getRace_name().equals(key.getRace_name())) {
                         if (!(Race.getRap() == key.getRap())) {
                              player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + Race.getRace_name() + " is Active Race Please wait");
                              return;
                         }
                         if (Race.getJoin_Amount() == Race_Run.get(Race).size()) {
                              player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Sorry " + Race.getRace_name() + " is Max Player");
                              return;
                         }
                         if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getName().equals(player.getName())) {
                              Race_Run.get(Race).add(val);
                              val.UPDate(Race, Race_Run.get(key).size() - 1);
                              JoinMesseage(Race, player);
                              //////////////////////////

                              return;
                         }
                         Race_Runner Runner = new Race_Runner(player, Race, Race_Run.get(Race).size());
                         Race_Run.get(Race).add(Runner);
                         JoinMesseage(Race, player);
                         return;
                    }
                    Race.setRap(Rap);
                    if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getName().equals(player.getName())) {
                         val.setMode(Race_Mode.WAIT);
                         Race_Run.put(Race, new ArrayList<Race_Runner>());
                         Race_Run.get(Race).add(val);
                         val.UPDate(Race, 0);
                         JoinMesseage(Race, player);
                         return;
                    }
                    Race_Runner Runner = new Race_Runner(player, Race, 0);
                    Race_Run.put(Race, new ArrayList<Race_Runner>());
                    Race_Run.get(Race).add(Runner);
                    JoinMesseage(Race, player);
                    break;
               case RUN:
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + Race.getRace_name() + " is Active Race Please wait");
                    break;
               case GOAL:
                    Race.setMode(Race_Mode.WAIT);
                    joinRace(Race, Rap, player);
                    break;
          }
     }

     public static void JoinMesseage(Race race, Player player) {
          for (Race_Runner runner : Race_Run.get(race)) {
               runner.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + " " + Race_Run.get(race).size() + "/" + race.getJoin_Amount() + " : [" + ChatColor.AQUA + player.getName() + ChatColor.WHITE + "] is Join");
               runner.UpdateScoreboard();
          }
     }

     public static void LeaveMesseage(Race race, Player player) {
          for (Race_Runner runner : Race_Run.get(race)) {
               runner.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + " " + Race_Run.get(race).size() + "/" + race.getJoin_Amount() + " : [" + ChatColor.AQUA + player.getName() + ChatColor.WHITE + "] is Leave");
               runner.UpdateScoreboard();
          }
     }

     public static void removeRunner(Player player) {
          for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getUniqueId() == player.getUniqueId()) if (val.getMode() == Race_Mode.GOAL) {
               Race_Runner_List.remove(val);
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Leave the race");
               player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               return;
          }

          if (!isJoin(player)) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Not join the race");
               return;
          }
          if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getUniqueId() == player.getUniqueId()) {
               if (val.getRace().getRace_Type() == Race_Type.BOAT && !(val.getPlayer().getVehicle() == null)) {
                    Race_Runner_Onetime.add(val.getPlayer());

                    val.getPlayer().getVehicle().remove();
               }

               val.getPlayer().teleport(val.getst_Location());
               Race_Run.get(val.getRace()).remove(val);
               Race_Runner_List.remove(val);
               if (!(Race_Run.get(val.getRace()).size() == 0)) {
                    for (int i = 0; i == Race_Run.get(val.getRace()).size(); i++) Race_Run.get(val.getRace()).get(i).setJoin_Count(i);
                    LeaveMesseage(val.getRace(), player);
               }

               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Leave the race");
               player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               break;
          }
     }

     public static boolean isJoin(Player player) {
          for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getUniqueId() == player.getUniqueId()) {
               if (val.getMode() == Race_Mode.GOAL) return false;
               return true;
          }
          return false;
     }

     public static Race getRace(String race) {
          for (int i = 0; i < Race_list.size(); i++) if (Race_list.get(i).getRace_name().equals(race)) return waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i);
          return null;
     }

     public static void Race_Start(Race race) {
          for (Race key : Race_Run.keySet()) if (race.getRace_name().equals(key.getRace_name()) && key.getMode() == Race_Mode.RUN) {
               for (Race_Runner val : Race_Run.get(key)) val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "[" + ChatColor.AQUA + key.getRace_name() + ChatColor.WHITE + "] " + " is Active Race Please wait");
               return;
          }
          race.setMode(Race_Mode.RUN);
          for (Race_Runner runner : Race_Run.get(race)) runner.Start();
          return;
     }

     public static void Race_Goal(Race race) {
          for (Race key : Race_Run.keySet()) if (race.getRace_name().equals(key.getRace_name())) {
               race.setMode(Race_Mode.GOAL);
          }
     }

     public static void clear() {
          Race_Run.clear();
          if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) {
               val.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               if (!(val.getPlayer().getVehicle() == null)) {
                    Race_Runner_Onetime.add(val.getPlayer());
                    val.getPlayer().getVehicle().remove();
               }
          }

          System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Atamamozi_D Memory clear");
     }
}
