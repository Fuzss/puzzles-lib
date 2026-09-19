package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.common.api.biome.v2.BiomeLoadingPhase;
import fuzs.puzzleslib.common.api.biome.v2.BiomeSelector;
import fuzs.puzzleslib.common.api.biome.v2.context.*;
import fuzs.puzzleslib.common.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.impl.biome.BiomeContext;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import fuzs.puzzleslib.neoforge.impl.biome.*;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.PackOutput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class BiomeModificationsContextNeoForgeImpl implements BiomeModificationsContext {
    private final Multimap<BiomeLoadingPhase, Map.Entry<BiomeSelector, fuzs.puzzleslib.common.api.biome.v2.BiomeModifier>> biomeModifications = HashMultimap.create();
    private final String modId;
    private final IEventBus eventBus;

    public BiomeModificationsContextNeoForgeImpl(String modId, IEventBus eventBus) {
        this.modId = modId;
        this.eventBus = eventBus;
    }

    @Override
    public void registerBiomeModification(BiomeLoadingPhase loadingPhase, BiomeSelector selector, fuzs.puzzleslib.common.api.biome.v2.BiomeModifier modifier) {
        Objects.requireNonNull(loadingPhase, "loading phase is null");
        Objects.requireNonNull(selector, "selector is null");
        Objects.requireNonNull(modifier, "modifier is null");
        if (this.biomeModifications.isEmpty()) {
            BiomeModifier biomeModifierImpl = new BiomeModifierImpl();
            DeferredRegister<MapCodec<? extends BiomeModifier>> deferredRegister = DeferredRegister.create(
                    NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
                    this.modId);
            deferredRegister.register(this.eventBus);
            Holder<MapCodec<? extends BiomeModifier>> holder = deferredRegister.register("biome_modifications",
                    biomeModifierImpl::codec);
            DataProviderBuilder.of(this.modId, (DataProviderContext context) -> {
                return new JsonCodecProvider<>(context.getPackOutput(),
                        PackOutput.Target.DATA_PACK,
                        NeoForgeRegistries.Keys.BIOME_MODIFIERS.identifier().toString().replace(':', '/'),
                        BiomeModifier.DIRECT_CODEC,
                        context.getRegistries(),
                        context.getModId()) {
                    @Override
                    protected void gather() {
                        this.unconditional(holder.getKey().identifier(), biomeModifierImpl);
                    }
                };
            });
        }

        this.biomeModifications.put(loadingPhase, Map.entry(selector, modifier));
    }

    /**
     * Originally somewhat inspired by <a
     * href="https://github.com/teamfusion/rottencreatures/blob/1.19.2/forge/src/main/java/com/github/teamfusion/platform/common/worldgen/forge/BiomeManagerImpl.java">BiomeManager</a>
     * from <a href="https://github.com/teamfusion/rottencreatures">Rotten Creatures mod</a>.
     */
    private class BiomeModifierImpl implements BiomeModifier {
        private static final Map<Phase, BiomeLoadingPhase> BIOME_PHASE_CONVERSIONS = Maps.immutableEnumMap(ImmutableMap.of(
                Phase.ADD,
                BiomeLoadingPhase.ADD,
                Phase.REMOVE,
                BiomeLoadingPhase.REMOVE,
                Phase.MODIFY,
                BiomeLoadingPhase.MODIFY,
                Phase.AFTER_EVERYTHING,
                BiomeLoadingPhase.POST));

        private final MapCodec<? extends BiomeModifier> codec = MapCodec.unit(this);

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            // no equivalent for BEFORE_EVERYTHING exists on Fabric, so we don't use it;
            // therefore, it is possible for no mapping to be found
            BiomeLoadingPhase biomeLoadingPhase = BIOME_PHASE_CONVERSIONS.get(phase);
            if (biomeLoadingPhase != null) {
                Collection<Map.Entry<BiomeSelector, fuzs.puzzleslib.common.api.biome.v2.BiomeModifier>> biomeModification = BiomeModificationsContextNeoForgeImpl.this.biomeModifications.get(
                        biomeLoadingPhase);
                if (!biomeModification.isEmpty()) {
                    MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
                    Objects.requireNonNull(minecraftServer, "minecraft server is null");
                    RegistryAccess registryAccess = minecraftServer.registryAccess();
                    BiomeContext biomeContext = createModificationContext(builder);
                    for (Map.Entry<BiomeSelector, fuzs.puzzleslib.common.api.biome.v2.BiomeModifier> entry : biomeModification) {
                        if (entry.getKey().test(registryAccess, biome)) {
                            entry.getValue().accept(registryAccess, biome, biomeContext);
                        }
                    }
                }
            }
        }

        private static BiomeContext createModificationContext(ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            AttributesContext attributes = new AttributesContextNeoForgeImpl(builder.getAttributes());
            ClimateContext climate = new ClimateContextNeoForgeImpl(builder.getClimateSettings());
            EffectsContext effects = new EffectsContextNeoForgeImpl(builder.getSpecialEffects());
            GenerationContext generation = new GenerationContextNeoForgeImpl(builder.getGenerationSettings());
            MobSpawnsContext mobSpawns = new MobSpawnsContextNeoForgeImpl(builder.getMobSpawnSettings());
            return new BiomeContext(attributes, climate, effects, generation, mobSpawns);
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return this.codec;
        }
    }
}
