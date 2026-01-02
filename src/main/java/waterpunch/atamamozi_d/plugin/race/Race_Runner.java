package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import waterpunch.atamamozi_d.plugin.main.Core;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Runner_Mode;
import waterpunch.atamamozi_d.plugin.score.Player_Score_Core;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.Location.LocationViewer;
import waterpunch.atamamozi_d.plugin.tool.Scoreboaed.Race_Scoreboard;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer_Type;

public class Race_Runner {

     private final Player player;
     private UUID raceID;
     private String raceName;
     private Race_Runner_Mode raceMode;
     private int joinCount, checkPoint, rap;
     private long start_time, end_time;
     private Location st_Location, old_Location, new_Location;
     private final Race_Scoreboard scoreboard;
     private final LocationViewer locationViewer;
     // スコアボード更新を抑制するためのタイムスタンプ（毎ティック再構築を避ける）
     private long lastScoreUpdate = 0L;
     // 前回レンダリングしたスコアボード行をキャッシュして差分更新に使う
     private List<String> lastScoreLines = new ArrayList<>();
     // 速度メーターのキャッシュ（最大100msごとに更新）
     private long lastSpeedUpdate = 0L;
     private int cachedSpeed = 0;
     private UUID car;
     private boolean enter;

     public Race_Runner(Player player) {
          this.player = player;
          this.raceMode = Race_Runner_Mode.NO_ENTRY;
          this.scoreboard = new Race_Scoreboard();
          this.locationViewer = new LocationViewer(this);
          // Register this runner after construction is complete
          register();
     }

     private void register() {
          Race_Core.Race_Runner_List.add(this);
          // also add to the fast lookup map
          if (this.player != null)
               Race_Core.Race_Runner_Map.put(this.player.getUniqueId(), this);
     }

     public Boolean UPDate(UUID Race_ID) {
          // if (Race_Core.getRace(Race_ID).getJoin_Amount() ==
          // Race_Core.Race_Run.get(getRaceID()).size()) {
          // player.sendMessage(CollarMessage.setInfo() + " MAX Player");
          // // Complete();
          // return false;
          // }
          this.new_Location = player.getLocation();
          this.old_Location = player.getLocation();
          this.raceID = Race_ID;
          this.raceMode = Race_Runner_Mode.WAIT;
          this.start_time = System.currentTimeMillis();
          this.st_Location = player.getLocation();

          // Ensure there's a list for this race id to avoid NPEs
          if (!Race_Core.Race_Run.containsKey(Race_ID) || Race_Core.Race_Run.get(Race_ID) == null) {
               Race_Core.Race_Run.put(Race_ID, new java.util.ArrayList<>());
          }
          this.joinCount = Race_Core.Race_Run.get(Race_ID).size() + 1;
          if (getJoin_Count() == 1)
               waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer.startTimer(Race_Timer_Type.WAIT,
                         getRaceID(), Core.getthis(), 0L, 20L);
          this.rap = 0;
          this.checkPoint = 0;
          Race_Core.Race_Run.get(Race_ID).add(this);
          UpdateScoreboard();
          return true;
     }

     public void UpdateScoreboard() {
          // Basic rate-limit: avoid rebuilding scoreboard too frequently
          final long now = System.currentTimeMillis();
          if (now - lastScoreUpdate < 500)
               return; // 500ms cooldown
          lastScoreUpdate = now;

          // Build text lines first (cheap compared to constructing a Scoreboard object)
          List<String> lines = scoreboard.buildLines(this);

          // If the mode expects no scoreboard (e.g., NO_ENTRY), clear and return
          if (lines == null) {
               player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               lastScoreLines = new ArrayList<>();
               return;
          }

          // If content hasn't changed, avoid rebuilding the scoreboard (diff update)
          if (lines.equals(lastScoreLines))
               return;

          // Build and apply new scoreboard when content changed
          Scoreboard s = scoreboard.buildBoardFromLines(lines);
          if (s != null) {
               player.setScoreboard(s);
               lastScoreLines = new ArrayList<>(lines);
               return;
          }
          player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
     }

     /**
      * キャッシュされた速度表示。最大で100msごとに更新される。
      */
     public int getCachedSpeed() {
          final long now = System.currentTimeMillis();
          if (now - lastSpeedUpdate >= 100) {
               lastSpeedUpdate = now;
               if (new_Location == null || old_Location == null) {
                    cachedSpeed = 0;
               } else {
                    double dx = new_Location.getX() - old_Location.getX();
                    double dz = new_Location.getZ() - old_Location.getZ();
                    double raw = Math.sqrt(dx * dx + dz * dz);
                    // preserve previous calculation semantics (scaled and rounded)
                    double speed = (raw * 20 * 60 * 60) / 1000.0;
                    cachedSpeed = new java.math.BigDecimal(speed).setScale(1, java.math.RoundingMode.HALF_UP)
                              .intValue();
               }
          }
          return cachedSpeed;
     }

     public Player getPlayer() {
          return player;
     }

     public UUID getRaceID() {
          return raceID;
     }

