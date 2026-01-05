package waterpunch.atamamozi_d.plugin.race.domain;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * レースに参加しているプレイヤーの状態を管理します。
 *
 * <p>
 * 責務:
 * <ul>
 * <li>プレイヤーの進行状況管理（ラップ、チェックポイント）</li>
 * <li>チェックポイント通過記録</li>
 * <li>タイム計測（開始時刻、終了時刻）</li>
 * </ul>
 * </p>
 *
 * <p>
 * このクラスは不変性を重視し、状態変更はメソッドを通じて行われます。
 * </p>
 */
public class RacePlayer {

     /**
      * プレイヤーの参加状態を表す列挙型
      */
     public enum PlayerState {
          /** 参加済み（待機中） */
          JOINED,
          /** レディ状態 */
          READY,
          /** レース実行中 */
          RUNNING,
          /** ゴール済み */
          FINISHED,
          /** 観戦モード */
          SPECTATING
     }

     // ========== 不変フィールド（最終） ==========
     /** Bukkitプレイヤーオブジェクト */
     private final Player bukkitPlayer;
     /** レースのUUID */
     private final UUID raceId;
     /** 参加順（スタート位置決定用） */
     private final int joinOrder;
     /** 参加前のプレイヤー位置 */
     private final Location originalLocation;

     // ========== 可変フィールド（状態） ==========
     /** 現在のラップ数 */
     private int currentLap;
     /** 現在のチェックポイントインデックス */
     private int currentCheckpoint;
     /** レース開始時刻（ミリ秒） */
     private long startTime;
     /** レース終了時刻（ミリ秒） */
     private long finishTime;
     /** プレイヤーの現在状態 */
     private PlayerState state;

     // ========== キャッシング（パフォーマンス最適化） ==========
     /** スコアボード更新の最終時刻 */
     private long lastScoreboardUpdate = 0L;
     /** 最後の位置情報更新時刻 */
     private long lastLocationUpdate = 0L;
     /** 前回レンダリングされたタイム文字列 */
     private String cachedTimeString = "";

     /**
      * RacePlayerを生成します。
      *
      * @param bukkitPlayer     Bukkitプレイヤーオブジェクト
      * @param raceId           レースのUUID
      * @param joinOrder        参加順序（1から始まる）
      * @param originalLocation 参加前の位置
      */
     public RacePlayer(Player bukkitPlayer, UUID raceId, int joinOrder, Location originalLocation) {
          this.bukkitPlayer = bukkitPlayer;
          this.raceId = raceId;
          this.joinOrder = joinOrder;
          this.originalLocation = originalLocation.clone();

          // 初期状態の設定
          this.currentLap = 0;
          this.currentCheckpoint = 0;
          this.startTime = 0L;
          this.finishTime = 0L;
          this.state = PlayerState.JOINED;
     }

     // ========== ゲッター（不変フィールド） ==========

     /**
      * Bukkitプレイヤーオブジェクトを取得します。
      */
     public Player getPlayer() {
          return bukkitPlayer;
     }

     /**
      * プレイヤーのUUIDを取得します。
      */
     public UUID getPlayerUUID() {
          return bukkitPlayer.getUniqueId();
     }

     /**
      * レースのUUIDを取得します。
      */
     public UUID getRaceId() {
          return raceId;
     }

     /**
      * 参加順（スタート位置インデックス用）を取得します。
      */
     public int getJoinOrder() {
          return joinOrder;
     }

     /**
      * 参加前のプレイヤー位置を取得します。
      */
     public Location getOriginalLocation() {
          return originalLocation.clone();
     }

     // ========== ゲッター（可変フィールド） ==========

     /**
      * 現在のラップ数を取得します。
      */
     public int getCurrentLap() {
          return currentLap;
     }

     /**
      * 現在のチェックポイントインデックスを取得します。
      */
     public int getCurrentCheckpoint() {
          return currentCheckpoint;
     }

     /**
      * レース開始時刻（ミリ秒）を取得します。
      * レースがまだ開始されていない場合は0を返します。
      */
     public long getStartTime() {
          return startTime;
     }

     /**
      * レース終了時刻（ミリ秒）を取得します。
      * レースがまだ終了していない場合は0を返します。
      */
     public long getFinishTime() {
          return finishTime;
     }

