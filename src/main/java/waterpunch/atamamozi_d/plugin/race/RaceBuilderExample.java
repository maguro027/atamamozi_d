package waterpunch.atamamozi_d.plugin.race;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;

/**
 * RaceBuilderとRaceBuilderSessionの使用例。
 * 
 * このクラスはサンプル実装です。実際のプロジェクトに合わせて調整してください。
 */
public class RaceBuilderExample {

    /**
     * レース作成を開始します。
     * コマンド実行時など、プレイヤーが作成を開始する際に呼び出します。
     * 
     * @param player レース作成を開始するプレイヤー
     */
    public static void startRaceCreation(Player player) {
        // 既存セッションがあればクリア
        if (RaceBuilderSession.hasSession(player)) {
            RaceBuilderSession.endSession(player);
        }

        // 新しいセッションを開始
        RaceBuilder builder = RaceBuilderSession.startSession(player);
        player.sendMessage(CollarMessage.setInfo() + "§aレース作成を開始しました");
        builder.preview(player);
    }

    /**
     * プレイヤーがレース名を入力した際に呼び出します。
     * 
     * @param player プレイヤー
     * @param input  入力されたテキスト
     */
    public static void setRaceName(Player player, String input) {
        RaceBuilder builder = RaceBuilderSession.getSession(player);
        if (builder == null) {
            player.sendMessage(CollarMessage.setWarning() + "レース作成セッションがありません");
            return;
        }

        builder.name(input);
        player.sendMessage(CollarMessage.setInfo() + "§aレース名を設定しました: " + input);
        builder.preview(player);
    }

    /**
     * プレイヤーがレースタイプを選択した際に呼び出します。
     * 
     * @param player プレイヤー
     * @param type   選択されたレースタイプ
     */
    public static void setRaceType(Player player, Race_Type type) {
        RaceBuilder builder = RaceBuilderSession.getSession(player);
        if (builder == null) {
            player.sendMessage(CollarMessage.setWarning() + "レース作成セッションがありません");
            return;
        }

        builder.type(type);
        player.sendMessage(CollarMessage.setInfo() + "§aレースタイプを設定しました: " + type);
        builder.preview(player);
    }

    /**
     * プレイヤーがアイコンを選択した際に呼び出します。
     * 
     * @param player   プレイヤー
     * @param material 選択されたマテリアル
     */
    public static void setRaceIcon(Player player, Material material) {
        RaceBuilder builder = RaceBuilderSession.getSession(player);
        if (builder == null) {
            player.sendMessage(CollarMessage.setWarning() + "レース作成セッションがありません");
            return;
        }

        builder.icon(material);
        player.sendMessage(CollarMessage.setInfo() + "§aアイコンを設定しました: " + material.name());
        builder.preview(player);
    }

    /**
     * レース作成を確定して最終化します。
     * 確認画面でプレイヤーが「作成」ボタンをクリックした際に呼び出します。
     * 
     * @param player プレイヤー
     */
    public static void finishRaceCreation(Player player) {
        RaceBuilder builder = RaceBuilderSession.getSession(player);
        if (builder == null) {
            player.sendMessage(CollarMessage.setWarning() + "レース作成セッションがありません");
            return;
        }

        // 最終的なRaceオブジェクトを作成
        Race race = builder.build();

        // Race_Coreに登録
        Race_Core.addRace(race);

        // セッションを終了
        RaceBuilderSession.endSession(player);

        player.sendMessage(CollarMessage.setInfo() + "§aレースを作成しました！");
        player.sendMessage(CollarMessage.setInfo() + "§e作成者: " + race.getCreator());
        player.sendMessage(CollarMessage.setInfo() + "§e名前: " + race.getRace_name());
    }

    /**
     * レース作成をキャンセルします。
     * キャンセルボタンをクリックした際に呼び出します。
     * 
     * @param player プレイヤー
     */
    public static void cancelRaceCreation(Player player) {
        if (!RaceBuilderSession.hasSession(player)) {
            player.sendMessage(CollarMessage.setWarning() + "レース作成セッションがありません");
            return;
        }

        RaceBuilderSession.endSession(player);
        player.sendMessage(CollarMessage.setInfo() + "§eレース作成をキャンセルしました");
    }

    /**
     * チャットイベントで実装例。
     * 名前入力用チャット受信時の例です。
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        RaceBuilder builder = RaceBuilderSession.getSession(player);
        if (builder != null) {
            // チャットメッセージをレース名として設定
            setRaceName(player, message);
            event.setCancelled(true); // チャットをキャンセル
        }
    }

    /**
     * 使用フロー例:
     * 
     * 1. startRaceCreation(player)
     * ↓
     * 2. setRaceName(player, "MyCourse")
     * ↓
     * 3. setRaceType(player, Race_Type.BOAT)
     * ↓
     * 4. setRaceIcon(player, Material.BOAT)
     * ↓
     * 5. finishRaceCreation(player) または cancelRaceCreation(player)
     */
}
