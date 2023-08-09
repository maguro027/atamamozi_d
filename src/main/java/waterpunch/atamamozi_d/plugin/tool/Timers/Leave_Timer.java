package waterpunch.atamamozi_d.plugin.tool.Timers;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
               for (Player val : Players) if (!waterpunch.atamamozi_d.plugin.race.Race_Core.isJoin(val)) waterpunch.atamamozi_d.plugin.race.Race_Core.removeRunner(val);
               cancel();
               return;
          }
          this.time--;
     }
}
