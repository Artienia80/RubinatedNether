package net.artienia.rubinated_nether.integrations;

public interface CompatHandler {

    void init();

    default void setup() {}

    default void clientSetup() {}
}
