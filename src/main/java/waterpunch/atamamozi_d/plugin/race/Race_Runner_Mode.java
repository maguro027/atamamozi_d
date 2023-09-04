package waterpunch.atamamozi_d.plugin.race;

public enum Race_Runner_Mode {
     NO_ENTRY(0),
     WAIT(1),
     RUN(2),
     ALL_GOAL_WAIT(3),
     GOAL(4),
     EDIT(5);

     @SuppressWarnings("unused")
     private int id;

     private Race_Runner_Mode(int id) {
          this.id = id;
     }
}