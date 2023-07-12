package waterpunch.atamamozi_d.plugin.tool.Timers;

public enum Race_Timer_Type {
     WAIT(1),
     START(2);

     @SuppressWarnings("unused")
     private int id;

     private Race_Timer_Type(int id) {
          this.id = id;
     }
}
