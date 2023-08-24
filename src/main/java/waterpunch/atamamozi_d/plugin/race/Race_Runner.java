package waterpunch.atamamozi_d.plugin.race;

import java.util.UUID;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import waterpunch.atamamozi_d.plugin.tool.Location.LocationViewer;
import waterpunch.atamamozi_d.plugin.tool.Scoreboaed.Race_Scoreboard;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer_Type;

public class Race_Runner {

     private Player Player;
     private UUID Race_ID;
     private String Race_Name;
     private Race_Mode Race_mode;
     private int Join_Count, CheckPoint, Rap;
     private long start_time, end_time;
     private Location st_Location, old_Location, new_Location;
     private Race_Scoreboard scoreboard;
     private LocationViewer locationViewer;
     private UUID Car;

     public Race_Runner(Player player, UUID Race_ID) {
          this.Player = player;
          UPDate(Race_ID);
          this.scoreboard = new Race_Scoreboard();
          this.new_Location = player.getLocation();
          this.old_Location = player.getLocation();
          waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_List.add(this);
          this.locationViewer = new LocationViewer(this);
     }

     public void UPDate(UUID Race_ID) {
          this.Race_ID = Race_ID;
          this.Race_mode = Race_Mode.WAIT;
          this.start_time = System.currentTimeMillis();
          this.st_Location = Player.getLocation();
          this.Join_Count = waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race_ID).size() + 1;
          if (getJoin_Count() == 1) new Race_Timer(Race_Timer_Type.WAIT, getRaceID()).runTaskTimer(waterpunch.atamamozi_d.plugin.main.Core.getthis(), 0L, 20L);
          this.Rap = 0;
          this.CheckPoint = 0;
     }

     public void UpdateScoreboard() {
          Player.setScoreboard(scoreboard.updateScoreboard(this));
     }

     public Player getPlayer() {
          return Player;
     }

     public UUID getRaceID() {
          return Race_ID;
     }

     public void setRaceID(UUID ID) {
          this.Race_ID = ID;
     }

     public int getJoinCount() {
          return waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(getRaceID()).size() - 1;
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
          return getEnd_time() - getStart_time();
     }

     public String getTimest() {
          return DurationFormatUtils.formatPeriod(getStart_time(), getEnd_time(), "HH:mm:ss.SSS");
     }

     public String getNOWTimest() {
          return DurationFormatUtils.formatPeriod(getStart_time(), System.currentTimeMillis(), "HH:mm:ss.SSS");
     }

     public UUID getCar() {
          return Car;
     }

     public void setCar(UUID uuid) {
          this.Car = uuid;
     }

     public void addCheckPoint() {
          this.CheckPoint++;
          if (waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(Race_ID).getCheckPointLoc().size() == getCheckPoint()) {
               setCheckPoint(0);
               addRap();
          } else {
               this.Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               locationViewer.DrawCircle(CheckPoint);
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
          if (waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(Race_ID).getRap() == Rap) Goal();
     }

     public void setRap(int i) {
          this.Rap = i;
     }

     public LocationViewer getLocationViewer() {
          return this.locationViewer;
     }

     public void Start() {
          this.Race_mode = Race_Mode.RUN;
          Race RACE = waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(Race_ID);
          Player.teleport(RACE.getStartPointLoc().get(Join_Count - 1).getLocation());

          Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          Player.sendTitle(ChatColor.GREEN + " - START - ", "", 10, 15, 10);

          switch (waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(Race_ID).getRace_Type()) {
               case BOAT:
                    waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_Onetime.add(Player);
                    Player.getLocation().getWorld().spawnEntity(RACE.getStartPointLoc().get(Join_Count - 1).getLocation(), EntityType.BOAT).addPassenger(Player);
                    Car = Player.getVehicle().getUniqueId();
                    break;
               case WALK:
                    break;
               default:
                    Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + ChatColor.RED + "A fatal error has occurred");
                    Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + ChatColor.RED + "----------------------");
                    Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + ChatColor.RED + Race_Name);
                    Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + ChatColor.RED + "Unknown Race Type [" + RACE.getRace_Type() + "]");
                    Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setWarning() + ChatColor.RED + "----------------------");
                    waterpunch.atamamozi_d.plugin.race.Race_Core.removeRunner(Player);
                    break;
          }
          this.Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "START");
          this.start_time = System.currentTimeMillis();
          start_time = System.currentTimeMillis();
          UpdateScoreboard();
     }

     public void ReSpawn() {
          Race RACE = waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(Race_ID);
          switch (Race_mode) {
               case EDIT:
               case GOAL:
                    Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Race is Not Active");
                    return;
               default:
                    break;
          }
          switch (RACE.getRace_Type()) {
               case WALK:
                    if (getCheckPoint() == 0) {
                         Player.teleport(RACE.getStartPointLoc().get(getJoin_Count()).getLocation());
                    } else {
                         Player.teleport(RACE.getCheckPointLoc().get(getCheckPoint() - 1).getLocation());
                    }
                    break;
               case BOAT:
                    waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_Onetime.add(Player);
                    if (!(getPlayer().getVehicle() == null)) getPlayer().getVehicle().remove();
                    if (getCheckPoint() == 0) {
                         RACE.getStartPointLoc().get(getJoin_Count()).getLocation().getWorld().spawnEntity(RACE.getStartPointLoc().get(getJoin_Count()).getLocation(), EntityType.BOAT).addPassenger(Player);
                    } else {
                         RACE.getCheckPointLoc().get(getCheckPoint() - 1).getLocation().getWorld().spawnEntity(RACE.getCheckPointLoc().get(getCheckPoint() - 1).getLocation(), EntityType.BOAT).addPassenger(Player);
                    }

                    break;
          }

          this.Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Respawn");
     }

     public void Goal() {
          Race RACE = waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(Race_ID);
          this.end_time = System.currentTimeMillis();
          setMode(Race_Mode.GOAL);
          getPlayer().playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "GOAL!!");
          for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(RACE.getUUID())) {
               val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "[" + ChatColor.AQUA + Player.getName() + ChatColor.WHITE + "] " + getTimest());
               val.UpdateScoreboard();
          }
          if (RACE.getRace_Type() == Race_Type.BOAT) this.Player.getVehicle().remove();
          getPlayer().teleport(st_Location);
          UpdateScoreboard();
          waterpunch.atamamozi_d.plugin.score.Player_Score_Core.addPlayer_Score(getPlayer(), getRaceID(), getTime());
          int i = 0;
          for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(RACE.getUUID())) if (val.getMode() == Race_Mode.GOAL) i++;
          if (i == waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(RACE.getUUID()).size()) waterpunch.atamamozi_d.plugin.race.Race_Core.AllGoal(RACE.getUUID());
     }

     public void sendMessage(String string) {}
}
