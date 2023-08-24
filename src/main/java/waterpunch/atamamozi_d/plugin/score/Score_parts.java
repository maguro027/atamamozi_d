package waterpunch.atamamozi_d.plugin.score;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Score_parts {

     private UUID RACE_ID;
     private int COUNT;
     private List<Long> TIMEs = new ArrayList<Long>();

     public Score_parts(UUID race_id, Long time) {
          this.RACE_ID = race_id;
          addTime(time);
     }

     public UUID getRace_ID() {
          return RACE_ID;
     }

     public Boolean addTime(Long time) {
          TIMEs.add(time);
          COUNT++;
          List<Long> onetime = TIMEs.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
          TIMEs = onetime;
          if (TIMEs.size() == 11) TIMEs.remove(10);
          if (TIMEs.get(0) == time) return true;
          return false;
     }

     public Long getTime(int i) {
          return TIMEs.get(i);
     }

     public int getCount() {
          return COUNT;
     }

     public List<Long> getAllScore() {
          return TIMEs;
     }
}
