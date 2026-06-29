package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RubinationNames {
	private static final ResourceLocation ALT_FONT = RubinatedNether.id("rubin");
	private static final Style ROOT_STYLE;
	private static final RubinationNames INSTANCE;
	private final RandomSource random = RandomSource.create();
	private final String[] words = new String[]{"ruby", "coal", "deal", "blue", "fire", "cold", "free", "cube", "self", "ball", "grow", "arti", "love", "wife", "kolo"};

	public static RubinationNames getInstance() {
		return INSTANCE;
	}

	public FormattedText getRandomName(Font fontRenderer, int maxWidth) {
		var word = Util.getRandom(this.words, this.random);
		var result = fontRenderer.getSplitter().headByWidth(
				Component.literal(word).withStyle(ROOT_STYLE),
				maxWidth,
				Style.EMPTY
		);

		return result;
	}

	public void initSeed(long seed) {
		this.random.setSeed(seed);
	}

	static {
		ROOT_STYLE = Style.EMPTY.withFont(ALT_FONT);
		INSTANCE = new RubinationNames();
	}
}
