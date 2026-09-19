package fuzs.puzzleslib.common.api.biome.v2.context;

import net.minecraft.world.level.biome.Biome;

/**
 * Context for modifying the climate settings of a biome.
 *
 * @see Biome.ClimateSettings
 */
public interface ClimateContext {
    /**
     * Set whether the biome has precipitation.
     *
     * @param hasPrecipitation whether the biome has precipitation
     * @see Biome.ClimateSettings#hasPrecipitation()
     * @see Biome.BiomeBuilder#hasPrecipitation(boolean)
     */
    void hasPrecipitation(boolean hasPrecipitation);

    /**
     * Check whether the biome has precipitation.
     *
     * @return whether the biome has precipitation
     *
     * @see Biome.ClimateSettings#hasPrecipitation()
     * @see Biome.BiomeBuilder#hasPrecipitation(boolean)
     */
    boolean hasPrecipitation();

    /**
     * Set the base temperature of the biome.
     *
     * @param temperature the base temperature
     * @see Biome.ClimateSettings#temperature()
     * @see Biome.BiomeBuilder#temperature(float)
     */
    void setTemperature(float temperature);

    /**
     * Get the base temperature of the biome.
     *
     * @return the base temperature
     *
     * @see Biome.ClimateSettings#temperature()
     * @see Biome.BiomeBuilder#temperature(float)
     */
    float getTemperature();

    /**
     * Set the temperature modifier of the biome.
     *
     * @param temperatureModifier the temperature modifier
     * @see Biome.ClimateSettings#temperatureModifier()
     * @see Biome.BiomeBuilder#temperatureAdjustment(Biome.TemperatureModifier)
     */
    void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

    /**
     * Get the temperature modifier of the biome.
     *
     * @return the temperature modifier
     *
     * @see Biome.ClimateSettings#temperatureModifier()
     * @see Biome.BiomeBuilder#temperatureAdjustment(Biome.TemperatureModifier)
     */
    Biome.TemperatureModifier getTemperatureModifier();

    /**
     * Set the downfall of the biome.
     *
     * @param downfall the downfall
     * @see Biome.ClimateSettings#downfall()
     * @see Biome.BiomeBuilder#downfall(float)
     */
    void setDownfall(float downfall);

    /**
     * Get the downfall of the biome.
     *
     * @return the downfall
     *
     * @see Biome.ClimateSettings#downfall()
     * @see Biome.BiomeBuilder#downfall(float)
     */
    float getDownfall();
}
