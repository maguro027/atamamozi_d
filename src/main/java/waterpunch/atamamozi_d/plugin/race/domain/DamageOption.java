package waterpunch.atamamozi_d.plugin.race.domain;

//レース中のダメージを設定するクラス

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

    public boolean isNoFallDamage() {
        return noFallDamage;
    }

    public boolean isNoFireDamage() {
        return noFireDamage;
    }

    public boolean isNoEntityDamage() {
        return noEntityDamage;
    }

    public boolean isNoDrowningDamage() {
        return noDrowningDamage;
    }

    public boolean isNoHungry() {
        return noHungry;
    }

    public int getDeathCooldown() {
        return deathCooldown;
    }

    public DamageOption setNoFallDamage(boolean noFallDamage) {
        this.noFallDamage = noFallDamage;
        return this;
    }

    public DamageOption setNoFireDamage(boolean noFireDamage) {
        this.noFireDamage = noFireDamage;
        return this;
    }

    public DamageOption setNoEntityDamage(boolean noEntityDamage) {
        this.noEntityDamage = noEntityDamage;
        return this;
    }

    public DamageOption setNoDrowningDamage(boolean noDrowningDamage) {
        this.noDrowningDamage = noDrowningDamage;
        return this;
    }

    public DamageOption setNoHungry(boolean noHungry) {
        this.noHungry = noHungry;
        return this;
    }

    public DamageOption setDeathCooldown(int deathCooldown) {
        this.deathCooldown = deathCooldown;
        return this;
    }
}
