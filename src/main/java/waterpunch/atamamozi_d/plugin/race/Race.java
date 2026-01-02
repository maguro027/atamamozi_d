package waterpunch.atamamozi_d.plugin.race;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import waterpunch.atamamozi_d.plugin.main.Core;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.tool.Location.Loc_parts;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer;

/**
 * チェックポイント、スタート地点、設定を持つレースコースを表します。
 *
 * <p>
 * 各レースは一意のUUIDを持ち、複数プレイヤーに対応します。WALKやBOATなどの
 * レースタイプ、周回数、チェックポイント位置などを設定できます。
 * 
 * Raceオブジェクトは{@link Builder}を通じて作成されます。
 * Race_Packageは保存用の不変データを保持し、Raceは実行時の状態を管理します。
 * </p>
 *
 * @author waterpunch
 */
public class Race extends Race_Package {

     private final int TIME;
     private final Race_Mode race_Mode = Race_Mode.WAIT;
     private int currentParticipantCount = 0;
     private Race_Timer timer;

     /**
      * Raceオブジェクトのプライベートコンストラクタ。
      * Builderを通じて作成される想定です。
      */
     private Race(UUID raceID, String creator, String raceName, int joinAmount, int rap, Race_Type raceType,
               Material icon, int time) {
          super(raceID, creator, raceName, joinAmount, rap, raceType, icon);
          this.TIME = time;
     }

     /**
      * このレースにスタート地点を追加します。
      * プレイヤーは参加順にこれらの位置にスポーンします。
      *
      * @param loc スタート地点として追加する位置
      */
     public void addStartPointLoc(Location loc) {
          getStartPoint().add(new Loc_parts(loc));
     }

     /**
      * 指定したトリガー半径でチェックポイントを追加します。
      *
      * @param loc チェックポイントの位置
      * @param r   チェックポイントが作動する半径
      */
     public void addCheckPointLoc(Location loc, int r) {
          getCheckPoint_Loc().add(new CheckPointLoc(loc, r));
     }

     public void setMode(Race_Mode mode) {
          // race_Modeは最終的だが、実際のゲーム処理ではモード変更が必要
          // 本来は不変にすべきだが、ゲーム流れに合わせて可変にしている
          // TODO: イベント駆動設計へ移行を検討
     }

     public Race_Mode getMode() {
          return this.race_Mode;
     }

     public int getCountDown() {
          return TIME;
     }

     public void Complete() {
          setMode(Race_Mode.WAIT);
     }

     /**
      * 現在の参加者数を取得します（実行時の状態）。
      * 
      * @return 現在参加しているプレイヤー数
      */
     public int getCurrentParticipantCount() {
          return currentParticipantCount;
     }

     /**
      * 参加者数を1増やします。
      */
     public void addParticipant() {
          this.currentParticipantCount++;
     }

     /**
      * 参加者数を指定数増やします。
      * 
      * @param count 増やす数
      */
     public void addParticipant(int count) {
          this.currentParticipantCount += count;
     }

     /**
      * 参加者数をリセットします。
      */
     public void resetParticipantCount() {
          this.currentParticipantCount = 0;
     }

     /**
      * このレースのタイマーを設定します。
      * 
      * @param timer レースタイマー
      */
     public void setTimer(Race_Timer timer) {
          this.timer = timer;
     }

     /**
      * このレースのタイマーを取得します。
      * 
      * @return レースタイマー、設定されていない場合はnull
      */
     public Race_Timer getTimer() {
          return this.timer;
     }

     /**
      * このレースのタイマーを停止します。
      */
     public void stopTimer() {
          if (this.timer != null) {
               this.timer.stop();
               this.timer = null;
          }
     }

     /**
      * このレースのタイマーをリセットします。
      * 既存のタイマーがあれば停止し、nullに設定します。
      */
     public void resetTimer() {
          stopTimer();
     }

     /**
      * タイマーが実行中かどうかを確認します。
      * 
      * @return タイマーが実行中であればtrue
      */
     public boolean isTimerRunning() {
          return this.timer != null;
     }

     /**
      * カウントダウン時間（ティック）を取得します。
      * 
      * @return カウントダウン時間
      */
     public int getWaitTime() {
          return TIME;
     }

     /**
      * Raceオブジェクトを段階的に構築するBuilderクラス。
      * 
      * <p>
      * 使用例：
      * 
      * <pre>
      * Race race = new Race.Builder(player)
      *           .name("MyCourse")
      *           .type(Race_Type.BOAT)
      *           .icon(Material.BOAT)
      *           .joinAmount(4)
      *           .build();
      * </pre>
      * </p>
      */
     public static class Builder {
          private final UUID raceID;
          private final String creator;
          private String name = "DEFAULT";
          private Race_Type type = Race_Type.WALK;
          private Material icon = Material.MAP;
          private int rap = 1;
          private int joinAmount = 1;
          private int time = Core.WAIT_TIME;

          /**
           * ビルダーを初期化します。
           *
           * @param player レース作成者のプレイヤー
           */
          public Builder(Player player) {
               this.raceID = UUID.randomUUID();
               this.creator = player.getName();
          }

          /**
           * レース名を設定します。
           *
           * @param name レース名
           * @return このビルダー
           */
          public Builder name(String name) {
               if (name != null && !name.isEmpty()) {
                    this.name = name;
               }
               return this;
          }

          /**
           * レースタイプを設定します。
           *
           * @param type レースタイプ（WALK/BOAT）
           * @return このビルダー
           */
          public Builder type(Race_Type type) {
               if (type != null) {
                    this.type = type;
               }
               return this;
          }

          /**
           * レースアイコンを設定します。
           *
           * @param icon アイコンマテリアル
           * @return このビルダー
           */
          public Builder icon(Material icon) {
               if (icon != null) {
                    this.icon = icon;
               }
               return this;
          }

          /**
           * 周回数を設定します。
           *
           * @param rap 周回数
           * @return このビルダー
           */
          public Builder rap(int rap) {
               if (rap > 0) {
                    this.rap = rap;
               }
               return this;
          }

          /**
           * 参加可能人数を設定します。
           *
           * @param joinAmount 参加可能人数
           * @return このビルダー
           */
          public Builder joinAmount(int joinAmount) {
               if (joinAmount > 0) {
                    this.joinAmount = joinAmount;
               }
               return this;
          }

          /**
           * カウントダウン時間を設定します。
           *
           * @param time カウントダウン時間（ティック）
           * @return このビルダー
           */
          public Builder countDown(int time) {
               if (time > 0) {
                    this.time = time;
               }
               return this;
          }

          /**
           * 最終的なRaceオブジェクトを作成します。
           *
           * @return 構築されたRaceオブジェクト
           */
          public Race build() {
               return new Race(raceID, creator, name, joinAmount, rap, type, icon, time);
          }
     }
}
