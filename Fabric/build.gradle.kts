import fuzs.multiloader.extension.packageName

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-fabric")
}

dependencies {
    modApi(sharedLibs.fabricapi.fabric)
    modApi(sharedLibs.forgeconfigapiport.fabric)
}

multiloader {
    modFile {
        packagePrefix.set("impl")
        library.set(true)
    }

    mixins {
        plugin.set("${project.group}.${project.packageName}.mixin.MixinConfigPluginFabricImpl")
        mixin(
            "AbstractHorseFabricMixin",
            "AnimalFabricMixin",
            "AnvilMenuFabricMixin",
            "BlockEntityFabricMixin",
            "BlockFabricMixin",
            "BoneMealItemFabricMixin",
            "BowItemFabricMixin",
            "CatFabricMixin",
            "ChunkMapFabricMixin",
            "CrossbowItemFabricMixin",
            "EnderManFabricMixin",
            "EntityFabricMixin",
            "ExperienceOrbFabricMixin",
            "FoxBreedGoalFabricMixin",
            "GrindstoneMenu\u0024ResultSlotFabricMixin",
            "GrindstoneMenuFabricMixin",
            "ItemEntityFabricMixin",
            "LivingEntityFabricMixin",
            "MagmaCubeFabricMixin",
            "MenuProviderWithDataFabricMixin",
            "MobFabricMixin",
            "MonsterFabricMixin",
            "NaturalSpawnerFabricMixin",
            "OcelotFabricMixin",
            "PackRepositoryFabricMixin",
            "ParrotFabricMixin",
            "PlayerChunkSenderFabricMixin",
            "PlayerFabricMixin",
            "ProjectileFabricMixin",
            "ReloadableServerResourcesFabricMixin",
            "RunAroundLikeCrazyGoalFabricMixin",
            "ServerExplosionFabricMixin",
            "ServerLevelFabricMixin",
            "ServerPlayerFabricMixin",
            "StartAttackingFabricMixin",
            "ThrownEnderpearlFabricMixin",
            "WolfFabricMixin"
        )
        clientMixin(
            "AbstractClientPlayerFabricMixin",
            "CameraFabricMixin",
            "ChatListenerFabricMixin",
            "ClientLevelFabricMixin",
            "ClientPacketListenerFabricMixin",
            "EffectsInInventoryFabricMixin",
            "EntityRendererFabricMixin",
            "FirstPersonHandsAndItemsRendererFabricMixin",
            "FogRendererFabricMixin",
            "GameRendererFabricMixin",
            "GuiFabricMixin",
            "GuiGraphicsExtractorFabricMixin",
            "KeyboardHandlerFabricMixin",
            "KeyMappingFabricMixin",
            "LivingEntityRendererFabricMixin",
            "LocalPlayerFabricMixin",
            "MinecraftFabricMixin",
            "MouseHandlerFabricMixin",
            "OptionsFabricMixin",
            "SkullBlockRendererFabricMixin",
            "ToastComponentFabricMixin"
        )
        serverMixin("ServerMainFabricMixin")
    }
}
