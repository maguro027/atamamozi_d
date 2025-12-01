package waterpunch.atamamozi_d.plugin.tool.Timers;

/**
 * レースタイマータイプ列挙型
 * タイマーの種類を表す
 * 
 * Race timer type enumeration
 * Represents type of timer
 */
public enum Race_Timer_Type {
     /** 待機タイマー / Wait timer */
     WAIT(0),
     
     /** スタートカウントダウン / Start countdown */
     START(1),
     
     /** 余韻タイマー / Lingering timer */
     YOIN(2);

     @SuppressWarnings("unused")
     private int id;

     private Race_Timer_Type(int id) {
          this.id = id;
     }
}
