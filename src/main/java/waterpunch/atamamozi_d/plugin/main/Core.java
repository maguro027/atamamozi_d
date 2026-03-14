package waterpunch.atamamozi_d.plugin.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import waterpunch.atamamozi_d.plugin.database.PlayerScoreDatabase;
import waterpunch.atamamozi_d.plugin.race.RacePackage;
import waterpunch.atamamozi_d.plugin.race.domain.Race;
import waterpunch.atamamozi_d.plugin.race.domain.RaceNormalizer;
import waterpunch.atamamozi_d.plugin.tool.RaceSystem;
import waterpunch.atamamozi_d.plugin.tools.LegacyRaceConverter;
import waterpunch.atamamozi_d.plugin.tools.PlayerScoreMigration;

public class Core extends JavaPlugin {

    public static final File FILE_LOC = new File(new File("").getAbsolutePath() + "/plugins/Atamamozi_D");
    public static final File FILE_RACE = new File(FILE_LOC, "Races");
    public static final File FILE_SCORE = new File(FILE_LOC, "Player_Scores");
    public static final File FILE_RACE_MENU = FILE_LOC;

    private PlayerScoreDatabase database;
    private BukkitTask scoreBatchTask;
    private final AtomicBoolean scoreBatchRunning = new AtomicBoolean(false);

    @Override
    public void onEnable() {
        initializeDataFolders();
        initializeDatabase();
        waterpunch.atamamozi_d.plugin.tool.LangManager.initialize(this);
        loadConfig();
        RaceSystem.logInfo(getLogger(), "Loading Race...");
        getRaces();
        scheduleScoreBatchImport();
    }

    private void loadConfig() {
        saveDefaultConfig();

        // パーティクル表示間隔を読み込み（秒→ミリ秒に変換）
        double particleSeconds = getConfig().getDouble("Setting.particle", 0.5);
        long particleMillis = (long) (particleSeconds * 1000);
        waterpunch.atamamozi_d.plugin.race.export.Hachitai.setParticleInterval(particleMillis);

        RaceSystem.logInfo(getLogger(), String.format("Particle interval: %.1f seconds", particleSeconds));
    }

    private void initializeDataFolders() {
        if (!getDataFolder().exists()) {
            RaceSystem.logInfo(getLogger(), "Welcome to the Atamamozi_D plugin");
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

            if (dbExists) {
                RaceSystem.logInfo(getLogger(), "Database already exists. Skipping JSON migration.");
                return;
            }

            // 初回のみJSONからマイグレーション
            RaceSystem.logInfo(getLogger(), "Database not found. Creating new database and importing JSON scores...");

            PlayerScoreMigration migration = new PlayerScoreMigration(database);

            // 本番用: プラグインデータフォルダのPlayer_Scoresから移行
            if (FILE_SCORE.exists()) {
                migration.migrateFromDirectory(FILE_SCORE);
            } else {
                RaceSystem.logWarn(getLogger(), "Player_Scores directory not found — skipping migration.");
            }

            // 開発用: testdataが存在すれば移行（任意）
            File testdataDir = new File(new File("").getAbsolutePath() + "/testdata/Player_Scores");
            if (testdataDir.exists()) {
                RaceSystem.logInfo(getLogger(), "Found testdata directory, starting migration (dev only)...");
                migration.migrateFromDirectory(testdataDir);
            }

        } catch (SQLException e) {
            RaceSystem.logError(getLogger(), "Failed to initialize database", e);
        }
    }

    private void convertLegacyData(File file) throws Exception {
        String version = this.getDescription().getVersion();
        LegacyRaceConverter.convertFile(file, version, true, false);
    }

    @Override
    public void onDisable() {
        if (scoreBatchTask != null) {
            scoreBatchTask.cancel();
            scoreBatchTask = null;
        }
        if (database != null) {
            database.close();
        }
    }

