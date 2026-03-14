package waterpunch.atamamozi_d.plugin.race;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPoint;
import waterpunch.atamamozi_d.plugin.race.domain.DamageOption;
import waterpunch.atamamozi_d.plugin.race.enums.RaceType;
import waterpunch.atamamozi_d.plugin.tool.Location.LocParts;

/**
 * 最小互換性スタブ: 主に JSON の入出力に使います。
 */

@Getter
public class RacePackage {
    /**
     * JSON の入出力に使う DTO。既存 JSON が snake_case の場合にも対応できるよう
     * {@link SerializedName} を付与してある。
     */
    // 旧データ読み込み用
    @SerializedName(value = "race_id", alternate = { "race_ID", "raceId", "id" })
    protected UUID raceId;

    @SerializedName("race_name")
    protected String raceName;

    protected String creator;

    @SerializedName("join_amount")
    protected int joinAmount;

    protected int rap;

    @SerializedName("race_type")
    protected RaceType raceType;

    protected Material icon;

    @SerializedName("start_point")
    protected List<LocParts> startPoint;

    @SerializedName(value = "check_point", alternate = { "check_point_loc", "CheckPoint_Loc", "CheckPointLoc",
            "checkPointLoc",
            "CheckPoint_Loc" })
    protected List<CheckPoint> checkPoint;
    // 旧データ読み込み用ここまで

    protected DamageOption damageOption;

    // Gson 用コンストラクタ
    public RacePackage() {
    }

    public RacePackage(UUID raceId, String raceName, String creator, int joinAmount, int rap, RaceType raceType,
            Material icon, List<LocParts> startPoint, List<CheckPoint> checkPoint) {
        this.raceId = raceId;
        this.raceName = raceName;
        this.creator = creator;
        this.joinAmount = joinAmount;
        this.rap = rap;
        this.raceType = raceType;
        this.icon = icon;
        this.startPoint = startPoint;
        this.checkPoint = checkPoint;
    }

    public UUID getID() {
        return raceId;
    }

}
