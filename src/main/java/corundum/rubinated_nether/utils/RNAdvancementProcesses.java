package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RNAdvancementProcesses {
	public record Process(ResourceLocation id, String translationKey, List<ResourceLocation> members,
						  ResourceLocation obtainedFrame, ResourceLocation unobtainedFrame, int frameWidth) {
		public Process {
			members = List.copyOf(members);
		}
	}

	private static final Map<ResourceLocation, Process> BY_ADVANCEMENT = new HashMap<>();

	public static final Process POCKETWATCH = register(new Process(
			RubinatedNether.id("pocketwatch"),
			"advancement_process.rubinated_nether.pocketwatch",
			List.of(
					RubinatedNether.id("craft_pocketwatch"),
					RubinatedNether.id("charge_pocketwatch"),
					RubinatedNether.id("overcharge_pocketwatch"),
					RubinatedNether.id("escape_death"),
					RubinatedNether.id("repair_pocketwatch")
			),
			RubinatedNether.id("advancements/pocketwatch/obtained"),
			RubinatedNether.id("advancements/pocketwatch/unobtained"),
			33
	));

	private static Process register(Process process) {
		for (ResourceLocation member : process.members()) {
			BY_ADVANCEMENT.put(member, process);
		}
		return process;
	}

		@Nullable
	public static Process get(ResourceLocation advancementId) {
		return BY_ADVANCEMENT.get(advancementId);
	}

	private RNAdvancementProcesses() {}
}
