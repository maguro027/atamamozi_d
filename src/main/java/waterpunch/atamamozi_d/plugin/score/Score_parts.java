package waterpunch.atamamozi_d.plugin.score;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Package;

/**
 * スコアパーツクラス
 * 特定レースのプレイヤースコアを管理する
 * 
 * Score parts class
 * Manages player scores for a specific race
 */
public class Score_parts {

     /** レースのUUID / Race UUID */
     private UUID RACE_ID;
     
     /** 参加回数 / Participation count */
     private int COUNT;
     
     /** ベストラップタイムリスト / Best lap time list */
     private List<Long> BEST_RAP = new ArrayList<Long>();
     
     /** タイムリスト（上位10件まで保存） / Time list (saves up to top 10) */
     private List<Long> TIMEs = new ArrayList<Long>();

     /**
      * 新しいスコアパーツを作成する
      * 
      * Creates new score parts
      * 
      * @param race_id レースUUID / Race UUID
      * @param time 初回タイム / Initial time
      */
     public Score_parts(UUID race_id, Long time) {
          this.RACE_ID = race_id;
          addTime(time);
     }

     /**
      * レースUUIDを取得する
      * 
      * Gets race UUID
      * 
      * @return レースUUID / Race UUID
      */
     public UUID getRace_ID() {
          return RACE_ID;
     }

     /**
      * タイムを追加する
      * ベストタイム更新時はtrueを返す
      * 
      * Adds a time
      * Returns true if best time was updated
      * 
      * @param time タイム / Time
      * @return ベスト更新ならtrue / True if best was updated
      */
     public Boolean addTime(Long time) {
          TIMEs.add(time);
          COUNT++;
          // レースパッケージの参加回数を更新
          // Update race package participation count
          for (Race_Package Package : Race_Core.Race_packages)
               if (Package.getRace_ID().equals(RACE_ID))
                    Package.addJoinCount();
          List<Long> onetime = TIMEs.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
          TIMEs = onetime;
          // 上位10件のみ保持
          // Keep only top 10
          if (TIMEs.size() == 11)
               TIMEs.remove(10);
          if (TIMEs.get(0).equals(time))
               return true;
          return false;
     }

     /**
      * 指定順位のタイムを取得する
      * 
      * Gets time at specified rank
      * 
      * @param i 順位（0から） / Rank (0-based)
      * @return タイム / Time
      */
     public Long getTime(int i) {
          return TIMEs.get(i);
     }

     /**
      * 参加回数を取得する
      * 
      * Gets participation count
      * 
      * @return 参加回数 / Count
      */
     public int getCount() {
          return COUNT;
     }

     /**
      * 指定ラップのベストタイムを取得する
      * 
      * Gets best time for specified lap
      * 
      * @param i ラップ番号 / Lap number
      * @return ラップタイム、またはデータなしの場合は-1 / Lap time, or -1 if no data
      */
     public Long getRAP_TIME(int i) {
          if (BEST_RAP.isEmpty())
               return (long) -1;
          return BEST_RAP.get(i);
     }

     /**
      * 全スコアを取得する
      * 
      * Gets all scores
      * 
      * @return スコアリスト / Score list
      */
     public List<Long> getAllScore() {
          return TIMEs;
     }
}
