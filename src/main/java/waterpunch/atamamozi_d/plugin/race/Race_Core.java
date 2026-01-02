package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import waterpunch.atamamozi_d.plugin.main.Core;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Runner_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.score.Player_Score_Core;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tool.CreateJson;
import waterpunch.atamamozi_d.plugin.tool.Timers.Leave_Timer;
import waterpunch.atamamozi_d.plugin.tool.Timers.Race_Timer_Type;

/**
 * レースとレース参加者（ランナー）の中央管理クラス。
 * 
 * <p>
 * <b>責務</b>:
 * </p>
 * <ul>
 * <li>アクティブレースの管理 (addRace, getRace)</li>
 * <li>プレイヤー参加の管理 (joinRace, removeRunner)</li>
 * <li>レースライフサイクルの制御 (Race_Start, AllGoal, Race_Goal)</li>
 * <li>ランナー検索とマップ管理</li>
 * </ul>
 * 
 * <p>
 * <b>改善点 (Ver.1.5):</b>
 * </p>
 * <ul>
 * <li>PascalCase → camelCase フィールド名統一</li>
 * <li>Stream API 活用で重複ロジック削減</li>
 * <li>Optional パターンで null 安全性向上</li>
 * <li>RaceBuilder との統合で immutable Race_Package 実装</li>
 * </ul>
 * 
 * @author waterpunch
 */
public class Race_Core {

     // ================== コレクション管理 ==================

     /** 全アクティブレースのリスト */
     private static final ArrayList<Race> raceList = new ArrayList<>();

     /** プレイヤーUUID → ランナー高速検索マップ */
     private static final HashMap<UUID, Race_Runner> raceRunnerMap = new HashMap<>();

     /** レースUUID → 参加ランナーリスト */
     private static final HashMap<UUID, ArrayList<Race_Runner>> raceRunners = new HashMap<>();

     // ================== RaceBuilder との統合メソッド ==================

     /**
      * RaceBuilderで作成されたRaceオブジェクトをシステムに登録します。
      * 
      * <p>
      * <b>フロー</b>:
      * </p>
      * 
      * <pre>
      * RaceBuilder builder = RaceBuilderSession.startSession(player);
      * builder.name("MyRace").type(Race_Type.BOAT).icon(Material.BOAT);
      * Race race = builder.build();
      * Race_Core.addRace(race); // ← このメソッド
      * </pre>
      * 
      * @param race RaceBuilder で構築された Race オブジェクト
      */
     public static void addRace(Race race) {
          if (race == null) {
               Bukkit.getLogger().warning("Race_Core: null race を登録しようとしました");
               return;
          }

          // 既に同じIDのレースが存在しないかチェック
          if (raceList.stream().anyMatch(r -> r.getRace_ID().equals(race.getRace_ID()))) {
               Bukkit.getLogger()
                         .warning("Race_Core: 既に ID " + race.getRace_ID() + " のレースが存在します");
               return;
          }

          // リストに追加
          raceList.add(race);

          // 永続化: Race_Package をJSON保存
          Race_Package pkg = new Race_Package(
                    race.getRace_ID(),
                    race.getCreator(),
                    race.getRace_name(),
                    race.getJoin_Amount(),
                    race.getRap(),
                    race.getRace_Type(),
                    race.getIcon());
          CreateJson.saveRacePackage(pkg); // <- 要実装

          Bukkit.getLogger()
                    .info("レース登録: " + race.getRace_name() + " (ID: " + race.getRace_ID() + ")");
     }

     // ================== 参加・退出の管理 ==================

