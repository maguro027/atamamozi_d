package waterpunch.atamamozi_d.plugin.score;

public class Ranking_parts {

     private String Player_NAME;
     private Long TIME = null;

     public Ranking_parts(String Player_NAME, Long Time) {
          this.Player_NAME = Player_NAME;
          this.TIME = Time;
     }

     public String getNAME() {
          return Player_NAME;
     }

     public Long getTIME() {
          return TIME;
     }
}
