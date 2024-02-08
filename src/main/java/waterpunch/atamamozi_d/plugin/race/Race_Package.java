package waterpunch.atamamozi_d.plugin.race;

// import java.util.Calendar;
import java.util.UUID;

public class Race_Package {

     private UUID Race_ID;
     private int Count;

     // private Calendar Cal;

     public Race_Package(UUID Race_ID) {
          this.Race_ID = Race_ID;
     }

     public UUID getRace_ID() {
          return Race_ID;
     }

     public int getJoinCount() {
          return Count;
     }

     public void addJoinCount() {
          this.Count++;
     }

     public void addJoinCount(int add) {
          this.Count = Count + add;
     }
}
