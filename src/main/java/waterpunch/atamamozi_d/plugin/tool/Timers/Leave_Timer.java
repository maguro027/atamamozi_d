package waterpunch.atamamozi_d.plugin.tool.Timers;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner_Mode;

public class Leave_Timer extends BukkitRunnable {

     private int time;
     private ArrayList<Player> Players = new ArrayList<>();

     public Leave_Timer(ArrayList<Player> Players) {
          this.Players = Players;
          this.time = 30;
     }

     @Override
     public void run() {
          if (this.time == 0) {
               for (Player val : Players) if (val.isOnline() && Race_Core.getRuner(val).getMode() == Race_Runner_Mode.NO_ENTRY) Race_Core.removeRunner(val);
               cancel();
               return;
          }
          this.time--;
     }
}
