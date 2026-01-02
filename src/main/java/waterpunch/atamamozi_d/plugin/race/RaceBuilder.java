package waterpunch.atamamozi_d.plugin.race;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import waterpunch.atamamozi_d.plugin.main.Core;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.tool.CollarMessage;

/**
 * レース作成中の状態を保持し、段階的にレースを構築するビルダークラス。
 *
 * <p>
 * ゲーム内でプレイヤーがレースを段階的に作成する際に、
 * 名前、タイプ、アイコンなどの設定を一時的に保持します。
 * </p>
 *
 * @author waterpunch
 */
public class RaceBuilder {

    private final UUID creatorId;
    private String name = "DEFAULT";
    private Race_Type type = Race_Type.WALK;
    private Material icon = Material.MAP;
    private int rap = 1;
    private int joinAmount = 1;
    private int countDown = Core.WAIT_TIME;

    /**
     * RaceBuilderを初期化します。
     *
     * @param creator レース作成者のプレイヤー
     */
    public RaceBuilder(Player creator) {
        this.creatorId = creator.getUniqueId();
    }

    /**
     * レースの名前を設定します。
     *
     * @param name レース名
     * @return このビルダーインスタンス（メソッドチェーン用）
     */
    public RaceBuilder name(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        return this;
    }

    /**
     * レースのタイプを設定します。
     *
     * @param type レースタイプ（WALK/BOAT）
     * @return このビルダーインスタンス（メソッドチェーン用）
     */
    public RaceBuilder type(Race_Type type) {
        if (type != null) {
            this.type = type;
        }
        return this;
    }

    /**
     * レースのアイコンを設定します。
     *
     * @param icon レースアイコン（Material）
     * @return このビルダーインスタンス（メソッドチェーン用）
     */
    public RaceBuilder icon(Material icon) {
        if (icon != null) {
            this.icon = icon;
        }
        return this;
    }

    /**
     * 周回数を設定します。
     *
     * @param rap 周回数
     * @return このビルダーインスタンス（メソッドチェーン用）
     */
    public RaceBuilder rap(int rap) {
        if (rap > 0) {
            this.rap = rap;
        }
        return this;
    }

    /**
     * 参加人数を設定します。
     *
     * @param joinAmount 参加可能人数
     * @return このビルダーインスタンス（メソッドチェーン用）
     */
    public RaceBuilder joinAmount(int joinAmount) {
        if (joinAmount > 0) {
            this.joinAmount = joinAmount;
        }
        return this;
    }

    /**
     * カウントダウン時間を設定します。
     *
     * @param countDown カウントダウン時間（ティック）
     * @return このビルダーインスタンス（メソッドチェーン用）
     */
    public RaceBuilder countDown(int countDown) {
        if (countDown > 0) {
            this.countDown = countDown;
        }
        return this;
    }

    /**
     * 現在の設定内容をプレイヤーに表示します。
     *
     * @param player 表示対象のプレイヤー
     */
    public void preview(Player player) {
        player.sendMessage("");
        player.sendMessage(CollarMessage.setInfo() + "§6==== レース作成中 ====");
        player.sendMessage(CollarMessage.setInfo() + "§e名前: " + name);
        player.sendMessage(CollarMessage.setInfo() + "§eタイプ: " + type);
        player.sendMessage(CollarMessage.setInfo() + "§eアイコン: " + icon.name());
        player.sendMessage(CollarMessage.setInfo() + "§e周回数: " + rap);
        player.sendMessage(CollarMessage.setInfo() + "§e最大参加人数: " + joinAmount);
        player.sendMessage(CollarMessage.setInfo() + "§eカウントダウン: " + countDown + "ティック");
        player.sendMessage("");
    }

    /**
     * 現在の設定でレースオブジェクトを作成して確定します。
     *
     * @return 作成されたRaceオブジェクト
     * @throws IllegalStateException 作成者がオンラインでない場合
     */
    public Race build() {
        Player creator = Bukkit.getPlayer(creatorId);
        if (creator == null) {
            throw new IllegalStateException("Race creator is not online");
        }
        return new Race.Builder(creator)
                .name(name)
                .type(type)
                .icon(icon)
                .rap(rap)
                .joinAmount(joinAmount)
                .countDown(countDown)
                .build();
    }

    /**
     * ビルダーの現在の状態をMapで返します。（セッション保存用）
     *
     * @return 設定状態を含むMap
     */
    public Map<String, Object> saveState() {
        Map<String, Object> state = new HashMap<>();
        state.put("name", name);
        state.put("type", type);
        state.put("icon", icon);
        state.put("rap", rap);
        state.put("joinAmount", joinAmount);
        state.put("countDown", countDown);
        return state;
    }

    /**
     * 保存されたMapから状態を復元します。
     *
     * @param state 復元対象のMap
     */
    public void loadState(Map<String, Object> state) {
        if (state == null)
            return;

        if (state.containsKey("name"))
            name((String) state.get("name"));
        if (state.containsKey("type"))
            type((Race_Type) state.get("type"));
        if (state.containsKey("icon"))
            icon((Material) state.get("icon"));
        if (state.containsKey("rap"))
            rap((Integer) state.get("rap"));
        if (state.containsKey("joinAmount"))
            joinAmount((Integer) state.get("joinAmount"));
        if (state.containsKey("countDown"))
            countDown((Integer) state.get("countDown"));
    }

    /**
     * ビルダーをリセットしてデフォルト値に戻します。
     */
    public void reset() {
        this.name = "DEFAULT";
        this.type = Race_Type.WALK;
        this.icon = Material.MAP;
        this.rap = 1;
        this.joinAmount = 1;
        this.countDown = Core.WAIT_TIME;
    }

    // ゲッター（確認用）

    public UUID getCreatorId() {
        return creatorId;
    }

    public Player getCreator() {
        return Bukkit.getPlayer(creatorId);
    }

    public String getCreatorName() {
        Player player = getCreator();
        return player != null ? player.getName() : "Unknown";
    }

    public boolean isCreatorOnline() {
        return getCreator() != null;
    }

    public String getName() {
        return name;
    }

    public Race_Type getType() {
        return type;
    }

    public Material getIcon() {
        return icon;
    }

    public int getRap() {
        return rap;
    }

    public int getJoinAmount() {
        return joinAmount;
    }

    public int getCountDown() {
        return countDown;
    }
}
