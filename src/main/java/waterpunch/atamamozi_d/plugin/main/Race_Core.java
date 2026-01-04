package waterpunch.atamamozi_d.plugin.main;

import java.util.ArrayList;
import java.util.List;

import waterpunch.atamamozi_d.plugin.race.domain.Race;

/**
 * 最小互換性スタブ: 既存コードからの呼び出しを受け止めます。
 * 将来的には RaceManager 等に置き換えてください。
 */
public final class Race_Core {

    private static final List<Race> REGISTRY = new ArrayList<>();

    private Race_Core() {
    }

    public static void addRace(Race r) {
        if (r == null)
            return;
        REGISTRY.add(r);
    }

    public static List<Race> getAll() {
        return REGISTRY;
    }
}
