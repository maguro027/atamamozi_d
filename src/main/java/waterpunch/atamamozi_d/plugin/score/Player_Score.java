package waterpunch.atamamozi_d.plugin.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.CreateJson;

/**
 * プレイヤースコアクラス
 * 個々のプレイヤーのレーススコアを管理する
 * 
 * Player score class
 * Manages individual player's race scores
 */
public class Player_Score {

     /** スコアパーツリスト / Score parts list */
     private List<Score_parts> Scores = new ArrayList<Score_parts>();
     
     /** プレイヤーのUUID / Player UUID */
     private UUID uuid;
     
     /** プレイヤー名 / Player name */
     private String Name;

     /**
      * プレイヤースコアを作成する
      * 
      * Creates a player score
      * 
      * @param player プレイヤー / Player
      */
     public Player_Score(Player player) {
          this.uuid = player.getUniqueId();
          this.Name = player.getName();
     }

     /**
      * プレイヤーのUUIDを取得する
      * 
      * Gets player's UUID
      * 
      * @return UUID
      */
     public UUID getUUID() {
          return uuid;
     }

     /**
      * プレイヤー名を取得する
      * 
      * Gets player name
      * 
      * @return プレイヤー名 / Player name
      */
     public String getName() {
          return Name;
     }

     /**
      * 指定レースのベストスコアを取得する
      * 
      * Gets best score for specified race
      * 
      * @param RACE_ID レースUUID / Race UUID
      * @return ベストスコア、またはnull / Best score, or null
      */
     public Long getTOPScore(UUID RACE_ID) {
          if (Scores.isEmpty()) return null;
          for (Score_parts parts : Scores) if (parts.getRace_ID().equals(RACE_ID)) return parts.getTime(0);
          return null;
     }

     /**
      * 全レースのベストスコアを取得する
      * 
      * Gets best scores for all races
      * 
      * @return レースUUIDとベストスコアのマップ / Map of race UUID to best score
      */
     public HashMap<UUID, Long> getTOPScores() {
          if (Scores.isEmpty()) return null;
          HashMap<UUID, Long> rt = new HashMap<UUID, Long>();
          for (Score_parts parts : Scores) rt.put(parts.getRace_ID(), parts.getTime(0));
          return rt;
     }

     /**
      * 指定レースの参加回数を取得する
      * 
      * Gets participation count for specified race
      * 
      * @param RACE_ID レースUUID / Race UUID
      * @return 参加回数 / Count
      */
     public int getCount(UUID RACE_ID) {
          if (Scores.isEmpty()) return 0;
          for (Score_parts parts : Scores) if (parts.getRace_ID().equals(RACE_ID)) return parts.getCount();
          return 0;
     }

     /**
      * スコアを設定/追加する
      * 新記録の場合は通知を表示
      * 
      * Sets or adds a score
      * Shows notification if new record
      * 
      * @param RACE_ID レースUUID / Race UUID
      * @param i スコア / Score
      */
     public void setScore(UUID RACE_ID, Long i) {
          if (Scores.isEmpty()) {
               Scores.add(new Score_parts(RACE_ID, i));
               setTOP(RACE_ID, i);
               CreateJson.Scoresave(this);
               return;
          }
          for (Score_parts parts : Scores) if (parts.getRace_ID().equals(RACE_ID)) if (parts.addTime(i)) {
               setTOP(RACE_ID, i);
               CreateJson.Scoresave(this);
               return;
          } else return;
          Scores.add(new Score_parts(RACE_ID, i));
          CreateJson.Scoresave(this);
          return;
     }

     /**
      * スコアパーツリストを取得する
      * 
      * Gets score parts list
      * 
      * @return スコアパーツリスト / Score parts list
      */
     public List<Score_parts> getScore_parts() {
          return Scores;
     }

     /**
      * 新記録通知を表示する
      * 
      * Displays new record notification
      * 
      * @param RACE_ID レースUUID / Race UUID
      * @param i スコア / Score
      */
     public void setTOP(UUID RACE_ID, Long i) {
          for (Player player : Bukkit.getOnlinePlayers()) if (player.getUniqueId().equals(getUUID())) {
               player.sendMessage(CollarMessage.setInfo() + "NEW RECORD!!");
               player.getPlayer().sendTitle(ChatColor.GREEN + " - " + ChatColor.AQUA + "NEW RECORD!!" + ChatColor.GREEN + " - ", "", 10, 40, 10);
               player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
               Player_Score_Core.addRanking(RACE_ID, player.getName(), i);
               Player_Score_Core.SortRanking(RACE_ID);
               break;
          }
     }
}
