package waterpunch.atamamozi_d.plugin.race.domain.raceType;

import org.bukkit.entity.Player;

import waterpunch.atamamozi_d.plugin.race.domain.Race;

/**
 * ボートレース
 * 水上での速さと操縦能力を競うレース
 */
public class BOAT extends Race {

    // ボートレース固有の設定
    private double boatSpeed = 1.0; // ボートのスピード倍率
    private boolean allowWaterCurrent = true; // 水流の影響を受けるか
    private double waterCurrentSpeed = 0.5; // 水流速度

    public BOAT(Player creator) {
        super(creator);
    }

    /**
     * ボートのスピード倍率を設定
     * 
     * @param speed 1.0が標準スピード、2.0で2倍速
     * @return メソッドチェーン用
     */
    public BOAT setBoatSpeed(double speed) {
        this.boatSpeed = speed;
        return this;
    }

    /**
     * 水流の影響を有効/無効に設定
     * 
     * @param allow true=影響を受ける, false=影響なし
     * @return メソッドチェーン用
     */
    public BOAT setAllowWaterCurrent(boolean allow) {
        this.allowWaterCurrent = allow;
        return this;
    }

    /**
     * 水流速度を設定
     * 
     * @param speed 水流の速度
     * @return メソッドチェーン用
     */
    public BOAT setWaterCurrentSpeed(double speed) {
        this.waterCurrentSpeed = speed;
        return this;
    }

    public double getBoatSpeed() {
        return boatSpeed;
    }

    public boolean isAllowWaterCurrent() {
        return allowWaterCurrent;
    }

    public double getWaterCurrentSpeed() {
        return waterCurrentSpeed;
    }
}
