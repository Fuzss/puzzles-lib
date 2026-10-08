# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v26.3.13-mc26.3.x] - 2026-10-08

### Fixed

- Fix unstable ordering of the recipe unlock advancements generated via `IdBoundRecipeOutput`

## [v26.3.12-mc26.3.x] - 2026-10-07

### Added

- Add `BlockSetFamily.Writable::provideFor` and `BlockSetFamily.Writable::provide` for providing existing blocks, items,
  and entity types to a block set family
- Add `BlockSetFamily::getBaseName`, and `BlockSetFamily::getAll*Variants`

## [v26.3.11-mc26.3.x] - 2026-10-06

### Changed

- Read biome data during biome modification from Fabric API's live `BiomeModificationContext`
- Use the public `TagBuilder` methods in `FabricTagAppender` instead of Fabric API internals

### Fixed

- Fix a possible crash while loading registries when no pending registration is available for modifying enchantments on
  NeoForge

## [v26.3.10-mc26.3.x] - 2026-10-05

### Added

- Add `ModifyEnchantmentsCallback`
- Add back `CalculateLivingVisibilityCallback`
- Add back `GameplayContentContext::registerFuel` and `GameplayContentContext::registerCompostable` for registering fuel
  values and composter values via data-driven context providers

### Changed

- Rework `BlockSetFamily` registration for wooden cooking times and flammability to use `GameplayContentContext`
- Remove the `hasProjectile` argument from `ArrowLooseCallback`

### Fixed

- Fix `ComputeFovModifierCallback` receiving the FOV effect scale instead of the actual FOV modifier on Fabric
- Fix NeoForge block state model baking with the updated model baking API

### Removed

- Remove the internal `Defaulted*` event value classes in favor of the `Mutable*` variants

## [v26.3.9-mc26.3.x] - 2026-09-30

### Changed

- Generate registry objects for built-in packs via `GatherDataEvent#getPackGenerator` on NeoForge

### Fixed

- Fix NeoForge config types for the renamed local and synced config types

## [v26.3.8-mc26.3.x] - 2026-09-29

### Added

- Add `BlockSetFamily#VARIANT_WOODEN_COOKING_TIME` and `BlockSetFamily::registerFor` for `ItemComponentsContext` to
  register cooking fuel values for wooden block set families
- Add `RenderPipelinesContext::registerOptionalPipeline` and `RenderPipelinesContext::registerOitPipelineSet`

### Fixed

- Fix an extra movement packet being sent when a mod intercepts item use with a non-`SUCCESS` result, causing
  `Invalid move player packet received` while moving on Fabric
- Ensure the carried item is sent before the item use packet when a mod intercepts item use for parity with NeoForge on
  Fabric

## [v26.3.7-mc26.3.x] - 2026-09-28

### Added

- Add `DataProviderContext::getInputs`

### Changed

- Deprecate `DataProviderContext::getWorldRegistries` in favor of the full registry lookup provider
- Disable the `white-list` in the dedicated server properties used for run configurations

### Fixed

- Fix the custom biome modifier implementation on NeoForge

## [v26.3.6-mc26.3.x] - 2026-09-26

### Changed

- Support registering `ContextKeySet` holders in `DataProviderBuilder`

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
