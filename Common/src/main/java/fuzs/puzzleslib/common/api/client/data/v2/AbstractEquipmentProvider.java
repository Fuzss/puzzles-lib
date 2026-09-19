package fuzs.puzzleslib.common.api.client.data.v2;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * A base implementation of {@link EquipmentAssetProvider} for generating equipment asset definitions for the mod.
 * <p>
 * Subclasses implement {@link #addEquipmentAssets(BiConsumer)} and register equipment client info via the provided
 * consumer, mirroring the vanilla provider. The registered assets are emitted by {@link #run(CachedOutput)}.
 */
public abstract class AbstractEquipmentProvider extends EquipmentAssetProvider {

    /**
     * @param context the data provider context
     */
    public AbstractEquipmentProvider(DataProviderContext context) {
        this(context.getPackOutput());
    }

    /**
     * @param packOutput the pack output instance
     */
    public AbstractEquipmentProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> values = new LinkedHashMap<>();
        this.addEquipmentAssets((ResourceKey<EquipmentAsset> resourceKey, EquipmentClientInfo equipmentClientInfo) -> {
            if (values.putIfAbsent(resourceKey, equipmentClientInfo) != null) {
                throw new IllegalStateException("Tried to register equipment asset twice for id: " + resourceKey);
            }
        });

        return DataProvider.saveAll(cachedOutput, EquipmentClientInfo.CODEC, this.pathProvider::json, values);
    }

    /**
     * Registers all equipment assets of this provider via the given consumer, mirroring the vanilla provider.
     *
     * @param equipmentAssetConsumer the consumer used for registering equipment client info
     */
    public abstract void addEquipmentAssets(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssetConsumer);

    /**
     * Creates equipment client info with only humanoid layers for the given texture identifier.
     *
     * @param identifier the equipment texture identifier
     * @return the equipment client info
     *
     * @see EquipmentAssetProvider#onlyHumanoid(String)
     */
    public static EquipmentClientInfo onlyHumanoid(Identifier identifier) {
        return EquipmentClientInfo.builder().addHumanoidLayers(identifier).build();
    }

    /**
     * Creates equipment client info with humanoid layers and a dyeable horse body layer for the given texture
     * identifier.
     *
     * @param identifier the equipment texture identifier
     * @return the equipment client info
     *
     * @see EquipmentAssetProvider#humanoidAndMountArmor(String)
     */
    public static EquipmentClientInfo humanoidAndHorse(Identifier identifier) {
        return EquipmentClientInfo.builder()
                .addHumanoidLayers(identifier)
                .addLayers(EquipmentClientInfo.LayerType.HORSE_BODY,
                        EquipmentClientInfo.Layer.leatherDyeable(identifier, false))
                .build();
    }

    /**
     * Creates equipment client info with a single layer of the given type for the given texture identifier.
     *
     * @param layerType  the equipment layer type
     * @param identifier the equipment texture identifier
     * @return the equipment client info
     */
    public static EquipmentClientInfo simple(EquipmentClientInfo.LayerType layerType, Identifier identifier) {
        return EquipmentClientInfo.builder().addLayers(layerType, new EquipmentClientInfo.Layer(identifier)).build();
    }
}
