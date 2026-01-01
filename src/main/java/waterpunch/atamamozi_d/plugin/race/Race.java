package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import waterpunch.atamamozi_d.plugin.main.Core;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.tool.Location.Loc_parts;

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
     private String race_name;
     private UUID race_ID;
     private Race_Type race_type;
     private Material icon;
     private int join_amount, rap, TIME, Error_Count;
     private Race_Mode race_Mode;
     private final ArrayList<Loc_parts> StartPoint = new ArrayList<>();
     private final ArrayList<CheckPointLoc> CheckPoint_Loc = new ArrayList<>();

     /**
      * デフォルト設定で新しいレースを作成します。
      *
      * @param creator レースを作成するプレイヤー
      */
     public Race(Player creator) {
          this.creator = creator.getName();
          this.race_ID = UUID.randomUUID();
          this.race_name = "DEFAULT";
          this.race_type = Race_Type.WALK;
          this.icon = Material.MAP;
          this.rap = 1;
          this.race_Mode = Race_Mode.WAIT;
          this.join_amount = 1;
          this.TIME = Core.WAIT_TIME;
     }

     /**
      * このレースにスタート地点を追加します。
      * プレイヤーは参加順にこれらの位置にスポーンします。
      *
      * @param loc スタート地点として追加する位置
      */
     public void addStartPointLoc(Location loc) {
          StartPoint.add(new Loc_parts(loc));
     }

     /**
      * このレースの全スタート地点を取得します。
      *
      * @return スタート地点のリスト
      */
     public ArrayList<Loc_parts> getStartPointLoc() {
          return this.StartPoint;
     }

     /**
      * 指定したトリガー半径でチェックポイントを追加します。
      *
      * @param loc チェックポイントの位置
      * @param r   チェックポイントが作動する半径
      */
     public void addCheckPointLoc(Location loc, int r) {
          CheckPoint_Loc.add(new CheckPointLoc(loc, r));
     }

     /**
      * このレースのすべてのチェックポイントを取得します。
      *
      * @return 半径を含むチェックポイントのリスト
      */
     public ArrayList<CheckPointLoc> getCheckPointLoc() {
          return this.CheckPoint_Loc;
     }

     public String getCreator() {
          return this.creator;
     }

     public void setRace_name(String race_name) {
          this.race_name = race_name;
     }

     public Race_Type getRace_Type() {
          return this.race_type;
     }

     public void setRace_Type(Race_Type race_type) {
          this.race_type = race_type;
     }

     public String getRace_name() {
          return this.race_name;
     }

     public void setIcon(Material icon) {
          this.icon = icon;
     }

     public Material getIcon() {
          return this.icon;
     }

     public void setJoin_Amount(int join_amount) {
          this.join_amount = join_amount;
     }

     public int getJoin_Amount() {
          return this.join_amount;
     }

     public void setRap(int Rap) {
          this.rap = Rap;
     }

     public int getRap() {
          return this.rap;
     }

     public void setErrorCount(int i) {
          this.Error_Count = i;
     }

     public void addErrorCount() {
          this.Error_Count++;
     }

     public int getErrorCount() {
          return this.Error_Count;
     }

     public void setMode(Race_Mode mode) {
          this.race_Mode = mode;
     }

     public Race_Mode getMode() {
          return this.race_Mode;
     }

     public void setUUID() {
          this.race_ID = UUID.randomUUID();
     }

     public UUID getUUID() {
          return this.race_ID;
     }

     public int getCountDown() {
          return TIME;
     }

     public void setCountDown(int i) {
          this.TIME = i;
     }

     public void Complete() {
          setMode(Race_Mode.WAIT);
     }
}
