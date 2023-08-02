package waterpunch.atamamozi_d.plugin.tool.Timers;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import waterpunch.atamamozi_d.plugin.race.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;

public class Race_Timer extends BukkitRunnable {

     private Race_Timer_Type Type;
     private int time;
     private UUID Race_UUID;

     public Race_Timer(Race_Timer_Type type, UUID race) {
          this.Type = type;
          this.Race_UUID = race;
          switch (type) {
               case START:
                    this.time = 5;
                    break;
               case WAIT:
               case LEAVE:
                    this.time = 30;
                    break;
               default:
                    break;
          }
          for (int i = 0; i < waterpunch.atamamozi_d.plugin.race.Race_Core.Timers.size(); i++) if (waterpunch.atamamozi_d.plugin.race.Race_Core.Timers.get(i).getUUID() == getUUID()) waterpunch.atamamozi_d.plugin.race.Race_Core.Timers.get(i).cancel();
          waterpunch.atamamozi_d.plugin.race.Race_Core.Timers.add(this);
     }

     public int getCountDown() {
          return this.time;
     }

     public Race_Timer_Type getType() {
          return this.Type;
     }

     public UUID getUUID() {
          return this.Race_UUID;
     }

     @Override
     public void run() {
          if (waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.isEmpty() || waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race_UUID) == null) {
               cancel();
               return;
          }

          if (waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(Race_UUID).getMode() != Race_Mode.WAIT) {
               cancel();
               return;
          }
          waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(Race_UUID).setCountDown(this.time);

          if (this.time == 0) {
               waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Start(Race_UUID);
               cancel();
               return;
          }
          for (Race_Runner val : waterpunch.atamamozi_d.plugin.race.Race_Core.Race_Run.get(Race_UUID)) {
               val.getPlayer().teleport(waterpunch.atamamozi_d.plugin.race.Race_Core.getRace(Race_UUID).getStartPointLoc().get(val.getJoin_Count() - 1).getLocation());
               val.UpdateScoreboard();
               val.getPlayer().playSound(val.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               val.getPlayer().sendTitle(ChatColor.GREEN + " - " + ChatColor.AQUA + time + ChatColor.GREEN + " - ", "", 10, 15, 10);
          }

          this.time--;
     }
}
