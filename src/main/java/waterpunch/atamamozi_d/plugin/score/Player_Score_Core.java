package waterpunch.atamamozi_d.plugin.score;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

public class Player_Score_Core {

     public static ArrayList<Player_Score> Score = new ArrayList<Player_Score>();
     public static LinkedHashMap<UUID, List<Long>> Ranking = new LinkedHashMap<>();

     public static void addPlayer_Score(Player Player, UUID RACE_ID, Long score) {
          if (Score.isEmpty()) Score.add(new Player_Score(Player));
          for (Player_Score val : Score) if (val.getUUID().equals(Player.getUniqueId())) val.setScore(RACE_ID, score);
     }

     public static Long getPlayer_TOP_Score(Player Player, UUID RACE_ID) {
          if (Score.isEmpty()) return null;
          for (Player_Score val : Score) if (val.getUUID().equals(Player.getUniqueId())) return val.getTOPScore(RACE_ID);
          return null;
     }

     public static List<Long> getPlayer_ALL_Score(Player Player, UUID RACE_ID) {
          if (Score.isEmpty()) return null;
          for (Player_Score val : Score) if (val.getUUID().equals(Player.getUniqueId())) return val.getScores(RACE_ID);
          return null;
     }

     public static void addRanking(Player Player, UUID RACE_ID, Long score) {}
}
