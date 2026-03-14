package waterpunch.atamamozi_d.plugin.race.domain.raceType;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import waterpunch.atamamozi_d.plugin.race.domain.Race;
import waterpunch.atamamozi_d.plugin.race.enums.RaceType;

@Getter
@Setter
@Accessors(chain = true)
public class WALK extends Race {

    private boolean isNoitems;
    private boolean isNoPotion;

    public WALK(Player creator) {
        super(creator);
        setRaceType(RaceType.WALK);
    }
}
