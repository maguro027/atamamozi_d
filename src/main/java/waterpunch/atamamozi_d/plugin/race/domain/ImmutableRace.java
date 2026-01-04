package waterpunch.atamamozi_d.plugin.race.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;

import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Mode;
import waterpunch.atamamozi_d.plugin.race.enums.Race_Type;
import waterpunch.atamamozi_d.plugin.tool.Location.LocParts;

/**
 * Raceの不変スナップショット。
 * 生成後は内部状態を書き換えられません。
 */
public final class ImmutableRace {
    private final UUID raceId;
    private final String creator;
    private final String raceName;
    private final Race_Type raceType;
    private final Material icon;
    private final int joinAmount;
    private final int rap;
    private final int time;
    private final int errorCount;
    private final Race_Mode raceMode;
    private final List<LocParts> startPoints;
    private final List<CheckPointLoc> checkPoints;

    public ImmutableRace(UUID raceId,
            String creator,
            String raceName,
            Race_Type raceType,
            Material icon,
            int joinAmount,
            int rap,
            int time,
            int errorCount,
            Race_Mode raceMode,
            List<LocParts> startPoints,
            List<CheckPointLoc> checkPoints) {
        this.raceId = raceId;
        this.creator = creator;
        this.raceName = raceName;
        this.raceType = raceType;
        this.icon = icon;
        this.joinAmount = joinAmount;
        this.rap = rap;
        this.time = time;
        this.errorCount = errorCount;
        this.raceMode = raceMode;
        this.startPoints = Collections.unmodifiableList(new ArrayList<>(startPoints));
        this.checkPoints = Collections.unmodifiableList(new ArrayList<>(checkPoints));
    }

    public UUID getRaceId() {
        return raceId;
    }

    public String getCreator() {
        return creator;
    }

    public String getRaceName() {
        return raceName;
    }

    public Race_Type getRaceType() {
        return raceType;
    }

    public Material getIcon() {
        return icon;
    }

    public int getJoinAmount() {
        return joinAmount;
    }

    public int getRap() {
        return rap;
    }

    public int getTime() {
        return time;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public Race_Mode getRaceMode() {
        return raceMode;
    }

    public List<LocParts> getStartPoints() {
        return startPoints;
    }

    public List<CheckPointLoc> getCheckPoints() {
        return checkPoints;
    }
}