     /**
      * プレイヤーがレースに参加します。
      * 
      * <p>
      * <b>参加フロー</b>:
      * </p>
      * <ol>
      * <li>Race_Runner オブジェクト生成</li>
      * <li>raceRunnerMap に追加</li>
      * <li>raceRunners に紐付け</li>
      * <li>他の参加者に通知メッセージ送信</li>
      * <li>スコアボード更新</li>
      * </ol>
      * 
      * @param race   参加対象のレース
      * @param player 参加プレイヤー
      */
     public static void joinRace(Race race, Player player) {
          if (race == null || player == null) {
               return;
          }

          // 既に参加していないかチェック
          if (getRunner(player) != null && getRunner(player).getRaceID().equals(race.getRace_ID())) {
               player.sendMessage(CollarMessage.setWarning() + "既にこのレースに参加しています");
               return;
          }

          // 参加人数チェック
          ArrayList<Race_Runner> currentParticipants = raceRunners.getOrDefault(race.getRace_ID(),
                    new ArrayList<>());
          if (currentParticipants.size() >= race.getJoin_Amount()) {
               player.sendMessage(CollarMessage.setWarning() + "参加定員に達しています");
               return;
          }

          // Race_Runner 生成
          Race_Runner runner = new Race_Runner(player, race.getRace_ID(), race.getRace_Type());

          // 2つのコレクションに登録
          raceRunnerMap.put(player.getUniqueId(), runner);
          currentParticipants.add(runner);

          if (!raceRunners.containsKey(race.getRace_ID())) {
               raceRunners.put(race.getRace_ID(), currentParticipants);
          }

          // 参加メッセージ送信
          sendJoinMessage(race, player);

          Bukkit.getLogger().info(
                    "プレイヤー参加: " + player.getName() + " -> " + race.getRace_name() + " (" + race.getRace_ID()
                              + ")");
     }

     /**
      * プレイヤーがレースから退出します。
      * 
      * <p>
      * <b>退出フロー</b>:
      * </p>
      * <ol>
      * <li>Race_Runner の現在モードをチェック</li>
      * <li>モード別に異なる処理を実行</li>
      * <li>すべてのコレクションから削除</li>
      * <li>タイマーのクリア</li>
      * <li>スコアボードのクリア</li>
      * </ol>
      * 
      * @param player 退出するプレイヤー
      */
     public static void removeRunner(Player player) {
          if (player == null || !isJoin(player)) {
               clearPlayerScoreboard(player);
               return;
          }

          Optional<Race_Runner> runnerOpt = Optional.ofNullable(getRunner(player));
          if (!runnerOpt.isPresent()) {
               return;
          }

          Race_Runner run = runnerOpt.get();
          Optional<Race> raceOpt = Optional.ofNullable(getRace(run.getRaceID()));

          switch (run.getMode()) {
               case NO_ENTRY:
                    player.sendMessage(CollarMessage.setInfo() + "レース未参加");
                    return;

               case EDIT:
                    // 編集モード中の退出
                    if (raceOpt.isPresent()) {
                         raceList.remove(raceOpt.get());
                    }
                    removeRunnerFromCollections(run.getPlayer(), run.getRaceID());
                    clearPlayerScoreboard(player);
                    player.sendMessage(CollarMessage.setInfo() + "レース編集をキャンセルしました");
                    run.Complete();
                    break;

               case WAIT:
               case RUN:
               case ALL_GOAL_WAIT:
                    // 実行中の退出
                    handleActiveRaceExit(run);
                    break;

               default:
                    break;
          }
     }

     /**
      * 実行中のレースからプレイヤーが退出する場合の処理。
      * (内部ヘルパーメソッド)
      */
     private static void handleActiveRaceExit(Race_Runner run) {
          Player player = run.getPlayer();
          player.sendMessage(CollarMessage.setInfo() + "レースから退出しました");

          // ボート削除
          removeCar(player);

          // スポーン地点にテレポート
          player.teleport(run.getStartLocation());

          // タイマーの停止
          stopTimerIfNeeded(run.getRaceID());

          // コレクションから削除
          removeRunnerFromCollections(player, run.getRaceID());

          // 他のランナーに退出を通知
          sendLeaveMessage(getRace(run.getRaceID()), player);

          // 全員ゴール判定
          checkAllGoalCondition(run.getRaceID());

          run.Complete();
     }

