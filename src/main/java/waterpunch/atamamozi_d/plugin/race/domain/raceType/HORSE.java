package waterpunch.atamamozi_d.plugin.race.domain.raceType;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import waterpunch.atamamozi_d.plugin.race.domain.Race;

/**
 * 馬レース
 * 馬を操り、ジャンプやスピードを活かしたレース
 */
@Getter
@Setter
@Accessors(chain = true)
public class HORSE extends Race {

    // 馬レース固有の設定
    private double horseSpeed = 1.0; // 馬のスピード倍率
    private double jumpHeight = 1.0; // ジャンプ高度倍率
    private boolean allowAccelerate = true; // 加速を許可
    private double staminaDrain = 1.0; // スタミナ消費率

    public HORSE(Player creator) {
        super(creator);
    }
}
