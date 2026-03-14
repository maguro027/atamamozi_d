package waterpunch.atamamozi_d.plugin.tool;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * レース機能向けの共通メッセージ送信ユーティリティ。
 */
public final class RaceSystem {

    private RaceSystem() {
    }

    public enum MessageType {
        ERROR(ChatColor.RED, "ERROR", Level.SEVERE),
        INFO(ChatColor.BLUE, "INFO", Level.INFO),
        WARN(ChatColor.YELLOW, "WARN", Level.WARNING);

        private final ChatColor color;
        private final String label;
        private final Level logLevel;

        MessageType(ChatColor color, String label, Level logLevel) {
            this.color = color;
            this.label = label;
            this.logLevel = logLevel;
        }

        public String coloredPrefix() {
            return color + "[" + label + "]";
        }

        public String plainPrefix() {
            return "[" + label + "]";
        }

        public Level logLevel() {
            return logLevel;
        }
    }

    public static void sendMessage(MessageType type, CommandSender target, String message) {
        if (type == null || target == null || message == null) {
            return;
        }
        target.sendMessage(formatColored(type, message));
    }

    public static void sendMessage(MessageType type, String message) {
        sendMessage(type, Bukkit.getConsoleSender(), message);
    }

    // 既存提案の呼び名を残す（スペル互換）
    public static void sendmesseage(MessageType type, CommandSender target, String message) {
        sendMessage(type, target, message);
    }

    // 既存提案の呼び名を残す（スペル互換）
    public static void sendmesseage(MessageType type, String message) {
        sendMessage(type, message);
    }

    public static void logInfo(Logger logger, String message) {
        log(logger, MessageType.INFO, message, null);
    }

    public static void logInfo(String message) {
        log(null, MessageType.INFO, message, null);
    }

    public static void logWarn(Logger logger, String message) {
        log(logger, MessageType.WARN, message, null);
    }

    public static void logWarn(String message) {
        log(null, MessageType.WARN, message, null);
    }

    public static void logWarn(Logger logger, String message, Throwable throwable) {
        log(logger, MessageType.WARN, message, throwable);
    }

    public static void logWarn(String message, Throwable throwable) {
        log(null, MessageType.WARN, message, throwable);
    }

    public static void logError(Logger logger, String message) {
        log(logger, MessageType.ERROR, message, null);
    }

    public static void logError(String message) {
        log(null, MessageType.ERROR, message, null);
    }

    public static void logError(Logger logger, String message, Throwable throwable) {
        log(logger, MessageType.ERROR, message, throwable);
    }

    public static void logError(String message, Throwable throwable) {
        log(null, MessageType.ERROR, message, throwable);
    }

    private static void log(Logger logger, MessageType type, String message, Throwable throwable) {
        if (type == null || message == null) {
            return;
        }

        // コンソールには色付きで表示
        sendMessage(type, Bukkit.getConsoleSender(), message);

        // 例外はログにも残す
        if (throwable == null) {
            return;
        }

        Logger targetLogger = logger != null ? logger : Bukkit.getLogger();
        targetLogger.log(type.logLevel(), formatPlain(type, message), throwable);
    }

    private static String formatColored(MessageType type, String message) {
        return type.coloredPrefix() + " " + ChatColor.WHITE + message;
    }

    private static String formatPlain(MessageType type, String message) {
        return type.plainPrefix() + " " + message;
    }
}
