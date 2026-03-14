package waterpunch.atamamozi_d.plugin.race.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import waterpunch.atamamozi_d.plugin.main.RaceCore;
import waterpunch.atamamozi_d.plugin.tool.LangManager;

/**
 * 実行中のレースセッション（可変）
 * 
 * 責務:
 * - レース進行状態の管理
 * - 参加プレイヤーの管理
 * - 各プレイヤーの周回数・チェックポイント管理
 * - 状態遷移（WAITING → COUNTDOWN → RUNNING → FINISHED）
 */
public class RaceSession {

    @Getter
    private final UUID sessionId = UUID.randomUUID();
    @Getter
    private final Race race;
    private final Map<UUID, RacePlayer> players;

    // 進行状況管理
    private final Map<UUID, PlayerProgress> playerProgress; // プレイヤーID → 進行状況
    private final Map<UUID, Integer> playerJoinOrders; // プレイヤーID → 参加順序

    @Getter
    private RaceState state;
    private boolean rankingDirty; // ランキングキャッシュ無効化フラグ
    private List<RacePlayer> rankingCache;

    private BukkitTask countdownTask;
    private int countdownRemaining; // 残りカウントダウン秒数
    private static final int COUNTDOWN_SECONDS = 30; // TODO: 設定ファイルから読み込み
    private static final int COUNTDOWN_LOCK_SECONDS = 5; // この秒数以下で参加ロック

    public enum RaceState {
        WAITING, // 参加者待ち
        COUNTDOWN, // カウントダウン中
        RUNNING, // レース実行中
        FINISHED, // レース終了
        END// セッション終了（クリーンアップ待ち）

    }

    /**
     * プレイヤーの進行状況を管理する内部クラス
     */
    private static class PlayerProgress {
        int lap; // 現在の周回数
        int checkpoint; // 現在のチェックポイント番号

        PlayerProgress() {
            this.lap = 0;
            this.checkpoint = 0;
        }
    }

    public RaceSession(UUID raceId) {
        this.race = RaceCore.getRace(raceId);
        this.players = new HashMap<>();
        this.playerProgress = new HashMap<>();
        this.playerJoinOrders = new HashMap<>();
        this.state = RaceState.WAITING;
        this.rankingDirty = true;
        this.rankingCache = new ArrayList<>();
    }

    /**
     * プレイヤーをセッションに追加
     * 
     * @param player 追加するプレイヤー
     * @return 成功ならtrue、失敗ならfalse
     */
    public boolean addPlayer(Player player) {
        // WAITING状態以外は参加拒否
        if (state != RaceState.WAITING) {
            String message = LangManager.getMessage("race.countlock", race.getRaceName());
            player.sendMessage(message);
            return false;
        }

        // プレイヤーをセッションに追加
        UUID playerId = player.getUniqueId();
        int joinOrder = players.size(); // 先着順で採番
        players.put(playerId, new RacePlayer(player));
        playerProgress.put(playerId, new PlayerProgress());
        playerJoinOrders.put(playerId, joinOrder);
        rankingDirty = true;

        // 通知
        String joinedMessage = LangManager.getMessage("race.joined", player.getName());
        for (RacePlayer existingPlayer : players.values()) {
            existingPlayer.getPlayer().sendMessage(joinedMessage);
        }

        return true;
    }

    /**
     * プレイヤーをセッションから削除
     * 
     * @param player 削除するプレイヤー
     */
    public void removePlayer(Player player) {

        String leftMessage = LangManager.getMessage("race.left", player.getName());
        for (RacePlayer existingPlayer : players.values()) {
            existingPlayer.getPlayer().sendMessage(leftMessage);
        }

        UUID playerId = player.getUniqueId();
        players.remove(playerId);
        playerProgress.remove(playerId);
        playerJoinOrders.remove(playerId);
        rankingDirty = true;

        // プレイヤーが減った後、全員ゴールしているかチェック
        checkAllPlayersFinished();
    }

    /**
     * プレイヤーがチェックポイントを通過
     * 
     * @param player          プレイヤー
     * @param checkpointIndex 通過したチェックポイント番号
     */
    public void passCheckpoint(Player player, int checkpointIndex) {
        UUID playerId = player.getUniqueId();
        PlayerProgress progress = playerProgress.get(playerId);
        if (progress == null)
            return;

        // 最後のチェックポイントから最初のチェックポイント(0)に戻った = 1周完了
        int lastCheckpointIndex = race.getCheckPoint().size() - 1;
        if (progress.checkpoint == lastCheckpointIndex && checkpointIndex == 0) {
            completeLap(player); // 内部でチェックポイントを1に設定するのでreturn
            return;
        }

        progress.checkpoint = checkpointIndex;
        rankingDirty = true;
    }

    /**
     * プレイヤーが1周完了
     * 
     * @param player プレイヤー
     */
    public void completeLap(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerProgress progress = playerProgress.get(playerId);
        if (progress == null)
            return;

        progress.lap++;
        progress.checkpoint = 0; // チェックポイントをリセット
        rankingDirty = true;

        // 指定周回数に到達したらゴール
        if (progress.lap >= race.getRap()) {
            RacePlayer racePlayer = players.get(playerId);
            if (racePlayer != null && !racePlayer.isFinished()) {
                racePlayer.finish();
                // ゴール後、全員ゴールしているかチェック
                checkAllPlayersFinished();
            }
        }
    }

