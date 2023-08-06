package waterpunch.atamamozi_d.plugin.score;

import java.util.LinkedHashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

public class Score_Core {

     public static LinkedHashMap<UUID, Scores> Score_List = new LinkedHashMap<>();

     public static Long getScore(UUID RACE_ID, UUID Player) {
          if (Score_List.isEmpty()) return null;
          if (Score_List.get(RACE_ID) == null) return null;
          return Score_List.get(RACE_ID).getScore(Player);
     }

     public static Boolean setScore(UUID RACE_ID, Player Player, Long Time) {
          if (Score_List.get(RACE_ID) == null) Score_List.put(RACE_ID, new Scores());
          return Score_List.get(RACE_ID).setScore(Player, Time);
     }
}
