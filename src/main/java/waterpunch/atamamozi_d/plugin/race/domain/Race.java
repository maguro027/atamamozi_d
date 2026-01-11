package waterpunch.atamamozi_d.plugin.race.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import waterpunch.atamamozi_d.plugin.race.RacePackage;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPoint;
import waterpunch.atamamozi_d.plugin.race.enums.RaceType;
import waterpunch.atamamozi_d.plugin.tool.Location.LocParts;

/**
 * レースを表すドメインオブジェクト。
 *
 * 各レースは一意のUUIDを持ち、複数プレイヤーに対応します。WALKやBOATなどの
 * レースタイプ、周回数、チェックポイント位置などを設定できます。
 *
 * @author waterpunch
 */
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
      * レース名を設定します。
      * 
      * @param raceName 設定するレース名
      * @return メソッドチェーン用に自身を返却
      */
     public Race setRaceName(String raceName) {
          this.raceName = raceName;
          return this;
     }

     /**
      * クリエイター名を設定します。
      * 
      * @param creator 設定するクリエイター名
      * @return メソッドチェーン用に自身を返却
      */
     public Race setCreator(String creator) {
          this.creator = creator;
          return this;
     }

     /**
      * レースタイプを設定します。
      * 
      * @param raceType 設定するレースタイプ（WALK, BOATなど）
      * @return メソッドチェーン用に自身を返却
      */
     public Race setRaceType(RaceType raceType) {
          this.raceType = raceType;
          return this;
     }

     public Race setRaceId(UUID raceId) {
          this.raceId = raceId;
          return this;
     }

     /**
      * GUIアイコンを設定します。
      * 
      * @param icon 設定するマテリアル
      * @return メソッドチェーン用に自身を返却
      */
     public Race setIcon(Material icon) {
          this.icon = icon;
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

     public Race setStartPoint(List<LocParts> startPoint) {
          this.startPoint = startPoint;
          return this;
     }

     public Race setCheckPoint(List<CheckPoint> checkPoint) {
          this.checkPoint = checkPoint;
          return this;
     }

     public Race setDamageOption(DamageOption damageOption) {
          this.damageOption = damageOption;
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

     public DamageOption getDamageOption() {
          return damageOption;
     }

}