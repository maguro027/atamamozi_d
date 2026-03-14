package waterpunch.atamamozi_d.plugin.race.domain.raceType;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import waterpunch.atamamozi_d.plugin.race.domain.Race;

/**
 * ボートレース
 * 水上での速さと操縦能力を競うレース
 */
@Getter
@Setter
@Accessors(chain = true)
public class BOAT extends Race {

    // ボートレース固有の設定
    private double boatSpeed = 1.0; // ボートのスピード倍率
    private boolean allowWaterCurrent = true; // 水流の影響を受けるか
    private double waterCurrentSpeed = 0.5; // 水流速度

    public BOAT(Player creator) {
        super(creator);
    }
}
