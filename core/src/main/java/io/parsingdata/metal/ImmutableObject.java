package io.parsingdata.metal;

/**
 * When objects are immutable, that means their hashcode will stay the same the moment it is created.
 * It will improve performance if these objects caches their hash.
 * <p>
 * Also, for every immutable object, the equality methods must be defined, which we make mandatory this way.
 */
public abstract class ImmutableObject {

    private Integer hash;

    abstract public int cachingHashCode();

    @Override
    public int hashCode() {
//        return cachingHashCode();
        if (hash == null) {
            hash = cachingHashCode();
        }
        return hash;
    }
}
