package fuzs.puzzleslib.common.impl.data;

/**
 * Scoped values used during data generation.
 * <p>
 * Values are bound by the loader-specific registration for the duration of a data provider's construction, so
 * providers can capture them in their constructor when they are required later, e.g. when running on a different
 * thread.
 */
public final class DataGenerationScopes {
    /**
     * The mod id of the data provider currently being created.
     */
    public static final ScopedValue<String> MOD_ID = ScopedValue.newInstance();

    private DataGenerationScopes() {
        // NO-OP
    }
}
