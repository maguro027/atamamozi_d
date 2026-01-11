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
 * 言語ファイル（JPN.yml）からメッセージを取得するユーティリティ
 */
public class LangManager {

    private static FileConfiguration langConfig;
    private static final Map<String, String> cache = new HashMap<>();

    /**
     * 言語ファイルを初期化
     * 
     * @param plugin プラグインインスタンス
     */
    public static void initialize(Plugin plugin) {
        File langFile = new File(plugin.getDataFolder(), "Lang/JPN.yml");

        // ファイルが存在しない場合はリソースから生成
        if (!langFile.exists()) {
            plugin.saveResource("Lang/JPN.yml", false);
        }

        try (InputStream input = new FileInputStream(langFile)) {
            langConfig = YamlConfiguration.loadConfiguration(
                    new java.io.InputStreamReader(input, java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load Lang/JPN.yml: " + e.getMessage());
            langConfig = new YamlConfiguration();
        }
    }

    /**
     * メッセージを取得
     * 
     * @param key メッセージキー（例: "race.countlock"）
     * @return メッセージ（見つからない場合はキー自体を返す）
     */
    public static String getMessage(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        if (langConfig == null) {
            return key;
        }

        String message = langConfig.getString(key, key);
        cache.put(key, message);
        return message;
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

    /**
     * キャッシュをクリア（リロード時用）
     */
    public static void clearCache() {
        cache.clear();
    }
}
