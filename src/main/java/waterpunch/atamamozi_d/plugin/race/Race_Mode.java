package waterpunch.atamamozi_d.plugin.race;

public enum Race_Mode {
     EMPTY(0),
     WAIT(1),
     RUN(2),
     GOAL(3),
     EDIT(4);

     @SuppressWarnings("unused")
     private int id;

     private Race_Mode(int id) {
          this.id = id;
     }
}