    /**
     * プレイヤーの現在の周回数を取得
     */
    public int getCurrentLap(Player player) {
        PlayerProgress progress = playerProgress.get(player.getUniqueId());
        return progress != null ? progress.lap : 0;
    }

    /**
     * プレイヤーの現在のチェックポイント番号を取得
     */
    public int getCurrentCheckpoint(Player player) {
        PlayerProgress progress = playerProgress.get(player.getUniqueId());
        return progress != null ? progress.checkpoint : 0;
    }

    /**
     * 指定プレイヤーがゴール済みか判定
     */
    public boolean isPlayerFinished(Player player) {
        RacePlayer racePlayer = players.get(player.getUniqueId());
        return racePlayer != null && racePlayer.isFinished();
    }

    /**
     * 全プレイヤーがゴール済みか判定
     */
    public boolean isAllPlayersFinished() {
        return players.values().stream().allMatch(RacePlayer::isFinished);
    }

    /**
     * 全員ゴールしているか確認し、全員ゴールならレース終了
     */
    private void checkAllPlayersFinished() {
        if (state == RaceState.RUNNING && !players.isEmpty() && isAllPlayersFinished()) {
            finish();
        }
    }

    /**
     * ランキングを取得（キャッシュ利用）
     * 完走者優先、未完走者は進行度順
     */
    public List<RacePlayer> getRanking() {
        if (rankingDirty) {
            rankingCache = players.values().stream()
                    .sorted((p1, p2) -> {
                        // 完走者が優先
                        boolean f1 = p1.isFinished();
                        boolean f2 = p2.isFinished();
                        if (f1 && !f2)
                            return -1;
                        if (!f1 && f2)
                            return 1;

                        // 両方完走ならタイムで比較
                        if (f1 && f2) {
                            return Long.compare(p1.getElapsedTimeMillis(), p2.getElapsedTimeMillis());
                        }

                        // 両方未完走なら進行度で比較（周回数→チェックポイント）
                        UUID id1 = p1.getPlayer().getUniqueId();
                        UUID id2 = p2.getPlayer().getUniqueId();

                        PlayerProgress progress1 = playerProgress.get(id1);
                        PlayerProgress progress2 = playerProgress.get(id2);

                        int lap1 = progress1 != null ? progress1.lap : 0;
                        int lap2 = progress2 != null ? progress2.lap : 0;
                        if (lap1 != lap2)
                            return Integer.compare(lap2, lap1); // 周回数が多い方が上

                        int cp1 = progress1 != null ? progress1.checkpoint : 0;
                        int cp2 = progress2 != null ? progress2.checkpoint : 0;
                        return Integer.compare(cp2, cp1); // チェックポイントが進んでる方が上
                    })
                    .collect(Collectors.toList());
            rankingDirty = false;
        }
        return new ArrayList<>(rankingCache);
    }

    /**
     * 状態遷移: カウントダウン開始
     */
    public void startCountdown() {
        if (state != RaceState.WAITING) {
            return;
        }

        state = RaceState.COUNTDOWN;
        countdownRemaining = COUNTDOWN_SECONDS;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Atamamozi_D");
        if (plugin == null) {
            return;
        }

        // カウントダウンタイマー開始
        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // 全プレイヤーにカウントダウン通知
            for (RacePlayer racePlayer : players.values()) {
                racePlayer.countDown(countdownRemaining);
            }

            // カウントダウン終了
            if (countdownRemaining <= 0) {
                if (countdownTask != null) {
                    countdownTask.cancel();
                    countdownTask = null;
                }
                start();
            }

            countdownRemaining--;
        }, 0L, 20L); // 1秒ごと（20tick）
    }

    /**
     * 状態遷移: レース開始
     */
    public void start() {
        if (state != RaceState.COUNTDOWN) {
            return;
        }

        state = RaceState.RUNNING;

        // 各プレイヤーをスタート地点にテレポート
        for (RacePlayer racePlayer : players.values()) {
            UUID playerId = racePlayer.getPlayer().getUniqueId();
            int joinOrder = playerJoinOrders.getOrDefault(playerId, 0);
            Location startLocation = race.getStartPoint(joinOrder).getLocation();

            if (startLocation != null) {
                racePlayer.getPlayer().teleport(startLocation);
            }
        }

        // 全プレイヤーのタイマーを開始
        players.values().forEach(RacePlayer::startTimer);
    }

    /**
     * 状態遷移: レース終了
     */
    public void finish() {
        stopTimer();
        state = RaceState.FINISHED;
    }

    /**
     * タイマーを停止
     */
    public void stopTimer() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }

    public RacePlayer getPlayer(UUID playerId) {
        return players.get(playerId);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public Map<UUID, RacePlayer> getPlayers() {
        return new HashMap<>(players);
    }

    /**
     * プレイヤーの参加順序を取得
     */
    public int getJoinOrder(Player player) {
        return playerJoinOrders.getOrDefault(player.getUniqueId(), 0);
    }
}
