package waterpunch.atamamozi_d.plugin.score;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Score_parts {

     private List<Long> TINE = new ArrayList<Long>();
     private int COUNT;
     private UUID RACE_ID;

     public Score_parts(UUID race_id) {
          this.RACE_ID = race_id;
     }

     public void addTime(Long time) {
          this.TINE.add(time);
     }
}
