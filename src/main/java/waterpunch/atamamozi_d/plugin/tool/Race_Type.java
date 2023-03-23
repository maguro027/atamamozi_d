package waterpunch.atamamozi_d.plugin.tool;

public enum Race_Type {
     WALK(1),
     BOAT(2);

     // RUN(Material.TOTEM_OF_UNDYING), BOAT(Material.OAK_BOAT);

     @SuppressWarnings("unused")
     private int id;

     private Race_Type(int id) {
          this.id = id;
     }
}
