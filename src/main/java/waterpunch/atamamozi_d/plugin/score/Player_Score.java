package waterpunch.atamamozi_d.plugin.score;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Player_Score {

     private LinkedHashMap<UUID, List<Long>> Scores = new LinkedHashMap<>();
     private UUID uuid;
     private String Name;

     public Player_Score(Player player) {
          this.uuid = player.getUniqueId();
          this.Name = player.getName();
     }

     public UUID getUUID() {
          return uuid;
     }

     public String getName() {
          return Name;
     }

     public Long getTOPScore(UUID RACE_ID) {
          if (Scores.isEmpty()) return null;
          if (Scores.get(RACE_ID) == null) return null;
          return Scores.get(RACE_ID).get(0);
     }

     public void setScore(UUID RACE_ID, Long i) {
          if (Scores.isEmpty()) Scores.put(RACE_ID, new ArrayList<Long>());
          if (Scores.get(RACE_ID) == null) Scores.put(RACE_ID, new ArrayList<Long>());
          Scores.get(RACE_ID).add(i);
          List<Long> sortedList = Scores.get(RACE_ID).stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
          Scores.remove(RACE_ID);
          Scores.put(RACE_ID, sortedList);

          if (Scores.get(RACE_ID).size() == 11) Scores.get(RACE_ID).remove(10);
          waterpunch.atamamozi_d.plugin.tool.CreateJson.Scoresave(this);
          if (0 == i - Scores.get(RACE_ID).get(0)) setTOP();
     }

     public List<Long> getScores(UUID RACE_ID) {
          if (Scores.isEmpty()) return null;
          if (Scores.get(RACE_ID) == null) return null;
          return Scores.get(RACE_ID);
     }

     public void setTOP() {
          for (Player player : Bukkit.getOnlinePlayers()) if (player.getUniqueId().equals(getUUID())) {
               player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "NEW RECORD!!");
               player.getPlayer().sendTitle(ChatColor.GREEN + " - " + ChatColor.AQUA + "NEW RECORD!!" + ChatColor.GREEN + " - ", "", 10, 40, 10);
               player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
               break;
          }
     }
}
