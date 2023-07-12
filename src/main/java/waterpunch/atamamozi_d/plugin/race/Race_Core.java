package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class Race_Core {

     public static LinkedHashMap<Race, ArrayList<Race_Runner>> Race_Run = new LinkedHashMap<>();
     public static ArrayList<Race> Race_list = new ArrayList<>();
     public static ArrayList<Player> Race_Runner_Onetime = new ArrayList<>();
     public static ArrayList<Race_Runner> Race_Runner_List = new ArrayList<>();

     public static void joinRace(Race Race, Player player) {
          if ((player.getDisplayName().equals("."))) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Sorry Only the Java version can participate in the race");
               return;
          }
          if (isJoin(player)) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Already join race");
               return;
          }
          switch (Race.getMode()) {
               case WAIT:
                    if (Race_Run.isEmpty()) {
                         Race.setRap(Race.getRap());
                         if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getUniqueId() == player.getUniqueId()) {
                              val.setMode(Race_Mode.WAIT);
                              Race_Run.put(Race, new ArrayList<Race_Runner>());
                              Race_Run.get(Race).add(val);
                              val.UPDate(Race.getUUID(), 0);
                              JoinMesseage(Race, player);
                              return;
                         }
                         Race_Runner Runner = new Race_Runner(player, Race.getUUID(), 0);
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
                         if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getUniqueId() == player.getUniqueId()) {
                              Race_Run.get(Race).add(val);
                              val.UPDate(Race.getUUID(), Race_Run.get(key).size() - 1);
                              JoinMesseage(Race, player);
                              return;
                         }
                         Race_Runner Runner = new Race_Runner(player, Race.getUUID(), Race_Run.get(Race).size());
                         Race_Run.get(Race).add(Runner);
                         JoinMesseage(Race, player);
                         return;
                    }
                    Race.setRap(Race.getRap());
                    if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getUniqueId() == player.getUniqueId()) {
                         val.setMode(Race_Mode.WAIT);
                         Race_Run.put(Race, new ArrayList<Race_Runner>());
                         Race_Run.get(Race).add(val);
                         val.UPDate(Race.getUUID(), 0);
                         JoinMesseage(Race, player);
                         return;
                    }
                    Race_Runner Runner = new Race_Runner(player, Race.getUUID(), 0);
                    Race_Run.put(Race, new ArrayList<Race_Runner>());
                    Race_Run.get(Race).add(Runner);
                    JoinMesseage(Race, player);
                    break;
               case RUN:
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + Race.getRace_name() + " is Active Race Please wait");
                    break;
               case GOAL:
                    Race.setMode(Race_Mode.WAIT);
                    joinRace(Race, player);
                    break;
               case EDIT:
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + Race.getRace_name() + " is Editing now");
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + Race.getRace_name() + " Give it some time to try.");
                    break;
               default:
                    break;
          }
     }

     public static void JoinMesseage(Race race, Player player) {
          for (Race_Runner runner : Race_Run.get(race)) {
               runner.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + " " + Race_Run.get(race).size() + "/" + race.getJoin_Amount() + " : [" + ChatColor.AQUA + player.getName() + ChatColor.WHITE + "] is Join");
               runner.UpdateScoreboard();
          }
          if (Race_Run.get(race).size() == 1) race.Count();
     }

     public static void LeaveMesseage(Race race, Player player) {
          for (Race_Runner runner : Race_Run.get(race)) {
               runner.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + " " + Race_Run.get(race).size() + "/" + race.getJoin_Amount() + " : [" + ChatColor.AQUA + player.getName() + ChatColor.WHITE + "] is Leave");
               runner.UpdateScoreboard();
          }
     }

     public static void removeRunner(Player player) {
          if (Race_Run.isEmpty()) {
               if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) return;
               if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getDisplayName().equals("Atamamozi_" + ChatColor.RED + "D")) player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               return;
          }
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          if (run == null) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Not join the race");
               return;
          }
          switch (run.getMode()) {
               case EMPTY:
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Not join the race");
                    break;
               case EDIT:
                    waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID())).remove(run);
                    Race_list.remove(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()));

                    Race_Runner_List.remove(run);
                    run.setMode(Race_Mode.EMPTY);

                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Leave the race");

                    break;
               case WAIT:
               case RUN:
               case GOAL:
                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Leave the race");
                    Race_Runner_List.remove(getRuner(player));
                    run.setMode(Race_Mode.EMPTY);

                    if (waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()).getRace_Type() == Race_Type.BOAT && !(run.getPlayer().getVehicle() == null)) {
                         Race_Runner_Onetime.add(run.getPlayer());
                         run.getPlayer().getVehicle().remove();
                    }
                    run.getPlayer().teleport(run.getst_Location());
                    if (!Race_Run.isEmpty()) Race_Run.get(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID())).remove(run);
                    Race_Runner_List.remove(run);
                    if (!(Race_Run.get(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID())).size() == 0)) {
                         for (int i = 0; i == Race_Run.get(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID())).size(); i++) Race_Run.get(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID())).get(i).setJoin_Count(i);
                         LeaveMesseage(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()), player);
                    } else {
                         waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()).setMode(Race_Mode.WAIT);
                         Race_Run.remove(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(run.getRaceID()));
                    }
                    break;
               default:
                    Race_Runner_List.remove(run);
                    break;
          }
     }

     public static boolean isJoin(Player player) {
          Race_Runner run = waterpunch.atamamozi_d.plugin.race.Race_Core.getRuner(player);
          if (run == null || run.getMode() == Race_Mode.GOAL) return false;
          return true;
     }

     public static Race_Runner getRuner(Player player) {
          for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getUniqueId() == player.getUniqueId()) return val;
          return null;
     }

     public static Race getRace(String race_st) {
          for (Race val : Race_list) if (val.getRace_name().equals(race_st)) return val;
          return null;
     }

     public static Race getRace(UUID race_uu) {
          for (Race val : Race_list) if (val.getUUID().equals(race_uu)) return val;
          return null;
     }

     public static void Race_Start(Race race) {
          switch (race.getMode()) {
               case WAIT:
                    for (Race key : Race_Run.keySet()) if (race.getRace_name().equals(key.getRace_name())) for (Race_Runner val : Race_Run.get(key)) val.Start();
                    race.setMode(Race_Mode.RUN);
                    break;
               case EDIT:
                    for (Race key : Race_Run.keySet()) if (race.getRace_name().equals(key.getRace_name())) {
                         for (Race_Runner val : Race_Run.get(key)) if (val.getMode() == Race_Mode.EDIT) val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + race.getRace_name() + " is Editing now");
                         return;
                    }
                    break;
               case GOAL:
                    for (Race key : Race_Run.keySet()) if (race.getRace_name().equals(key.getRace_name())) {
                         for (Race_Runner val : Race_Run.get(key)) if (val.getMode() == Race_Mode.EDIT) val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + race.getRace_name() + " is End");
                         return;
                    }
                    break;
               case RUN:
                    for (Race key : Race_Run.keySet()) if (race.getRace_name().equals(key.getRace_name())) {
                         for (Race_Runner val : Race_Run.get(key)) if (val.getMode() == Race_Mode.EDIT) val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + race.getRace_name() + " is Active Race Please wait");
                         return;
                    }
                    break;
               default:
                    break;
          }
     }

     public static void Race_Goal(Race race) {
          for (Race key : Race_Run.keySet()) if (race.getRace_name().equals(key.getRace_name())) race.setMode(Race_Mode.GOAL);
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
