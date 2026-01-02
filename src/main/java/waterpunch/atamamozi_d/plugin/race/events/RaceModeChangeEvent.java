package waterpunch.atamamozi_d.plugin.race.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import waterpunch.atamamozi_d.plugin.race.Race;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;

/**
 * イベントは、レースの状態（Race_Mode）が変更されるときに発火されます。
 * 
 * <p>
 * このイベントはキャンセル可能ではありません。状態の変更を観察し、
 * 必要な処理（タイマー開始、スコアボード更新など）をイベントハンドラで実行します。
 * </p>
 *
 * @author waterpunch
 */
public class RaceModeChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Race race;
    private final Race_Mode oldMode;
    private final Race_Mode newMode;

    /**
     * RaceModeChangeEventのコンストラクタ。
     *
     * @param race    状態が変更されるレース
     * @param oldMode 以前の状態
     * @param newMode 新しい状態
     */
    public RaceModeChangeEvent(Race race, Race_Mode oldMode, Race_Mode newMode) {
        this.race = race;
        this.oldMode = oldMode;
        this.newMode = newMode;
    }

    /**
     * 状態が変更されるレースを取得します。
     *
     * @return レースオブジェクト
     */
    public Race getRace() {
        return race;
    }

    /**
     * 以前のレース状態を取得します。
     *
     * @return 以前の状態
     */
    public Race_Mode getOldMode() {
        return oldMode;
    }

    /**
     * 新しいレース状態を取得します。
     *
     * @return 新しい状態
     */
    public Race_Mode getNewMode() {
        return newMode;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