     public void setRaceID(UUID ID) {
          this.raceID = ID;
     }

     public void setnewLoc(Location loc) {
          this.new_Location = loc;
     }

     public void setoldLoc(Location loc) {
          this.old_Location = loc;
     }

     public Location getnewLoc() {
          return new_Location;
     }

     public Location getoldLoc() {
          return old_Location;
     }

     public void setJoin_Count(int i) {
          this.joinCount = i;
     }

     public int getJoin_Count() {
          return joinCount;
     }

     public void setMode(Race_Runner_Mode mode) {
          this.raceMode = mode;
     }

     public Race_Runner_Mode getMode() {
          return raceMode;
     }

     public Location getst_Location() {
          return st_Location;
     }

     public int getCheckPoint() {
          return checkPoint;
     }

     public Long getStart_time() {
          return start_time;
     }

     public Long getEnd_time() {
          return end_time;
     }

     public Long getTime() {
          return getEnd_time() - getStart_time();
     }

     public String getTimest() {
          return DurationFormatUtils.formatPeriod(getStart_time(), getEnd_time(), "HH:mm:ss.SSS");
     }

     public String getNOWTimest() {
          return DurationFormatUtils.formatPeriod(getStart_time(), System.currentTimeMillis(), "HH:mm:ss.SSS");
     }

     public UUID getCar() {
          return car;
     }

     public void setCar(UUID uuid) {
          this.car = uuid;
     }

     public void addCheckPoint() {
          this.checkPoint++;
          Race r = Race_Core.getRace(raceID);
          if (r == null)
               return;
          if (r.getCheckPoint_Loc().size() == getCheckPoint()) {
               setCheckPoint(0);
               addRap();
          } else {
               this.player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               locationViewer.DrawCircle(checkPoint);
               // Force immediate scoreboard refresh for checkpoint events
               lastScoreUpdate = 0L;
               UpdateScoreboard();
          }
     }

     public void setCheckPoint(int i) {
          checkPoint = i;
     }

     public int getRap() {
          return rap;
     }

     public void addRap() {
          this.rap++;
          player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          // Force immediate update after rap increment
          lastScoreUpdate = 0L;
          UpdateScoreboard();
          Race r = Race_Core.getRace(raceID);
          if (r != null && r.getRap() == rap)
               Goal();
     }

     public void setRap(int i) {
          this.rap = i;
     }

     public LocationViewer getLocationViewer() {
          return this.locationViewer;
     }

     public void setEnter(boolean yn) {
          this.enter = yn;
     }

     public Boolean getEnter() {
          return this.enter;
     }

     public void Start() {
          this.raceMode = Race_Runner_Mode.RUN;
          Race RACE = Race_Core.getRace(raceID);
          if (RACE == null) {
               player.sendMessage(CollarMessage.setWarning() + "Race data missing");
               this.raceMode = Race_Runner_Mode.NO_ENTRY;
               return;
          }
          // ensure start point exists for this join index
          if (RACE.getStartPoint() == null || RACE.getStartPoint().size() < joinCount) {
               player.sendMessage(CollarMessage.setWarning() + "Start point not configured for your join slot");
               this.raceMode = Race_Runner_Mode.NO_ENTRY;
               return;
          }
          player.teleport(RACE.getStartPoint().get(joinCount - 1).getLocation());

          player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          player.sendTitle(ChatColor.GREEN + " - START - ", "", 10, 15, 10);

          switch (Race_Core.getRace(raceID).getRace_Type()) {
               case BOAT:
                    enter = true;
                    if (RACE.getStartPoint() == null || joinCount <= 0
                              || joinCount > RACE.getStartPoint().size()) {
                         break;
                    }
                    Location startLoc = RACE.getStartPoint().get(joinCount - 1).getLocation();
                    if (startLoc == null)
                         break;

                    org.bukkit.World world = startLoc.getWorld();
                    if (world == null)
                         break;
                    world.spawnEntity(startLoc, EntityType.BOAT).addPassenger(player);
                    Entity playerVehicle = player.getVehicle();
                    if (playerVehicle != null) {
                         car = playerVehicle.getUniqueId();
                    } else {
                         // safeguard: vehicle may not be immediately attached; leave car null and set
                         // enter flag
                         car = null;
                    }
                    break;
               case WALK:
                    break;
               default:
                    player.sendMessage(CollarMessage.setWarning() + ChatColor.RED + "A fatal error has occurred");
                    player.sendMessage(CollarMessage.setWarning() + ChatColor.RED + "----------------------");
                    player.sendMessage(CollarMessage.setWarning() + ChatColor.RED + raceName);
                    player.sendMessage(CollarMessage.setWarning() + ChatColor.RED + "Unknown Race Type ["
                              + RACE.getRace_Type() + "]");
                    player.sendMessage(CollarMessage.setWarning() + ChatColor.RED + "----------------------");
                    Race_Core.removeRunner(player);
                    break;
          }
          this.player.sendMessage(CollarMessage.setInfo() + "START");
          // Set start time for race timing
          this.start_time = System.currentTimeMillis();
          // immediate update for start
          lastScoreUpdate = 0L;
          UpdateScoreboard();
     }

