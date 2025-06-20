package corundum.rubinated_nether.content;

public enum TarnishingBronzeStep {
    BRONZE,
    DISCOLORED,
    CORRODED,
    TARNISHED,
    CRYSTALLIZED;

    public static TarnishingBronzeStep byIndex(int id) {
        return TarnishingBronzeStep.values()[id];
    }
}