     /**
      * すべてのランナーがゴールしたかを判定し、必要に応じてAllGoalを実行します。
      * (内部ヘルパーメソッド)
      */
     private static void checkAllGoalCondition(UUID raceId) {
          List<Race_Runner> runners = raceRunners.get(raceId);
          if (runners == null || runners.isEmpty()) {
               return;
          }

          // ゴール待機状態のランナー数をカウント
          long goalCount = runners.stream()
                    .filter(r -> r.getMode() == Race_Runner_Mode.ALL_GOAL_WAIT)
                    .count();

          if (goalCount == runners.size()) {
               allGoal(raceId);
          }
     }

     /**
      * タイマーが必要ならば停止します。
      * (内部ヘルパーメソッド)
      */
     private static void stopTimerIfNeeded(UUID raceId) {
          List<Race_Runner> runners = raceRunners.get(raceId);
          if (runners == null || runners.size() > 1) {
               return; // 他のランナーがいる場合は継続
          }

          // 最後のランナーが退出した場合のみ停止
          Optional.ofNullable(getRace(raceId))
                    .ifPresent(Race::stopTimer);
     }

     /**
      * ランナーをすべてのコレクションから削除します。
      * (内部ヘルパーメソッド)
      */
     private static void removeRunnerFromCollections(Player player, UUID raceId) {
          raceRunnerMap.remove(player.getUniqueId());

          ArrayList<Race_Runner> runners = raceRunners.get(raceId);
          if (runners != null) {
               runners.removeIf(r -> r.getPlayer().getUniqueId().equals(player.getUniqueId()));
               if (runners.isEmpty()) {
                    raceRunners.remove(raceId);
               }
          }
     }

     // ================== ゴール・タイマー処理 ==================

     /**
      * レース内のすべてのランナーがゴール状態になった場合の処理。
      * スコアボード表示、ランキング計算、ウェイトなどを実行します。
      * 
      * @param raceId ゴール完了したレースのID
      */
     public static void allGoal(UUID raceId) {
          Optional<Race> raceOpt = Optional.ofNullable(getRace(raceId));
          if (!raceOpt.isPresent()) {
               return;
          }

          Race race = raceOpt.get();
          List<Race_Runner> runners = raceRunners.getOrDefault(raceId, new ArrayList<>());

          // タイム順でソート
          runners.sort(Comparator.comparing(Race_Runner::getTime));

          // 各ランナーにスコアボード更新と完了メッセージ送信
          runners.forEach(runner -> {
               runner.UpdateScoreboard();
               sayScore(runner, race.getRace_name());
               runner.setMode(Race_Runner_Mode.NO_ENTRY);
               new Leave_Timer(runner.getPlayer()).runTaskTimer(Core.getthis(), 0L, 20L);
          });

          // ランキング計算
          Player_Score_Core.SortRanking(raceId);

          // レース状態をゴールに変更
          raceGoal(raceId);
          race.Complete();

          // クリーンアップ
          raceRunners.remove(raceId);
     }

     /**
      * レース開始の処理。
      * WAIT → RUN 状態遷移など。
      * 
      * @param raceId 開始するレースのID
      */
     public static void raceStart(UUID raceId) {
          Optional<Race> raceOpt = Optional.ofNullable(getRace(raceId));
          if (!raceOpt.isPresent()) {
               return;
          }

          Race race = raceOpt.get();
          switch (race.getMode()) {
               case WAIT:
                    List<Race_Runner> waitRunners = raceRunners.getOrDefault(raceId, new ArrayList<>());
                    waitRunners.forEach(Race_Runner::Start);
                    race.setMode(Race_Mode.RUN);
                    break;

               case EDIT:
                    notifyEditorsRaceNotReady(raceId);
                    break;

               case GOAL:
               case RUN:
                    // その他の状態では処理なし
                    notifyEditorsRaceStatus(raceId, race.getMode());
                    break;

               default:
                    break;
          }
     }

     /**
      * レース状態をGOALに変更します。
      * (内部ヘルパーメソッド)
      */
     private static void raceGoal(UUID raceId) {
          Optional.ofNullable(getRace(raceId))
                    .ifPresent(race -> race.setMode(Race_Mode.GOAL));
     }