    private void scheduleScoreBatchImport() {
        double intervalHours = getConfig().getDouble("Setting.scoreBatchHours", 5.0);
        if (intervalHours <= 0) {
            RaceSystem.logInfo(getLogger(), "Score batch import is disabled (Setting.scoreBatchHours <= 0).");
            return;
        }

        long intervalTicks = Math.max(1L, (long) (intervalHours * 60 * 60 * 20));
        long initialDelayTicks = 20L;

        scoreBatchTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (!scoreBatchRunning.compareAndSet(false, true)) {
                return;
            }
            try {
                if (database == null) {
                    return;
                }

                if (!FILE_SCORE.exists()) {
                    RaceSystem.logWarn(getLogger(), "Player_Scores directory not found — skipping batch import.");
                    return;
                }

                RaceSystem.logInfo(getLogger(), "Starting score batch import...");
                PlayerScoreMigration migration = new PlayerScoreMigration(database);
                migration.migrateFromDirectory(FILE_SCORE);
                RaceSystem.logInfo(getLogger(), "Score batch import completed.");
            } catch (Exception e) {
                RaceSystem.logWarn(getLogger(), "Score batch import failed", e);
            } finally {
                scoreBatchRunning.set(false);
            }
        }, initialDelayTicks, intervalTicks);
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
                RacePackage pkg = gson.fromJson(fileReader, RacePackage.class);

                // ノーマライズ & バリデーション
                Race r = RaceNormalizer.fromPackage(pkg);
                if (r == null) {
                    RaceSystem.logWarn(getLogger(), "Race validation failed (null): " + tmpFile.getName());
                    continue;
                }

                // 致命的エラーチェック
                String validationError = validateRace(r, tmpFile.getName());
                if (validationError != null) {
                    RaceSystem.logWarn(getLogger(), validationError);
                    continue;
                }

                // RaceCoreに登録
                RaceCore.addRace(r);
            } catch (JsonSyntaxException | JsonIOException | IOException e) {
                RaceSystem.logWarn(getLogger(), "Race Data Broken..." + tmpFile.getName());
                RaceSystem.logWarn(getLogger(),
                        "Failed to parse race JSON: " + tmpFile.getName() + " — attempting conversion", e);

                // 例外が発生したら該当ファイルを変換を試み、変換後に再読み込み
                try {
                    convertLegacyData(tmpFile);
                    // 再試行
                    try (FileReader rr = new FileReader(tmpFile)) {
                        Gson gson2 = new Gson();
                        RacePackage pkg2 = gson2.fromJson(rr, RacePackage.class);
                        Race r2 = RaceNormalizer.fromPackage(pkg2);
                        if (r2 != null) {
                            String validationError2 = validateRace(r2, tmpFile.getName());
                            if (validationError2 == null) {
                                RaceCore.addRace(r2);
                            } else {
                                RaceSystem.logWarn(getLogger(), validationError2);
                            }
                        }
                    } catch (Exception re) {
                        RaceSystem.logWarn(getLogger(), "Re-parse failed after conversion: " + tmpFile.getName(), re);
                    }
                } catch (Exception convEx) {
                    RaceSystem.logWarn(getLogger(), "Conversion failed for: " + tmpFile.getName(), convEx);
                }
            }
        }
    }

    /**
     * レースの致命的エラーをチェックします。
     * 
     * @param race     レースオブジェクト
     * @param fileName ファイル名（ログ用）
     * @return エラーメッセージ（問題なければnull）
     */
    private String validateRace(Race race, String fileName) {
        // チェックポイントが空
        if (race.getCheckPoint() == null || race.getCheckPoint().isEmpty()) {
            return "Race validation failed (no checkpoints): " + fileName;
        }

        // スタートポイントが空
        if (race.getStartPoint() == null || race.getStartPoint().isEmpty()) {
            return "Race validation failed (no start points): " + fileName;
        }

        // スタートポイント数とjoinAmountが不一致
        if (race.getStartPoint().size() != race.getJoinAmount()) {
            return String.format(
                    "Race validation failed (start points: %d != joinAmount: %d): %s",
                    race.getStartPoint().size(), race.getJoinAmount(), fileName);
        }

        // 問題なし
        return null;
    }
}
