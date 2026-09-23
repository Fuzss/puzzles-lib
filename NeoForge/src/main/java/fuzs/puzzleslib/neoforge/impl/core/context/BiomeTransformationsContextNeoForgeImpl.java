package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.common.api.biome.v2.BiomeLoadingPhase;
import fuzs.puzzleslib.common.api.biome.v2.BiomeSelector;
import fuzs.puzzleslib.common.api.biome.v2.BiomeTransformer;
import fuzs.puzzleslib.common.api.biome.v2.context.*;
import fuzs.puzzleslib.common.api.core.v1.context.BiomeTransformationsContext;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.impl.biome.BiomeTransformerContextImpl;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import fuzs.puzzleslib.neoforge.impl.biome.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;
import java.util.Objects;

public final class BiomeTransformationsContextNeoForgeImpl implements BiomeTransformationsContext {
    private final Multimap<BiomeLoadingPhase, BiomeTransformer> transformers = HashMultimap.create();
    private final String modId;
    private final IEventBus eventBus;

    public BiomeTransformationsContextNeoForgeImpl(String modId, IEventBus eventBus) {
        this.modId = modId;
        this.eventBus = eventBus;
    }

    @Override
    public void registerBiomeTransformation(BiomeLoadingPhase loadingPhase, BiomeSelector selector, BiomeTransformer transformer) {
        Objects.requireNonNull(loadingPhase, "loading phase is null");
        Objects.requireNonNull(selector, "selector is null");
        Objects.requireNonNull(transformer, "transformer is null");
        if (this.transformers.isEmpty()) {
            MapCodec<BiomeModifierImpl> mapCodec = this.mapCodec();
            DeferredRegister<MapCodec<? extends BiomeModifier>> deferredRegister = DeferredRegister.create(
                    NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
                    this.modId);
            deferredRegister.register(this.eventBus);
            Holder<MapCodec<? extends BiomeModifier>> holder = deferredRegister.register("biome_modifications",
                    () -> mapCodec);
            DataProviderBuilder.of(this.modId, (DataProviderContext context) -> {
                return new JsonCodecProvider<>(context.getPackOutput(),
                        PackOutput.Target.DATA_PACK,
                        NeoForgeRegistries.Keys.BIOME_MODIFIERS.identifier().toString().replace(':', '/'),
                        BiomeModifier.DIRECT_CODEC,
                        context.getRegistries(),
                        context.getModId()) {
                    @Override
                    protected void gather() {
                        this.unconditional(holder.getKey().identifier(),
                                new BiomeModifierImpl(mapCodec, RegistryAccess.EMPTY));
                    }
                };
            });
        }

        this.transformers.put(loadingPhase,
                (HolderGetter.Provider registries, Holder<Biome> biome, BiomeTransformer.Context context) -> {
                    if (selector.test(registries, biome)) {
                        transformer.accept(registries, biome, context);
                    }
                });
    }

    private MapCodec<BiomeModifierImpl> mapCodec() {
        return MapCodec.recursive("BiomeTransformationsContextNeoForgeImpl.BiomeModifierImpl",
                (Codec<BiomeModifierImpl> codec) -> {
                    return ExtraCodecs.retrieveContext((DynamicOps<?> ops) -> {
                        if (ops instanceof RegistryOps<?> registryOps) {
                            return DataResult.success(new BiomeModifierImpl(MapCodec.assumeMapUnsafe(codec),
                                    registryOps.lookupProvider::lookup));
                        } else {
                            return DataResult.error(() -> "Not a registry ops");
                        }
                    });
                });
    }

    /**
     * Originally somewhat inspired by <a
     * href="https://github.com/teamfusion/rottencreatures/blob/1.19.2/forge/src/main/java/com/github/teamfusion/platform/common/worldgen/forge/BiomeManagerImpl.java">BiomeManager</a>
     * from <a href="https://github.com/teamfusion/rottencreatures">Rotten Creatures mod</a>.
     */
    private class BiomeModifierImpl implements BiomeModifier {
        private static final Map<Phase, BiomeLoadingPhase> LOADING_PHASES = Maps.immutableEnumMap(Map.of(Phase.ADD,
                BiomeLoadingPhase.ADD,
                Phase.REMOVE,
                BiomeLoadingPhase.REMOVE,
                Phase.MODIFY,
                BiomeLoadingPhase.MODIFY,
                Phase.AFTER_EVERYTHING,
                BiomeLoadingPhase.POST));

        private final MapCodec<? extends BiomeModifier> codec;
        private final HolderGetter.Provider registries;

        BiomeModifierImpl(MapCodec<? extends BiomeModifier> codec, HolderGetter.Provider registries) {
            this.codec = codec;
            this.registries = registries;
        }

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            BiomeTransformer.Context context = buildTransformerContext(builder);
            BiomeLoadingPhase loadingPhase = LOADING_PHASES.get(phase);
            // Not all phases may exist in our implementation, so this can be null.
            if (loadingPhase != null) {
                BiomeTransformationsContextNeoForgeImpl.this.transformers.get(loadingPhase)
                        .forEach((BiomeTransformer transformer) -> {
                            transformer.accept(this.registries, biome, context);
                        });
            }
        }

        private static BiomeTransformer.Context buildTransformerContext(ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            AttributesContext attributes = new AttributesContextNeoForgeImpl(builder.getAttributes());
            ClimateContext climate = new ClimateContextNeoForgeImpl(builder.getClimateSettings());
            EffectsContext effects = new EffectsContextNeoForgeImpl(builder.getSpecialEffects());
            GenerationContext generation = new GenerationContextNeoForgeImpl(builder.getGenerationSettings());
            MobSpawnsContext mobSpawns = new MobSpawnsContextNeoForgeImpl(builder.getMobSpawnSettings());
            return new BiomeTransformerContextImpl(attributes, climate, effects, generation, mobSpawns);
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return this.codec;
        }
    }
}
