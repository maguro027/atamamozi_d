package waterpunch.atamamozi_d.plugin.tool;

import org.bukkit.ChatColor;

/**
 * カラーメッセージユーティリティクラス
 * プラグインで使用するフォーマット済みメッセージプレフィックスを提供
 * 
 * Color message utility class
 * Provides formatted message prefixes for plugin use
 */
public class CollarMessage {

     /**
      * レース関連メッセージ用プレフィックスを取得
      * 
      * Gets prefix for race-related messages
      * 
      * @return "[RACE]" プレフィックス / "[RACE]" prefix
      */
     public static String setRace() {
          return ChatColor.GOLD + "[" + ChatColor.RED + "RACE" + ChatColor.GOLD + "]" + ChatColor.WHITE;
     }

     /**
      * 情報メッセージ用プレフィックスを取得
      * 
      * Gets prefix for info messages
      * 
      * @return "[INFO]" プレフィックス / "[INFO]" prefix
      */
     public static String setInfo() {
          return ChatColor.GOLD + "[" + ChatColor.BLUE + "INFO" + ChatColor.GOLD + "]" + ChatColor.WHITE;
     }

     /**
      * 警告メッセージ用プレフィックスを取得
      * 
      * Gets prefix for warning messages
      * 
      * @return "[WARN]" プレフィックス / "[WARN]" prefix
      */
     public static String setWarning() {
          return ChatColor.GOLD + "[" + ChatColor.RED + "WARN" + ChatColor.GOLD + "]" + ChatColor.WHITE;
     }

     /**
      * 権限なしメッセージを取得
      * 
      * Gets no permission message
      * 
      * @return 権限なしメッセージ / No permission message
      */
     public static String setNotPermission() {
          return setWarning() + "You Don't have Permission";
     }
}
