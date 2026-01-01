package waterpunch.atamamozi_d.plugin.tool.Timers;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;

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
          // Constructor no longer cancels or registers itself to avoid leaking
          // `this` during construction and to avoid calling overridable methods.
          // Use the static helper `startTimer(...)` to schedule timers safely.
     }

     /**
      * Safely cancel existing timers for the given race and schedule a new one.
      */
     public static Race_Timer startTimer(Race_Timer_Type type, UUID race, org.bukkit.plugin.Plugin plugin,
               long delay, long period) {
          // cancel any existing timers for this race
          for (int i = 0; i < Race_Core.Timers.size(); i++) {
               Race_Timer t = Race_Core.Timers.get(i);
               UUID u = t.getUUID();
               if (u != null && u.equals(race)) {
                    t.cancel();
               }
          }
          Race_Timer rt = new Race_Timer(type, race);
          Race_Core.Timers.add(rt);
          rt.runTaskTimer(plugin, delay, period);
          return rt;
     }

     public Race_Timer(Player player) {
          this.Type = Race_Timer_Type.YOIN;
          this.time = waterpunch.atamamozi_d.plugin.main.Core.YOIN_TIME;
          this.player = player;
     }

     public int getCountDown() {
          return this.time;
     }

     public Race_Timer_Type getType() {
          return this.Type;
     }

     public final UUID getUUID() {
          return this.Race_UUID;
     }

     public void stop() {
          cancel();
     }

     @Override
     public void run() {
          if (Type == Race_Timer_Type.YOIN) {
               Race_Runner r = Race_Core.getRunner(player);
               if (r == null) {
                    cancel();
                    return;
               }
               if (this.time == 0) {
                    switch (r.getMode()) {
                         case ALL_GOAL_WAIT:
                         case NO_ENTRY:
                              waterpunch.atamamozi_d.plugin.race.Race race_temp = Race_Core.getRace(r.getRaceID());
                              if (race_temp != null
                                        && race_temp.getRace_Type() == Race_Type.BOAT
                                        && player.getVehicle() != null)
                                   player.getVehicle().remove();
                              player.teleport(r.getst_Location());
                              break;
                         default:
                              break;
                    }
                    cancel();
                    return;
               }
               this.time--;
               return;
          }

          // 防御的なチェック: ランナーがいない、またはレースデータが無い場合はタイマーを停止する
          if (Race_Core.Race_Run.isEmpty()) {
               cancel();
               return;
          }

          java.util.List<Race_Runner> __runners = Race_Core.Race_Run.get(Race_UUID);
          if (__runners == null) {
               cancel();
               return;
          }

          waterpunch.atamamozi_d.plugin.race.Race race = Race_Core.getRace(Race_UUID);
          if (race == null || race.getMode() != Race_Mode.WAIT) {
               cancel();
               return;
          }
          race.setCountDown(this.time);
          try {
               switch (Type) {
                    case WAIT:
                         for (Race_Runner val : __runners)
                              val.UpdateScoreboard();
                         if (this.time == 0) {
                              waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer.startTimer(
                                        Race_Timer_Type.START, Race_UUID,
                                        waterpunch.atamamozi_d.plugin.main.Core.getthis(), 0L, 20L);
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
                         for (Race_Runner val : __runners) {
                              val.getPlayer().teleport(Race_Core.getRace(Race_UUID).getStartPointLoc()
                                        .get(val.getJoin_Count() - 1).getLocation());
                              val.UpdateScoreboard();
                              val.getPlayer().playSound(val.getPlayer().getLocation(),
                                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                              val.getPlayer().sendTitle(
                                        ChatColor.GREEN + " - " + ChatColor.AQUA + time + ChatColor.GREEN + " - ", "",
                                        10, 15, 10);
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
