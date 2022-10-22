package waterpunch.atamamozi_d.plugin.race;

import org.bukkit.entity.Player;

public class Race_Runner {

     private Player player;
     private Race race;
     private int CheckPoint, Rap, Now_Rap;

     public Race_Runner(Player player, Race race) {
          this.player = player;
          this.race = race;
          this.Rap = race.getRap();
          waterpunch.atamamozi_d.plugin.tool.Scoreboard.setScoreboard(this);
     }

     public Race_Runner(String string, Player player2) {}

     public Player getPlayer() {
          return player;
     }

     public void setRace(Race race) {
          this.race = race;
     }

     public Race getRace() {
          return race;
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

     public int getNowRap() {
          return Now_Rap;
     }

     public void addNowRap() {
          Now_Rap++;
     }

     public void setNowRap(int i) {
          Now_Rap = i;
     }
}
