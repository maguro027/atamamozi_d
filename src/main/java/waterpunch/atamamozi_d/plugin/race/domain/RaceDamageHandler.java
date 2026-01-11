package waterpunch.atamamozi_d.plugin.race.domain;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * レースのダメージハンドリングインターフェース
 * 各レースタイプがダメージを異なる方法で処理する
 */
public interface RaceDamageHandler {

    /**
     * プレイヤーがダメージを受けたときの処理
     * 
     * @param player      ダメージを受けたプレイヤー
     * @param damageEvent ダメージイベント
     * @param racePlayer  レース参加者情報
     * @return true=ダメージを無視（キャンセル）, false=通常のダメージ処理
     */
    boolean handleDamage(Player player, EntityDamageEvent damageEvent, RacePlayer racePlayer);

    /**
     * プレイヤーが死亡したときの処理
     * 
     * @param player     死亡したプレイヤー
     * @param racePlayer レース参加者情報
     */
    void handleDeath(Player player, RacePlayer racePlayer);

    /**
     * ダメージハンドラーの名前を取得
     */
    String getName();
}
