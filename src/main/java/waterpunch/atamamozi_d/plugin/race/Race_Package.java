package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;

import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.tool.Location.Loc_parts;

/**
 * レースのデータを保持するJSON保存用クラス。
 * 
 * <p>
 * このクラスはレースの永続化が必要な情報のみを保持します。
 * 実行時の状態（参加者数、モードなど）は含まれません。
 * 全てのフィールドがfinalで不変のため、スレッドセーフです。
 * </p>
 * 
 * <h3>保存されるデータ：</h3>
 * <ul>
 * <li>基本情報：作成者、レース名、UUID</li>
 * <li>設定：参加可能人数、周回数、レースタイプ、アイコン</li>
 * <li>コース：スタート地点、チェックポイント</li>
 * </ul>
 */
public class Race_Package {

     private final String creator;
     private final String race_name;
     private final int join_amount;
     private final int rap;
     private final Race_Type race_type;
     private final Material icon;
     private final UUID race_ID;

     private final ArrayList<Loc_parts> StartPoint = new ArrayList<>();
     private final ArrayList<CheckPointLoc> CheckPoint_Loc = new ArrayList<>();

     public Race_Package(UUID race_ID, String creator, String race_name, int join_amount, int rap, Race_Type race_type,
               Material icon) {
          this.race_ID = race_ID;
          this.creator = creator;
          this.race_name = race_name;
          this.join_amount = join_amount;
          this.rap = rap;
          this.race_type = race_type;
          this.icon = icon;
     }

     public UUID getRace_ID() {
          return race_ID;
     }

     public String getCreator() {
          return this.creator;
     }

     public String getRace_name() {
          return this.race_name;
     }

     public int getJoin_Amount() {
          return this.join_amount;
     }

     public int getRap() {
          return this.rap;
     }

     public Race_Type getRace_Type() {
          return this.race_type;
     }

     public Material getIcon() {
          return this.icon;
     }

     public ArrayList<Loc_parts> getStartPoint() {
          return this.StartPoint;
     }

     public ArrayList<CheckPointLoc> getCheckPoint_Loc() {
          return this.CheckPoint_Loc;
     }
}
