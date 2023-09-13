package waterpunch.atamamozi_d.plugin.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.CreateJson;

public class Player_Score {

     private List<Score_parts> Scores = new ArrayList<Score_parts>();
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
          for (Score_parts parts : Scores) if (parts.getRace_ID().equals(RACE_ID)) return parts.getTime(0);
          return null;
     }

     public HashMap<UUID, Long> getTOPScores() {
          if (Scores.isEmpty()) return null;
          HashMap<UUID, Long> rt = new HashMap<UUID, Long>();
          for (Score_parts parts : Scores) rt.put(parts.getRace_ID(), parts.getTime(0));
          return rt;
     }

     public int getCount(UUID RACE_ID) {
          if (Scores.isEmpty()) return 0;
          for (Score_parts parts : Scores) if (parts.getRace_ID().equals(RACE_ID)) return parts.getCount();
          return 0;
     }

     public void setScore(UUID RACE_ID, Long i) {
          if (Scores.isEmpty()) {
               Scores.add(new Score_parts(RACE_ID, i));
               setTOP(RACE_ID, i);
               CreateJson.Scoresave(this);
               return;
          }
          for (Score_parts parts : Scores) if (parts.getRace_ID().equals(RACE_ID)) if (parts.addTime(i)) {
               setTOP(RACE_ID, i);
               CreateJson.Scoresave(this);
               return;
          } else return;
          Scores.add(new Score_parts(RACE_ID, i));
          CreateJson.Scoresave(this);
          return;
     }

     public void setTOP(UUID RACE_ID, Long i) {
          for (Player player : Bukkit.getOnlinePlayers()) if (player.getUniqueId().equals(getUUID())) {
               player.sendMessage(CollarMessage.setInfo() + "NEW RECORD!!");
               player.getPlayer().sendTitle(ChatColor.GREEN + " - " + ChatColor.AQUA + "NEW RECORD!!" + ChatColor.GREEN + " - ", "", 10, 40, 10);
               player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
               Player_Score_Core.addRanking(RACE_ID, player.getName(), i);
               break;
          }
     }
}
