package symbolics.division.gik;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.minecraft.client.data.BlockStateModelGenerator.*;
import static symbolics.division.gik.block.CardboardBlock.AXIS;
import static symbolics.division.gik.block.CardboardBlock.SINISTER;

public class GIKDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(GIKModels::new);
    }

    public static class GIKModels extends FabricModelProvider {
        public GIKModels(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            Identifier id = TextureMap.getId(GIK.CARDBOARD);
            TextureMap textures = getBlockTexture(id, false);
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(
                    Models.CUBE_DIRECTIONAL.upload(GIK.CARDBOARD, textures, blockStateModelGenerator.modelCollector)
            );
            TextureMap texturesSwap = getBlockTexture(id.withSuffixedPath("_swap"), true);
            WeightedVariant weightedVariantSwap = BlockStateModelGenerator.createWeightedVariant(
                    Models.CUBE_DIRECTIONAL.upload(GIK.CARDBOARD, "_swap", texturesSwap, blockStateModelGenerator.modelCollector)
            );
            blockStateModelGenerator.registerItemModel(GIK.CARDBOARD);
            blockStateModelGenerator.blockStateCollector
                    .accept(
                            VariantsBlockModelDefinitionCreator.of(GIK.CARDBOARD)
                                    .with(createBooleanModelMap(SINISTER, weightedVariantSwap, weightedVariant))
                                    .coordinate(
                                            BlockStateVariantMap.operations(AXIS)
                                                    .register(Direction.Axis.Y, ROTATE_X_90)
                                                    .register(Direction.Axis.X, ROTATE_Y_90)
                                                    .register(Direction.Axis.Z, NO_OP)
                                    )
                    );
        }

        private static TextureMap getBlockTexture(Identifier id, boolean swap) {
            return new TextureMap()
                    .put(TextureKey.TEXTURE, id)
                    .put(TextureKey.NORTH, TextureMap.getSubId(GIK.CARDBOARD, swap ? "_front_swap" : "_front"))
                    .put(TextureKey.SOUTH, TextureMap.getSubId(GIK.CARDBOARD, swap ? "_front_swap" : "_front"))
                    .put(TextureKey.EAST, TextureMap.getSubId(GIK.CARDBOARD, swap ? "_top" : "_side"))
                    .put(TextureKey.WEST, TextureMap.getSubId(GIK.CARDBOARD, swap ? "_top" : "_side"))
                    .put(TextureKey.UP, TextureMap.getSubId(GIK.CARDBOARD, swap ? "_side" : "_top"))
                    .put(TextureKey.DOWN, TextureMap.getSubId(GIK.CARDBOARD, swap ? "_side" : "_top"))
                    .put(TextureKey.PARTICLE, TextureMap.getSubId(GIK.CARDBOARD, "_top"));
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
//            itemModelGenerator.register
//			itemModelGenerator.registerWithTextureSource(GIK.CARDBOARD_ITEM, Items.OAK_LOG, Models.GENERATED);
        }
    }
}
