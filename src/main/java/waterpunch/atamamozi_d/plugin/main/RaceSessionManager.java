package waterpunch.atamamozi_d.plugin.main;

import java.util.HashMap;
import java.util.UUID;

import waterpunch.atamamozi_d.plugin.race.domain.Race;

/**
 * 最小互換性スタブ: 既存コードからの呼び出しを受け止めます。
 * 将来的には RaceManager 等に置き換えてください。
 */
public class RaceSessionManager {

    static final HashMap<UUID, Race> Sessions = new HashMap<>();

    public static void addRace(Race r) {
        if (r == null)
            return;
        Sessions.put(r.getID(), r);
    }

    public static Race getSession(UUID id) {
        if (id == null)
            return null;

        return Sessions.get(id);

    }

    public static HashMap<UUID, Race> getAll() {
        return Sessions;
    }
}
