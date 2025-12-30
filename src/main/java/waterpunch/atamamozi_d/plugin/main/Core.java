package waterpunch.atamamozi_d.plugin.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import waterpunch.atamamozi_d.plugin.event.Event;
import waterpunch.atamamozi_d.plugin.menus.Menus;
import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.Race_Core;
import waterpunch.atamamozi_d.plugin.race.Race_Runner;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Runner_Mode;
import waterpunch.atamamozi_d.plugin.score.Player_Score;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.CreateJson;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer_Type;

/**
 * ATAMAMOZI-D Racing Plugin - Core Plugin Class
 * 
 * <p>
 * This plugin provides a comprehensive racing system for Minecraft servers,
 * supporting both walking and boat races with checkpoint management,
 * scoreboard tracking, and player ranking features.
 * </p>
 * 
 * @author waterpunch
 * @version 0.1
 */
public class Core extends JavaPlugin {

     /** Plugin instance reference for static access */
     static Plugin Data;

     /** Global timing configuration values (in seconds) */
     public static int WAIT_TIME, START_TIME, YOIN_TIME, LEAVE_TIME, MENU_RANK_VIEW;

     /**
      * Plugin initialization - loads configuration, registers events, and restores
      * player states.
      * Called automatically by Bukkit when the plugin is enabled.
      */
     @Override
     public void onEnable() {
          Bukkit.getLogger().info("ATAMAMOZI-D ENGINE START");

          // Load or create config file
          saveDefaultConfig();

          // Set default values for missing config keys BEFORE reading them
          if (!getConfig().contains("Setting.CountDown.WAIT"))
               getConfig().set("Setting.CountDown.WAIT", 30);
          if (!getConfig().contains("Setting.CountDown.START"))
               getConfig().set("Setting.CountDown.START", 5);
          if (!getConfig().contains("Setting.CountDown.YOIN"))
               getConfig().set("Setting.CountDown.YOIN", 5);
          if (!getConfig().contains("Setting.CountDown.LEAVE"))
               getConfig().set("Setting.CountDown.LEAVE", 10);
          if (!getConfig().contains("Setting.CountDown.MENU_RANK_VIEW"))
               getConfig().set("Setting.CountDown.MENU_RANK_VIEW", 20);
          this.saveConfig();

          // Now read the configuration values (with defaults applied)
          WAIT_TIME = getConfig().getInt("Setting.CountDown.WAIT");
          START_TIME = getConfig().getInt("Setting.CountDown.START");
          YOIN_TIME = getConfig().getInt("Setting.CountDown.YOIN");
          LEAVE_TIME = getConfig().getInt("Setting.CountDown.LEAVE");
          MENU_RANK_VIEW = getConfig().getInt("Setting.MENU_RANK_VIEW");

          // Store plugin instance for static access
          Data = this;

          // Register event listeners
          @SuppressWarnings("unused")
          Event eventListener = new Event(this);

          // Load saved race and score data from JSON files
          Main.loadData();

          // Initialize Race_Runner for all online players (handles plugin reload case)
          for (Player p : this.getServer().getOnlinePlayers()) {
               // Close any race creation menus that may be open from previous session
               if (p.getOpenInventory().getTitle().equals("RACE_CREATE"))
                    p.closeInventory();

               // Create runner instance for player tracking
               @SuppressWarnings("unused")
               Race_Runner runner = new Race_Runner(p);

               // Clear any existing plugin scoreboards from previous session
               Objective sidebar = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
               if (sidebar != null && sidebar.getDisplayName().equals("Atamamozi_" + ChatColor.RED + "D")) {
                    p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               }
          }
     }

     /**
      * Plugin shutdown - saves all player scores and clears race data.
      * Called automatically by Bukkit when the plugin is disabled.
      */
     @Override
     public void onDisable() {
          Bukkit.getLogger().info("ATAMAMOZI-D ENGINE STOP");

          // Save all player scores to JSON files
          for (Player_Score ps : waterpunch.atamamozi_d.plugin.score.Player_Score_Core.Score)
               CreateJson.Scoresave(ps);

          // Clear all active race data and timers
          Race_Core.clear();
     }

     /**
      * Get the plugin instance for static access.
      * 
      * @return The Core plugin instance
      */
     public static Plugin getthis() {
          return Data;
     }

     /**
      * Handle /atd (or /race) commands for race management.
      * 
      * <p>
      * Supported commands:
      * <ul>
      * <li>/atd - Open main race menu</li>
      * <li>/atd list - Show available races</li>
      * <li>/atd create - Create new race</li>
      * <li>/atd join [name] - Join a race</li>
      * <li>/atd leave - Leave current race</li>
      * <li>/atd start - Start countdown (race creator only)</li>
      * <li>/atd respawn - Respawn at last checkpoint</li>
      * <li>/atd rank - View ranking</li>
      * </ul>
      * 
      * @param sender       Command sender (must be a player)
      * @param cmd          Command object
      * @param commandLabel Command label used
      * @param args         Command arguments
      * @return false (allows default command handling)
      */
     @Override
     public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
          // Only players can use race commands
          if (!(sender instanceof Player))
               return false;

