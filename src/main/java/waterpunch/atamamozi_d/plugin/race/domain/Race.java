package waterpunch.atamamozi_d.plugin.race.domain;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import waterpunch.atamamozi_d.plugin.race.RacePackage;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPoint;
import waterpunch.atamamozi_d.plugin.tool.Location.LocParts;

/**
 * レースを表すドメインオブジェクト。
 *
 * 各レースは一意のUUIDを持ち、複数プレイヤーに対応します。WALKやBOATなどの
 * レースタイプ、周回数、チェックポイント位置などを設定できます。
 *
 * @author waterpunch
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class Race extends RacePackage {
     /** JSON 読み込み用やファクトリ用のデフォルトコンストラクタ。 */
     protected Race() {
     }

     /**
      * デフォルト設定で新しいレースを作成します。
      *
      * @param creator レースを作成するプレイヤー
      */
     public Race(Player creator) {
          this.raceId = java.util.UUID.randomUUID();
          this.creator = creator.getName();
          this.raceName = "DEFAULT";
     }

     public void start() {
          // TODO:
          // レース開始ロジックをここに実装
     }

     /**
      * このレースにスタート地点を追加します。
      * プレイヤーは参加順にこれらの位置にスポーンします。
      *
      * @param loc スタート地点として追加する位置
      * @return メソッドチェーン用に自身を返却
      */
     public Race addStartPoint(Location loc) {
          if (startPoint == null)
               startPoint = new ArrayList<>();
          startPoint.add(new LocParts(loc));
          return this;
     }

     /**
      * 指定したトリガー半径でチェックポイントを追加します。
      *
      * @param loc チェックポイントの位置
      * @param r   チェックポイントが作動する半径
      * @return メソッドチェーン用に自身を返却
      */
     public Race addCheckPoint(Location loc, int r) {
          if (checkPoint == null)
               checkPoint = new ArrayList<>();

          checkPoint.add(new CheckPoint(loc, r));
          return this;
     }

     /**
      * 最大参加人数を設定します。
      * 
      * @param joinAmount 最大参加人数
      * @return メソッドチェーン用に自身を返却
      */
     public Race setMaxPlayers(int joinAmount) {
          this.joinAmount = joinAmount;
          return this;
     }

     /**
      * 周回数を設定します。
      * 
      * @param rap 周回数
      * @return メソッドチェーン用に自身を返却
      */
     public Race setLaps(int rap) {
          this.rap = rap;
          return this;
     }

     /**
      * 指定インデックスのスタート地点を安全に取得します。
      *
      * @param idx 取得するスタート地点のインデックス
      * @return 見つからない場合は null を返します
      */
     public LocParts getStartPoint(int idx) {
          if (startPoint == null || idx < 0 || idx >= startPoint.size())
               return null;
          return startPoint.get(idx);

     }

     /**
      * 指定インデックスのチェックポイントを安全に取得します。
      *
      * @param idx 取得するチェックポイントのインデックス
      * @return 見つからない場合は null を返します
      */
     public CheckPoint getCheckPoint(int idx) {
          if (checkPoint == null || idx < 0 || idx >= checkPoint.size())
               return null;
          return checkPoint.get(idx);
     }

}