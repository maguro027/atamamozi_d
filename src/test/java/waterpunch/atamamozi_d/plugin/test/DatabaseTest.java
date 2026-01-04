package waterpunch.atamamozi_d.plugin.test;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import waterpunch.atamamozi_d.plugin.database.PlayerScoreDatabase;
import waterpunch.atamamozi_d.plugin.tools.PlayerScoreMigration;

/**
 * データベースとマイグレーション機能のテストクラス
 */
public class DatabaseTest {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("DatabaseTest");

        try {
            // テスト用データベースファイル
            File dbFile = new File("test_player_scores.db");
            if (dbFile.exists()) {
                dbFile.delete();
                logger.info("Deleted existing test database");
            }

            // データベースの初期化
            PlayerScoreDatabase database = new PlayerScoreDatabase(dbFile);
            database.initialize();
            logger.info("Database initialized");

            // testdataからマイグレーション
            File testdataDir = new File("testdata/Player_Scores");
            if (!testdataDir.exists()) {
                logger.severe("testdata/Player_Scores directory not found!");
                return;
            }

            PlayerScoreMigration migration = new PlayerScoreMigration(database);
            migration.migrateFromDirectory(testdataDir);

            // ランキングの取得テスト
            logger.info("\n=== Ranking Test ===");
            List<PlayerScoreDatabase.RankingEntry> ranking = database.getRanking("78ed75a2-538f-4ce3-b07f-0bb6619baec8",
                    10);

            for (int i = 0; i < ranking.size(); i++) {
                PlayerScoreDatabase.RankingEntry entry = ranking.get(i);
                logger.info(String.format("%d. %s - %.2f",
                        i + 1, entry.getPlayerName(), entry.getBestTime()));
            }

            // プレイヤー名取得テスト
            logger.info("\n=== Player Name Test ===");
            String playerName = database.getPlayerName("7ccf0555-137d-4832-ae69-083896f2ac77");
            logger.log(Level.INFO, "Player name: {0}", playerName);

            database.close();
            logger.info("\nTest completed successfully!");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Test failed", e);
        }
    }
}
