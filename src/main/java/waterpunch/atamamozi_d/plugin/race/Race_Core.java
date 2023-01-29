package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Race_Core {

     public static HashMap<Race, ArrayList<Race_Runner>> Race_Run = new HashMap<>();
     public static HashMap<Race, ArrayList<Race_Runner>> Race_Wait = new HashMap<>();
     public static ArrayList<Player> Race_Runner_Onetime = new ArrayList<>();
     public static ArrayList<Race_Runner> Race_Runner_List = new ArrayList<>();
     public static ArrayList<Race_Runner> Race_Runner_Wait_list = new ArrayList<>();
     public static ArrayList<Race> Race_list = new ArrayList<>();

     public static void joinRace(Race Race, int Rap, Player player) {
          if (!isJoin(player)) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Already join race");
               return;
          }
          if (Race_Wait.isEmpty()) {
               Race.setRap(Rap);
               Race_Runner Runner = new Race_Runner(player, Race, 1);
               Race_Runner_Wait_list.add(Runner);
               Race_Wait.put(Race, new ArrayList<Race_Runner>());
               Race_Wait.get(Race).add(Runner);
               JoinMesseage(Race, player);
               return;
          }

          for (Race key : Race_Wait.keySet()) if (Race.getRace_name().equals(key.getRace_name())) {
               if (!(key.getRap() == Race.getRap())) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + key.getRace_name() + " is Active Race Please wait");
                    return;
               }
               if (key.getJoin_Amount() == Race_Wait.get(Race).size()) {
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Sorry Max Player");
                    return;
               }
               Race_Runner Runner = new Race_Runner(player, Race, Race_Wait.get(Race).size() + 1);
               Race_Wait.get(Race).add(Runner);
               Race_Runner_Wait_list.add(Runner);
               JoinMesseage(Race, player);
               return;
          }

          Race.setRap(Rap);
          Race_Runner Runner = new Race_Runner(player, Race, 1);
          Race_Wait.put(Race, new ArrayList<Race_Runner>());
          Race_Wait.get(Race).add(Runner);
          Race_Runner_List.add(Runner);
          JoinMesseage(Race, player);
     }

     public static void JoinMesseage(Race race, Player player) {
          for (Race_Runner runner : Race_Wait.get(race)) {
               runner.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + " " + Race_Wait.get(race).size() + "/" + race.getJoin_Amount() + " : [" + ChatColor.AQUA + player.getName() + ChatColor.WHITE + "]");
          }
     }

     public static void removeRunner(Player player) {
          if (isJoin(player)) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Not join the race");
               return;
          }
          player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Leave the race");
          player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
          if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) if (val.getPlayer() == player) {
               if (val.getRace().getRace_Type() == Race_Type.BOAT && !(val.getPlayer().getVehicle() == null)) {
                    Race_Runner_Onetime.add(val.getPlayer());
                    val.getPlayer().getVehicle().remove();
               }
               val.getPlayer().teleport(val.getst_Location());

               Race_Runner_List.remove(val);
               break;
          }
          if (!Race_Runner_Wait_list.isEmpty()) for (Race_Runner val : Race_Runner_Wait_list) if (val.getPlayer() == player) {
               Race_Runner_Wait_list.remove(val);
               break;
          }
          if (!Race_Run.isEmpty()) for (Race Run_key : Race_Run.keySet()) for (Race_Runner Run_run : Race_Run.get(Run_key)) if (Run_run.getPlayer() == player) {
               Race_Run.get(Run_key).remove(Run_run);
               break;
          }
          if (!Race_Wait.isEmpty()) for (Race Run_key : Race_Wait.keySet()) for (Race_Runner Run_run : Race_Wait.get(Run_key)) if (Run_run.getPlayer() == player) {
               Race_Wait.get(Run_key).remove(Run_run);
               break;
          }
     }

     public static boolean isJoin(Player player) {
          for (Race_Runner val : Race_Runner_List) if (val.getPlayer() == player) return false;
          for (Race_Runner val : Race_Runner_Wait_list) if (val.getPlayer() == player) return false;
          return true;
     }

     public static Race getRace(String race) {
          for (int i = 0; i < Race_list.size(); i++) if (Race_list.get(i).getRace_name().equals(race)) return waterpunch.atamamozi_d.plugin.race.Race_Core.Race_list.get(i);
          return null;
     }

     public static void Race_Start(Race race) {
          for (Race key : Race_Run.keySet()) if (race.getRace_name().equals(key.getRace_name())) {
               for (Race_Runner val : Race_Wait.get(key)) val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "[" + ChatColor.AQUA + key.getRace_name() + ChatColor.WHITE + "] " + " is Active Race Please wait");
               return;
          }
          Race_Run.put(race, Race_Wait.get(race));
          Race_Wait.remove(race);

          if (Race_Run.get(race).isEmpty()) return;
          for (Race_Runner runner : Race_Run.get(race)) {
               Race_Runner_List.add(runner);
               Race_Runner_Wait_list.remove(runner);
               runner.Start();
          }
          return;
     }

     public static void clear() {
          Race_Run.clear();
          Race_Wait.clear();
          if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) {
               val.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               if (!(val.getPlayer().getVehicle() == null)) {
                    Race_Runner_Onetime.add(val.getPlayer());
                    val.getPlayer().getVehicle().remove();
               }
          }
          if (!Race_Runner_Wait_list.isEmpty()) for (Race_Runner val : Race_Runner_Wait_list) val.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
          Race_Runner_List.clear();
          Race_Runner_Wait_list.clear();

          System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Atamamozi_D Memory clear");
     }
}
