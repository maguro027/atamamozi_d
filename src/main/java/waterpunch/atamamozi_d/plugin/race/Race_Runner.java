package waterpunch.atamamozi_d.plugin.race;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Race_Runner {

     private Player Player;
     private Race Race;
     private int amount, CheckPoint, Rap;
     private long start_time, end_time;
     private Location st_Location;

     public Race_Runner(Player player, Race race, int amount) {
          this.Player = player;
          this.Race = race;
          this.start_time = System.currentTimeMillis();
          this.st_Location = player.getLocation();
          this.amount = amount;
          waterpunch.atamamozi_d.plugin.tool.Scoreboard.setScoreboard(this);
     }

     public void UpdateScoreboard(Player player) {
          waterpunch.atamamozi_d.plugin.tool.Scoreboard.setScoreboard(this);
     }

     public Player getPlayer() {
          return Player;
     }

     public void setRace(Race Race) {
          this.Race = Race;
     }

     public Race getRace() {
          return Race;
     }

     public Location getst_Location() {
          return st_Location;
     }

     public int getCheckPoint() {
          return CheckPoint;
     }

     public void addCheckPoint() {
          this.CheckPoint++;

          if (Race.getCheckPointLoc().size() == CheckPoint) {
               setCheckPoint(0);
               addRap();
          } else {
               this.Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               waterpunch.atamamozi_d.plugin.tool.Scoreboard.setScoreboard(this);
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
          if (Race.getRap() == Rap) {
               Goal();
          } else {
               waterpunch.atamamozi_d.plugin.tool.Scoreboard.setScoreboard(this);
          }
     }

     public void setRap(int i) {
          this.Rap = i;
     }

     public void tp(Player player, Location location) {
          player.getLocation(location);
     }

     public void Start() {
          Player.getLocation(Race.getStartPointLoc().get(amount - 1).getLocation());
          if (Race.getRace_Type() == Race_Type.BOAT) Race.getStartPointLoc().get(amount - 1).getLocation().getWorld().spawnEntity(Race.getStartPointLoc().get(amount - 1).getLocation(), EntityType.BOAT).addPassenger(Player);
          Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "START");
          start_time = System.currentTimeMillis();
     }

     public void ReSpawn() {
          if (getCheckPoint() == 0) {
               Player.getLocation(Race.getStartPointLoc().get(amount - 1).getLocation());
          } else {
               Player.getLocation(Race.getCheckPointLoc().get(getCheckPoint() - 1).getLocation());
          }

          if (Race.getRace_Type() == Race_Type.BOAT) {
               waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_Onetime.add(Player);
               Player.getVehicle().remove();
               Player.getLocation().getWorld().spawnEntity(Race.getStartPointLoc().get(amount - 1).getLocation(), EntityType.BOAT).addPassenger(Player);
          }
          this.Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Respawn");
     }

     public void Goal() {
          this.end_time = System.currentTimeMillis();
          Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "GOAL!!");
          for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race)) {
               val.getPlayer().sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "[" + ChatColor.AQUA + Player.getName() + ChatColor.WHITE + "] " + DurationFormatUtils.formatPeriod(start_time, end_time, "HH:mm:ss.SSS"));
          }
          waterpunch.atamamozi_d.plugin.race.Race_Core.removeRunner(Player);
          //  waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_Onetime.add(this.Player);
          // if (Race.getRace_Type() == Race_Type.BOAT) this.Player.getVehicle().remove();
          Player.teleport(st_Location);
     }

     public void AllGoal() {}
}
