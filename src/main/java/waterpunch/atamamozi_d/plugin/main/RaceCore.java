package waterpunch.atamamozi_d.plugin.main;

import java.util.ArrayList;
import java.util.UUID;

import waterpunch.atamamozi_d.plugin.race.domain.Race;

public class RaceCore {
    static ArrayList<Race> races = new ArrayList<>();

    public static void addRace(Race race) {
        races.add(race);
    }

    public static void removeRace(Race race) {
        races.remove(race);
    }

    public static Race getRace(UUID uuid) {
        for (Race race : races) {
            if (race.getRaceId().equals(uuid)) {
                return race;
            }
        }
        return null;
    }
}
