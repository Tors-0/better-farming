package io.github.Tors_0.raesbetterfarming.registry;

import io.github.Tors_0.raesbetterfarming.RaesBetterFarming;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import org.quiltmc.qsl.villager.api.TradeOfferHelper;

import static io.github.Tors_0.raesbetterfarming.RaesBetterFarming.LOGGER;

public class ModTradeOffers {
    public static void init() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.TOOLSMITH, 5,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 15),
                            new ItemStack(ModItems.HAMMER_, 1),
                            3, 5, 0.2f
                    ));
                });

        LOGGER.info("{} finished registering trade offers", RaesBetterFarming.ID);
    }
}
