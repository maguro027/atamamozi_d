package waterpunch.atamamozi_d.plugin.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import waterpunch.atamamozi_d.plugin.database.PlayerScoreDatabase;
import waterpunch.atamamozi_d.plugin.tool.RaceSystem;

/**
 * Player_ScoresフォルダーからJSONファイルを読み込んでSQLiteに移行するクラス
 */
public class PlayerScoreMigration {
    private final PlayerScoreDatabase database;
    private static final Logger logger = Logger.getLogger(PlayerScoreMigration.class.getName());
    private final Gson gson;

    public PlayerScoreMigration(PlayerScoreDatabase database) {
        this.database = database;
        this.gson = new Gson();
    }

    /**
     * Player_Scoresフォルダーを再帰的に走査してJSONファイルを読み込む
     */
    public void migrateFromDirectory(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            RaceSystem.logWarn(logger,
                    String.format("Player_Scores directory not found: %s", directory.getAbsolutePath()));
            return;
        }

        RaceSystem.logInfo(logger, String.format("Starting migration from: %s", directory.getAbsolutePath()));
        int count = processDirectory(directory, 0);
        RaceSystem.logInfo(logger, String.format("Migration completed. %d player score files processed.", count));
    }

    /**
     * ディレクトリを再帰的に処理
     */
    private int processDirectory(File directory, int count) {
        File[] files = directory.listFiles();
        if (files == null) {
            return count;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // サブディレクトリを再帰処理
                count = processDirectory(file, count);
            } else if (file.getName().endsWith(".json")) {
                // JSONファイルを処理
                try {
                    migrateJsonFile(file);
                    count++;
                    RaceSystem.logInfo(logger, String.format("Migrated: %s", file.getName()));
                } catch (IOException | SQLException e) {
                    RaceSystem.logError(logger, "Error migrating " + file.getName(), e);
                }
            }
        }

        return count;
    }

    /**
     * 単一のJSONファイルをデータベースに移行
     */
    public void migrateJsonFile(File jsonFile) throws IOException, SQLException {
        try (FileReader reader = new FileReader(jsonFile)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            // プレイヤー情報を取得
            String uuid = root.get("uuid").getAsString();
            String name = root.get("Name").getAsString();

            // プレイヤーを登録（既存の場合は名前を確認して更新）
            String storedName = database.getPlayerName(uuid);
            if (storedName == null) {
                database.registerPlayer(uuid, name);
                RaceSystem.logInfo(logger, String.format("New player registered: %s (%s)", name, uuid));
            } else if (!storedName.equals(name)) {
                database.updatePlayerName(uuid, name);
            }

            // スコア情報を取得
            JsonArray scores = root.getAsJsonArray("Scores");
            if (scores != null) {
                for (int i = 0; i < scores.size(); i++) {
                    JsonObject score = scores.get(i).getAsJsonObject();
                    migrateScore(uuid, score);
                }
            }
        }
    }

    /**
     * 個別のスコアエントリーを移行
     */
    private void migrateScore(String playerUuid, JsonObject scoreData) throws SQLException {
        String raceId = scoreData.get("RACE_ID").getAsString();
        int count = scoreData.get("COUNT").getAsInt();

        // タイム配列を取得
        JsonArray timesArray = scoreData.getAsJsonArray("TIMEs");
        if (timesArray == null || timesArray.isEmpty()) {
            RaceSystem.logWarn(logger, String.format("No times found for race %s of player %s", raceId, playerUuid));
            return;
        }

        // race_scoresレコードを取得または作成
        int raceScoreId = database.getRaceScoreId(playerUuid, raceId);

        // 既存のタイム記録をクリア
        database.clearTimeRecords(raceScoreId);

        // タイムを配列から取得してソート（最小5つまで）
        double[] times = new double[timesArray.size()];
        for (int i = 0; i < timesArray.size(); i++) {
            times[i] = timesArray.get(i).getAsDouble();
        }
        java.util.Arrays.sort(times);

        // ベストタイムを取得
        double bestTime = times[0];

        // 最大5つのタイム記録を保存
        int recordCount = Math.min(5, times.length);
        for (int i = 0; i < recordCount; i++) {
            database.addTimeRecord(raceScoreId, times[i]);
        }

        // race_scoresテーブルを更新
        database.updateRaceScore(raceScoreId, count, bestTime);

        RaceSystem.logInfo(logger, String.format("Score migrated: %s - Race: %s - Best: %s - Records: %d",
                playerUuid, raceId, bestTime, recordCount));
    }

    /**
     * JSONの"BEST_RAP"フィールドが空でない場合の処理（現在は不要）
     * 将来的にラップタイムデータが必要になった場合に使用
     */
    @SuppressWarnings("unused")
    private void migrateLapTimes(int timeRecordId, JsonArray bestRap) throws SQLException {
        // 将来の拡張用
        // 現在は BEST_RAP は不要なので実装しない
    }
}
