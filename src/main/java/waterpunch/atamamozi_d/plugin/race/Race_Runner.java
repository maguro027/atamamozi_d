package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.Comparator;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import waterpunch.atamamozi_d.plugin.tool.LocationViewer;
import waterpunch.atamamozi_d.plugin.tool.Race_Mode;
import waterpunch.atamamozi_d.plugin.tool.Race_Scoreboard;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Race_Runner {

     private Player Player;
     private Race Race;
     private Race_Mode Race_mode;
     private int Join_Count, CheckPoint, Rap;
     private long start_time, end_time;
     private Location st_Location, old_Location, new_Location;
     private Race_Scoreboard scoreboard;
     private LocationViewer locationViewer;

     public Race_Runner(Player player, Race race, int Join_Count) {
          this.Player = player;
          this.Race = race;
          this.Race_mode = Race_Mode.WAIT;
          this.start_time = System.currentTimeMillis();
          this.st_Location = player.getLocation();
          this.Join_Count = Join_Count;
          this.scoreboard = new Race_Scoreboard();
          this.new_Location = player.getLocation();
          this.old_Location = player.getLocation();
          waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_List.add(this);
          this.locationViewer = new LocationViewer(this);
     }

     public void UPDate(Race race, int Join_Count) {
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

     public void setnewLoc(Location loc) {
          this.new_Location = loc;
     }

     public void setoldLoc(Location loc) {
          this.old_Location = loc;
     }

     public Location getnewLoc() {
          return new_Location;
     }

     public Location getoldLoc() {
          return old_Location;
     }

     public void setJoin_Count(int i) {
          this.Join_Count = i;
     }

     public int getJoin_Count() {
          return Join_Count;
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

          if (Race.getCheckPointLoc().size() == getCheckPoint()) {
               setCheckPoint(0);
               addRap();
          } else {
               this.Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               UpdateScoreboard();
               locationViewer.DrawCircle();
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

     public LocationViewer getLocationViewer() {
          return this.locationViewer;
     }

     public void Start() {
          this.Race_mode = Race_Mode.RUN;
          this.Player.teleport(Race.getStartPointLoc().get(Join_Count).getLocation());
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
          switch (Race.getRace_Type()) {
               case RUN:
                    if (getCheckPoint() == 0) {
                         getPlayer().teleport(Race.getStartPointLoc().get(Join_Count).getLocation());
                    } else {
                         getPlayer().teleport(Race.getCheckPointLoc().get(getCheckPoint()).getLocation());
                    }
                    break;
               case BOAT:
                    if (Race.getRace_Type() == Race_Type.BOAT) {
                         waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_Onetime.add(Player);
                         getPlayer().getVehicle().remove();
                         if (getCheckPoint() == 0) {
                              Race.getStartPointLoc().get(getJoin_Count()).getLocation().getWorld().spawnEntity(Race.getStartPointLoc().get(getJoin_Count()).getLocation(), EntityType.BOAT).addPassenger(Player);
                         } else {
                              Race.getCheckPointLoc().get(getCheckPoint() - 1).getLocation().getWorld().spawnEntity(Race.getCheckPointLoc().get(getCheckPoint() - 1).getLocation(), EntityType.BOAT).addPassenger(Player);
                         }
                    }
                    break;
          }

          this.Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Respawn");
     }

     public void Goal() {
          this.end_time = System.currentTimeMillis();
          setMode(Race_Mode.GOAL);
          getPlayer().playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "GOAL!!");
          for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race)) {
               val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "[" + ChatColor.AQUA + Player.getName() + ChatColor.WHITE + "] " + getTimest());
               val.UpdateScoreboard();
          }
          if (Race.getRace_Type() == Race_Type.BOAT) this.Player.getVehicle().remove();
          getPlayer().teleport(st_Location);
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
