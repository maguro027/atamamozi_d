package waterpunch.atamamozi_d.plugin.race.domain;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
public class Race extends RacePackage {

     /**
      * デフォルト設定で新しいレースを作成します。
      *
      * @param creator レースを作成するプレイヤー
      */
     public Race(Player creator) {
          // this.creator = creator.getName();
          // this.raceId = UUID.randomUUID();
          // this.raceName = "DEFAULT";
          // this.raceType = RaceType.WALK;
          // this.icon = Material.MAP;
          // this.rap = 1;
          // this.joinAmount = 1;
          // this.time = Core.WAIT_TIME;
     }

     /**
      * このレースにスタート地点を追加します。
      * プレイヤーは参加順にこれらの位置にスポーンします。
      *
      * @param loc スタート地点として追加する位置
      */
     public void addStartPointLoc(Location loc) {
          if (startPoint == null)
               startPoint = new ArrayList<>();

          startPoint.add(new LocParts(loc));
     }

     /**
      * 指定したトリガー半径でチェックポイントを追加します。
      *
      * @param loc チェックポイントの位置
      * @param r   チェックポイントが作動する半径
      */
     public void addCheckPoint(Location loc, int r) {
          if (checkPoint == null)
               checkPoint = new ArrayList<>();

          checkPoint.add(new CheckPoint(loc, r));
     }

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