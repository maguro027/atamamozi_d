package waterpunch.atamamozi_d.plugin.tool;

public enum Race_Mode {
     WAIT(1),
     RUN(2),
     GOAL(3);

     @SuppressWarnings("unused")
     private int id;

     private Race_Mode(int id) {
          this.id = id;
     }
}
