package waterpunch.atamamozi_d.plugin.tool.Timers;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.race.Race_Runner_Mode;

public class Race_Timer extends BukkitRunnable {

     private Race_Timer_Type Type;
     private int time;
     private UUID Race_UUID;
     private Player player;

     public Race_Timer(Race_Timer_Type type, UUID race) {
          this.Type = type;
          this.Race_UUID = race;
          switch (type) {
               case START:
                    this.time = waterpunch.atamamozi_d.plugin.main.Core.START_TIME;
                    break;
               case WAIT:
                    this.time = waterpunch.atamamozi_d.plugin.main.Core.WAIT_TIME;
                    break;
               case YOIN:
                    this.time = waterpunch.atamamozi_d.plugin.main.Core.YOIN_TIME;
                    break;
               default:
                    break;
          }
          for (int i = 0; i < Race_Core.Timers.size(); i++) if (Race_Core.Timers.get(i).getUUID() == getUUID()) Race_Core.Timers.get(i).cancel();
          Race_Core.Timers.add(this);
     }

     public Race_Timer(Player player) {
          this.time = 10;
          this.Type = Race_Timer_Type.YOIN;
          this.player = player;
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

     public void stop() {
          cancel();
     }

     @Override
     public void run() {
          if (Race_Core.Race_Run.isEmpty()) cancel();
          if (Race_Core.Race_Run.get(Race_UUID) == null) cancel();
          if (Race_Core.getRace(Race_UUID).getMode() != Race_Mode.WAIT) cancel();
          Race_Core.getRace(Race_UUID).setCountDown(this.time);
          try {
               switch (Type) {
                    case WAIT:
                         for (Race_Runner val : Race_Core.Race_Run.get(Race_UUID)) val.UpdateScoreboard();
                         if (this.time == 0) {
                              new Race_Timer(Race_Timer_Type.START, Race_UUID).runTaskTimer(waterpunch.atamamozi_d.plugin.main.Core.getthis(), 0L, 20L);
                              cancel();
                              return;
                         }
                         break;
                    case START:
                         if (this.time == 0) {
                              Race_Core.Race_Start(Race_UUID);
                              cancel();
                              return;
                         }
                         for (Race_Runner val : Race_Core.Race_Run.get(Race_UUID)) {
                              val.getPlayer().teleport(Race_Core.getRace(Race_UUID).getStartPointLoc().get(val.getJoin_Count() - 1).getLocation());
                              val.UpdateScoreboard();
                              val.getPlayer().playSound(val.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                              val.getPlayer().sendTitle(ChatColor.GREEN + " - " + ChatColor.AQUA + time + ChatColor.GREEN + " - ", "", 10, 15, 10);
                         }
                         break;
                    case YOIN:
                         if (this.time == 0) {
                              Race_Runner run = Race_Core.getRuner(player);
                              if (run.getMode() == Race_Runner_Mode.ALL_GOAL_WAIT || run.getMode() == Race_Runner_Mode.GOAL) player.teleport(run.getst_Location());
                              cancel();
                              return;
                         }
                         break;
                    default:
                         break;
               }
          } catch (NullPointerException e) {
               cancel();
          }

          this.time--;
     }
}
