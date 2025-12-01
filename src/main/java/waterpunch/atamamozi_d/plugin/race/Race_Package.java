package waterpunch.atamamozi_d.plugin.race;

import java.util.Calendar;
import java.util.UUID;

/**
 * レースパッケージクラス
 * レースの統計情報を管理する
 * 
 * Race package class
 * Manages race statistics
 */
public class Race_Package {

     /** レースのUUID / Race UUID */
     private UUID Race_ID;
     
     /** 総参加回数 / Total participation count */
     private int Count;
     
     /** カレンダー（予約用） / Calendar (reserved) */
     private Calendar Cal;

     /**
      * レースパッケージを作成する
      * 
      * Creates a race package
      * 
      * @param Race_ID レースUUID / Race UUID
      */
     public Race_Package(UUID Race_ID) {
          this.Race_ID = Race_ID;
     }

     /**
      * レースUUIDを取得する
      * 
      * Gets race UUID
      * 
      * @return レースUUID / Race UUID
      */
     public UUID getRace_ID() {
          return Race_ID;
     }

     /**
      * 参加回数を取得する
      * 
      * Gets participation count
      * 
      * @return 参加回数 / Count
      */
     public int getJoinCount() {
          return Count;
     }

     /**
      * 参加回数を1増加する
      * 
      * Increments participation count by 1
      */
     public void addJoinCount() {
          this.Count++;
     }

     /**
      * 参加回数を指定値増加する
      * 
      * Adds specified value to participation count
      * 
      * @param add 増加値 / Value to add
      */
     public void addJoinCount(int add) {
          this.Count = Count + add;
     }
}
