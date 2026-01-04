package waterpunch.atamamozi_d.plugin.race.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.tool.Location.LocParts;

/**
 * チェックポイント、スタート地点、設定を持つレースコースを表します。
 *
 * <p>
 * 各レースは一意のUUIDを持ち、複数プレイヤーに対応します。WALKやBOATなどの
 * レースタイプ、周回数、チェックポイント位置などを設定できます。
 * </p>
 *
 * @author waterpunch
 */
public class Race {

     private final String creator;
     private String raceName;
     private UUID raceId;
     private Race_Type raceType;
     private Material icon;
     private int joinAmount, rap, time, errorCount;
     private Race_Mode raceMode;
     private final ArrayList<LocParts> startPoint = new ArrayList<>();
     private final ArrayList<CheckPointLoc> checkPointLoc = new ArrayList<>();

     /**
      * デフォルト設定で新しいレースを作成します。
      *
      * @param creator レースを作成するプレイヤー
      */
     public Race(Player creator) {
          this.creator = creator.getName();
          this.raceId = UUID.randomUUID();
          this.raceName = "DEFAULT";
          this.raceType = Race_Type.WALK;
          this.icon = Material.MAP;
          this.rap = 1;
          this.raceMode = Race_Mode.WAIT;
          this.joinAmount = 1;
          // this.time = Core.WAIT_TIME;
     }

     /**
      * このレースにスタート地点を追加します。
      * プレイヤーは参加順にこれらの位置にスポーンします。
      *
      * @param loc スタート地点として追加する位置
      */
     public void addStartPointLoc(Location loc) {
          startPoint.add(new LocParts(loc));
     }

     /**
      * このレースの全スタート地点を取得します。
      *
      * @return スタート地点のリスト
      */
     public ArrayList<LocParts> getStartPointLoc() {
          return new ArrayList<>(this.startPoint);
     }

     /**
      * 指定したトリガー半径でチェックポイントを追加します。
      *
      * @param loc チェックポイントの位置
      * @param r   チェックポイントが作動する半径
      */
     public void addCheckPointLoc(Location loc, int r) {
          checkPointLoc.add(new CheckPointLoc(loc, r));
     }

     /**
      * このレースのすべてのチェックポイントを取得します。
      *
      * @return 半径を含むチェックポイントのリスト
      */
     public ArrayList<CheckPointLoc> getCheckPointLoc() {
          return new ArrayList<>(this.checkPointLoc);
     }

     /**
      * 指定インデックスのチェックポイントを安全に取得します。
      *
      * @param idx 取得するチェックポイントのインデックス
      * @return 見つからない場合は null を返します
      */
     public CheckPointLoc getCheckPoint(int idx) {
          if (idx < 0 || idx >= this.checkPointLoc.size())
               return null;
          return this.checkPointLoc.get(idx);
     }

     /**
      * 不変スナップショットを返します（外部から変更不可）。
      */
     public ImmutableRace toImmutable() {
          return new ImmutableRace(
                    this.raceId,
                    this.creator,
                    this.raceName,
                    this.raceType,
                    this.icon,
                    this.joinAmount,
                    this.rap,
                    this.time,
                    this.errorCount,
                    this.raceMode,
                    Collections.unmodifiableList(new ArrayList<>(this.startPoint)),
                    Collections.unmodifiableList(new ArrayList<>(this.checkPointLoc)));
     }

     public String getCreator() {
          return this.creator;
     }

     public void setRaceName(String raceName) {
          this.raceName = raceName;
     }

     public Race_Type getRaceType() {
          return this.raceType;
     }

     public void setRaceType(Race_Type raceType) {
          this.raceType = raceType;
     }

     public String getRaceName() {
          return this.raceName;
     }

     public void setIcon(Material icon) {
          this.icon = icon;
     }

     public Material getIcon() {
          return this.icon;
     }

     public void setJoinAmount(int joinAmount) {
          this.joinAmount = joinAmount;
     }

     public int getJoinAmount() {
          return this.joinAmount;
     }

     public void setRap(int Rap) {
          this.rap = Rap;
     }

     public int getRap() {
          return this.rap;
     }

     public void setErrorCount(int i) {
          this.errorCount = i;
     }

     public void addErrorCount() {
          this.errorCount++;
     }

     public int getErrorCount() {
          return this.errorCount;
     }

     public void setMode(Race_Mode mode) {
          this.raceMode = mode;
     }

     public Race_Mode getMode() {
          return this.raceMode;
     }

     public void setUUID() {
          this.raceId = UUID.randomUUID();
     }

     public UUID getUUID() {
          return this.raceId;
     }

     public int getCountDown() {
          return time;
     }

     public void setCountDown(int i) {
          this.time = i;
     }

     public void Complete() {
          setMode(Race_Mode.WAIT);
     }
}