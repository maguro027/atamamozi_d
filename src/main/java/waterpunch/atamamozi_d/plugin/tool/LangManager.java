package waterpunch.atamamozi_d.plugin.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * 言語ファイルからメッセージを取得するユーティリティ
 */
public class LangManager {

    private static final String DEFAULT_LANGUAGE = "JPN";
    private static final Map<String, FileConfiguration> langConfigs = new HashMap<>();
    private static final Map<String, String> cache = new HashMap<>();
    private static String currentLanguage = DEFAULT_LANGUAGE;

    /**
     * 言語ファイルを初期化
     * 
     * @param plugin プラグインインスタンス
     */
    public static void initialize(Plugin plugin) {
        ensureLanguageResource(plugin, "JPN.yml");
        ensureLanguageResource(plugin, "ENG.yml");

        loadLanguageFiles(plugin);

        String configuredLanguage = plugin.getConfig().getString("Setting.language", DEFAULT_LANGUAGE);
        setCurrentLanguage(configuredLanguage);
    }

    private static void ensureLanguageResource(Plugin plugin, String fileName) {
        File langFile = new File(plugin.getDataFolder(), "Lang/" + fileName);
        if (!langFile.exists()) {
            plugin.saveResource("Lang/" + fileName, false);
        }
    }

    private static void loadLanguageFiles(Plugin plugin) {
        langConfigs.clear();
        clearCache();

        File langDir = new File(plugin.getDataFolder(), "Lang");
        if (!langDir.exists() && !langDir.mkdirs()) {
            RaceSystem.logWarn(plugin.getLogger(), "Failed to create language directory: " + langDir.getAbsolutePath());
            return;
        }

        File[] files = langDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (files == null) {
            RaceSystem.logWarn(plugin.getLogger(), "Failed to list language files in: " + langDir.getAbsolutePath());
            return;
        }

        for (File file : files) {
            String fileName = file.getName();
            String langCode = fileName.substring(0, fileName.lastIndexOf('.')).toUpperCase();
            try (InputStream input = new FileInputStream(file)) {
                FileConfiguration loaded = YamlConfiguration.loadConfiguration(
                        new java.io.InputStreamReader(input, java.nio.charset.StandardCharsets.UTF_8));
                langConfigs.put(langCode, loaded);
            } catch (Exception e) {
                RaceSystem.logWarn(plugin.getLogger(),
                        "Failed to load language file " + fileName + ": " + e.getMessage());
            }
        }

        if (!langConfigs.containsKey(DEFAULT_LANGUAGE)) {
            langConfigs.put(DEFAULT_LANGUAGE, new YamlConfiguration());
        }
    }

    /**
     * メッセージを取得
     * 
     * @param key メッセージキー（例: "race.countlock"）
     * @return メッセージ（見つからない場合はキー自体を返す）
     */
    public static String getMessage(String key) {
        String cacheKey = currentLanguage + ":" + key;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        String message = getMessageFromLanguage(currentLanguage, key);
        if (message == null && !DEFAULT_LANGUAGE.equals(currentLanguage)) {
            message = getMessageFromLanguage(DEFAULT_LANGUAGE, key);
        }
        if (message == null) {
            message = key;
        }

        cache.put(cacheKey, message);
        return message;
    }

    private static String getMessageFromLanguage(String language, String key) {
        FileConfiguration config = langConfigs.get(language);
        if (config == null) {
            return null;
        }
        return config.getString(key);
    }

    /**
     * プレースホルダー付きメッセージを取得
     * 
     * @param key    メッセージキー
     * @param params プレースホルダーに置き換える値（%s に順番に適用）
     * @return フォーマット済みメッセージ
     */
    public static String getMessage(String key, Object... params) {
        String message = getMessage(key);
        return String.format(message, params);
    }

    public static void setCurrentLanguage(String language) {
        if (language == null || language.trim().isEmpty()) {
            currentLanguage = DEFAULT_LANGUAGE;
            clearCache();
            return;
        }

        String normalized = language.trim().toUpperCase();
        if (!langConfigs.containsKey(normalized)) {
            currentLanguage = DEFAULT_LANGUAGE;
            clearCache();
            return;
        }

        currentLanguage = normalized;
        clearCache();
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * キャッシュをクリア（リロード時用）
     */
    public static void clearCache() {
        cache.clear();
    }
}