     public void ReSpawn() {
          Race RACE = Race_Core.getRace(raceID);
          if (RACE == null)
               return;
          switch (RACE.getMode()) {
               case EDIT:
               case GOAL:
                    player.sendMessage(CollarMessage.setInfo() + "Race is Not Active");
                    return;
               default:
                    break;
          }
          switch (RACE.getRace_Type()) {
               case WALK:
                    if (getCheckPoint() == 0) {
                         int idx = getJoin_Count() - 1;
                         if (RACE.getStartPoint() == null || idx < 0 || idx >= RACE.getStartPoint().size()) {
                              player.sendMessage(CollarMessage.setWarning() + "Respawn start point not available");
                              player.teleport(getst_Location());
                         } else {
                              player.teleport(RACE.getStartPoint().get(idx).getLocation());
                         }
                    } else {
                         int cpIdx = getCheckPoint() - 1;
                         if (RACE.getCheckPoint_Loc() == null || cpIdx < 0
                                   || cpIdx >= RACE.getCheckPoint_Loc().size()) {
                              player.sendMessage(CollarMessage.setWarning() + "Checkpoint location not available");
                              player.teleport(getst_Location());
                         } else {
                              player.teleport(RACE.getCheckPoint_Loc().get(cpIdx).getLocation());
                         }
                    }
                    break;
               case BOAT:
                    Entity vehicle = getPlayer().getVehicle();
                    enter = true;
                    if (vehicle != null)
                         vehicle.remove();
                    if (getCheckPoint() == 0) {
                         int idx = getJoin_Count() - 1;
                         if (RACE.getStartPoint() == null || idx < 0 || idx >= RACE.getStartPoint().size()) {
                              player.sendMessage(
                                        CollarMessage.setWarning() + "Respawn start point not available for boat");
                              break;
                         }
                         if (RACE.getStartPoint().get(idx) != null) {
                              Location spawnLoc = RACE.getStartPoint().get(idx).getLocation();
                              if (spawnLoc != null) {
                                   org.bukkit.World world = spawnLoc.getWorld();
                                   if (world != null) {
                                        world.spawnEntity(spawnLoc, EntityType.BOAT).addPassenger(player);
                                   }
                              }
                         }
                    } else {
                         int cpIdx = getCheckPoint() - 1;
                         if (RACE.getCheckPoint_Loc() == null || cpIdx < 0
                                   || cpIdx >= RACE.getCheckPoint_Loc().size()) {
                              player.sendMessage(
                                        CollarMessage.setWarning() + "Respawn checkpoint not available for boat");
                              break;
                         }
                         if (RACE.getCheckPoint_Loc().get(cpIdx) != null) {
                              Location cpLoc = RACE.getCheckPoint_Loc().get(cpIdx).getLocation();
                              if (cpLoc != null) {
                                   org.bukkit.World world = cpLoc.getWorld();
                                   if (world != null) {
                                        world.spawnEntity(cpLoc, EntityType.BOAT).addPassenger(player);
                                   }
                              }
                         }
                    }
                    break;
          }

          player.sendMessage(CollarMessage.setInfo() + "Respawn");
     }

     public void Goal() {
          Race RACE = Race_Core.getRace(raceID);
          if (RACE == null)
               return;
          this.end_time = System.currentTimeMillis();

          getPlayer().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          getPlayer().sendMessage(CollarMessage.setInfo() + "GOAL!!");
          java.util.List<Race_Runner> __runners_goal = Race_Core.Race_Run.get(RACE.getRace_ID());
          if (__runners_goal != null) {
               for (Race_Runner val : __runners_goal) {
                    val.getPlayer().sendMessage(CollarMessage.setInfo() + "[" + ChatColor.AQUA + player.getName()
                              + ChatColor.WHITE + "] " + getTimest());
                    val.UpdateScoreboard();
               }
          }

          // ensure scoreboard updates immediately after goal
          lastScoreUpdate = 0L;
          UpdateScoreboard();
          Player_Score_Core.addPlayer_Score(getPlayer(), getRaceID(), getTime());
          setMode(Race_Runner_Mode.ALL_GOAL_WAIT);
          new Race_Timer(getPlayer()).runTaskTimer(Core.getthis(), 0L, 20L);
          int i = 0;
          java.util.List<Race_Runner> __runners_goal_check = Race_Core.Race_Run.get(RACE.getRace_ID());
          if (__runners_goal_check != null) {
               for (Race_Runner val : __runners_goal_check)
                    if (val.getMode() == Race_Runner_Mode.ALL_GOAL_WAIT)
                         i++;
               if (i == __runners_goal_check.size())
                    Race_Core.AllGoal(RACE.getRace_ID());
          }
     }

     public void Complete() {
          nullRace();
          this.raceMode = Race_Runner_Mode.NO_ENTRY;
          player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
     }

     public void nullRace() {
          raceID = null;
     }
}
