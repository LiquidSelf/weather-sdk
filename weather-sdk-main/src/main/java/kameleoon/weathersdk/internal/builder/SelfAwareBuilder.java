package kameleoon.weathersdk.internal.builder;

public abstract class SelfAwareBuilder<SELF, TYPE_TO_BE_BUILT> {
    /**
     * required to maintain type of child builders
     */
    protected abstract SELF self();

    public abstract TYPE_TO_BE_BUILT build();
}
