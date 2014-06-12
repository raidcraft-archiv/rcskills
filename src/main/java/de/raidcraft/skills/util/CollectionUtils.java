package de.raidcraft.skills.util;

import java.util.Collection;

/**
 * Provides utility methods for {@link Collection} instances.
 * <p>
 * Based on Apache Commons CollectionUtils
 */
public class CollectionUtils {

    /**
     * Null-safe check if the specified collection is empty.
     *
     * @param coll  the collection to check, may be null
     * @return      true if empty or null
     */
    public static boolean isEmpty(Collection coll) {
        return (coll == null) || coll.isEmpty();
    }
}
