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
 * レースデータを表すクラス
 * レースの名前、タイプ、チェックポイント、スタートポイントなどを管理
 * 
 * Class representing race data
 * Manages race name, type, checkpoints, start points, etc.
 */
public class Race {

     /** 作成者名 / Creator name */
     private String creator;
     
     /** レース名 / Race name */
     private String race_name;
     
     /** レースの一意識別子 / Race unique identifier */
     private UUID race_ID;
     
     /** レースタイプ（WALK/BOAT） / Race type (WALK/BOAT) */
     private Race_Type race_type;
     
     /** メニューアイコン / Menu icon */
     private Material icon;
     
     /** 参加可能人数、周回数、カウントダウン時間、エラーカウント */
     /** Max participants, lap count, countdown time, error count */
     private int join_amount, rap, TIME, Error_Count;
     
     /** レースモード / Race mode */
     private Race_Mode race_Mode;
     
     /** スタートポイントリスト / Start point list */
     private ArrayList<Loc_parts> StartPoint = new ArrayList<>();
     
     /** チェックポイントリスト / Checkpoint list */
     private ArrayList<CheckPointLoc> CheckPoint_Loc = new ArrayList<>();

     /**
      * 新しいレースを作成する
      * 
      * Creates a new race
      * 
      * @param creator 作成者プレイヤー / Creator player
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
      * スタートポイントを追加する
      * 
      * Adds a start point
      * 
      * @param loc 位置 / Location
      */
     public void addStartPointLoc(Location loc) {
          StartPoint.add(new Loc_parts(loc));
     }

     /**
      * スタートポイントリストを取得する
      * 
      * Gets start point list
      * 
      * @return スタートポイントリスト / Start point list
      */
     public ArrayList<Loc_parts> getStartPointLoc() {
          return this.StartPoint;
     }

     /**
      * チェックポイントを追加する
      * 
      * Adds a checkpoint
      * 
      * @param loc 位置 / Location
      * @param r 半径 / Radius
      */
     public void addCheckPointLoc(Location loc, int r) {
          CheckPoint_Loc.add(new CheckPointLoc(loc, r));
     }

     /**
      * チェックポイントリストを取得する
      * 
      * Gets checkpoint list
      * 
      * @return チェックポイントリスト / Checkpoint list
      */
     public ArrayList<CheckPointLoc> getCheckPointLoc() {
          return this.CheckPoint_Loc;
     }

     /** 作成者名を取得 / Gets creator name */
     public String getCreator() {
          return this.creator;
     }

     /** レース名を設定 / Sets race name */
     public void setRace_name(String race_name) {
          this.race_name = race_name;
     }

     /** レースタイプを取得 / Gets race type */
     public Race_Type getRace_Type() {
          return this.race_type;
     }

     /** レースタイプを設定 / Sets race type */
     public void setRace_Type(Race_Type race_type) {
          this.race_type = race_type;
     }

     /** レース名を取得 / Gets race name */
     public String getRace_name() {
          return this.race_name;
     }

     /** アイコンを設定 / Sets icon */
     public void setIcon(Material icon) {
          this.icon = icon;
     }

     /** アイコンを取得 / Gets icon */
     public Material getIcon() {
          return this.icon;
     }

     /** 参加可能人数を設定 / Sets max participants */
     public void setJoin_Amount(int join_amount) {
          this.join_amount = join_amount;
     }

     /** 参加可能人数を取得 / Gets max participants */
     public int getJoin_Amount() {
          return this.join_amount;
     }

     /** 周回数を設定 / Sets lap count */
     public void setRap(int Rap) {
          this.rap = Rap;
     }

     /** 周回数を取得 / Gets lap count */
     public int getRap() {
          return this.rap;
     }

     /** エラーカウントを設定 / Sets error count */
     public void setErrorCount(int i) {
          this.Error_Count = i;
     }

     /** エラーカウントを増加 / Increments error count */
     public void addErrorCount() {
          this.Error_Count++;
     }

     /** エラーカウントを取得 / Gets error count */
     public int getErrorCount() {
          return this.Error_Count;
     }

     /** モードを設定 / Sets mode */
     public void setMode(Race_Mode mode) {
          this.race_Mode = mode;
     }

     /** モードを取得 / Gets mode */
     public Race_Mode getMode() {
          return this.race_Mode;
     }

     /** 新しいUUIDを設定 / Sets a new UUID */
     public void setUUID() {
          this.race_ID = UUID.randomUUID();
     }

     /** UUIDを取得 / Gets UUID */
     public UUID getUUID() {
          return this.race_ID;
     }

     /** カウントダウン時間を取得 / Gets countdown time */
     public int getCountDown() {
          return TIME;
     }

     /** カウントダウン時間を設定 / Sets countdown time */
     public void setCountDown(int i) {
          this.TIME = i;
     }

     /**
      * レースを完了状態にする（待機モードに戻す）
      * 
      * Completes the race (returns to wait mode)
      */
     public void Complete() {
          setMode(Race_Mode.WAIT);
     }
}
