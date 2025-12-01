package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.DisplaySlot;
import waterpunch.atamamozi_d.plugin.main.Core;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Runner_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.score.Player_Score_Core;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.Timers.Leave_Timer;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer_Type;

/**
 * レースのコアロジッククラス
 * レースデータ、ランナー、タイマーの管理を行う
 * 
 * Core logic class for races
 * Manages race data, runners, and timers
 */
public class Race_Core {

     /** 登録済みレースのリスト / List of registered races */
     public static ArrayList<Race> Race_list = new ArrayList<>();
     
     /** ランナーリスト（互換性のため保持） / Runner list (kept for compatibility) */
     // Keep a list for iteration compat and also a map for fast lookups by player UUID
     public static ArrayList<Race_Runner> Race_Runner_List = new ArrayList<>();
     
     /** ランナーの高速検索用マップ / Map for fast runner lookup */
     public static Map<UUID, Race_Runner> Race_Runner_Map = new ConcurrentHashMap<>();
     
     /** レースパッケージリスト / List of race packages */
     public static ArrayList<Race_Package> Race_packages = new ArrayList<>();
     
     /** 実行中レースとそのランナーのマップ / Map of running races and their runners */
     public static LinkedHashMap<UUID, ArrayList<Race_Runner>> Race_Run = new LinkedHashMap<>();
     
     /** アクティブなタイマーリスト / List of active timers */
     public static ArrayList<Race_Timer> Timers = new ArrayList<>();
     
     /** トップメニューデータ / Top menu data */
     public static LinkedHashMap<Integer, ArrayList<Inventory>> TOP_MENU = new LinkedHashMap<>();

     /**
      * レースを登録する
      * 
      * Registers a race
      * 
      * @param Race 登録するレース / Race to register
      */
     public static void addRace(Race Race) {
          Race_list.add(Race);
          Race_packages.add(new Race_Package(Race.getUUID()));
     }

     /**
      * プレイヤーをレースに参加させる
      * レースのモードに応じた処理を行う
      * 
      * Makes a player join a race
      * Processes according to race mode
      * 
      * @param Race 参加するレース / Race to join
      * @param player 参加するプレイヤー / Player joining
      */
     public static void joinRace(Race Race, Player player) {
          // Ensure a Race_Runner exists for the player. PlayerJoin usually creates one
          // but be defensive.
          Race_Runner run = getRunner(player);
          if (run == null) {
               // create a runner for safety so subsequent code does not NPE
               run = new Race_Runner(player);
          }
          if (isJoin(player)) {
               player.sendMessage(CollarMessage.setWarning() + "Already join race");
               return;
          }
          // if (Race_Run.get(Race.getUUID()).size() == Race.getJoin_Amount()) {
          // player.sendMessage(CollarMessage.setInfo() + "MAX Player");
          // return;
          // }

          if (!Race_Run.containsKey(Race.getUUID()))
               Race_Run.put(Race.getUUID(), new ArrayList<Race_Runner>());

          switch (Race.getMode()) {
               case WAIT:
                    for (Race_Runner run_ : Race_Run.get(Race.getUUID()))
                         if (run_.getPlayer().getUniqueId().equals(run.getPlayer().getUniqueId())) {
                              player.sendMessage(CollarMessage.setWarning() + "Already join race");
                              return;
                         }
                    if (Race_Run.get(Race.getUUID()).size() == Race.getJoin_Amount()) {
                         player.sendMessage(CollarMessage.setInfo() + "MAX Player");
                         return;
                    }
                    if (run.UPDate(Race.getUUID()))
                         JoinMesseage(Race, player);
                    break;
               case RUN:
                    player.sendMessage(CollarMessage.setInfo() + Race.getRace_name() + " is Active Race Please wait");
                    break;
               case GOAL:
                    Race.setMode(Race_Mode.WAIT);
                    joinRace(Race, player);
                    break;
               case EDIT:
                    player.sendMessage(CollarMessage.setInfo() + Race.getRace_name() + " is EDIT now");
                    player.sendMessage(CollarMessage.setInfo() + Race.getRace_name() + " Give it some time to try.");
                    break;
               default:
                    break;
          }
     }

