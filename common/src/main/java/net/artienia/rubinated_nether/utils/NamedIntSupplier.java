package net.artienia.rubinated_nether.utils;

import java.util.function.IntSupplier;

public record NamedIntSupplier(String name, IntSupplier provider) implements IntSupplier {

    @Override
    public int getAsInt() {
        return provider.getAsInt();
    }
}
