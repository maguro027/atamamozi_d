package waterpunch.atamamozi_d.plugin.race;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * プレイヤーのレース作成セッションを管理するクラス。
 *
 * <p>
 * ゲーム内でレース作成中のプレイヤーに対応するRaceBuilderを管理します。
 * 複数のプレイヤーが同時にレース作成を行う場合に使用します。
 * </p>
 *
 * @author waterpunch
 */
public class RaceBuilderSession {

    private static final Map<UUID, RaceBuilder> sessions = new HashMap<>();

    /**
     * プレイヤーの新しいレース作成セッションを開始します。
     *
     * @param player セッション開始者
     * @return 新しいRaceBuilder
     */
    public static RaceBuilder startSession(Player player) {
        RaceBuilder builder = new RaceBuilder(player);
        sessions.put(player.getUniqueId(), builder);
        return builder;
    }

    /**
     * プレイヤーの現在のセッションを取得します。
     *
     * @param player プレイヤー
     * @return セッションに対応するRaceBuilder、存在しない場合はnull
     */
    public static RaceBuilder getSession(Player player) {
        return sessions.get(player.getUniqueId());
    }

    /**
     * プレイヤーの現在のセッションを取得します。
     *
     * @param playerId プレイヤーUUID
     * @return セッションに対応するRaceBuilder、存在しない場合はnull
     */
    public static RaceBuilder getSession(UUID playerId) {
        return sessions.get(playerId);
    }

    /**
     * プレイヤーのセッションを終了します。
     *
     * @param player プレイヤー
     */
    public static void endSession(Player player) {
        sessions.remove(player.getUniqueId());
    }

    /**
     * プレイヤーのセッションを終了します。
     *
     * @param playerId プレイヤーUUID
     */
    public static void endSession(UUID playerId) {
        sessions.remove(playerId);
    }

    /**
     * プレイヤーがセッション中かどうかを確認します。
     *
     * @param player プレイヤー
     * @return セッション中の場合はtrue
     */
    public static boolean hasSession(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    /**
     * プレイヤーがセッション中かどうかを確認します。
     *
     * @param playerId プレイヤーUUID
     * @return セッション中の場合はtrue
     */
    public static boolean hasSession(UUID playerId) {
        return sessions.containsKey(playerId);
    }

    /**
     * すべてのセッションをクリアします。
     */
    public static void clearAllSessions() {
        sessions.clear();
    }

    /**
     * アクティブなセッション数を取得します。
     *
     * @return セッション数
     */
    public static int getSessionCount() {
        return sessions.size();
    }
}
