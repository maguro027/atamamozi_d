package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.Comparator;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import waterpunch.atamamozi_d.plugin.tool.Race_Mode;
import waterpunch.atamamozi_d.plugin.tool.Race_Scoreboard;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Race_Runner {

     private Player Player;
     private Race Race;
     private Race_Mode Race_mode;
     private int Join_Count, CheckPoint, Rap;
     private long start_time, end_time;
     private Location st_Location;
     private Race_Scoreboard scoreboard;

     public Race_Runner(Player player, Race race, int Join_Count) {
          this.Player = player;
          this.Race = race;
          this.Race_mode = Race_Mode.WAIT;
          this.start_time = System.currentTimeMillis();
          this.st_Location = player.getLocation();
          this.Join_Count = Join_Count;
          this.scoreboard = new Race_Scoreboard();
          waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_List.add(this);
     }

     public void upDate(Race race, int Join_Count) {
          this.Race = race;
          this.Race_mode = Race_Mode.WAIT;
          this.start_time = System.currentTimeMillis();
          this.st_Location = Player.getLocation();
          this.Join_Count = Join_Count;
          this.Rap = 0;
          this.CheckPoint = 0;
     }

     public void UpdateScoreboard() {
          Player.setScoreboard(scoreboard.updateScoreboard(this));
     }

     public Player getPlayer() {
          return Player;
     }

     public Race getRace() {
          return Race;
     }

     public void setMode(Race_Mode mode) {
          this.Race_mode = mode;
     }

     public Race_Mode getMode() {
          return Race_mode;
     }

     public Location getst_Location() {
          return st_Location;
     }

     public int getCheckPoint() {
          return CheckPoint;
     }

     public Long getStart_time() {
          return start_time;
     }

     public Long getEnd_time() {
          return end_time;
     }

     public Long getTime() {
          return getStart_time() - getEnd_time();
     }

     public String getTimest() {
          return DurationFormatUtils.formatPeriod(getStart_time(), getEnd_time(), "HH:mm:ss.SSS");
     }

     public void addCheckPoint() {
          this.CheckPoint++;

          if (Race.getCheckPointLoc().size() == CheckPoint) {
               setCheckPoint(0);
               addRap();
          } else {
               this.Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               UpdateScoreboard();
          }
     }

     public void setCheckPoint(int i) {
          CheckPoint = i;
     }

     public int getRap() {
          return Rap;
     }

     public void addRap() {
          this.Rap++;
          Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          UpdateScoreboard();
          if (Race.getRap() == Rap) Goal();
     }

     public void setRap(int i) {
          this.Rap = i;
     }

     public void Start() {
          this.Race_mode = Race_Mode.RUN;
          this.Player.getLocation(Race.getStartPointLoc().get(Join_Count).getLocation());
          if (Race.getRace_Type() == Race_Type.BOAT) this.Player.getLocation().getWorld().spawnEntity(this.Race.getStartPointLoc().get(Join_Count).getLocation(), EntityType.BOAT).addPassenger(Player);
          this.Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "START");
          this.start_time = System.currentTimeMillis();
          start_time = System.currentTimeMillis();
          UpdateScoreboard();
     }

     public void ReSpawn() {
          if (!(Race_mode == Race_Mode.RUN)) {
               Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Race is Not Active");
               return;
          }
          if (CheckPoint == 0) {
               this.Player.getLocation(Race.getStartPointLoc().get(Join_Count).getLocation());
          } else {
               this.Player.getLocation(Race.getCheckPointLoc().get(getCheckPoint()).getLocation());
          }

          if (Race.getRace_Type() == Race_Type.BOAT) {
               waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_Onetime.add(Player);
               this.Player.getVehicle().remove();
               this.Player.getLocation().getWorld().spawnEntity(Race.getStartPointLoc().get(Join_Count).getLocation(), EntityType.BOAT).addPassenger(Player);
          }
          this.Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Respawn");
     }

     public void Goal() {
          this.end_time = System.currentTimeMillis();
          setMode(Race_Mode.GOAL);
          Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "GOAL!!");
          for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race)) {
               val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "[" + ChatColor.AQUA + Player.getName() + ChatColor.WHITE + "] " + getTimest());
               val.UpdateScoreboard();
          }
          if (Race.getRace_Type() == Race_Type.BOAT) this.Player.getVehicle().remove();
          Player.teleport(st_Location);
          UpdateScoreboard();
          int i = 0;
          for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race)) if (val.getMode() == Race_Mode.GOAL) i++;
          if (i == waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race).size()) {
               ArrayList<String> Score = new ArrayList<>();
               Score.add("------------" + "Atamamozi_" + ChatColor.RED + "D" + ChatColor.WHITE + "------------");

               Comparator<Race_Runner> comparator = Comparator.comparing(Race_Runner::getTime).reversed();
               waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race).stream().sorted(comparator).forEach(a -> Score.add("[" + ChatColor.AQUA + a.getPlayer().getName() + ChatColor.WHITE + "] : " + a.getTimest()));
               Score.add("------------" + "Atamamozi_" + ChatColor.RED + "D" + ChatColor.WHITE + "------------");
               Race.setScore(Score);
               AllGoal();
          }
     }

     public void AllGoal() {
          for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race)) {
               for (String st : Race.getScore()) val.getPlayer().sendMessage(st);
               val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Race leave is  /atamamoazi_d leave");
               val.setMode(Race_Mode.GOAL);
          }
          waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Goal(Race);
          waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.remove(Race);
     }
}
