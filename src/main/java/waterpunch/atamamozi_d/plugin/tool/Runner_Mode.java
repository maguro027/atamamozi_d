package waterpunch.atamamozi_d.plugin.tool;

public enum Runner_Mode {
     WAIT(1),
     RUN(2),
     GOAL(3);

     @SuppressWarnings("unused")
     private int id;

     private Runner_Mode(int id) {
          this.id = id;
     }
}
