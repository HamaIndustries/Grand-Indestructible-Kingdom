package symbolics.division.gik;

import net.fabricmc.api.ModInitializer;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import symbolics.division.gik.block.CardboardBlock;

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

	@Override
	public void onInitialize() {

	}
}