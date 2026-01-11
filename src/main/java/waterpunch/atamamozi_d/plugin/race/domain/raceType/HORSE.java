package waterpunch.atamamozi_d.plugin.race.domain.raceType;

import org.bukkit.entity.Player;

import waterpunch.atamamozi_d.plugin.race.domain.Race;

/**
 * 馬レース
 * 馬を操り、ジャンプやスピードを活かしたレース
 */
public class HORSE extends Race {

    // 馬レース固有の設定
    private double horseSpeed = 1.0; // 馬のスピード倍率
    private double jumpHeight = 1.0; // ジャンプ高度倍率
    private boolean allowAccelerate = true; // 加速を許可
    private double staminaDrain = 1.0; // スタミナ消費率

    public HORSE(Player creator) {
        super(creator);
    }

    /**
     * 馬のスピード倍率を設定
     * 
     * @param speed 1.0が標準スピード、2.0で2倍速
     * @return メソッドチェーン用
     */
    public HORSE setHorseSpeed(double speed) {
        this.horseSpeed = speed;
        return this;
    }

    /**
     * ジャンプ高度倍率を設定
     * 
     * @param height 1.0が標準高度
     * @return メソッドチェーン用
     */
    public HORSE setJumpHeight(double height) {
        this.jumpHeight = height;
        return this;
    }

    /**
     * 加速を許可するか設定
     * 
     * @param allow true=加速可能, false=加速不可
     * @return メソッドチェーン用
     */
    public HORSE setAllowAccelerate(boolean allow) {
        this.allowAccelerate = allow;
        return this;
    }

    /**
     * スタミナ消費率を設定
     * 
     * @param rate 1.0が標準消費、0.5で消費少ない
     * @return メソッドチェーン用
     */
    public HORSE setStaminaDrain(double rate) {
        this.staminaDrain = rate;
        return this;
    }

    public double getHorseSpeed() {
        return horseSpeed;
    }

    public double getJumpHeight() {
        return jumpHeight;
    }

    public boolean isAllowAccelerate() {
        return allowAccelerate;
    }

    public double getStaminaDrain() {
        return staminaDrain;
    }
}
