package waterpunch.atamamozi_d.plugin.tool;

import java.util.UUID;
import org.bukkit.scheduler.BukkitRunnable;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Race_Timer extends BukkitRunnable {

     private int time;
     private Race Race;

     public Race_Timer(int time, Race race) {
          this.time = time;
          this.Race = race;
          System.out.println("c");
     }

     public int getCountDown() {
          return this.time;
     }

     public UUID getUUID() {
          return this.Race.getUUID();
     }

     @Override
     public void run() {
          if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.isEmpty()) {
               cancel();
               return;
          }
          if (Race.getMode() != Race_Mode.WAIT) {
               cancel();
               return;
          }
          Race.setCountDown(this.time);
          System.out.println(Race.getCountDown());
          if (this.time == 0) {
               waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Start(Race);
               cancel();
               return;
          }
          for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race)) val.UpdateScoreboard();
          this.time--;
     }
}
