/*
 * Written by Gil Tene and Martin Thompson, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 */

package org.ObjectLayout;

import java.lang.reflect.Constructor;

/**
 * Supports the construction of a new StructuredArrays that are sub-arrays of other StructuredArrays. This is
 * a package-internal provider, any given instance of this provider is only expected to be used in an
 * externally serialized manner (i.e. recycling and use are done on a serialized manner and do not need to
 * be internally synchronized)
 *
 * @param <A> type of the element occupying each array slot
 */
class ArrayCtorAndArgsProvider<T, A extends StructuredArray<T>> extends AbstractCtorAndArgsProvider<A> {

    private final Constructor<A> constructor;
    private final ArrayConstructionArgs originalArgs;

    private CtorAndArgs<A> cachedCtorAndArgs = null;
    private ArrayConstructionArgs cachedArrayConstructionArgs = null;
    private long[] cachedContainingIndex = null;

    /**
     * Used to apply a fixed constructor with a given set of arguments to all elements.
     *
     * @param constructor The element constructor
     * @param args The arguments to be passed to the constructor for all elements
     * @throws NoSuchMethodException if a constructor matching argTypes
     * @throws IllegalArgumentException if argTypes and args conflict
     */
    ArrayCtorAndArgsProvider(final Constructor<A> constructor,
                             final ArrayConstructionArgs args) throws NoSuchMethodException {
        this.constructor = constructor;
        this.originalArgs = args;
    }

    /**
     * Get a {@link CtorAndArgs} instance to be used in constructing a given sub-array at a given element index in
     * a {@link StructuredArray}
     *
     * @param context The construction context (index, containing array, etc.) of the element to be constructed
     * @return {@link CtorAndArgs} instance to used in element construction
     * @throws NoSuchMethodException
     */
    @Override
    public CtorAndArgs<A> getForContext(final ConstructionContext context) throws NoSuchMethodException {
        CtorAndArgs<A> ctorAndArgs;
        ArrayConstructionArgs arrayConstructionArgs;
        long[] containingIndex;

        // Try (but not too hard) to use a cached, previously allocated ctorAndArgs object:
        ctorAndArgs = cachedCtorAndArgs;
        cachedCtorAndArgs = null;
        arrayConstructionArgs = cachedArrayConstructionArgs;
        cachedArrayConstructionArgs = null;
        containingIndex = cachedContainingIndex;
        cachedContainingIndex = null;

        if ((containingIndex == null) || (containingIndex.length != 1))  {
            containingIndex = new long[1];
        }
        containingIndex[0] = context.getIndex();

        if (arrayConstructionArgs == null) {
            arrayConstructionArgs = new ArrayConstructionArgs(originalArgs);
        }

        arrayConstructionArgs.setContainingIndex(containingIndex);

        if (ctorAndArgs == null) {
            // We have nothing cached that's not being used. A bit of allocation in contended cases won't kill us:
            ctorAndArgs = new CtorAndArgs<A>(constructor, arrayConstructionArgs);
        }
        ctorAndArgs.setArgs(arrayConstructionArgs);

        return ctorAndArgs;
    }

    /**
     * Recycle an {@link CtorAndArgs} instance (place it back in the internal cache if desired). This is [very]
     * useful for avoiding a re-allocation of a new {@link CtorAndArgs} and an associated args array for
     * {@link #getForContext(ConstructionContext)} invocation in cases such as this (where the returned
     * {@link CtorAndArgs} is not constant across indices).
     * Recycling is optional, and is not guaranteed to occur.
     *
     * @param ctorAndArgs the {@link CtorAndArgs} instance to recycle
     */
    @SuppressWarnings("unchecked")
    public void recycle(final CtorAndArgs<A> ctorAndArgs) {
        // Only recycle ctorAndArgs if ctorAndArgs is compatible with our state:
        if ((ctorAndArgs == null) || (ctorAndArgs.getConstructor() != constructor)) {
            return;
        }

        Object[] args = ctorAndArgs.getArgs();
        if ((args == null) || (args.length == 0)) {
            return;
        }
        ArrayConstructionArgs arrayConstructionArgs = (ArrayConstructionArgs) args[0];
        if (arrayConstructionArgs == null) {
            return;
        }

        long[] containingIndex = arrayConstructionArgs.getContainingIndex();

        cachedCtorAndArgs = ctorAndArgs;
        cachedArrayConstructionArgs = arrayConstructionArgs;
        cachedContainingIndex = containingIndex;
    }
}