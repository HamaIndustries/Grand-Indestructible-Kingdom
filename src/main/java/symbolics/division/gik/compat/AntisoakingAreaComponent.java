package symbolics.division.gik.compat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.command.argument.AreaArgument;
import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.component.AreaDataComponentType;
import dev.doublekekse.area_lib.data.AreaSavedData;
import dev.doublekekse.area_lib.registry.AreaDataComponentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import symbolics.division.gik.GIK;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class AntisoakingAreaComponent implements AreaDataComponent {
    public static final AreaDataComponentType<AntisoakingAreaComponent> TYPE =
            AreaDataComponentTypeRegistry.registerTracking(GIK.id("time_zone"), AntisoakingAreaComponent::new);

    private record Data(BlockState state) {
        public static Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.fieldOf("ticks").forGetter(Data::state)
        ).apply(instance, Data::new));

        public static Data NONE = new Data(Blocks.AIR.getDefaultState());
    }

    private Data data = Data.NONE;

    public AntisoakingAreaComponent() {
    }


    @Override
    public void load(AreaSavedData savedData, NbtCompound compoundTag) {
    }

    @Override
    public NbtCompound save() {
        return new NbtCompound();
    }

    public static int restrict(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Area area = AreaArgument.getArea(context, "area");
        area.put(context.getSource().getServer(), TYPE, new AntisoakingAreaComponent());
        context.getSource().sendFeedback(() -> Text.translatable("gik.command.restrict.success"), true);
        return Command.SINGLE_SUCCESS;
    }

    public static int clear(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Area area = AreaArgument.getArea(context, "area");
        area.remove(context.getSource().getServer(), TYPE);
        context.getSource().sendFeedback(() -> Text.translatable("gik.command.clear.success"), true);
        return Command.SINGLE_SUCCESS;
    }


    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) ->
                dispatcher.register(literal("gik")
                        .then(literal("restrict")
                                .then(argument("area", AreaArgument.area())
                                        .then((LiteralArgumentBuilder) literal("please").executes(ctx -> AntisoakingAreaComponent.restrict((CommandContext<ServerCommandSource>) (Object) ctx))
                                        ))
                        )
                ));

        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) ->
                dispatcher.register(literal("gik")
                        .then(literal("clear")
                                .then(argument("area", AreaArgument.area())
                                        .then((LiteralArgumentBuilder) literal("please").executes(ctx -> AntisoakingAreaComponent.clear((CommandContext<ServerCommandSource>) (Object) ctx))
                                        ))
                        )
                ));
    }
}
