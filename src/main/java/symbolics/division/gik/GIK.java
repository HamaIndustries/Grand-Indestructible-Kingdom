package symbolics.division.gik;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import symbolics.division.gik.block.CardboardBlock;
import symbolics.division.gik.block.VerticalCardboardBlock;

import java.util.function.Function;

public class GIK implements ModInitializer {
    public static final String MOD_ID = "gik";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier id(String id) {
        return Identifier.of(MOD_ID, id);
    }

    public static final RegistryKey<Block> CARDBOARD_KEY = RegistryKey.of(RegistryKeys.BLOCK, id("cardboard"));
    public static final Block CARDBOARD = Registry.register(Registries.BLOCK, CARDBOARD_KEY, new CardboardBlock(AbstractBlock.Settings.create().registryKey(CARDBOARD_KEY)));
    public static final RegistryKey<Item> CARDBOARD_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, id("cardboard"));
    public static final Item CARDBOARD_ITEM = Registry.register(Registries.ITEM, CARDBOARD_ITEM_KEY, new BlockItem(CARDBOARD, new Item.Settings().registryKey(CARDBOARD_ITEM_KEY)));
    public static final Block SOAKED_CARDBOARD = registerBlock("soaked_cardboard", CardboardBlock::new);

    public static final RegistryKey<Block> VERTICAL_CARDBOARD_KEY = RegistryKey.of(RegistryKeys.BLOCK, id("vertical_cardboard"));
    public static final Block VERTICAL_CARDBOARD = Registry.register(Registries.BLOCK, VERTICAL_CARDBOARD_KEY, new VerticalCardboardBlock(AbstractBlock.Settings.create().registryKey(VERTICAL_CARDBOARD_KEY)));
    public static final RegistryKey<Item> VERTICAL_CARDBOARD_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, id("vertical_cardboard"));
    public static final Item VERTICAL_CARDBOARD_ITEM = Registry.register(Registries.ITEM, VERTICAL_CARDBOARD_ITEM_KEY, new BlockItem(VERTICAL_CARDBOARD, new Item.Settings().registryKey(VERTICAL_CARDBOARD_ITEM_KEY)));
    public static final Block SOAKED_VERTICAL_CARDBOARD = registerBlock("soaked_vertical_cardboard", VerticalCardboardBlock::new);

    public static final Block CARDBOARD_TRAPDOOR = registerBlock("cardboard_trapdoor", settings -> new TrapdoorBlock(BlockSetType.OAK, settings));
    public static final Item CARDBOARD_TRAPDOOR_ITEM = registerItem("cardboard_trapdoor", settings -> new BlockItem(CARDBOARD_TRAPDOOR, settings));

    public static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> blockProvider) {
        Identifier id = id(name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        return Registry.register(Registries.BLOCK, key, blockProvider.apply(AbstractBlock.Settings.create().registryKey(key)));
    }

    public static Item registerItem(String name, Function<Item.Settings, Item> itemProvider) {
        Identifier id = id(name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        return Registry.register(Registries.ITEM, id, itemProvider.apply(new Item.Settings().registryKey(key)));
    }


    @Override
    public void onInitialize() {

    }
}