     // ================== メッセージ送信ヘルパー ==================

     /**
      * 参加メッセージを全ランナーに送信します。
      */
     private static void sendJoinMessage(Race race, Player newPlayer) {
          List<Race_Runner> runners = raceRunners.getOrDefault(race.getRace_ID(), new ArrayList<>());
          int currentCount = runners.size();

          runners.forEach(runner -> {
               runner.getPlayer()
                         .sendMessage(CollarMessage.setInfo() + " " + currentCount + "/" + race.getJoin_Amount()
                                   + " : [" + ChatColor.AQUA + newPlayer.getName() + ChatColor.WHITE + "] が参加しました");
               Race_Timer.startTimer(Race_Timer_Type.WAIT, race.getRace_ID(), Core.getthis(), 0L, 20L);
               runner.UpdateScoreboard();
          });
     }

     /**
      * 退出メッセージを全ランナーに送信します。
      */
     private static void sendLeaveMessage(Race race, Player leavingPlayer) {
          if (race == null) {
               return;
          }

          List<Race_Runner> runners = raceRunners.getOrDefault(race.getRace_ID(), new ArrayList<>());
          int remainingCount = runners.size();

          runners.forEach(runner -> {
               runner.getPlayer()
                         .sendMessage(CollarMessage.setInfo() + " " + remainingCount + "/" + race.getJoin_Amount()
                                   + " : [" + ChatColor.AQUA + leavingPlayer.getName() + ChatColor.WHITE
                                   + "] が退出しました");
               runner.UpdateScoreboard();
          });
     }

     /**
      * ゴール時のスコア表示メッセージ。
      */
     public static void sayScore(Race_Runner runner, String raceName) {
          runner.getPlayer().sendMessage(
                    "----------" + "Atamamozi_" + ChatColor.RED + "D" + ChatColor.WHITE + "----------");
          runner.getPlayer().sendMessage("[" + ChatColor.GREEN + raceName + ChatColor.WHITE + "]");

          List<Race_Runner> allRunners = raceRunners.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

          allRunners.forEach(other -> {
               if (other.getPlayer().getUniqueId().equals(runner.getPlayer().getUniqueId())) {
                    runner.getPlayer()
                              .sendMessage("[" + ChatColor.AQUA + other.getPlayer().getName() + ChatColor.WHITE + "] : "
                                        + ChatColor.YELLOW + other.getTimest());
               } else {
                    runner.getPlayer()
                              .sendMessage("[" + ChatColor.AQUA + other.getPlayer().getName() + ChatColor.WHITE + "] : "
                                        + other.getTimest());
               }
          });

          runner.getPlayer().sendMessage(
                    "----------" + "Atamamozi_" + ChatColor.RED + "D" + ChatColor.WHITE + "----------");
          runner.getPlayer().sendMessage(
                    CollarMessage.setInfo() + "レース終了: " + ChatColor.LIGHT_PURPLE + "/atd leave");
     }

     /**
      * エディタにレース準備不足を通知します。
      */
     private static void notifyEditorsRaceNotReady(UUID raceId) {
          List<Race_Runner> runners = raceRunners.getOrDefault(raceId, new ArrayList<>());
          runners.stream()
                    .filter(r -> r.getMode() == Race_Runner_Mode.EDIT)
                    .forEach(r -> r.getPlayer()
                              .sendMessage(CollarMessage.setInfo() + getRace(raceId).getRace_name() + " はまだ準備中です"));
     }

     /**
      * エディタにレース状態を通知します。
      */
     private static void notifyEditorsRaceStatus(UUID raceId, Race_Mode mode) {
          List<Race_Runner> runners = raceRunners.getOrDefault(raceId, new ArrayList<>());
          String statusMsg = mode == Race_Mode.RUN ? "実行中" : "終了";

          runners.stream()
                    .filter(r -> r.getMode() == Race_Runner_Mode.EDIT)
                    .forEach(r -> r.getPlayer()
                              .sendMessage(
                                        CollarMessage.setInfo() + getRace(raceId).getRace_name() + " は" + statusMsg));
     }