     /**
      * プレイヤーをレースから削除する
      * スコアボードのクリアとテレポート処理を行う
      * 
      * Removes a player from a race
      * Clears scoreboard and handles teleportation
      * 
      * @param player 削除するプレイヤー / Player to remove
      */
     public static void removeRunner(Player player) {
          // If the player is not in a race or runner doesn't exist, clear scoreboard if
          // present and return.
          if (!isJoin(player)) {
               if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null)
                    if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getDisplayName()
                              .equals("Atamamozi_" + ChatColor.RED + "D"))
                         player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
               return;
          }

          Race_Runner run = getRunner(player);
          if (run == null)
               return; // defensive: nothing else to do

          switch (run.getMode()) {
               case NO_ENTRY:
                    run.getPlayer().sendMessage(CollarMessage.setInfo() + "Not join the race");
                    return;
               case EDIT:
                    Race_list.remove(getRace(run.getRaceID()));
                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    player.sendMessage(CollarMessage.setInfo() + "Leave the race");
                    run.Complete();
                    break;
               case WAIT:
               case RUN:
               case ALL_GOAL_WAIT:
                    run.getPlayer().sendMessage(CollarMessage.setInfo() + "Leave the race");
                    RemoveCar(run.getPlayer());
                    run.getPlayer().teleport(run.getst_Location());

                    if (Race_Run.get(run.getRaceID()) != null && Race_Run.get(run.getRaceID()).size() == 1) {
                         if (Timers.isEmpty())
                              return;
                         for (int i = 0; i < Timers.size(); i++)
                              if (Timers.get(i).getUUID().equals(run.getRaceID()))
                                   Timers.get(i).stop();
                    }

                    for (UUID a : Race_Run.keySet())
                         if (run.getRaceID().equals(a) && Race_Run.get(a) != null)
                              Race_Run.get(a).remove(run);
                    LeaveMesseage(getRace(run.getRaceID()), run.getPlayer());
                    int GOAL = 0;
                    java.util.List<Race_Runner> __runners_after_leave = Race_Run.get(run.getRaceID());
                    if (__runners_after_leave != null) {
                         for (Race_Runner val : __runners_after_leave)
                              if (val.getMode() == Race_Runner_Mode.ALL_GOAL_WAIT)
                                   GOAL++;
                    }
                    if (__runners_after_leave != null && GOAL == __runners_after_leave.size())
                         AllGoal(run.getRaceID());

                    run.Complete();
                    break;
               default:
                    break;
          }
     }

     /**
      * 全員ゴール後の処理を行う
      * スコアを表示し、ランキングをソートする
      * 
      * Handles post-race processing when all runners finish
      * Displays scores and sorts rankings
      * 
      * @param Race_ID レースのUUID / Race UUID
      */
     public static void AllGoal(UUID Race_ID) {
          Race RACE = Race_Core.getRace(Race_ID);
          if (RACE == null)
               return;
          ArrayList<Player> Players = new ArrayList<>();

          Comparator<Race_Runner> comparator = Comparator.comparing(Race_Runner::getTime).reversed();
          java.util.List<Race_Runner> __runners_allgoal = Race_Run.get(RACE.getUUID());
          if (__runners_allgoal != null) {
               __runners_allgoal.stream().sorted(comparator);
               Collections.reverse(__runners_allgoal);

               for (Race_Runner val : __runners_allgoal) {
                    val.UpdateScoreboard();
                    sayScore(val, getRace(Race_ID).getRace_name());
                    Players.add(val.getPlayer());
                    val.setMode(Race_Runner_Mode.NO_ENTRY);
                    new Leave_Timer(val.getPlayer()).runTaskTimer(Core.getthis(), 0L, 20L);
               }
          }
          Player_Score_Core.SortRanking(Race_ID);

          Race_Goal(RACE.getUUID());
          RACE.Complete();
          Race_Run.remove(RACE.getUUID());
     }

     /**
      * プレイヤーがレースに参加中かどうかを確認する
      * 
      * Checks if a player is currently in a race
      * 
      * @param player 確認するプレイヤー / Player to check
      * @return 参加中ならtrue / True if in race
      */
     public static boolean isJoin(Player player) {
          Race_Runner runner = getRunner(player);
          if (runner == null)
               return false;
          return runner.getMode() != Race_Runner_Mode.NO_ENTRY;
     }

     /**
      * レース参加メッセージを全参加者に送信する
      * 
      * Sends join message to all race participants
      * 
      * @param race レース / Race
      * @param player 参加したプレイヤー / Player who joined
      */
     public static void JoinMesseage(Race race, Player player) {
          java.util.List<Race_Runner> __join_runners = Race_Run.get(race.getUUID());
          if (__join_runners == null)
               return;
          final int __join_count = __join_runners.size();
          for (Race_Runner runner : __join_runners) {
               runner.getPlayer()
                         .sendMessage(CollarMessage.setInfo() + " " + __join_count + "/"
                                   + race.getJoin_Amount() + " : [" + ChatColor.AQUA + player.getName()
                                   + ChatColor.WHITE + "] is Join");
               new Race_Timer(Race_Timer_Type.WAIT, race.getUUID()).runTaskTimer(Core.getthis(), 0L, 20L);
               runner.UpdateScoreboard();
          }
     }

     /**
      * レース離脱メッセージを全参加者に送信する
      * 
      * Sends leave message to all race participants
      * 
      * @param race レース / Race
      * @param player 離脱したプレイヤー / Player who left
      */
     public static void LeaveMesseage(Race race, Player player) {
          java.util.List<Race_Runner> __leave_runners = Race_Run.get(race.getUUID());
          if (__leave_runners == null)
               return;
          final int __leave_count = __leave_runners.size();
          for (Race_Runner runner : __leave_runners) {
               runner.getPlayer()
                         .sendMessage(CollarMessage.setInfo() + " " + __leave_count + "/"
                                   + race.getJoin_Amount() + " : [" + ChatColor.AQUA + player.getName()
                                   + ChatColor.WHITE + "] is Leave");
               runner.UpdateScoreboard();
          }
     }

     /**
      * プレイヤーの乗り物（ボート）を削除する
      * 
      * Removes player's vehicle (boat)
      * 
      * @param player 対象プレイヤー / Target player
      */
     public static void RemoveCar(Player player) {
          Race_Runner run = getRunner(player);
          if (run == null)
               return;
          Race race = getRace(run.getRaceID());
          if (race == null)
               return;
          if (race.getRace_Type() == Race_Type.BOAT && player.getVehicle() != null) {
               run.setEnter(false);
               player.getVehicle().remove();
          }
     }

     /**
      * プレイヤーに対応するランナーを取得する
      * まずマップから高速検索し、見つからなければリストを走査する
      * 
      * Gets the runner for a player
      * First checks map for fast lookup, then scans list if not found
      * 
      * @param player プレイヤー / Player
      * @return ランナー、または見つからない場合はnull / Runner, or null if not found
      */
     public static Race_Runner getRunner(Player player) {
          if (player == null)
               return null;
          // fast map-based lookup
          Race_Runner mapped = Race_Runner_Map.get(player.getUniqueId());
          if (mapped != null)
               return mapped;

          // fallback to list (compat) and populate map when found
          if (Race_Runner_List.isEmpty())
               return null;
          for (Race_Runner val : Race_Runner_List) {
               if (val.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                    Race_Runner_Map.put(player.getUniqueId(), val);
                    return val;
               }
          }
          return null;
     }

     /**
      * レース名からレースを取得する
      * 
      * Gets a race by name
      * 
      * @param race_st レース名 / Race name
      * @return レース、または見つからない場合はnull / Race, or null if not found
      */
     public static Race getRace(String race_st) {
          if (Race_list.isEmpty())
               return null;
          for (Race val : Race_list)
               if (val.getRace_name().equals(race_st))
                    return val;
          return null;
     }

     /**
      * UUIDからレースを取得する
      * 
      * Gets a race by UUID
      * 
      * @param race_uu レースのUUID / Race UUID
      * @return レース、または見つからない場合はnull / Race, or null if not found
      */
     public static Race getRace(UUID race_uu) {
          for (Race val : Race_list)
               if (val.getUUID().equals(race_uu))
                    return val;
          return null;
     }

     /**
      * レースを開始する
      * モードに応じた開始処理を行う
      * 
      * Starts a race
      * Handles start processing according to mode
      * 
      * @param Race_UUID レースのUUID / Race UUID
      */
     public static void Race_Start(UUID Race_UUID) {
          Race race = getRace(Race_UUID);
          if (race == null)
               return;
          switch (race.getMode()) {
               case WAIT:
                    for (UUID key : Race_Run.keySet()) {
                         if (Race_UUID != null && Race_UUID.equals(key)) {
                              java.util.List<Race_Runner> __runners_start = Race_Run.get(key);
                              if (__runners_start == null)
                                   continue;
                              for (Race_Runner val : __runners_start)
                                   val.Start();
                         }
                    }
                    getRace(Race_UUID).setMode(Race_Mode.RUN);
                    break;
               case EDIT:
                    for (UUID key : Race_Run.keySet())
                         if (Race_UUID != null && Race_UUID.equals(key)) {
                              java.util.List<Race_Runner> __runners_edit = Race_Run.get(key);
                              if (__runners_edit == null)
                                   continue;
                              for (Race_Runner val : __runners_edit)
                                   if (val.getMode() == Race_Runner_Mode.EDIT)
                                        val.getPlayer().sendMessage(CollarMessage.setInfo()
                                                  + getRace(Race_UUID).getRace_name() + " is EDIT now");
                              return;
                         }
                    break;
               case GOAL:
                    for (UUID key : Race_Run.keySet())
                         if (Race_UUID != null && Race_UUID.equals(key)) {
                              java.util.List<Race_Runner> __runners_end = Race_Run.get(key);
                              if (__runners_end == null)
                                   continue;
                              for (Race_Runner val : __runners_end)
                                   if (val.getMode() == Race_Runner_Mode.EDIT)
                                        val.getPlayer().sendMessage(CollarMessage.setInfo()
                                                  + getRace(Race_UUID).getRace_name() + " is End");
                              return;
                         }
                    break;
               case RUN:
                    for (UUID key : Race_Run.keySet())
                         if (Race_UUID != null && Race_UUID.equals(key)) {
                              java.util.List<Race_Runner> __runners_active = Race_Run.get(key);
                              if (__runners_active == null)
                                   continue;
                              for (Race_Runner val : __runners_active)
                                   if (val.getMode() == Race_Runner_Mode.EDIT)
                                        val.getPlayer().sendMessage(CollarMessage.setInfo()
                                                  + getRace(Race_UUID).getRace_name() + " is Active Race Please wait");
                              return;
                         }
                    break;
               default:
                    break;
          }
     }

     /**
      * ランナーにスコアを表示する
      * 
      * Displays score to a runner
      * 
      * @param val ランナー / Runner
      * @param RACE_NAME レース名 / Race name
      */
     public static void sayScore(Race_Runner val, String RACE_NAME) {
          val.getPlayer().sendMessage(
                    "------------" + "Atamamozi_" + ChatColor.RED + "D" + ChatColor.WHITE + "------------");
          val.getPlayer().sendMessage("[" + ChatColor.GREEN + RACE_NAME + ChatColor.WHITE + "]");
          java.util.List<Race_Runner> __runners_say = Race_Run.get(val.getRaceID());
          if (__runners_say != null) {
               for (Race_Runner sc : __runners_say) {
                    if (sc.getPlayer().getUniqueId().equals(val.getPlayer().getUniqueId())) {
                         val.getPlayer().sendMessage("[" + ChatColor.AQUA + sc.getPlayer().getName() + ChatColor.WHITE
                                   + "] : " + ChatColor.YELLOW + sc.getTimest());
                    } else {
                         val.getPlayer().sendMessage("[" + ChatColor.AQUA + sc.getPlayer().getName() + ChatColor.WHITE
                                   + "] : " + sc.getTimest());
                    }
               }
          }

          val.getPlayer().sendMessage(
                    "------------" + "Atamamozi_" + ChatColor.RED + "D" + ChatColor.WHITE + "------------");
          val.getPlayer()
                    .sendMessage(CollarMessage.setInfo() + "Race leave is " + ChatColor.LIGHT_PURPLE + "/atd leave");
     }

     /**
      * レースをゴール状態に設定する
      * 
      * Sets a race to goal state
      * 
      * @param Race_UUID レースのUUID / Race UUID
      */
     public static void Race_Goal(UUID Race_UUID) {
          for (UUID key : Race_Run.keySet())
               if (Race_UUID != null && Race_UUID.equals(key)) {
                    getRace(Race_UUID).setMode(Race_Mode.GOAL);
                    return;
               }
     }

     /**
      * 全てのレースデータとランナーをクリアする
      * プラグイン無効化時に呼び出される
      * 
      * Clears all race data and runners
      * Called when plugin is disabled
      */
     public static void clear() {
          Race_Run.clear();
          Race_Runner_Map.clear();
          if (!Race_Runner_List.isEmpty())
               for (Race_Runner val : Race_Runner_List)
                    if (isJoin(val.getPlayer())) {
                         val.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                         RemoveCar(val.getPlayer());
                    }

          Bukkit.getLogger().info(CollarMessage.setInfo() + "Atamamozi_D Memory clear");
     }
}
