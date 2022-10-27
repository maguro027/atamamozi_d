package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Race {

     private String creator, race_name;
     private Race_Type race_type;
     private Material icon;
     private int join_amount;
     private int rap;
     private ArrayList<CheckPointLoc> CheckPoint_Loc = new ArrayList<>();
     private int Error_Count;

     public Race(Player creator) {
          this.creator = creator.getName();
          this.race_name = "DEFAULT";
          this.race_type = Race_Type.RUN;
          this.icon = Material.MAP;
          this.rap = 1;
     }

     public Race(Race Race) {
          this.creator = Race.getCreator();
          this.race_name = Race.getRace_name();
          this.race_type = Race.getRace_Type();
          this.icon = Race.getIcon();
          this.join_amount = Race.getJoin_Amount();
          this.rap = Race.getRap();
          this.CheckPoint_Loc = Race.getCheckPointLoc();
     }

     public void setCheckPointLoc(Location loc, int r) {
          CheckPoint_Loc.add(new CheckPointLoc(loc, r));
     }

     public ArrayList<CheckPointLoc> getCheckPointLoc() {
          return this.CheckPoint_Loc;
     }

     public void setCheckPoints(ArrayList<CheckPointLoc> checkPointLoc) {
          this.CheckPoint_Loc = checkPointLoc;
     }

     public String getCreator() {
          return this.creator;
     }

     public void setRace_name(String race_name) {
          this.race_name = race_name;
     }

     public Race_Type getRace_Type() {
          return this.race_type;
     }

     public void setRace_Type(Race_Type race_type) {
          this.race_type = race_type;
     }

     public String getRace_name() {
          return this.race_name;
     }

     public void setIcon(Material icon) {
          this.icon = icon;
     }

     public Material getIcon() {
          return this.icon;
     }

     public void setJoin_Amount(int join_amount) {
          this.join_amount = join_amount;
     }

     public int getJoin_Amount() {
          return this.join_amount;
     }

     public void setRap(int Rap) {
          this.rap = Rap;
     }

     public int getRap() {
          return this.rap;
     }

     public void setErrorCount(int i) {
          this.Error_Count = i;
     }

     public void addErrorCount() {
          this.Error_Count++;
     }

     public int getErrorCount() {
          return this.Error_Count;
     }
}
