package waterpunch.atamamozi_d.plugin.score;

/**
 * ランキングパーツクラス
 * プレイヤー名とタイムのペアを保持する
 * 
 * Ranking parts class
 * Holds player name and time pair
 */
public class Ranking_parts {

     /** プレイヤー名 / Player name */
     private String Player_NAME;
     
     /** タイム / Time */
     private Long TIME = null;

     /**
      * ランキングパーツを作成する
      * 
      * Creates ranking parts
      * 
      * @param Player_NAME プレイヤー名 / Player name
      * @param Time タイム / Time
      */
     public Ranking_parts(String Player_NAME, Long Time) {
          this.Player_NAME = Player_NAME;
          this.TIME = Time;
     }

     /**
      * プレイヤー名を取得する
      * 
      * Gets player name
      * 
      * @return プレイヤー名 / Player name
      */
     public String getNAME() {
          return Player_NAME;
     }

     /**
      * タイムを取得する
      * 
      * Gets time
      * 
      * @return タイム / Time
      */
     public Long getTIME() {
          return TIME;
     }
}
