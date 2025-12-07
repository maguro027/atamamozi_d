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

     private final Player Player;
     private UUID Race_ID;
     private String Race_Name;
     private Race_Runner_Mode Race_mode;
     private int Join_Count, CheckPoint, Rap;
     private long start_time, end_time;
     private Location st_Location, old_Location, new_Location;
     private final Race_Scoreboard scoreboard;
     private final LocationViewer locationViewer;
     // Timestamp used to throttle scoreboard updates to avoid rebuilding every tick
     private long lastScoreUpdate = 0L;
     // Cache previous rendered scoreboard lines to do diff updates
     private List<String> lastScoreLines = new ArrayList<>();
     // Speed meter caching (update at most every 100ms)
     private long lastSpeedUpdate = 0L;
     private int cachedSpeed = 0;
     private UUID Car;
     private boolean Enter;

     public Race_Runner(Player player) {
          this.Player = player;
          this.Race_mode = Race_Runner_Mode.NO_ENTRY;
          this.scoreboard = new Race_Scoreboard();
          this.locationViewer = new LocationViewer(this);
          // Register this runner after construction is complete
          register();
     }

     private void register() {
          Race_Core.Race_Runner_List.add(this);
          // also add to the fast lookup map
          if (this.Player != null)
               Race_Core.Race_Runner_Map.put(this.Player.getUniqueId(), this);
     }

     public Boolean UPDate(UUID Race_ID) {
          // if (Race_Core.getRace(Race_ID).getJoin_Amount() ==
          // Race_Core.Race_Run.get(getRaceID()).size()) {
          // Player.sendMessage(CollarMessage.setInfo() + " MAX Player");
          // // Complete();
          // return false;
          // }
          this.new_Location = Player.getLocation();
          this.old_Location = Player.getLocation();
          this.Race_ID = Race_ID;
          this.Race_mode = Race_Runner_Mode.WAIT;
          this.start_time = System.currentTimeMillis();
          this.st_Location = Player.getLocation();

          // Ensure there's a list for this race id to avoid NPEs
          if (!Race_Core.Race_Run.containsKey(Race_ID) || Race_Core.Race_Run.get(Race_ID) == null) {
               Race_Core.Race_Run.put(Race_ID, new java.util.ArrayList<>());
          }
          this.Join_Count = Race_Core.Race_Run.get(Race_ID).size() + 1;
          if (getJoin_Count() == 1)
               new Race_Timer(Race_Timer_Type.WAIT, getRaceID()).runTaskTimer(Core.getthis(), 0L, 20L);
          this.Rap = 0;
          this.CheckPoint = 0;
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
               Player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               lastScoreLines = new ArrayList<>();
               return;
          }

          // If content hasn't changed, avoid rebuilding the scoreboard (diff update)
          if (lines.equals(lastScoreLines))
               return;

          // Build and apply new scoreboard when content changed
          Scoreboard s = scoreboard.buildBoardFromLines(lines);
          if (s != null) {
               Player.setScoreboard(s);
               lastScoreLines = new ArrayList<>(lines);
               return;
          }
          Player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
     }

     /**
      * Cached speed readout updated at most every 100ms.
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
          return Player;
     }

     public UUID getRaceID() {
          return Race_ID;
     }

     public void setRaceID(UUID ID) {
          this.Race_ID = ID;
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
          this.Join_Count = i;
     }

     public int getJoin_Count() {
          return Join_Count;
     }

     public void setMode(Race_Runner_Mode mode) {
          this.Race_mode = mode;
     }

     public Race_Runner_Mode getMode() {
          return Race_mode;
     }

     public Location getst_Location() {
          return st_Location;
     }

     public int getCheckPoint() {
          return CheckPoint;
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
          return Car;
     }

     public void setCar(UUID uuid) {
          this.Car = uuid;
     }

     public void addCheckPoint() {
          this.CheckPoint++;
          Race r = Race_Core.getRace(Race_ID);
          if (r == null)
               return;
          if (r.getCheckPointLoc().size() == getCheckPoint()) {
               setCheckPoint(0);
               addRap();
          } else {
               this.Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               locationViewer.DrawCircle(CheckPoint);
               // Force immediate scoreboard refresh for checkpoint events
               lastScoreUpdate = 0L;
               UpdateScoreboard();
          }
     }

     public void setCheckPoint(int i) {
          CheckPoint = i;
     }

     public int getRap() {
          return Rap;
     }

     public void addRap() {
          this.Rap++;
          Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          // Force immediate update after rap increment
          lastScoreUpdate = 0L;
          UpdateScoreboard();
          Race r = Race_Core.getRace(Race_ID);
          if (r != null && r.getRap() == Rap)
               Goal();
     }

     public void setRap(int i) {
          this.Rap = i;
     }

     public LocationViewer getLocationViewer() {
          return this.locationViewer;
     }

     public void setEnter(boolean yn) {
          this.Enter = yn;
     }

     public Boolean getEnter() {
          return this.Enter;
     }

     public void Start() {
          this.Race_mode = Race_Runner_Mode.RUN;
          Race RACE = Race_Core.getRace(Race_ID);
          if (RACE == null) {
               Player.sendMessage(CollarMessage.setWarning() + "Race data missing");
               this.Race_mode = Race_Runner_Mode.NO_ENTRY;
               return;
          }
          // ensure start point exists for this join index
          if (RACE.getStartPointLoc() == null || RACE.getStartPointLoc().size() < Join_Count) {
               Player.sendMessage(CollarMessage.setWarning() + "Start point not configured for your join slot");
               this.Race_mode = Race_Runner_Mode.NO_ENTRY;
               return;
          }
          Player.teleport(RACE.getStartPointLoc().get(Join_Count - 1).getLocation());

          Player.playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          Player.sendTitle(ChatColor.GREEN + " - START - ", "", 10, 15, 10);

          switch (Race_Core.getRace(Race_ID).getRace_Type()) {
               case BOAT:
                    Enter = true;
                    if (RACE.getStartPointLoc() != null && Join_Count > 0
                              && Join_Count <= RACE.getStartPointLoc().size()) {
                         Location startLoc = RACE.getStartPointLoc().get(Join_Count - 1).getLocation();
                         if (startLoc != null) {
                              org.bukkit.World world = startLoc.getWorld();
                              if (world != null) {
                                   world.spawnEntity(startLoc, EntityType.BOAT).addPassenger(Player);
                                   Entity playerVehicle = Player.getVehicle();
                                   if (playerVehicle != null) {
                                        Car = playerVehicle.getUniqueId();
                                   } else {
                                        // safeguard: vehicle may not be immediately attached; leave Car null and set
                                        // Enter flag
                                        Car = null;
                                   }
                              }
                         }
                    }
                    break;
               case WALK:
                    break;
               default:
                    Player.sendMessage(CollarMessage.setWarning() + ChatColor.RED + "A fatal error has occurred");
                    Player.sendMessage(CollarMessage.setWarning() + ChatColor.RED + "----------------------");
                    Player.sendMessage(CollarMessage.setWarning() + ChatColor.RED + Race_Name);
                    Player.sendMessage(CollarMessage.setWarning() + ChatColor.RED + "Unknown Race Type ["
                              + RACE.getRace_Type() + "]");
                    Player.sendMessage(CollarMessage.setWarning() + ChatColor.RED + "----------------------");
                    Race_Core.removeRunner(Player);
                    break;
          }
          this.Player.sendMessage(CollarMessage.setInfo() + "START");
          // Set start time for race timing
          this.start_time = System.currentTimeMillis();
          // immediate update for start
          lastScoreUpdate = 0L;
          UpdateScoreboard();
     }

     public void ReSpawn() {
          Race RACE = Race_Core.getRace(Race_ID);
          if (RACE == null)
               return;
          switch (RACE.getMode()) {
               case EDIT:
               case GOAL:
                    Player.sendMessage(CollarMessage.setInfo() + "Race is Not Active");
                    return;
               default:
                    break;
          }
          switch (RACE.getRace_Type()) {
               case WALK:
                    if (getCheckPoint() == 0) {
                         int idx = getJoin_Count() - 1;
                         if (RACE.getStartPointLoc() == null || idx < 0 || idx >= RACE.getStartPointLoc().size()) {
                              Player.sendMessage(CollarMessage.setWarning() + "Respawn start point not available");
                              Player.teleport(getst_Location());
                         } else {
                              Player.teleport(RACE.getStartPointLoc().get(idx).getLocation());
                         }
                    } else {
                         int cpIdx = getCheckPoint() - 1;
                         if (RACE.getCheckPointLoc() == null || cpIdx < 0 || cpIdx >= RACE.getCheckPointLoc().size()) {
                              Player.sendMessage(CollarMessage.setWarning() + "Checkpoint location not available");
                              Player.teleport(getst_Location());
                         } else {
                              Player.teleport(RACE.getCheckPointLoc().get(cpIdx).getLocation());
                         }
                    }
                    break;
               case BOAT:
                    Entity vehicle = getPlayer().getVehicle();
                    Enter = true;
                    if (vehicle != null)
                         vehicle.remove();
                    if (getCheckPoint() == 0) {
                         int idx = getJoin_Count() - 1;
                         if (RACE.getStartPointLoc() == null || idx < 0 || idx >= RACE.getStartPointLoc().size()) {
                              Player.sendMessage(
                                        CollarMessage.setWarning() + "Respawn start point not available for boat");
                              break;
                         }
                         if (RACE.getStartPointLoc().get(idx) != null) {
                              Location spawnLoc = RACE.getStartPointLoc().get(idx).getLocation();
                              if (spawnLoc != null) {
                                   org.bukkit.World world = spawnLoc.getWorld();
                                   if (world != null) {
                                        world.spawnEntity(spawnLoc, EntityType.BOAT).addPassenger(Player);
                                   }
                              }
                         }
                    } else {
                         int cpIdx = getCheckPoint() - 1;
                         if (RACE.getCheckPointLoc() == null || cpIdx < 0 || cpIdx >= RACE.getCheckPointLoc().size()) {
                              Player.sendMessage(
                                        CollarMessage.setWarning() + "Respawn checkpoint not available for boat");
                              break;
                         }
                         if (RACE.getCheckPointLoc().get(cpIdx) != null) {
                              Location cpLoc = RACE.getCheckPointLoc().get(cpIdx).getLocation();
                              if (cpLoc != null) {
                                   org.bukkit.World world = cpLoc.getWorld();
                                   if (world != null) {
                                        world.spawnEntity(cpLoc, EntityType.BOAT).addPassenger(Player);
                                   }
                              }
                         }
                    }
                    break;
          }

          Player.sendMessage(CollarMessage.setInfo() + "Respawn");
     }

     public void Goal() {
          Race RACE = Race_Core.getRace(Race_ID);
          if (RACE == null)
               return;
          this.end_time = System.currentTimeMillis();

          getPlayer().playSound(Player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
          getPlayer().sendMessage(CollarMessage.setInfo() + "GOAL!!");
          java.util.List<Race_Runner> __runners_goal = Race_Core.Race_Run.get(RACE.getUUID());
          if (__runners_goal != null) {
               for (Race_Runner val : __runners_goal) {
                    val.getPlayer().sendMessage(CollarMessage.setInfo() + "[" + ChatColor.AQUA + Player.getName()
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
          java.util.List<Race_Runner> __runners_goal_check = Race_Core.Race_Run.get(RACE.getUUID());
          if (__runners_goal_check != null) {
               for (Race_Runner val : __runners_goal_check)
                    if (val.getMode() == Race_Runner_Mode.ALL_GOAL_WAIT)
                         i++;
               if (i == __runners_goal_check.size())
                    Race_Core.AllGoal(RACE.getUUID());
          }
     }

     public void Complete() {
          nullRace();
          this.Race_mode = Race_Runner_Mode.NO_ENTRY;
          Player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
     }

     public void nullRace() {
          Race_ID = null;
     }
}
