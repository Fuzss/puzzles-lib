package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.common.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.fabric.impl.core.context.*;

public final class FabricModConstructor implements ModConstructorImpl<ModConstructor> {

    @Override
    public void construct(String modId, ModConstructor modConstructor) {
        modConstructor.onConstructMod();
        modConstructor.onCommonSetup();
        modConstructor.onRegisterPayloadTypes(FabricProxy.get().createPayloadTypesContext(modId));
        modConstructor.onRegisterEntityAttributes(new EntityAttributesContextFabricImpl());
        modConstructor.onRegisterSpawnPlacements(new SpawnPlacementsContextFabricImpl());
        modConstructor.onRegisterGameplayContent(new GameplayContentContextFabricImpl());
        modConstructor.onRegisterBiomeTransformations(new BiomeTransformationsContextFabricImpl(modId));
        modConstructor.onAddDataPackFinders(new DataPackSourcesContextFabricImpl());
        modConstructor.onRegisterGameRegistries(new GameRegistriesContextFabricImpl());
        modConstructor.onRegisterDataPackRegistries(new DataPackRegistriesContextFabricImpl());
        modConstructor.onAddDataPackReloadListeners(new DataPackReloadListenersContextFabricImpl());
        modConstructor.onRegisterItemComponentPatches(new ItemComponentsContextFabricImpl());
    }
}
