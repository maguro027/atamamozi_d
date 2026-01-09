package waterpunch.atamamozi_d.plugin.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import waterpunch.atamamozi_d.plugin.database.PlayerScoreDatabase;
import waterpunch.atamamozi_d.plugin.race.domain.Race;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;
import waterpunch.atamamozi_d.plugin.tools.LegacyRaceConverter;
import waterpunch.atamamozi_d.plugin.tools.PlayerScoreMigration;

public class Core extends JavaPlugin {

    public static final File FILE_LOC = new File(new File("").getAbsolutePath() + "/plugins/Atamamozi_D");
    public static final File FILE_RACE = new File(FILE_LOC, "Races");
    public static final File FILE_SCORE = new File(FILE_LOC, "Player_Scores");
    public static final File FILE_RACE_MENU = FILE_LOC;

    private PlayerScoreDatabase database;

    @Override
    public void onEnable() {
        initializeDataFolders();
        initializeDatabase();
        getLogger().info("Loading Race...");
        getRaces();
    }

    private void initializeDataFolders() {
        if (!getDataFolder().exists()) {
            getLogger().info("Welcome to the Atamamozi_D plugin");
            getDataFolder().mkdirs();
        }
        if (!FILE_RACE.exists()) {
            FILE_RACE.mkdirs();
        }
        if (!FILE_SCORE.exists()) {
            FILE_SCORE.mkdirs();
        }
    }

    private void initializeDatabase() {
        File dbFile = new File(FILE_LOC, "player_scores.db");
        database = new PlayerScoreDatabase(dbFile);

        try {
            database.initialize();
            boolean dbExists = dbFile.exists();

            // 初回のみJSONからマイグレーション
            if (!dbExists) {
                getLogger().info("Database not found. Creating new database and importing JSON scores...");

                PlayerScoreMigration migration = new PlayerScoreMigration(database);

                // 本番用: プラグインデータフォルダのPlayer_Scoresから移行
                if (FILE_SCORE.exists()) {
                    migration.migrateFromDirectory(FILE_SCORE);
                } else {
                    getLogger().warning("Player_Scores directory not found — skipping migration.");
                }

                // 開発用: testdataが存在すれば移行（任意）
                File testdataDir = new File(new File("").getAbsolutePath() + "/testdata/Player_Scores");
                if (testdataDir.exists()) {
                    getLogger().info("Found testdata directory, starting migration (dev only)...");
                    migration.migrateFromDirectory(testdataDir);
                }
            } else {
                getLogger().info("Database already exists. Skipping JSON migration.");
            }

        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Failed to initialize database", e);
        }
    }

    private void convertLegacyData(File file) throws Exception {
        String version = this.getDescription().getVersion();
        LegacyRaceConverter.convertFile(file, version, true, false);
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.close();
        }
    }

    private void getRaces() {
        if (!FILE_RACE.exists())
            return;
        loadRaces(FILE_RACE);
    }

    private void loadRaces(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File tmpFile : files) {
            if (tmpFile.isDirectory()) {
                // Skip Backup folder to avoid reprocessing
                if (tmpFile.getName().equals("Backup")) {
                    continue;
                }
                loadRaces(tmpFile);
                continue;
            }
            String name = tmpFile.getName();
            int idx = name.lastIndexOf('.');
            if (idx < 0)
                continue;
            if (!name.substring(idx).equals(".json"))
                continue;
            try (FileReader fileReader = new FileReader(tmpFile)) {
                Gson gson = new Gson();
                Race r = gson.fromJson(fileReader, Race.class);

                // 互換性維持のため最小スタブ経由で登録
                RaceSessionManager.addRace(r);
            } catch (JsonSyntaxException | JsonIOException | IOException e) {
                String message = CollarMessage.setWarning() + "Race Data Broken..." + tmpFile.getName();
                Bukkit.getLogger().warning(message);
                Bukkit.getLogger().log(Level.WARNING,
                        "Failed to parse race JSON: " + tmpFile.getName() + " — attempting conversion", e);

                // 例外が発生したら該当ファイルを変換を試み、変換後に再読み込み
                try {
                    convertLegacyData(tmpFile);
                    // 再試行
                    try (FileReader rr = new FileReader(tmpFile)) {
                        Gson gson2 = new Gson();
                        Race r2 = gson2.fromJson(rr, Race.class);
                        if (r2 != null) {
                            RaceSessionManager.addRace(r2);
                        }
                    } catch (Exception re) {
                        Bukkit.getLogger().log(Level.WARNING,
                                "Re-parse failed after conversion: " + tmpFile.getName(), re);
                    }
                } catch (Exception convEx) {
                    Bukkit.getLogger().log(Level.WARNING, "Conversion failed for: " + tmpFile.getName(), convEx);
                }
            }
        }
    }
}
