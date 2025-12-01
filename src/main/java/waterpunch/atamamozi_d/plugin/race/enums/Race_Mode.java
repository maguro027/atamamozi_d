package waterpunch.atamamozi_d.plugin.race.enums;

/**
 * レースモード列挙型
 * レースの現在の状態を表す
 * 
 * Race mode enumeration
 * Represents current state of the race
 */
public enum Race_Mode {
     /** 待機中 / Waiting */
     WAIT(0),
     
     /** 実行中 / Running */
     RUN(1),
     
     /** ゴール済み / Finished */
     GOAL(2),
     
     /** 編集中 / Editing */
     EDIT(3);

     @SuppressWarnings("unused")
     private int id;

     private Race_Mode(int id) {
          this.id = id;
     }
}
