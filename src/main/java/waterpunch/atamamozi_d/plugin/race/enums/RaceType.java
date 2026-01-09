package waterpunch.atamamozi_d.plugin.race.enums;

public enum RaceType {
    WALK(1),
    BOAT(2);

    @SuppressWarnings("unused")
    private final int id;

    private RaceType(int id) {
        this.id = id;
    }
}
