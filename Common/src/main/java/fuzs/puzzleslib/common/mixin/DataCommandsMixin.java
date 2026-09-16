package fuzs.puzzleslib.common.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fuzs.puzzleslib.common.impl.content.ItemDataAccessor;
import net.minecraft.server.commands.ArgProvider;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(DataCommands.class)
abstract class DataCommandsMixin {

    @ModifyExpressionValue(method = "<clinit>",
                           at = @At(value = "FIELD",
                                    target = "Lnet/minecraft/server/commands/data/DataCommands;ALL_PROVIDERS:Ljava/util/List;",
                                    opcode = Opcodes.GETSTATIC))
    private static List<ArgProvider.Factory<DataAccessor>> clinit(List<ArgProvider.Factory<DataAccessor>> allProviders) {
        return ImmutableList.<ArgProvider.Factory<DataAccessor>>builder()
                .addAll(allProviders)
                .add(ItemDataAccessor.PROVIDER)
                .build();
    }
}