     // ================== 検索・ユーティリティ ==================

     /**
      * プレイヤーが参加中のレースかどうかを判定します。
      * 
      * @param player プレイヤー
      * @return 参加中であれば true
      */
     public static boolean isJoin(Player player) {
          return Optional.ofNullable(getRunner(player))
                    .map(r -> r.getMode() != Race_Runner_Mode.NO_ENTRY)
                    .orElse(false);
     }

     /**
      * プレイヤーのRace_Runnerを取得します。
      * 
      * @param player プレイヤー
      * @return Race_Runner、見つからない場合は null
      */
     public static Race_Runner getRunner(Player player) {
          if (player == null) {
               return null;
          }

          return raceRunnerMap.get(player.getUniqueId());
     }

     /**
      * レース名からレースを取得します。
      * 
      * @param raceName レース名
      * @return Race、見つからない場合は null
      */
     public static Race getRace(String raceName) {
          return raceList.stream()
                    .filter(r -> r.getRace_name().equals(raceName))
                    .findFirst()
                    .orElse(null);
     }

     /**
      * UUIDからレースを取得します。
      * 
      * @param raceId レースID
      * @return Race、見つからない場合は null
      */
     public static Race getRace(UUID raceId) {
          return raceList.stream()
                    .filter(r -> r.getRace_ID().equals(raceId))
                    .findFirst()
                    .orElse(null);
     }

     /**
      * プレイヤーがボート（乗り物）に乗っている場合それを削除します。
      * 
      * @param player プレイヤー
      */
     private static void removeCar(Player player) {
          Optional<Race_Runner> runnerOpt = Optional.ofNullable(getRunner(player));
          if (!runnerOpt.isPresent()) {
               return;
          }

          Race_Runner runner = runnerOpt.get();
          Optional<Race> raceOpt = Optional.ofNullable(getRace(runner.getRaceID()));

          if (raceOpt.isPresent() && raceOpt.get().getRace_Type() == Race_Type.BOAT) {
               org.bukkit.entity.Entity vehicle = player.getVehicle();
               if (vehicle != null) {
                    runner.setEnter(false);
                    vehicle.remove();
               }
          }
     }

     /**
      * プレイヤーのスコアボードをクリアします。
      */
     private static void clearPlayerScoreboard(Player player) {
          if (player == null) {
               return;
          }

          org.bukkit.scoreboard.Objective sidebar = player.getScoreboard()
                    .getObjective(DisplaySlot.SIDEBAR);
          if (sidebar != null && sidebar.getDisplayName().equals("Atamamozi_" + ChatColor.RED + "D")) {
               player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
          }
     }

     /**
      * すべてのランナーをクリアして初期化します。
      * サーバーシャットダウン時などに呼び出します。
      */
     public static void clear() {
          // アクティブランナーをクリア（マップをクリアする前に処理）
          raceRunnerMap.values().stream()
                    .filter(runner -> isJoin(runner.getPlayer()))
                    .forEach(runner -> {
                         clearPlayerScoreboard(runner.getPlayer());
                         removeCar(runner.getPlayer());
                    });

          raceRunners.clear();
          raceRunnerMap.clear();

          String clearMessage = CollarMessage.setInfo() + "Atamamozi_D メモリクリア";
          Bukkit.getLogger().info(clearMessage);
     }

     // ================== 統計・デバッグ情報 ==================

     /**
      * 現在のアクティブレース数を取得します。
      */
     public static int getActiveRaceCount() {
          return raceList.size();
     }

     /**
      * 現在のアクティブランナー数を取得します。
      */
     public static int getActiveRunnerCount() {
          return raceRunnerMap.size();
     }

     /**
      * 特定レースの参加ランナー数を取得します。
      */
     public static int getRunnerCountInRace(UUID raceId) {
          return raceRunners.getOrDefault(raceId, new ArrayList<>()).size();
     }

}
