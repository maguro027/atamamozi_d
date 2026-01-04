package waterpunch.atamamozi_d.plugin.race;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;

import com.google.gson.annotations.SerializedName;

import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.tool.Location.LocParts;

/**
 * JSON の入出力に使う DTO。既存 JSON が snake_case の場合にも対応できるよう
 * {@link SerializedName} を付与してある。
 */
public class RacePackage {

    @SerializedName(value = "race_id", alternate = { "race_ID", "raceId", "id" })
    private UUID raceId;

    @SerializedName("race_name")
    private String raceName;

    private String creator;

    @SerializedName("join_amount")
    private int joinAmount;

    private int rap;

    @SerializedName("race_type")
    private String raceType;

    private Material icon;

    @SerializedName("start_point")
    private List<LocParts> startPoint;

    @SerializedName(value = "check_point_loc", alternate = { "CheckPoint_Loc", "CheckPointLoc", "checkPointLoc",
            "CheckPoint_Loc" })
    private List<CheckPointLoc> checkPointLoc;

    // no-arg constructor for Gson
    public RacePackage() {
    }

    public RacePackage(UUID raceId, String raceName, String creator, int joinAmount, int rap, String raceType,
            Material icon, List<LocParts> startPoint, List<CheckPointLoc> checkPointLoc) {
        this.raceId = raceId;
        this.raceName = raceName;
        this.creator = creator;
        this.joinAmount = joinAmount;
        this.rap = rap;
        this.raceType = raceType;
        this.icon = icon;
        this.startPoint = startPoint;
        this.checkPointLoc = checkPointLoc;
    }

    public UUID getRaceId() {
        return raceId;
    }

    public String getRaceName() {
        return raceName;
    }

    public String getCreator() {
        return creator;
    }

    public int getJoinAmount() {
        return joinAmount;
    }

    public int getRap() {
        return rap;
    }

    public String getRaceType() {
        return raceType;
    }

    public Material getIcon() {
        return icon;
    }

    public List<LocParts> getStartPoint() {
        return startPoint;
    }

    public List<CheckPointLoc> getCheckPointLoc() {
        return checkPointLoc;
    }

}
