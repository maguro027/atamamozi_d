package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;

public class CountDownTimer extends BukkitRunnable {

     static Plugin plugin;

     public CountDownTimer(Plugin plugindata) {
          plugin = plugindata;
     }

     private int seconds;
     private CheckPointLoc CL;

     private Player player;

     public CountDownTimer(CheckPointLoc CL, Player player, int seconds) {
          this.seconds = seconds;
          this.CL = CL;
          this.player = player;
     }

     public void start() {
          this.runTaskTimer(plugin, 0, 20);
     }

     public void stop() {
          this.cancel();
     }

     @Override
     public void run() {
          if (seconds <= 0) {
               this.cancel();
               return;
          }

          seconds--;
     }
}