     /**
      * プレイヤーの現在状態を取得します。
      */
     public PlayerState getState() {
          return state;
     }

     // ========== 進行状況管理 ==========

     /**
      * チェックポイントを通過します。
      *
      * @param checkpointIndex チェックポイントのインデックス
      */
     public void passCheckpoint(int checkpointIndex) {
          this.currentCheckpoint = checkpointIndex;
     }

     /**
      * 1ラップを完了します。
      * チェックポイントカウンターをリセットし、ラップ数をインクリメントします。
      */
     public void completeLap() {
          this.currentLap++;
          this.currentCheckpoint = 0;
     }

     /**
      * ゴール状態にします。
      * 終了時刻を現在時刻に設定し、状態をFINISHEDに変更します。
      */
     public void finish() {
          this.finishTime = System.currentTimeMillis();
          this.state = PlayerState.FINISHED;
     }

     /**
      * レースがゴール済みかを判定します。
      */
     public boolean isFinished() {
          return state == PlayerState.FINISHED;
     }

     // ========== タイマー制御 ==========

     /**
      * タイマーを開始します。
      * 開始時刻を現在時刻に設定し、状態をRUNNINGに変更します。
      */
     public void startTimer() {
          this.startTime = System.currentTimeMillis();
          this.state = PlayerState.RUNNING;
     }

     /**
      * レース開始時刻から現在までの経過時間（ミリ秒）を取得します。
      *
      * @return 経過時間（ミリ秒）。レースが開始されていない場合は0
      */
     public long getElapsedTime() {
          if (startTime == 0L)
               return 0L;

          long endTime = (finishTime != 0L) ? finishTime : System.currentTimeMillis();
          return endTime - startTime;
     }

     /**
      * 経過時間をフォーマット済み文字列で取得します。
      * フォーマット: "mm:ss.SSS"
      *
      * <p>
      * キャッシングにより、最後のキャッシュから100ms以内の呼び出しであれば
      * キャッシュされた値を返します。
      * </p>
      *
      * @return フォーマット済みのタイム文字列
      */
     public String getFormattedTime() {
          long now = System.currentTimeMillis();

          // キャッシュ有効期限チェック（100ms）
          if (now - lastLocationUpdate < 100L && !cachedTimeString.isEmpty()) {
               return cachedTimeString;
          }

          long elapsed = getElapsedTime();
          cachedTimeString = formatDuration(elapsed);
          lastLocationUpdate = now;

          return cachedTimeString;
     }

     /**
      * ミリ秒を "mm:ss.SSS" フォーマットに変換します。
      *
      * @param millis ミリ秒
      * @return フォーマット済み文字列
      */
     private static String formatDuration(long millis) {
          long seconds = millis / 1000;
          long millisRemainder = millis % 1000;

          long minutes = seconds / 60;
          long secondsRemainder = seconds % 60;

          return String.format("%02d:%02d.%03d", minutes, secondsRemainder, millisRemainder);
     }

     // ========== 状態遷移 ==========

     /**
      * プレイヤーの状態をREADYに変更します。
      */
     public void setReady() {
          this.state = PlayerState.READY;
     }

     /**
      * プレイヤーの状態をSPECTATINGに変更します。
      * ゴール済みプレイヤーが観戦モードに移行するときに使用します。
      */
     public void setSpectating() {
          this.state = PlayerState.SPECTATING;
     }

     // ========== ユーティリティ ==========

     /**
      * スコアボード更新の抑制制御用の最終更新時刻を取得します。
      */
     public long getLastScoreboardUpdate() {
          return lastScoreboardUpdate;
     }

     /**
      * スコアボード更新の最終更新時刻を更新します。
      */
     public void updateLastScoreboardTime() {
          this.lastScoreboardUpdate = System.currentTimeMillis();
     }

     @Override
     public String toString() {
          return "RacePlayer{" +
                    "playerName=" + bukkitPlayer.getName() +
                    ", raceId=" + raceId +
                    ", joinOrder=" + joinOrder +
                    ", currentLap=" + currentLap +
                    ", currentCheckpoint=" + currentCheckpoint +
                    ", state=" + state +
                    ", elapsedTime=" + getFormattedTime() +
                    '}';
     }
}
