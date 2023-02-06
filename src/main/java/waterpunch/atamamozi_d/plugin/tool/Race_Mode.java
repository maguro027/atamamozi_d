package waterpunch.atamamozi_d.plugin.tool;

public enum Race_Mode {
     WAIT(0),
     RUN(1),
     GOAL(2);

     @SuppressWarnings("unused")
     private int id;

     private Race_Mode(int id) {
          this.id = id;
     }
}
