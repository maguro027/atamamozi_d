package waterpunch.atamamozi_d.plugin.tool.Timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import waterpunch.atamamozi_d.plugin.main.Core;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Runner_Mode;

/**
 * 離脱タイマークラス
 * レース終了後にプレイヤーのスコアボードをクリアするためのタイマー
 * 
 * Leave timer class
 * Timer for clearing player's scoreboard after race ends
 */
public class Leave_Timer extends BukkitRunnable {

     /** 対象プレイヤー / Target player */
     private Player player;
     
     /** 残り時間（秒） / Remaining time (seconds) */
     private int time;

     /**
      * 離脱タイマーを作成する
      * 
      * Creates a leave timer
      * 
      * @param player 対象プレイヤー / Target player
      */
     public Leave_Timer(Player player) {
          this.time = Core.LEAVE_TIME;
          this.player = player;
     }

     /**
      * タイマー処理
      * 時間切れ時にスコアボードをクリアする
      * 
      * Timer processing
      * Clears scoreboard when time expires
      */
     @Override
     public void run() {
          if (this.time == 0) {
               // Guard against missing runner
               if (Race_Core.getRunner(player) == null
                         || Race_Core.getRunner(player).getMode() == Race_Runner_Mode.NO_ENTRY)
                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               cancel();
               return;
          }
          this.time--;
     }
}
