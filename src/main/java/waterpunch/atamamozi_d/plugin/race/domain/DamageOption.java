package waterpunch.atamamozi_d.plugin.race.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

//レース中のダメージを設定するクラス

@Getter
@Setter
@Accessors(chain = true)
public class DamageOption {
    private boolean noFallDamage; // 落下ダメージを無効化するか
    private boolean noFireDamage; // 火ダメージを無効化するか
    private boolean noEntityDamage; // エンティティからのダメージを無効化するか
    private boolean noDrowningDamage; // 溺れダメージを無効化するか
    private boolean noHungry; // 空腹ダメージを無効化するか
    private int deathCooldown = 0; // 0なら即復活、秒数指定も可能

    /** デフォルト値を持つインスタンスを返す。 */
    public static DamageOption withDefaults() {
        return new DamageOption()
                .setNoFallDamage(false)
                .setNoFireDamage(false)
                .setNoEntityDamage(false)
                .setNoDrowningDamage(false)
                .setNoHungry(false)
                .setDeathCooldown(0);
    }
}
