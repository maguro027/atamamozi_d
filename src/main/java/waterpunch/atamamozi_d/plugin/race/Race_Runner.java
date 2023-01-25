package waterpunch.atamamozi_d.plugin.race;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
          this.Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          if (Race.getRap() == Rap) {
               Goal();
          } else {
               waterpunch.atamamozi_d.plugin.tool.Scoreboard.setScoreboard(this);
          }
     }

     public void setRap(int i) {
          this.Rap = i;
     }

     public void Start() {
          this.Player.teleport(this.Race.getStartPointLoc().get(amount).getLocation());
          if (Race.getRace_Type() == Race_Type.BOAT) this.Player.getLocation().getWorld().spawnEntity(Player.getLocation(), EntityType.BOAT).addPassenger(Player);
          this.Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "START");
          this.start_time = System.currentTimeMillis();
     }

     public void ReSpawn() {
          this.Player.teleport(this.Race.getCheckPointLoc().get(getCheckPoint() - 1).getLocation());
          if (this.Race.getRace_Type() == Race_Type.BOAT) this.Player.getLocation().getWorld().spawnEntity(this.Player.getLocation(), EntityType.BOAT).addPassenger(this.Player);
          this.Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "Respawn");

          waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Runner_Onetime.add(this.Player);
     }

     public void Goal() {
          this.end_time = System.currentTimeMillis();
          this.Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          this.Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "GOAL!!");
          this.Player.sendMessage(DurationFormatUtils.formatPeriod(start_time, end_time, waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "HH:mm:ss.SSS"));

          waterpunch.atamamozi_d.plugin.race.Race_Core.removeRunner(Player);
          if (Race.getRace_Type() == Race_Type.BOAT) this.Player.getPassengers().get(0).remove();
          this.Player.teleport(st_Location);
     }
}
