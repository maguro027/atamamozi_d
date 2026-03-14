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

        UUID id = pkg.getID() != null ? pkg.getID() : UUID.randomUUID();
        String name = pkg.getRaceName() != null ? pkg.getRaceName() : "DEFAULT";
        String creator = pkg.getCreator() != null ? pkg.getCreator() : "UNKNOWN";

        return Race.newBuilder()
                .raceId(id)
                .raceName(name)
                .creator(creator)
                .raceType(type)
                .maxPlayers(pkg.getJoinAmount())
                .laps(pkg.getRap())
                .icon(pkg.getIcon() != null ? pkg.getIcon() : Material.PAPER)
                .startPoint(pkg.getStartPoint() != null ? pkg.getStartPoint() : new ArrayList<>())
                .checkPoint(pkg.getCheckPoint() != null ? pkg.getCheckPoint() : new ArrayList<>())
                .damageOption(pkg.getDamageOption() != null ? pkg.getDamageOption() : DamageOption.withDefaults())
                .build();
    }
}
