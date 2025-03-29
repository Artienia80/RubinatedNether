package corundum.rubinated_nether.content.items;

import net.minecraft.world.item.Item;

public class RuneItem extends Item {
    private Rubination rubination;

    public RuneItem(Properties properties, Rubination rubination) {
        super(properties);
        this.rubination = rubination;
    }

    public Rubination getRubination() {
        return rubination;
    }
}
