package waterpunch.atamamozi_d.plugin.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
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
 * Atamamozi_D プラグインのメインクラス
 * Minecraftサーバーでレースゲームを管理するプラグイン
 * 
 * Main class for the Atamamozi_D plugin
 * Plugin that manages race games on Minecraft servers
 */
public class Core extends JavaPlugin {

     /** プラグインインスタンスへの静的参照 / Static reference to plugin instance */
     static Plugin Data;
     
     /** 設定値: 待機時間、スタート時間、余韻時間、離脱時間、メニューランキング表示数 */
     /** Config values: wait time, start time, lingering time, leave time, menu ranking display count */
     public static int WAIT_TIME, START_TIME, YOIN_TIME, LEAVE_TIME, MENU_RANK_VIEW;

     /**
      * プラグイン有効化時の処理
      * 設定ファイルの読み込み、イベントリスナーの登録、データのロードを行う
      * 
      * Called when the plugin is enabled
      * Loads config files, registers event listeners, and loads data
      */
     @Override
     public void onEnable() {
          Bukkit.getLogger().info("ATAMAMOZI-D ENGINE START");

          saveDefaultConfig();
          getConfig();

          // 設定値の読み込み / Load configuration values
          WAIT_TIME = getConfig().getInt("Setting.CountDown.WAIT");
          START_TIME = getConfig().getInt("Setting.CountDown.START");
          YOIN_TIME = getConfig().getInt("Setting.CountDown.YOIN");
          LEAVE_TIME = getConfig().getInt("Setting.CountDown.LEAVE");
          MENU_RANK_VIEW = getConfig().getInt("Setting.MENU_RANK_VIEW");
          
          // デフォルト設定の保存（存在しない場合）
          // Save default settings (if not present)
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
          Data = this;
          
          // イベントリスナー登録 / Register event listener
          new Event(this);
          
          // データ読み込み / Load data
          Main.loadData();
          
          // オンラインプレイヤーの初期化
          // Initialize online players
          for (Player p : this.getServer().getOnlinePlayers()) {
               if (p.getOpenInventory().getTitle().equals("RACE_CREATE"))
                    p.closeInventory();
               new Race_Runner(p);
               if (p.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null)
                    if (p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getDisplayName()
                              .equals("Atamamozi_" + ChatColor.RED + "D"))
                         p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
          }
     }

     /**
      * プラグイン無効化時の処理
      * プレイヤースコアを保存し、メモリをクリアする
      * 
      * Called when the plugin is disabled
      * Saves player scores and clears memory
      */
     @Override
     public void onDisable() {
          Bukkit.getLogger().info("ATAMAMOZI-D ENGINE STOP");
          // 全プレイヤースコアを保存 / Save all player scores
          for (Player_Score ps : waterpunch.atamamozi_d.plugin.score.Player_Score_Core.Score)
               CreateJson.Scoresave(ps);
          Race_Core.clear();
     }

     /**
      * プラグインインスタンスを取得する
      * 
      * Gets the plugin instance
      * 
      * @return プラグインインスタンス / Plugin instance
      */
     public static Plugin getthis() {
          return Data;
     }

