package waterpunch.atamamozi_d.plugin.score;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

public class Player_Score_Core {

     public static ArrayList<Player_Score> Score = new ArrayList<Player_Score>();
     public static LinkedHashMap<UUID, List<Ranking_parts>> Ranking = new LinkedHashMap<>();

     public static void addPlayer_Score(Player Player, UUID RACE_ID, Long score) {
          if (Score.isEmpty()) Score.add(new Player_Score(Player));
          for (Player_Score val : Score) if (val.getUUID().equals(Player.getUniqueId())) {
               val.setScore(RACE_ID, score);
               return;
          }
          Score.add(new Player_Score(Player));
          for (Player_Score val : Score) if (val.getUUID().equals(Player.getUniqueId())) val.setScore(RACE_ID, score);
     }

     public static Long getPlayer_TOP_Score(Player Player, UUID RACE_ID) {
          if (Score.isEmpty()) return null;
          for (Player_Score val : Score) if (val.getUUID().equals(Player.getUniqueId())) return val.getTOPScore(RACE_ID);
          return null;
     }

     public static void addRanking(UUID RACE_ID, String Player, Long score) {
          if (Ranking.isEmpty()) Ranking.put(RACE_ID, new ArrayList<Ranking_parts>());
          if (Ranking.get(RACE_ID) == null) Ranking.put(RACE_ID, new ArrayList<Ranking_parts>());
          int i = getRank(RACE_ID, Player);
          if (i == -1) {
               Ranking.get(RACE_ID).add(new Ranking_parts(Player, score));
          } else {
               Ranking.get(RACE_ID).remove(i - 1);
               Ranking.get(RACE_ID).add(new Ranking_parts(Player, score));
          }
          SortRanking(RACE_ID);
     }

     public static void SortRanking(UUID RACE_ID) {
          if (Ranking.isEmpty()) return;
          if (Ranking.get(RACE_ID) == null) return;
          Comparator<Ranking_parts> comparator = Comparator.comparing(Ranking_parts::getTIME);
          List<Ranking_parts> onetime = Ranking.get(RACE_ID).stream().sorted(comparator).collect(Collectors.toList());
          Ranking.get(RACE_ID).clear();
          Ranking.get(RACE_ID).addAll(onetime);
     }

     public static int getRank(UUID RACE_ID, String Player) {
          if (Ranking.isEmpty()) return -1;
          if (Ranking.get(RACE_ID) == null) return -1;
          for (int count = 0; count < Ranking.get(RACE_ID).size(); count++) if (Ranking.get(RACE_ID).get(count).getNAME().equals(Player)) return count + 1;
          return -1;
     }

     public static List<Ranking_parts> getRanking(UUID RACE_ID) {
          if (Ranking.isEmpty()) return null;
          if (Ranking.get(RACE_ID) == null) return null;
          return Ranking.get(RACE_ID);
     }
}
