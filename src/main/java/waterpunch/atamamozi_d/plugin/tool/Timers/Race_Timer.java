package waterpunch.atamamozi_d.plugin.tool.Timers;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import waterpunch.atamamozi_d.plugin.main.Core;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Race_Timer extends BukkitRunnable {

     private int time;
     private UUID Race_UUID;

     public Race_Timer(int time, UUID race) {
          this.time = time;
          this.Race_UUID = race;

          for (int i = 0; i < Race_Core.Timers.size(); i++) if (Race_Core.Timers.get(i).getUUID() == getUUID()) Race_Core.Timers.get(i).cancel();
          Race_Core.Timers.add(this);
     }

     public int getCountDown() {
          return this.time;
     }

     public void upDateTimer(int i) {
          this.time = i;
     }

     public UUID getUUID() {
          return this.Race_UUID;
     }

     @Override
     public void run() {
          if (Race_Core.Race_Run.isEmpty() || Race_Core.getRunners(Race_UUID) == null) {
               cancel();
               return;
          }

          if (Race_Core.getRace(Race_UUID).getMode() != Race_Mode.WAIT) {
               cancel();
               return;
          }
          Race_Core.getRace(Race_UUID).setCountDown(this.time);

          if (this.time == 0) {
               Race_Core.Race_Start(Race_UUID);
               cancel();
               return;
          }
          for (Race_Runner val : Race_Core.getRunners(Race_UUID)) {
               val.UpdateScoreboard();

               if (Core.START >= time) {
                    val.getPlayer().teleport(Race_Core.getRace(Race_UUID).getStartPointLoc().get(val.getJoin_Count() - 1).getLocation());
                    val.getPlayer().playSound(val.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                    val.getPlayer().sendTitle(ChatColor.GREEN + " - " + ChatColor.AQUA + time + ChatColor.GREEN + " - ", "", 10, 15, 10);
               }
          }

          this.time--;
     }
}
