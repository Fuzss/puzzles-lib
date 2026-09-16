package fuzs.puzzleslib.common.impl.content;

import com.google.common.collect.Sets;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import fuzs.puzzleslib.common.impl.PuzzlesLib;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.commands.ArgProvider;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.Objects;

/**
 * @see net.minecraft.server.commands.data.EntityDataAccessor
 */
public class ItemDataAccessor implements DataAccessor {
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(entity -> Component.translatableEscape(
            "commands.enchant.failed.entity",
            entity));
    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(entity -> Component.translatableEscape(
            "commands.enchant.failed.itemless",
            entity));
    public static final ArgProvider.Factory<DataAccessor> PROVIDER = (String argumentName) -> ArgProvider.create("item",
            () -> Commands.argument(argumentName, EntityArgument.entity()),
            (CommandContext<CommandSourceStack> context) -> {
                Entity entity = EntityArgument.getEntity(context, argumentName);
                if (!(entity instanceof LivingEntity livingEntity)) {
                    throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
                }
                ItemStack itemStack = livingEntity.getMainHandItem();
                if (itemStack.isEmpty()) {
                    throw ERROR_NO_ITEM.create(entity.getName().getString());
                }
                return new ItemDataAccessor(context.getSource().registryAccess(), itemStack);
            });

    private final RegistryAccess registryAccess;
    private final ItemStack item;

    public ItemDataAccessor(RegistryAccess registryAccess, ItemStack item) {
        this.registryAccess = registryAccess;
        this.item = item;
    }

    @Override
    public void setData(CompoundTag compoundTag) {
        RegistryOps<Tag> registryOps = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
        DataComponentMap.CODEC.parse(registryOps, compoundTag)
                .resultOrPartial()
                .ifPresent((DataComponentMap updatedComponents) -> {
                    DataComponentMap previousComponents = this.item.getComponents();
                    this.item.applyComponents(this.constructDataComponentPatch(previousComponents, updatedComponents));
                });
    }

    <T> DataComponentPatch constructDataComponentPatch(DataComponentMap previousComponents, DataComponentMap updatedComponents) {
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        for (DataComponentType<?> type : Sets.union(previousComponents.keySet(), updatedComponents.keySet())) {
            T value = (T) updatedComponents.get(type);
            if (!updatedComponents.has(type)) {
                builder.remove(type);
            } else if (!Objects.equals(previousComponents.get(type), value)) {
                builder.set((DataComponentType<T>) type, value);
            }
        }
        return builder.build();
    }

    @Override
    public CompoundTag getData() {
        RegistryOps<Tag> registryOps = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
        return DataComponentMap.CODEC.encodeStart(registryOps, this.item.getComponents())
                .resultOrPartial(PuzzlesLib.LOGGER::error)
                .map((Tag tag) -> tag instanceof CompoundTag compoundTag ? compoundTag : null)
                .orElseGet(CompoundTag::new);
    }

    @Override
    public Component getModifiedSuccess() {
        return Component.translatable("commands.data.entity.modified", this.item.getDisplayName());
    }

    @Override
    public Component getPrintSuccess(Tag tag) {
        return Component.translatable("commands.data.entity.query",
                this.item.getDisplayName(),
                NbtUtils.toPrettyComponent(tag));
    }

    @Override
    public Component getPrintSuccess(NbtPathArgument.NbtPath path, double scale, int value) {
        return Component.translatable("commands.data.entity.get",
                path.asString(),
                this.item.getDisplayName(),
                String.format(Locale.ROOT, "%.2f", scale),
                value);
    }
}
