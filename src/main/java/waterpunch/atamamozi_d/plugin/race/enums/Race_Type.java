package waterpunch.atamamozi_d.plugin.race.enums;

/**
 * レースタイプ列挙型
 * レースの移動手段を表す
 * 
 * Race type enumeration
 * Represents mode of transportation in race
 */
public enum Race_Type {
     /** 徒歩 / Walking */
     WALK(1),
     
     /** ボート / Boat */
     BOAT(2);

     @SuppressWarnings("unused")
     private int id;

     private Race_Type(int id) {
          this.id = id;
     }
}