     /**
      * プラグインコマンドを処理する
      * /atamamozi_d または /atd コマンドのハンドラー
      * 
      * Handles plugin commands
      * Handler for /atamamozi_d or /atd commands
      * 
      * @param sender コマンド送信者 / Command sender
      * @param cmd コマンド / Command
      * @param commandLabel コマンドラベル / Command label
      * @param args コマンド引数 / Command arguments
      * @return 常にfalseを返す / Always returns false
      */
     @Override
     public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
          // プレイヤー以外からのコマンドは無視
          // Ignore commands from non-players
          if (!(sender instanceof Player))
               return false;
          if (args.length == 0) {
               ((Player) sender).openInventory(Menus.getTop((Player) sender));
               return false;
          }
          Race_Runner run = null;
          switch (args[0]) {
               case "help":
                    onhelp((Player) sender);
                    break;
               case "view":
                    // デバッグ用: レース実行状況を表示
                    // Debug: display race run status
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
                    // レース名を設定
                    // Set race name
                    if (args.length == 1) {
                         ((Player) sender).sendMessage(CollarMessage.setWarning() + "Need Name");
                         return false;
                    }
                    run = Race_Core.getRunner((Player) sender);
                    if (run.getMode() == Race_Runner_Mode.EDIT) {
                         if (args[1].equals("[ER]")) {
                              run.getPlayer().sendMessage(CollarMessage.setWarning() + "NG Word");
                              return false;
                         }
                         Race_Core.getRace(run.getRaceID()).setRace_name(args[1]);
                         run.getPlayer().openInventory(Menus.getRaceCreate(run.getPlayer()));
                         run.UpdateScoreboard();
                    }
                    break;
               case "addStartPoint":
               case "addstartpoint":
                    onaddStartpoint((Player) sender);
                    break;
               case "addCheckPoint":
               case "addcheckpoint":
                    // チェックポイントを追加（半径指定必須）
                    // Add checkpoint (radius required)
                    if (args.length == 1) {
                         ((Player) sender).sendMessage(CollarMessage.setWarning() + "Please int");
                         return false;
                    }
                    onaddCheckpoint((Player) sender, args[1]);
                    break;
               case "start":
                    // レースを開始
                    // Start race
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
                    // レースに参加
                    // Join race
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

     /**
      * タブ補完候補を返す
      * プレイヤーのモードに応じて適切なコマンドを提案
      * 
      * Returns tab completion suggestions
      * Suggests appropriate commands based on player's mode
      */
     @Override
     public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
          Race_Runner r = Race_Core.getRunner((Player) sender);
          ArrayList<String> subcmd = new ArrayList<String>();

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
      * ヘルプメッセージを表示する
      * 
      * Displays help message
      * 
      * @param player 対象プレイヤー / Target player
      */
     void onhelp(Player player) {
          player.sendMessage("---------------------");
          player.sendMessage("[help] this messeage");
          player.sendMessage("[list] /atamamozi_d list Open Race menu");
          player.sendMessage("[respawn] /atamamozi_d respawn :)");
          player.sendMessage("[leave] leave player");
          player.sendMessage("---------------------");
     }

     /** データ読み込み処理（未実装） / Load data (not implemented) */
     void onload(Player player) {
     }

     /** 停止処理（未実装） / Stop process (not implemented) */
     void onstop(Player player) {
     }

     /**
      * プレイヤーをレースから離脱させる
      * 
      * Makes player leave the race
      * 
      * @param player 離脱するプレイヤー / Player to leave
      */
     void onleave(Player player) {
          Race_Core.removeRunner(player);
     }

     /**
      * スタートポイントを追加する
      * 編集モードのプレイヤーのみ使用可能
      * 
      * Adds a start point
      * Only available for players in edit mode
      * 
      * @param player 追加するプレイヤー / Player adding the point
      */
     void onaddStartpoint(Player player) {
          Race_Runner run = Race_Core.getRunner(player);
          if (run == null || run.getMode() != Race_Runner_Mode.EDIT)
               return;

          Race_Core.getRace(run.getRaceID()).addStartPointLoc(player.getLocation());
          player.sendMessage(CollarMessage.setInfo() + "Set Start Point");
          waterpunch.atamamozi_d.plugin.race.export.Hachitai.setCircle(run, player.getLocation(), 1);
          run.UpdateScoreboard();
     }

     /**
      * チェックポイントを追加する
      * 半径を指定して現在位置にチェックポイントを設定
      * 
      * Adds a checkpoint
      * Sets checkpoint at current position with specified radius
      * 
      * @param player 追加するプレイヤー / Player adding the checkpoint
      * @param r 半径（文字列） / Radius (as string)
      */
     void onaddCheckpoint(Player player, String r) {
          Race_Runner run = Race_Core.getRunner(player);
          if (run == null || run.getMode() != Race_Runner_Mode.EDIT)
               return;
          try {
               if (Integer.parseInt(r) <= 0) {
                    player.sendMessage(CollarMessage.setWarning() + "Please enter Over 0");
                    return;
               }
               Race_Core.getRace(run.getRaceID()).addCheckPointLoc(player.getLocation(), Integer.parseInt(r));

               player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
               player.sendMessage(CollarMessage.setInfo() + "Set Check Point");
          } catch (NumberFormatException xr) {
               player.sendMessage(
                         CollarMessage.setWarning() + "<" + ChatColor.RED + r + ChatColor.GOLD + "> is Not Number");
          }
          run.UpdateScoreboard();
     }

     /**
      * チェックポイントを設定/更新する
      * 
      * Sets or updates a checkpoint
      * 
      * @param player プレイヤー / Player
      * @param r 半径 / Radius
      * @param no チェックポイント番号 / Checkpoint number
      */
     void onsetCheckPoint(Player player, int r, int no) {
          Race_Runner run = Race_Core.getRunner(player);
          if (run == null || run.getMode() != Race_Runner_Mode.EDIT)
               return;
          if (Race_Core.getRace(run.getRaceID()).getCheckPointLoc().size() == 0) {
               Race_Core.getRace(run.getRaceID()).addCheckPointLoc(player.getLocation(), r);
          } else {
               Race_Core.getRace(run.getRaceID()).getCheckPointLoc().set(no,
                         new CheckPointLoc(player.getLocation(), r));
          }
          run.UpdateScoreboard();
     }

     /**
      * プレイヤーを最後のチェックポイントにリスポーンさせる
      * 
      * Respawns player at last checkpoint
      * 
      * @param player リスポーンするプレイヤー / Player to respawn
      */
     void onrespawn(Player player) {
          Race_Runner run = Race_Core.getRunner(player);
          if (run == null || run.getMode() == Race_Runner_Mode.EDIT) {
               player.sendMessage(CollarMessage.setInfo() + "You not join race");
               return;
          }
          run.ReSpawn();
          return;
     }

     /**
      * プレイヤーをレースに参加させる
      * 
      * Makes player join a race
      * 
      * @param player 参加するプレイヤー / Player joining
      * @param args レース名 / Race name
      */
     private void onjoin(Player player, String args) {
          Race race = Race_Core.getRace(args);
          if (race == null)
               return;
          Race_Core.joinRace(race, player);
     }

     /**
      * チェックポイントを削除する
      * 
      * Removes a checkpoint
      * 
      * @param player プレイヤー / Player
      * @param no 削除するチェックポイント番号 / Checkpoint number to remove
      */
     void remCheckPoint(Player player, int no) {
          Race_Runner run = Race_Core.getRunner(player);
          if (run == null || run.getMode() != Race_Runner_Mode.EDIT)
               return;
          Race_Core.getRace(run.getRaceID()).getCheckPointLoc().remove(no);
          run.UpdateScoreboard();
     }
}
