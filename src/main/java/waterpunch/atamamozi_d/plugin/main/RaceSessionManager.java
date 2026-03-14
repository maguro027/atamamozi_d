package waterpunch.atamamozi_d.plugin.main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import waterpunch.atamamozi_d.plugin.race.domain.RaceSession;

/**
 * レース定義とアクティブセッションの管理
 * 
 * 責務:
 * - レース定義（Race）の登録・検索
 * - アクティブセッション（RaceSession）の管理
 * - プレイヤー→セッションのマッピング
 */
public class RaceSessionManager {

    // アクティブセッション（実行中のレース）
    private static final Map<UUID, RaceSession> activeSessions = new HashMap<>();

    // プレイヤー→セッションのキャッシュ（O(1)検索用）
    private static final Map<UUID, RaceSession> playerInSession = new HashMap<>();

    // ========== セッション管理 ==========
    /**
     * プレイヤーをセッションに参加させる
     * 参加の不可などはRaceSession側で判定する
     * ここではセッションの作成とプレイヤーのマッピングのみを行う
     * 
     * @param player
     * @param session
     */
    public static void joinSession(Player player, UUID raceID) {
        if (isInRace(player)) {
            return;
        }
        RaceSession session = createSession(raceID);
        if (session == null) {
            session = getSession(raceID);
        }

        if (session.addPlayer(player)) {
            addPlayerToSession(player, session);
        }

    }

    /**
     * 新しいセッションを作成
     * 
     * @param race レース定義
     * @return 作成されたセッション
     */
    public static RaceSession createSession(UUID raceId) {

        // 既存セッションがあればnullを返す
        if (activeSessions.containsKey(raceId)) {
            return null;
        }

        RaceSession session = new RaceSession(raceId);
        activeSessions.put(raceId, session);
        return session;
    }

    /**
     * セッションを取得
     * 
     * @param raceId レースID
     * @return セッション（存在しなければnull）
     */
    public static RaceSession getSession(UUID raceId) {
        return activeSessions.get(raceId);
    }

    /**
     * セッションを終了
     * 
     * @param raceId レースID
     */
    public static void endSession(UUID raceId) {
        RaceSession session = activeSessions.remove(raceId);
        if (session != null) {
            // プレイヤーマッピングをクリア
            session.getPlayers().keySet().forEach(playerInSession::remove);
        }
    }

    /**
     * アクティブセッションが存在するか確認
     */
    public static boolean hasActiveSession(UUID raceId) {
        return activeSessions.containsKey(raceId);
    }

    /**
     * 全アクティブセッションを取得
     */
    public static Map<UUID, RaceSession> getAllActiveSessions() {
        return new HashMap<>(activeSessions);
    }

    // ========== プレイヤー管理 ==========

    /**
     * プレイヤーをセッションにマッピング（O(1)検索用）
     */
    public static void addPlayerToSession(Player player, RaceSession session) {
        playerInSession.put(player.getUniqueId(), session);
    }

    /**
     * プレイヤーのマッピングを削除
     */
    public static void removePlayerFromSession(Player player) {
        playerInSession.remove(player.getUniqueId());
    }

    /**
     * プレイヤーが参加中のセッションを取得（O(1)）
     * 
     * @param player プレイヤー
     * @return セッション（参加していなければnull）
     */
    public static RaceSession getPlayerSession(Player player) {
        return playerInSession.get(player.getUniqueId());
    }

    /**
     * プレイヤーがレース中か判定
     */
    public static boolean isInRace(Player player) {
        return playerInSession.containsKey(player.getUniqueId());
    }
}
