package waterpunch.atamamozi_d.plugin.race.domain.raceType;

import org.bukkit.entity.Player;

import waterpunch.atamamozi_d.plugin.race.domain.Race;
import waterpunch.atamamozi_d.plugin.race.enums.RaceType;

public class WALK extends Race {

    private boolean isNoitems;
    private boolean isNoPotion;

    public WALK(Player creator) {
        super(creator);
        setRaceType(RaceType.WALK);
    }

    public WALK setNoitems(boolean isNoitems) {
        this.isNoitems = isNoitems;
        return this;
    }

    public WALK setNoPotion(boolean isNoPotion) {
        this.isNoPotion = isNoPotion;
        return this;
    }

    public boolean isNoitems() {
        return isNoitems;
    }

    public boolean isNoPotion() {
        return isNoPotion;
    }
}
