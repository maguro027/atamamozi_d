package waterpunch.atamamozi_d.plugin.race;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import waterpunch.atamamozi_d.plugin.race.checkpoint.CheckPointLoc;
import waterpunch.atamamozi_d.plugin.tool.Race_Type;

public class Race {

     private Player creator;
     private String race_name;
     private Race_Type race_type;
     private Material icon;
     private int join_amount;
     private ArrayList<Location> CheckPoint = new ArrayList<>();
     private ArrayList<Integer> CheckPoint_R = new ArrayList<>();
     private ArrayList<CheckPointLoc> CheckPoint_Loc = new ArrayList<>();
     private int Error_Count;

     public Race(Player creator) {
          this.creator = creator;
          this.race_name = "DEFAULT";
          this.race_type = Race_Type.RUN;
          this.icon = Material.MAP;
     }

     public void setCheckPointLoc(Location loc, int r) {}

     public Player getCreator() {
          return creator;
     }

     public void setRace_name(String race_name) {
          this.race_name = race_name;
     }

     public Race_Type getRace_Type() {
          return race_type;
     }

     public void setRace_Type(Race_Type race_type) {
          this.race_type = race_type;
     }

     public String getRace_name() {
          return race_name;
     }

     public void setIcon(Material icon) {
          this.icon = icon;
     }

     public Material getIcon() {
          return icon;
     }

     public void setJoin_Amount(int join_amount) {
          this.join_amount = join_amount;
     }

     public int getJoin_Amount() {
          return join_amount;
     }

     public ArrayList<Location> getCheckPoint() {
          return CheckPoint;
     }

     public ArrayList<Integer> getCheckPoint_R() {
          return CheckPoint_R;
     }

     public void setErrorCount(int i) {
          this.Error_Count = i;
     }

     public void addErrorCount() {
          this.Error_Count++;
     }

     public int getErrorCount() {
          return Error_Count;
     }

     @Override
     public String toString() {
          String json = "RaceInfo{" + "creator='" + creator + '\'' + ", race_name=" + race_name + '\'' + ", race_type=" + race_type + ", icon=" + icon + '\'' + ", join_amount=" + join_amount + '\'' + ", race_loc=" + CheckPoint + '}';

          return json;
     }
}
