package waterpunch.atamamozi_d.plugin.race.domain;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;

import waterpunch.atamamozi_d.plugin.race.RacePackage;
import waterpunch.atamamozi_d.plugin.race.enums.RaceType;

/**
 * RacePackage から Race を生成し、欠損フィールドを補完するユーティリティ。
 */
public final class RaceNormalizer {

    private RaceNormalizer() {
    }

    public static Race fromPackage(RacePackage pkg) {
        if (pkg == null)
            return null;

        RaceType type = pkg.getRaceType() != null ? pkg.getRaceType() : RaceType.WALK;
        Race race = new Race();

        UUID id = pkg.getID() != null ? pkg.getID() : UUID.randomUUID();
        String name = pkg.getRaceName() != null ? pkg.getRaceName() : "DEFAULT";
        String creator = pkg.getCreator() != null ? pkg.getCreator() : "UNKNOWN";

        race.setRaceId(id)
                .setRaceName(name)
                .setCreator(creator)
                .setRaceType(type)
                .setMaxPlayers(pkg.getJoinAmount())
                .setLaps(pkg.getRap())
                .setIcon(pkg.getIcon() != null ? pkg.getIcon() : Material.PAPER)
                .setStartPoint(pkg.getStartPoint() != null ? pkg.getStartPoint() : new ArrayList<>())
                .setCheckPoint(pkg.getCheckPoint() != null ? pkg.getCheckPoint() : new ArrayList<>())
                .setDamageOption(pkg.getDamageOption() != null ? pkg.getDamageOption() : DamageOption.withDefaults());

        return race;
    }
}
