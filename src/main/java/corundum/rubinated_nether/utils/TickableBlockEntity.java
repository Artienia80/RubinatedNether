package corundum.rubinated_nether.utils;

/**
 * Simple interface that when used together with {@link BEBlock} provides automatic ticking
 */
public interface TickableBlockEntity {

    /**
     * Override this if you need 1 tick method for both client and server
     * @param clientSide true if ticking on the logical client
     */
    default void tick(boolean clientSide) {
        if(clientSide) clientTick();
        else tick();
    }

    /**
     * Handles client-side ticking
     */
    default void clientTick() {}

    /**
     * Handle server-side ticking
     */
    void tick();
}
