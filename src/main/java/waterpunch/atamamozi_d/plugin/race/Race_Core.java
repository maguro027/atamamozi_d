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
          if (Race.getMode() == Race_Mode.RUN) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + Race.getRace_name() + " is Active Race Please wait");
               return;
          }
          if (Race_Run.isEmpty()) {
               Race.setRap(Rap);
               Race_Runner Runner = new Race_Runner(player, Race, 0);
               Race_Run.put(Race, new ArrayList<Race_Runner>());
               Race_Run.get(Race).add(Runner);
               Race_Runner_List.add(Runner);
               JoinMesseage(Race, player);
               return;
          }
          if (Race.getJoin_Amount() == Race_Run.get(Race).size()) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Sorry Max Player");
               return;
          }
          for (Race key : Race_Run.keySet()) if (Race.getRace_name().equals(key.getRace_name())) {
               Race_Runner Runner = new Race_Runner(player, Race, Race_Run.get(Race).size());
               Race_Run.get(Race).add(Runner);
               Race_Runner_List.add(Runner);
               JoinMesseage(Race, player);
               return;
          }
     }

     public static void JoinMesseage(Race race, Player player) {
          for (Race_Runner runner : Race_Run.get(race)) {
               runner.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + " " + Race_Run.get(race).size() + "/" + race.getJoin_Amount() + " : [" + ChatColor.AQUA + player.getName() + ChatColor.WHITE + "]");
               runner.UpdateScoreboard();
          }
     }

     public static void removeRunner(Player player) {
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
               Race_Runner_List.remove(val);
               Race_Runner_List.remove(val);

               break;
          }
          player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Leave the race");
          player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
     }

     public static boolean isJoin(Player player) {
          for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getUniqueId() == player.getUniqueId()) return true;
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

          for (Race_Runner runner : Race_Run.get(race)) {
               Race_Runner_List.add(runner);
               runner.setMode(Race_Mode.RUN);
               runner.Start();
          }
          return;
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
