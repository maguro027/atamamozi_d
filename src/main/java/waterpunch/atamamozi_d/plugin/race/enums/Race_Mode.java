package waterpunch.atamamozi_d.plugin.race.enums;

public enum Race_Mode {
     WAIT(0),
     RUN(1),
     GOAL(2),
     EDIT(3);

     @SuppressWarnings("unused")
     private int id;

     private Race_Mode(int id) {
          this.id = id;
     }
}
