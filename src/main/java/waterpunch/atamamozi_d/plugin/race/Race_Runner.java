package waterpunch.atamamozi_d.plugin.race;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class Race_Runner {

     private Player Player;
     private Race Race;
     private int CheckPoint, Rap;

     public Race_Runner(Player player, Race race) {
          this.Player = player;
          this.Race = race;
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
               this.Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               this.Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "CLEAR!!");
               waterpunch.atamamozi_d.plugin.race.Race_Core.removeRunner(Player);
          } else {
               waterpunch.atamamozi_d.plugin.tool.Scoreboard.setScoreboard(this);
          }
     }

     public void setRap(int i) {
          this.Rap = i;
     }
}
