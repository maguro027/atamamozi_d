package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import waterpunch.atamamozi_d.plugin.tool.Timers.Leave_Timer;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer;

public class Race_Core {

     public static LinkedHashMap<UUID, ArrayList<Race_Runner>> Race_Run = new LinkedHashMap<>();
     public static ArrayList<Race> Race_list = new ArrayList<>();

     public static ArrayList<Race_Runner> Race_Runner_List = new ArrayList<>();
     public static ArrayList<Player> Race_Runner_Onetime = new ArrayList<>();
     public static ArrayList<Race_Timer> Timers = new ArrayList<>();

     public static void joinRace(Race Race, Player player) {
          if ((player.getDisplayName().equals("."))) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Sorry Only the Java version can participate in the race");
               return;
          }
          Race_Runner run = getRuner(player);
          if (isJoin(player)) {
               switch (run.getMode()) {
                    case WAIT:
                    case RUN:
                    case EDIT:
                         player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + "Already join race");
                         return;
                    default:
                         break;
               }
          }
          switch (Race.getMode()) {
               case WAIT:
                    if (Race_Run.get(Race.getUUID()) == null) Race_Run.put(Race.getUUID(), new ArrayList<Race_Runner>());
                    run = new Race_Runner(player, Race.getUUID());
                    Race_Run.get(Race.getUUID()).add(run);
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
          for (Race_Runner runner : Race_Run.get(race.getUUID())) {
               runner.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + " " + Race_Run.get(race.getUUID()).size() + "/" + race.getJoin_Amount() + " : [" + ChatColor.AQUA + player.getName() + ChatColor.WHITE + "] is Join");
               runner.UpdateScoreboard();
          }
     }

     public static void LeaveMesseage(Race race, Player player) {
          for (Race_Runner runner : Race_Run.get(race.getUUID())) {
               runner.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + " " + Race_Run.get(race.getUUID()).size() + "/" + race.getJoin_Amount() + " : [" + ChatColor.AQUA + player.getName() + ChatColor.WHITE + "] is Leave");
               runner.UpdateScoreboard();
          }
     }

     public static void removeRunner(Player player) {
          if (!isJoin(player)) {
               if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) return;
               if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getDisplayName().equals("Atamamozi_" + ChatColor.RED + "D")) player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               return;
          }

          Race_Runner run = getRuner(player);
          switch (run.getMode()) {
               case EDIT:
                    Race_Run.get(run.getRaceID()).remove(run);
                    Race_list.remove(getRace(run.getRaceID()));
                    Race_Runner_List.remove(run);
                    run = null;

                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Leave the race");

                    break;
               case WAIT:
               case RUN:
               case GOAL:
                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Leave the race");

                    RemoveCar(player);
                    run.getPlayer().teleport(run.getst_Location());
                    Race_Run.get(run.getRaceID()).remove(run);
                    if (Race_Run.get(run.getRaceID()).size() != 0) {
                         for (int i = 0; i == Race_Run.get(run.getRaceID()).size(); i++) Race_Run.get(run.getRaceID()).get(i).setJoin_Count(i);
                         LeaveMesseage(getRace(run.getRaceID()), player);
                    } else {
                         getRace(run.getRaceID()).setMode(Race_Mode.WAIT);
                         Race_Run.remove(run.getRaceID());
                    }
                    Race_Runner_List.remove(run);
                    if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(run.getRaceID()) == null) return;

                    int GOAL = 0;
                    for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(run.getRaceID())) if (val.getMode() == Race_Mode.GOAL) GOAL++;
                    if (GOAL == waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(run.getRaceID()).size()) waterpunch.atamamozi_d.plugin.race.Race_Core.AllGoal(run.getRaceID());
                    break;
               default:
                    Race_Runner_List.remove(run);
                    break;
          }
     }

     public static boolean isJoin(Player player) {
          if (Bukkit.getServer().getPlayer(player.getName()) == null) return false;
          if (Race_Run.isEmpty()) return false;
          if (getRuner(player) == null) return false;
          return true;
     }

     public static void RemoveCar(Player player) {
          if (getRace(getRuner(player).getRaceID()).getRace_Type() == Race_Type.BOAT && player.getVehicle() != null) {
               Race_Runner_Onetime.add(player);
               player.getVehicle().remove();
          }
     }

     public static Race_Runner getRuner(Player player) {
          for (Race_Runner val : Race_Runner_List) if (val.getPlayer().getUniqueId().equals(player.getUniqueId())) return val;
          return null;
     }

     public static Race getRace(String race_st) {
          if (Race_list.isEmpty()) return null;
          for (Race val : Race_list) if (val.getRace_name().equals(race_st)) return val;
          return null;
     }

     public static Race getRace(UUID race_uu) {
          for (Race val : Race_list) if (val.getUUID().equals(race_uu)) return val;
          return null;
     }

     public static void Race_Start(UUID Race_UUID) {
          switch (getRace(Race_UUID).getMode()) {
               case WAIT:
                    for (UUID key : Race_Run.keySet()) if (Race_UUID == key) for (Race_Runner val : Race_Run.get(key)) val.Start();
                    getRace(Race_UUID).setMode(Race_Mode.RUN);
                    break;
               case EDIT:
                    for (UUID key : Race_Run.keySet()) if (Race_UUID == key) {
                         for (Race_Runner val : Race_Run.get(key)) if (val.getMode() == Race_Mode.EDIT) val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + getRace(Race_UUID).getRace_name() + " is Editing now");
                         return;
                    }
                    break;
               case GOAL:
                    for (UUID key : Race_Run.keySet()) if (Race_UUID == key) {
                         for (Race_Runner val : Race_Run.get(key)) if (val.getMode() == Race_Mode.EDIT) val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + getRace(Race_UUID).getRace_name() + " is End");
                         return;
                    }
                    break;
               case RUN:
                    for (UUID key : Race_Run.keySet()) if (Race_UUID == key) {
                         for (Race_Runner val : Race_Run.get(key)) if (val.getMode() == Race_Mode.EDIT) val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + getRace(Race_UUID).getRace_name() + " is Active Race Please wait");
                         return;
                    }
                    break;
               default:
                    break;
          }
     }

     public static void AllGoal(UUID Race_ID) {
          Race RACE = waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(Race_ID);
          ArrayList<String> Score = new ArrayList<>();
          Score.add("------------" + "Atamamozi_" + ChatColor.RED + "D" + ChatColor.WHITE + "------------");
          Comparator<Race_Runner> comparator = Comparator.comparing(Race_Runner::getTime).reversed();
          Race_Run.get(RACE.getUUID()).stream().sorted(comparator).forEach(a -> Score.add(getScore(a)));
          Score.add("------------" + "Atamamozi_" + ChatColor.RED + "D" + ChatColor.WHITE + "------------");
          RACE.setScore(Score);
          ArrayList<Player> Players = new ArrayList<>();
          for (Race_Runner val : Race_Run.get(RACE.getUUID())) {
               for (String st : RACE.getScore()) val.getPlayer().sendMessage(st);
               val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Race leave is  /atd leave");
               Race_Runner_List.remove(getRuner(val.getPlayer()));
               Players.add(val.getPlayer());
          }
          new Leave_Timer(Players).runTaskTimer(waterpunch.atamamozi_d.plugin.main.Core.getthis(), 0L, 20L);
          Race_Goal(RACE.getUUID());
          RACE.setMode(Race_Mode.WAIT);
          Race_Run.remove(RACE.getUUID());
     }

     public static String getScore(Race_Runner runner) {
          return "[" + ChatColor.AQUA + runner.getPlayer().getName() + ChatColor.WHITE + "] : " + runner.getTimest();
     }

     public static void Race_Goal(UUID Race_UUID) {
          for (UUID key : Race_Run.keySet()) if (Race_UUID == key) getRace(Race_UUID).setMode(Race_Mode.GOAL);
     }

     public static void clear() {
          Race_Run.clear();
          if (!Race_Runner_List.isEmpty()) for (Race_Runner val : Race_Runner_List) {
               val.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               RemoveCar(val.getPlayer());
          }

          System.out.println(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Atamamozi_D Memory clear");
     }
}