          // No arguments: open main menu
          if (args.length == 0) {
               ((Player) sender).openInventory(Menus.getTop((Player) sender));
               return false;
          }

          Race_Runner run;
          switch (args[0]) {
               case "help":
                    onhelp((Player) sender);
                    break;
               case "view": // Debug command to view race state
                    Bukkit.getLogger().info("----------------------");
                    try {
                         Bukkit.getLogger().info(Collections.singletonList(Race_Core.Race_Run).toString());
                    } catch (Exception ex) {
                         Bukkit.getLogger().log(Level.WARNING, "Failed to log race run data", ex);
                    }
                    Bukkit.getLogger().info("----------------------");
                    break;
               case "list":
                    ((Player) sender).openInventory(Menus.getRaceList(((Player) sender)));
                    break;
               case "leave":
                    onleave((Player) sender);
                    break;
               case "create":
                    ((Player) sender).openInventory(Menus.getRaceCreate(((Player) sender)));
                    break;
               case "setName":
               case "setname":
                    // Set race name during edit mode
                    if (args.length == 1) {
                         ((Player) sender).sendMessage(CollarMessage.setWarning() + "Need Name");
                         return false;
                    }
                    run = Race_Core.getRunner((Player) sender);
                    if (run == null) {
                         ((Player) sender).sendMessage(CollarMessage.setWarning() + "Runner not found");
                         return false;
                    }
                    if (run.getMode() == Race_Runner_Mode.EDIT) {
                         // Prevent error placeholder as race name
                         if (args[1].equals("[ER]")) {
                              run.getPlayer().sendMessage(CollarMessage.setWarning() + "NG Word");
                              return false;
                         }
                         Race race = Race_Core.getRace(run.getRaceID());
                         if (race != null) {
                              race.setRace_name(args[1]);
                              run.getPlayer().openInventory(Menus.getRaceCreate(run.getPlayer()));
                              run.UpdateScoreboard();
                         }
                    }
                    break;
               case "addStartPoint":
               case "addstartpoint":
                    onaddStartpoint((Player) sender);
                    break;
               case "addCheckPoint":
               case "addcheckpoint":
                    if (args.length == 1) {
                         ((Player) sender).sendMessage(CollarMessage.setWarning() + "Please int");
                         return false;
                    }
                    onaddCheckpoint((Player) sender, args[1]);
                    break;
               case "start":
                    run = Race_Core.getRunner((Player) sender);
                    if (run == null)
                         return false;
                    switch (run.getMode()) {
                         case WAIT:
                              new Race_Timer(Race_Timer_Type.START, run.getRaceID()).runTaskTimer(Core.getthis(), 0L,
                                        20L);
                              break;
                         default:
                    }
                    break;
               case "re":
               case "respawn":
                    onrespawn((Player) sender);
                    break;
               case "join":
                    if (args.length == 1) {
                         ((Player) sender).sendMessage(CollarMessage.setWarning() + "Need Race Name");
                         return false;
                    }
                    onjoin((Player) sender, args[1]);
                    break;
               case "rank":
                    ((Player) sender).openInventory(Menus.getRaceRanking(((Player) sender)));
                    break;
               default:
                    onhelp((Player) sender);
                    break;
          }
          return false;
     }

     @Override
     public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
          Race_Runner r = Race_Core.getRunner((Player) sender);
          ArrayList<String> subcmd = new ArrayList<>();

          if (args.length == 1) {
               subcmd.add("leave");
               subcmd.add("list");
               subcmd.add("help");
               subcmd.add("rank");
               switch (r.getMode()) {
                    case EDIT:
                         subcmd.add("addStartPoint");
                         subcmd.add("addCheckPoint");
                         subcmd.add("setName");
                         break;
                    case ALL_GOAL_WAIT:
                         break;
                    case NO_ENTRY:
                         subcmd.add("join");
                         break;
                    case RUN:
                         subcmd.add("respawn");
                         break;
                    case WAIT:
                         subcmd.add("start");
                         subcmd.add("join");
                         break;
                    default:
                         subcmd.add("leave");
                         subcmd.add("list");
                         subcmd.add("help");
                         subcmd.add("rank");
                         break;
               }
          }
          return subcmd;
     }

     /**
      * Display help message with available commands.
      * 
      * @param player Player to send help message to
      */
     void onhelp(Player player) {
          player.sendMessage("---------------------");
          player.sendMessage("[help] this message");
          player.sendMessage("[list] /atamamozi_d list - Open Race menu");
          player.sendMessage("[create] /atamamozi_d create - Create new race");
          player.sendMessage("[join] /atamamozi_d join <race_name> - Join race");
          player.sendMessage("[leave] /atamamozi_d leave - Leave race");
          player.sendMessage("[start] /atamamozi_d start - Start race countdown");
          player.sendMessage("[respawn] /atamamozi_d respawn - Respawn at checkpoint");
          player.sendMessage("[rank] /atamamozi_d rank - View ranking");
          player.sendMessage("[addStartPoint] /atamamozi_d addStartPoint - Set start point (edit mode)");
          player.sendMessage("[addCheckPoint] /atamamozi_d addCheckPoint <radius> - Set checkpoint (edit mode)");
          player.sendMessage("[setName] /atamamozi_d setName <name> - Set race name (edit mode)");
          player.sendMessage("---------------------");
     }

     /**
      * Remove player from current race.
      * 
      * @param player Player to remove from race
      */
     void onleave(Player player) {
          Race_Core.removeRunner(player);
     }

     /**
      * Add a start point at player's current location during race editing.
      * 
      * @param player Player editing the race (must be in EDIT mode)
      */
     void onaddStartpoint(Player player) {
          Race_Runner run = Race_Core.getRunner(player);
          if (run == null || run.getMode() != Race_Runner_Mode.EDIT)
               return;

          Race race = Race_Core.getRace(run.getRaceID());
          if (race == null)
               return;

          race.addStartPointLoc(player.getLocation());
          player.sendMessage(CollarMessage.setInfo() + "Set Start Point");
          waterpunch.atamamozi_d.plugin.race.export.Hachitai.setCircle(run, player.getLocation(), 1);
          run.UpdateScoreboard();
     }

     /**
      * Add a checkpoint at player's current location with specified radius.
      * 
      * @param player Player editing the race (must be in EDIT mode)
      * @param r      Checkpoint radius as string (must be positive integer)
      */
     void onaddCheckpoint(Player player, String r) {
          Race_Runner run = Race_Core.getRunner(player);
          if (run == null || run.getMode() != Race_Runner_Mode.EDIT)
               return;

          Race race = Race_Core.getRace(run.getRaceID());
          if (race == null)
               return;

          try {
               if (Integer.parseInt(r) <= 0) {
                    player.sendMessage(CollarMessage.setWarning() + "Please enter Over 0");
                    return;
               }
               race.addCheckPointLoc(player.getLocation(), Integer.parseInt(r));

               player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               player.sendMessage(CollarMessage.setInfo() + "Set Check Point");
          } catch (NumberFormatException xr) {
               player.sendMessage(
                         CollarMessage.setWarning() + "<" + ChatColor.RED + r + ChatColor.GOLD + "> is Not Number");
          }
          run.UpdateScoreboard();
     }

     /**
      * Modify an existing checkpoint location and radius.
      * 
      * @param player Player editing the race (must be in EDIT mode)
      * @param r      New checkpoint radius
      * @param no     Checkpoint index to modify
      */
     @SuppressWarnings("unused")
     void onsetCheckPoint(Player player, int r, int no) {
          Race_Runner run = Race_Core.getRunner(player);
          if (run == null || run.getMode() != Race_Runner_Mode.EDIT)
               return;

          Race race = Race_Core.getRace(run.getRaceID());
          if (race == null)
               return;

          if (race.getCheckPointLoc().isEmpty()) {
               race.addCheckPointLoc(player.getLocation(), r);
          } else {
               race.getCheckPointLoc().set(no, new CheckPointLoc(player.getLocation(), r));
          }
          run.UpdateScoreboard();
     }

     /**
      * Respawn player at their last checkpoint (or start point if no checkpoint
      * passed).
      * 
      * @param player Player requesting respawn
      */
     void onrespawn(Player player) {
          Race_Runner run = Race_Core.getRunner(player);
          if (run == null || run.getMode() == Race_Runner_Mode.EDIT) {
               player.sendMessage(CollarMessage.setInfo() + "You not join race");
               return;
          }
          run.ReSpawn();
     }

     /**
      * Join a race by name.
      * 
      * @param player Player trying to join
      * @param args   Race name to join
      */
     private void onjoin(Player player, String args) {
          Race race = Race_Core.getRace(args);
          if (race == null)
               return;
          Race_Core.joinRace(race, player);
     }

     /**
      * Remove a checkpoint from the race being edited.
      * 
      * @param player Player editing the race (must be in EDIT mode)
      * @param no     Checkpoint index to remove
      */
     @SuppressWarnings("unused")
     void remCheckPoint(Player player, int no) {
          Race_Runner run = Race_Core.getRunner(player);
          if (run == null || run.getMode() != Race_Runner_Mode.EDIT)
               return;

          Race race = Race_Core.getRace(run.getRaceID());
          if (race == null)
               return;

          race.getCheckPointLoc().remove(no);
          run.UpdateScoreboard();
     }
}
