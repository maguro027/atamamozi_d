package waterpunch.atamamozi_d.plugin.race.enums;

/**
 * ランナーモード列挙型
 * プレイヤーのレース参加状態を表す
 * 
 * Runner mode enumeration
 * Represents player's race participation state
 */
public enum Race_Runner_Mode {
     /** 未参加 / Not entered */
     NO_ENTRY(0),
     
     /** 待機中 / Waiting */
     WAIT(1),
     
     /** レース中 / Running */
     RUN(2),
     
     /** 全員ゴール待機 / Waiting for all to finish */
     ALL_GOAL_WAIT(3),

     /** 編集中 / Editing */
     EDIT(4);

     @SuppressWarnings("unused")
     private int id;

     private Race_Runner_Mode(int id) {
          this.id = id;
     }
}
