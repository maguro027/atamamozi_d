package waterpunch.atamamozi_d.plugin.tool.Timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import waterpunch.atamamozi_d.plugin.main.Core;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner_Mode;

public class Leave_Timer extends BukkitRunnable {

     private Player player;
     private int time;

     public Leave_Timer(Player player) {
          this.time = Core.LEAVE_TIME;
          this.player = player;
     }

     @Override
     public void run() {
          if (this.time == 0) {
               if (Race_Core.getRuner(player).getMode() == Race_Runner_Mode.NO_ENTRY) player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               cancel();
               return;
          }
          this.time--;
     }
}
