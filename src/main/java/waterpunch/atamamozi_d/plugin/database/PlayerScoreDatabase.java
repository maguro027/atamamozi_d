package waterpunch.atamamozi_d.plugin.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;

/**
 * プレイヤースコアデータベース管理クラス
 * SQLiteを使用してプレイヤーのレーススコアを管理します
 */
public class PlayerScoreDatabase {
    private final File databaseFile;
    private static final Logger logger = Logger.getLogger(PlayerScoreDatabase.class.getName());
    private Connection connection;

    public PlayerScoreDatabase(File databaseFile) {
        this.databaseFile = databaseFile;
    }

    /**
     * データベースに接続して初期化
     */
    public void initialize() throws SQLException {
        try {
            // SQLiteドライバーをロード
            Class.forName("org.sqlite.JDBC");

            // データベースファイルへの接続を確立
            String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);

            logger.log(Level.INFO, "Database connected: {0}", databaseFile.getAbsolutePath());

            // テーブル作成
            createTables();

        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }

    /**
     * テーブルを作成
     */
    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // プレイヤーマスターテーブル
            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "player_uuid VARCHAR(36) PRIMARY KEY, " +
                    "player_name VARCHAR(16) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // レースごとのベストスコアとプレイ回数
            stmt.execute("CREATE TABLE IF NOT EXISTS race_scores (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "race_id VARCHAR(255) NOT NULL, " +
                    "play_count INTEGER NOT NULL DEFAULT 0, " +
                    "best_time DOUBLE, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (player_uuid) REFERENCES players(player_uuid), " +
                    "UNIQUE (player_uuid, race_id))");

            // タイム記録（プレーヤー×レースごとに最大5つ保持）
            stmt.execute("CREATE TABLE IF NOT EXISTS time_records (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "race_score_id INTEGER NOT NULL, " +
                    "total_time DOUBLE NOT NULL, " +
                    "recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (race_score_id) REFERENCES race_scores(id))");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_race_best_time ON race_scores(race_id, best_time)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_race_score_time ON time_records(race_score_id, total_time)");

            logger.info("Database tables created successfully");
        }
    }

    /**
     * プレイヤーを登録または取得
     */
    public void registerPlayer(String uuid, String name) throws SQLException {
        String sql = "INSERT OR IGNORE INTO players (player_uuid, player_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        }
    }

    /**
     * プレイヤー名を取得
     */
    public String getPlayerName(String uuid) throws SQLException {
        String sql = "SELECT player_name FROM players WHERE player_uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("player_name");
                }
            }
        }
        return null;
    }

    /**
     * プレイヤー名を更新
     */
    public void updatePlayerName(String uuid, String newName) throws SQLException {
        String sql = "UPDATE players SET player_name = ? WHERE player_uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, uuid);
            pstmt.executeUpdate();
            logger.log(Level.INFO, "Player name updated: {0} -> {1}", new Object[] { uuid, newName });
        }
    }

    /**
     * レーススコアを登録または取得
     */
    public int getRaceScoreId(String playerUuid, String raceId) throws SQLException {
        String sql = "SELECT id FROM race_scores WHERE player_uuid = ? AND race_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid);
            pstmt.setString(2, raceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        // 存在しない場合は新規作成
        String insertSql = "INSERT INTO race_scores (player_uuid, race_id, play_count) VALUES (?, ?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, playerUuid);
            pstmt.setString(2, raceId);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create race_score");
    }

    /**
     * プレイ回数とベストタイムを更新
     */
    public void updateRaceScore(int raceScoreId, int playCount, Double bestTime) throws SQLException {
        String sql = "UPDATE race_scores SET play_count = ?, best_time = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, playCount);
            if (bestTime != null) {
                pstmt.setDouble(2, bestTime);
            } else {
                pstmt.setNull(2, Types.DOUBLE);
            }
            pstmt.setInt(3, raceScoreId);
            pstmt.executeUpdate();
        }
    }

    /**
     * タイム記録を追加
     */
    public void addTimeRecord(int raceScoreId, double time) throws SQLException {
        String sql = "INSERT INTO time_records (race_score_id, total_time) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, raceScoreId);
            pstmt.setDouble(2, time);
            pstmt.executeUpdate();
        }
    }

    /**
     * 既存のタイム記録を削除
     */
    public void clearTimeRecords(int raceScoreId) throws SQLException {
        String sql = "DELETE FROM time_records WHERE race_score_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, raceScoreId);
            pstmt.executeUpdate();
        }
    }

    /**
     * 指定したレーススコアのタイム記録数を取得
     */
    public int getTimeRecordCount(int raceScoreId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM time_records WHERE race_score_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, raceScoreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    /**
     * レースのランキングを取得（TOP N）
     */
    public List<RankingEntry> getRanking(String raceId, int limit) throws SQLException {
        String sql = "SELECT p.player_name, rs.best_time " +
                "FROM race_scores rs " +
                "JOIN players p ON rs.player_uuid = p.player_uuid " +
                "WHERE rs.race_id = ? AND rs.best_time IS NOT NULL " +
                "ORDER BY rs.best_time ASC " +
                "LIMIT ?";
        List<RankingEntry> ranking = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, raceId);
            pstmt.setInt(2, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ranking.add(new RankingEntry(
                            rs.getString("player_name"),
                            rs.getDouble("best_time")));
                }
            }
        }
        return ranking;
    }

    /**
     * データベース接続を閉じる
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing database", e);
            }
        }
    }

    /**
     * ランキングエントリー
     */
    @Getter
    public static class RankingEntry {
        private final String playerName;
        private final double bestTime;

        public RankingEntry(String playerName, double bestTime) {
            this.playerName = playerName;
            this.bestTime = bestTime;
        }
    }
}
