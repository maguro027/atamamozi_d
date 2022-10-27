package waterpunch.atamamozi_d.plugin.race;

import org.bukkit.entity.Player;

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
          CheckPoint++;
     }

     public void setCheckPoint(int i) {
          CheckPoint = i;
     }

     public int getRap() {
          return Rap;
     }

     public void addRap() {
          Rap++;
     }

     public void setRap(int i) {
          Rap = i;
     }
}
