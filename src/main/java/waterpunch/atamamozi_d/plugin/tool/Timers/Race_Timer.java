package waterpunch.atamamozi_d.plugin.tool.Timers;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Race_Timer extends BukkitRunnable {

     private int time;
     private Race Race;
     private Race_Timer_Type Type;

     public Race_Timer(Race_Timer_Type Type, int time, Race race) {
          this.time = time;
          this.Race = race;
          this.Type = Type;
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
          if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race) == null) cancel();
          switch (Type) {
               case START:
                    if (Race.getMode() != Race_Mode.WAIT) {
                         cancel();
                         return;
                    }
                    Race.setCountDown(this.time);
                    if (this.time == 0) {
                         waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Start(Race);
                         cancel();
                         return;
                    }
                    for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race)) {
                         val.UpdateScoreboard();
                         val.getPlayer().playSound(val.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                         val.getPlayer().sendTitle(ChatColor.GREEN + " - " + time + " - ", "", 10, 15, 10);
                    }
                    break;
               case WAIT:
                    if (Race.getMode() != Race_Mode.WAIT) {
                         cancel();
                         return;
                    }
                    Race.setCountDown(this.time);
                    if (this.time == 0) {
                         new Race_Timer(Race_Timer_Type.START, 5, Race).runTaskTimer(waterpunch.atamamozi_d.plugin.main.Core.getthis(), 0L, 20L);
                         cancel();
                         return;
                    }
                    for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race)) {
                         val.UpdateScoreboard();
                    }
                    break;
               default:
                    break;
          }
          this.time--;
     }
}
