package waterpunch.atamamozi_d.plugin.race.enums;

public enum Race_Type {
    WALK(1),
    BOAT(2);

    @SuppressWarnings("unused")
    private final int id;

    private Race_Type(int id) {
        this.id = id;
    }
}
