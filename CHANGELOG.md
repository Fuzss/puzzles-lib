# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v26.3.5-mc26.3.x] - 2026-09-26

### Added

- Add `ItemClickBehaviorCallback`

### Changed

- Make `DataPackRegistriesContext::registerReloadableRegistry` register an actual reloadable registry on NeoForge

### Fixed

- Fix status bar heights registered via `GuiLayersContext` on Fabric
- Fix `NewDatapackRegistryEvent` registration on NeoForge

## [v26.3.4-mc26.3.x] - 2026-09-23

### Added

- Add `TooltipBuilder::setExtraSpaceAfterFirstLine` for restoring the extra space vanilla inserts after the first
  tooltip line

### Changed

- Rework some methods in `DataProviderBuilder`

### Fixed

- Fix `SpawnerDataBuilder` passing arguments to `addSpawn` in the wrong order

## [v26.3.3-mc26.3.x] - 2026-09-22

### Added

- Add `DataPackRegistriesContext::registerReloadableRegistry` for registering reloadable registries

### Changed

- Update `GameplayContentContext` to use the new `TRANSFORMABLES` data map on NeoForge

## [v26.3.2-mc26.3.x] - 2026-09-21

### Fixed

- Fix `IdBoundRecipeOutput` creating recipe holders in foreign namespaces, which failed data generation

## [v26.3.1-mc26.3.x] - 2026-09-20

### Fixed

- Fix `EndermanFabricMixin` name in Mixin config

## [v26.3.0-mc26.3.x] - 2026-09-20

### Added

- Rework biome modification API, most notably introducing `BiomeSelector`, `BiomeTransformer` and `AttributesContext`
- Add `AbstractLootSubProvider`, `AbstractBlockLootSubProvider` and `AbstractEntityLootSubProvider` replacing
  `AbstractLootProvider`
- Add `AbstractBrewingProvider` replacing `RegisterPotionBrewingMixesCallback`
- Add `DataProviderBuilder` replacing `DataProviderHelper` on NeoForge
- Add `SubmitArmWithItemCallback` replacing `RenderHandEvents`

### Changed

- Update to Minecraft 26.3.x
- Rework `AbstractAdvancementProvider` and `AbstractRecipeProvider` around `BootstrapContext`
- Make `AbstractLanguageProvider` implement `TranslationBuilder`
- Rework `DataProviderContext` methods
- Rework `AbstractModPackResources` and `PackResourcesHelper` while introducing additional classes
- Allow `RenderStateExtraData` to handle render states other than for entities
- Rework `MutableBakedQuad`, removing NeoForge only method from the shared implementation surface
- Deprecate and slim down `ShapesHelper` in favor of vanilla's shape rotation methods
- Adjusted status bar heights registered via `GuiLayersContext` for changes in Fabric API
- Rename `SimpleContainerImpl` as `ContainerTemplate`

### Fixed

- Fix `AbstractTagAppender::addOptional` calling `add` instead of `addOptional` for its varargs overload

### Removed

- Remove `AbstractDatapackRegistriesProvider` while moving all helper methods to `ContentRegistrationHelper`
- Remove `RegistriesDataProvider`
- Remove `DynamicPackResources`
- Remove `GameRenderEvents` and `RenderBlockOverlayCallback`
- Remove `GameplayContentContext::registerFuel` and `GameplayContentContext::registerCompostable`
- Remove `ContentRegistrationHelper::registerContextKeySet`
- Remove most of `ResourceKeyHelper`, while moving the remaining methods to `ContentRegistrationHelper`
- Remove outdated trim support from `AbstractAtlasProvider`
- Remove `CalculateLivingVisibilityCallback`
