package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;

public final class RNAdvancementAwards {
	private RNAdvancementAwards() {
	}

	public static void award(ServerPlayer player, String advancement, String criterion) {
		AdvancementHolder holder = player.server.getAdvancements().get(RubinatedNether.id(advancement));
		if (holder != null) {
			player.getAdvancements().award(holder, criterion);
		}
	}
}
