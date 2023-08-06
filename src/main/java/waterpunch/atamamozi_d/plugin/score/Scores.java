package waterpunch.atamamozi_d.plugin.score;

import java.util.LinkedHashMap;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Scores {

     private LinkedHashMap<UUID, Long> Memory = new LinkedHashMap<>();

     public Boolean setScore(Player Player, Long Score) {
          if (getScore(Player.getUniqueId()) == null) {
               Memory.put(Player.getUniqueId(), Score);
               return true;
          }
          if (getScore(Player.getUniqueId()) >= Score) {
               Memory.put(Player.getUniqueId(), Score);
               newRecord(Player);
               return true;
          }
          return false;
     }

     public Long getScore(UUID Player) {
          return Memory.get(Player);
     }

     void newRecord(Player Player) {
          Player.sendMessage(waterpunch.atamamozi_d.plugin.tool.CollarMessage.setInfo() + "NEW RECORD!!");
          Player.getPlayer().sendTitle(ChatColor.GREEN + " - " + ChatColor.AQUA + "NEW RECORD!!" + ChatColor.GREEN + " - ", "", 10, 40, 10);
          Player.playSound(Player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
     }
}
