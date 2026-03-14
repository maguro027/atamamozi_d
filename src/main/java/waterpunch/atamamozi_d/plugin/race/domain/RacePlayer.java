package waterpunch.atamamozi_d.plugin.race.domain;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.Getter;
import waterpunch.atamamozi_d.plugin.tool.RaceSystem;
import waterpunch.atamamozi_d.plugin.tool.RaceSystem.MessageType;

/**
 * レース参加プレイヤーの情報とタイム計測を担当。
 * 進行状況（チェックポイント、周回数）はRaceSessionが管理。
 */
@Getter
public class RacePlayer {

     private final Player player;
     private final Location originalLocation;

     private long startTime; // System.currentTimeMillis()
     private long finishTime; // System.currentTimeMillis()

     public RacePlayer(Player player) {
          this.player = player;

          Location currentLocation = player.getLocation();
          if (currentLocation == null) {
               RaceSystem.sendMessage(MessageType.ERROR, player, "参加地点が参照できません");

               Location respawnLocation = player.getWorld().getSpawnLocation();
               player.teleport(respawnLocation);
               currentLocation = respawnLocation;
          }

          this.originalLocation = currentLocation.clone();
          this.startTime = 0;
          this.finishTime = 0;
     }

     public void countDown(int i) {
          Location loc = player.getLocation();
          if (loc == null)
               return;
          if (i <= 3 && i > 0) {
               player.sendTitle(ChatColor.RED + String.valueOf(i), "", 0, 20, 0);
               player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 0.9f);
          } else if (i == 0) {
               player.sendTitle(ChatColor.GREEN + "GO!", "", 0, 20, 20);
               player.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
          }
     }

     /**
      * タイマー開始
      */
     public void startTimer() {
          this.startTime = System.currentTimeMillis();
     }

     /**
      * タイマー停止（ゴール時）
      */
     public void finish() {
          this.finishTime = System.currentTimeMillis();
     }

     /**
      * ゴール済みか判定
      */
     public boolean isFinished() {
          return finishTime > 0;
     }

     /**
      * 経過時間をミリ秒で取得
      */
     public long getElapsedTimeMillis() {
          if (startTime == 0)
               return 0;
          long endTime = isFinished() ? finishTime : System.currentTimeMillis();
          return endTime - startTime;
     }

     /**
      * 経過時間を秒で取得
      */
     public long getElapsedTimeSeconds() {
          return TimeUnit.MILLISECONDS.toSeconds(getElapsedTimeMillis());
     }

     /**
      * フォーマットされたタイム文字列（MM:SS.mmm）
      */
     public String getFormattedTime() {
          long millis = getElapsedTimeMillis();
          long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
          long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
          long ms = millis % 1000;
          return String.format("%02d:%02d.%03d", minutes, seconds, ms);
     }

}
