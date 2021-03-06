/*
* Written by Gil Tene and Martin Thompson, and released to the public domain,
* as explained at http://creativecommons.org/publicdomain/zero/1.0/
*/

package org.ObjectLayoutApiTests;

import org.ObjectLayout.*;
import org.junit.Test;

import java.awt.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import static java.lang.Long.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class StructuredArrayTest {

    static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Test
    public void shouldConstructArrayOfGivenDirectLengths() throws NoSuchMethodException {
        final long[] lengths = {7L, 8L, 9L};

        @SuppressWarnings("unchecked")
        final StructuredArray<StructuredArray<StructuredArray<MockStructure>>> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        new StructuredArrayBuilder(
                                StructuredArray.class,
                                new StructuredArrayBuilder(
                                        lookup,
                                        StructuredArray.class,
                                        MockStructure.class,
                                        lengths[2]
                                ),
                                lengths[1]
                        ),
                        lengths[0]
                ).build();

        assertThat(valueOf(array.getLength()), is(lengths[0]));
        assertThat(valueOf(array.get(0).getLength()), is(lengths[1]));
        assertThat(valueOf(array.get(0).get(0).getLength()), is(lengths[2]));

        assertTrue(array.getElementClass().isAssignableFrom(StructuredArray.class));
        assertTrue(array.get(0).getElementClass().isAssignableFrom(StructuredArray.class));
        assertTrue(array.get(0).get(0).getElementClass() == MockStructure.class);
    }

    @Test
    public void shouldConstructArrayOfGivenDirectLengthsPublic() throws NoSuchMethodException {
        final long[] lengths = {7L, 8L, 9L};

        @SuppressWarnings("unchecked")
        StructuredArray<StructuredArray<StructuredArray<PublicMockStructure>>> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        new StructuredArrayBuilder(
                                StructuredArray.class,
                                new StructuredArrayBuilder(
                                        StructuredArray.class,
                                        PublicMockStructure.class,
                                        lengths[2]
                                ),
                                lengths[1]
                        ),
                        lengths[0]
                ).build();

        assertThat(valueOf(array.getLength()), is(lengths[0]));
        assertThat(valueOf(array.get(0).getLength()), is(lengths[1]));
        assertThat(valueOf(array.get(0).get(0).getLength()), is(lengths[2]));

        assertTrue(array.getElementClass().isAssignableFrom(StructuredArray.class));
        assertTrue(array.get(0).getElementClass().isAssignableFrom(StructuredArray.class));
        assertTrue(array.get(0).get(0).getElementClass() == PublicMockStructure.class);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithNewInstance() throws NoSuchMethodException {
        long length = 9L;
        StructuredArray<MockStructure> array = StructuredArray.newInstance(lookup, MockStructure.class, length);

        assertThat(valueOf(array.getLength()), is(valueOf(length)));
        assertTrue(array.getElementClass() == MockStructure.class);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithNewInstancePublic() throws NoSuchMethodException {
        long length = 9L;
        StructuredArray<PublicMockStructure> array = StructuredArray.newInstance(PublicMockStructure.class, length);

        assertThat(valueOf(array.getLength()), is(valueOf(length)));
        assertTrue(array.getElementClass() == PublicMockStructure.class);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilder() throws NoSuchMethodException {
        long length = 9L;
        @SuppressWarnings("unchecked")
        StructuredArray<MockStructure> array =
                new StructuredArrayBuilder(
                        lookup,
                        StructuredArray.class,
                        MockStructure.class,
                        length).
                        build();

        assertThat(valueOf(array.getLength()), is(valueOf(length)));
        assertTrue(array.getElementClass() == MockStructure.class);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderPublic() throws NoSuchMethodException {
        long length = 9L;
        @SuppressWarnings("unchecked")
        StructuredArray<PublicMockStructure> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        PublicMockStructure.class,
                        length).
                        build();

        assertThat(valueOf(array.getLength()), is(valueOf(length)));
        assertTrue(array.getElementClass() == PublicMockStructure.class);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderAndCtorAndArgs() throws NoSuchMethodException, IllegalAccessException {
        final Class[] initArgTypes = {long.class, long.class};
        final long expectedIndex = 4L;
        final long expectedValue = 777L;
        long length = 9L;

        final CtorAndArgs<MockStructure> ctorAndArgs =
                new CtorAndArgs<MockStructure>(lookup, MockStructure.class, initArgTypes, expectedIndex, expectedValue);

        @SuppressWarnings("unchecked")
        StructuredArray<MockStructure> array =
                new StructuredArrayBuilder(
                        lookup,
                        StructuredArray.class,
                        MockStructure.class,
                        length).
                        elementCtorAndArgs(ctorAndArgs).
                        build();

        assertCorrectFixedInitialisation(expectedIndex, expectedValue, new long[]{length}, array);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderAndCtorAndArgsPublic() throws NoSuchMethodException {
        final Class[] initArgTypes = {long.class, long.class};
        final long expectedIndex = 4L;
        final long expectedValue = 777L;
        long length = 9L;

        final CtorAndArgs<PublicMockStructure> ctorAndArgs =
                new CtorAndArgs<PublicMockStructure>(PublicMockStructure.class, initArgTypes, expectedIndex, expectedValue);

        @SuppressWarnings("unchecked")
        StructuredArray<PublicMockStructure> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        PublicMockStructure.class,
                        length).
                        elementCtorAndArgs(ctorAndArgs).
                        build();

        assertCorrectFixedInitialisation(expectedIndex, expectedValue, new long[] {length}, array);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderAndCtorAndArgs2() throws NoSuchMethodException {
        final long expectedIndex = 4L;
        final long expectedValue = 777L;
        long length = 9L;

        final Constructor<MockStructure> constructor = MockStructure.class.getConstructor(long.class, long.class);
        constructor.setAccessible(true); // When constructor is passed in, setAccessible is the caller responsibility...

        @SuppressWarnings("unchecked")
        StructuredArray<MockStructure> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        MockStructure.class,
                        length).
                        elementCtorAndArgs(constructor, expectedIndex, expectedValue).
                        build();

        assertCorrectFixedInitialisation(expectedIndex, expectedValue, new long[] {length}, array);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderAndCtorAndArgs2Public() throws NoSuchMethodException {
        final long expectedIndex = 4L;
        final long expectedValue = 777L;
        long length = 9L;

        final Constructor<PublicMockStructure> constructor =
                PublicMockStructure.class.getConstructor(long.class, long.class);

        @SuppressWarnings("unchecked")
        StructuredArray<PublicMockStructure> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        PublicMockStructure.class,
                        length).
                        elementCtorAndArgs(constructor, expectedIndex, expectedValue).
                        build();

        assertCorrectFixedInitialisation(expectedIndex, expectedValue, new long[] {length}, array);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderAndCtorAndArgsProvider() throws NoSuchMethodException {
        long length = 9L;

        final Constructor<MockStructure> constructor = MockStructure.class.getConstructor(long.class, long.class);
        constructor.setAccessible(true); // When constructor is passed in, setAccessible is the caller responsibility...

        @SuppressWarnings("unchecked")
        StructuredArray<MockStructure> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        MockStructure.class,
                        length).
                        elementCtorAndArgsProvider(
                                new CtorAndArgsProvider() {
                                    @Override
                                    public CtorAndArgs getForContext(
                                            ConstructionContext context) throws NoSuchMethodException {
                                        return new CtorAndArgs(constructor, context.getIndex(), context.getIndex() * 2);
                                    }
                                }
                        ).
                        build();

        assertCorrectVariableInitialisation(new long[]{length}, array);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderAndCtorAndArgsProviderPublic() throws NoSuchMethodException {
        long length = 9L;

        final Constructor<PublicMockStructure> constructor =
                PublicMockStructure.class.getConstructor(long.class, long.class);

        @SuppressWarnings("unchecked")
        StructuredArray<PublicMockStructure> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        PublicMockStructure.class,
                        length).
                        elementCtorAndArgsProvider(
                                new CtorAndArgsProvider() {
                                    @Override
                                    public CtorAndArgs getForContext(
                                            ConstructionContext context) throws NoSuchMethodException {
                                        return new CtorAndArgs(constructor, context.getIndex(), context.getIndex() * 2);
                                    }
                                }
                        ).
                        build();

        assertCorrectVariableInitialisation(new long[] {length}, array);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderAndCtorAndArgsProvider2() throws NoSuchMethodException {
        long length = 9L;

        final Constructor<MockStructure> constructor = MockStructure.class.getConstructor(long.class, long.class);
        constructor.setAccessible(true); // When constructor is passed in, setAccessible is the caller responsibility...
        final CtorAndArgs<MockStructure> ctorAndArgs = new CtorAndArgs<MockStructure>(constructor, (Object[]) null);
        final Object[] args = new Object[2];

        @SuppressWarnings("unchecked")
        StructuredArray<MockStructure> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        MockStructure.class,
                        length).
                        elementCtorAndArgsProvider(
                                new CtorAndArgsProvider() {
                                    @Override
                                    public CtorAndArgs getForContext(
                                            ConstructionContext context) throws NoSuchMethodException {
                                        args[0] = context.getIndex();
                                        args[1] = context.getIndex() * 2;
                                        return ctorAndArgs.setArgs(args);
                                    }
                                }
                        ).
                        build();

        assertCorrectVariableInitialisation(new long[] {length}, array);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderAndCtorAndArgsProvider2Public() throws NoSuchMethodException {
        long length = 9L;

        final Constructor<PublicMockStructure> constructor =
                PublicMockStructure.class.getConstructor(long.class, long.class);
        final CtorAndArgs<PublicMockStructure> ctorAndArgs =
                new CtorAndArgs<PublicMockStructure>(constructor, (Object[]) null);
        final Object[] args = new Object[2];

        @SuppressWarnings("unchecked")
        StructuredArray<PublicMockStructure> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        PublicMockStructure.class,
                        length).
                        elementCtorAndArgsProvider(
                                new CtorAndArgsProvider() {
                                    @Override
                                    public CtorAndArgs getForContext(
                                            ConstructionContext context) throws NoSuchMethodException {
                                        args[0] = context.getIndex();
                                        args[1] = context.getIndex() * 2;
                                        return ctorAndArgs.setArgs(args);
                                    }
                                }
                        ).
                        build();

        assertCorrectVariableInitialisation(new long[] {length}, array);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderAndCtorAndArgsProvider3() throws NoSuchMethodException {
        long length = 9L;

        final Constructor<MockStructure> constructor = MockStructure.class.getConstructor(MockStructure.class);
        constructor.setAccessible(true); // When constructor is passed in, setAccessible is the caller responsibility...
        final MockStructure paramMock = new MockStructure();
        final CtorAndArgs<MockStructure> ctorAndArgs = new CtorAndArgs<MockStructure>(constructor, paramMock);

        @SuppressWarnings("unchecked")
        StructuredArray<MockStructure> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        MockStructure.class,
                        length).
                        elementCtorAndArgsProvider(
                                new CtorAndArgsProvider() {
                                    @Override
                                    public CtorAndArgs getForContext(
                                            ConstructionContext context) throws NoSuchMethodException {
                                        paramMock.setIndex(context.getIndex());
                                        paramMock.setTestValue(context.getIndex() * 2);
                                        return ctorAndArgs;
                                    }
                                }
                        ).
                        build();

        assertCorrectVariableInitialisation(new long[] {length}, array);
    }

    @Test
    public void shouldConstructArrayOfGivenLengthWithBuilderAndCtorAndArgsProvider3Public() throws NoSuchMethodException {
        long length = 9L;

        final Constructor<PublicMockStructure> constructor =
                PublicMockStructure.class.getConstructor(PublicMockStructure.class);
        final PublicMockStructure paramMock = new PublicMockStructure();
        final CtorAndArgs<PublicMockStructure> ctorAndArgs =
                new CtorAndArgs<PublicMockStructure>(constructor, paramMock);

        @SuppressWarnings("unchecked")
        StructuredArray<PublicMockStructure> array =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        PublicMockStructure.class,
                        length).
                        elementCtorAndArgsProvider(
                                new CtorAndArgsProvider() {
                                    @Override
                                    public CtorAndArgs getForContext(
                                            ConstructionContext context) throws NoSuchMethodException {
                                        paramMock.setIndex(context.getIndex());
                                        paramMock.setTestValue(context.getIndex() * 2);
                                        return ctorAndArgs;
                                    }
                                }
                        ).
                        build();

        assertCorrectVariableInitialisation(new long[] {length}, array);
    }

    @Test
    public void shouldConstructArrayFromCollection() throws NoSuchMethodException {
        long length = 100;
        ArrayList<MockStructure> mockSource = new ArrayList<MockStructure>();
        for (int i = 0; i < length; i++) {
            mockSource.add(new MockStructure(i, i*2));
        }

        @SuppressWarnings("unchecked")
        StructuredArray<MockStructure> mocks =
                StructuredArray.newInstance(lookup, StructuredArray.class, MockStructure.class, mockSource);

        assertThat(valueOf(mocks.get(5).getIndex()), is(valueOf(5)));

        assertCorrectVariableInitialisation(new long[] {length}, mocks);
    }

    @Test
    public void shouldConstructArrayFromCollectionPublic() throws NoSuchMethodException {
        long length = 100;
        ArrayList<PublicMockStructure> mockSource = new ArrayList<PublicMockStructure>();
        for (int i = 0; i < length; i++) {
            mockSource.add(new PublicMockStructure(i, i*2));
        }

        @SuppressWarnings("unchecked")
        StructuredArray<PublicMockStructure> mocks = StructuredArray.newInstance(StructuredArray.class,
                PublicMockStructure.class, mockSource);

        assertThat(valueOf(mocks.get(5).getIndex()), is(valueOf(5)));

        assertCorrectVariableInitialisation(new long[] {length}, mocks);
    }

    @Test
    public void shouldConstructArrayElementsViaConstantCtorAndArgsProvider() throws NoSuchMethodException {
        final Class[] initArgTypes = {long.class, long.class};
        final long expectedIndex = 4L;
        final long expectedValue = 777L;
        long length = 9L;

        final CtorAndArgs<MockStructure> ctorAndArgs =
                new CtorAndArgs<MockStructure>(lookup, MockStructure.class, initArgTypes, expectedIndex, expectedValue);
        final CtorAndArgsProvider<MockStructure> ctorAndArgsProvider =
                new CtorAndArgsProvider<MockStructure>() {
                    @Override
                    public CtorAndArgs<MockStructure> getForContext(
                            ConstructionContext<MockStructure> context) throws NoSuchMethodException {
                        return ctorAndArgs;
                    }
                };

        final StructuredArray<MockStructure> array =
                StructuredArray.newInstance(MockStructure.class, ctorAndArgsProvider, length);

        assertCorrectFixedInitialisation(expectedIndex, expectedValue, new long[]{length}, array);
    }

    @Test
    public void shouldConstructArrayElementsViaConstantCtorAndArgsProviderPublic() throws NoSuchMethodException {
        final Class[] initArgTypes = {long.class, long.class};
        final long expectedIndex = 4L;
        final long expectedValue = 777L;
        long length = 9L;

        final CtorAndArgs<PublicMockStructure> ctorAndArgs =
                new CtorAndArgs<PublicMockStructure>(PublicMockStructure.class, initArgTypes, expectedIndex, expectedValue);
        final CtorAndArgsProvider<PublicMockStructure> ctorAndArgsProvider =
                new CtorAndArgsProvider<PublicMockStructure>() {
                    @Override
                    public CtorAndArgs<PublicMockStructure> getForContext(
                            ConstructionContext<PublicMockStructure> context) throws NoSuchMethodException {
                        return ctorAndArgs;
                    }
                };

        final StructuredArray<PublicMockStructure> array =
                StructuredArray.newInstance(PublicMockStructure.class, ctorAndArgsProvider, length);

        assertCorrectFixedInitialisation(expectedIndex, expectedValue, new long[] {length}, array);
    }

    @Test
    public void shouldConstructArrayElementsViaCtorAndArgsProvider() throws NoSuchMethodException {
        final long[] lengths = {9};
        final DefaultMockCtorAndArgsProvider ctorAndArgsProvider = new DefaultMockCtorAndArgsProvider();
        final StructuredArray<MockStructure> array =
                StructuredArray.newInstance(MockStructure.class, ctorAndArgsProvider, lengths[0]);

        assertCorrectVariableInitialisation(lengths, array);
    }

    @Test
    public void shouldConstructArrayElementsViaCtorAndArgsProviderPublic() throws NoSuchMethodException {
        final long[] lengths = {9};
        final DefaultPublicMockCtorAndArgsProvider ctorAndArgsProvider =
                new DefaultPublicMockCtorAndArgsProvider();
        final StructuredArray<PublicMockStructure> array =
                StructuredArray.newInstance(PublicMockStructure.class, ctorAndArgsProvider, lengths[0]);

        assertCorrectVariableInitialisation(lengths, array);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldConstructArrayElementsViaCtorAndArgsProvider3D() throws NoSuchMethodException {
        final long[] lengths = {7, 8, 9};
        final DefaultMockCtorAndArgsProvider ctorAndArgsProvider = new DefaultMockCtorAndArgsProvider();

        final StructuredArrayBuilder<StructuredArray<StructuredArray<StructuredArray<MockStructure>>>,
                StructuredArray<StructuredArray<MockStructure>>> builder = get3dBuilder(lengths);
        builder.getStructuredSubArrayBuilder().
                getStructuredSubArrayBuilder().
                elementCtorAndArgsProvider(ctorAndArgsProvider);

        final StructuredArray<StructuredArray<StructuredArray<MockStructure>>> array = builder.build();

        assertCorrectVariableInitialisation(lengths, array);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldConstructArrayElementsViaCtorAndArgsProvider3DPublic() throws NoSuchMethodException {
        final long[] lengths = {7, 8, 9};
        final DefaultPublicMockCtorAndArgsProvider ctorAndArgsProvider =
                new DefaultPublicMockCtorAndArgsProvider();

        final StructuredArrayBuilder<StructuredArray<StructuredArray<StructuredArray<PublicMockStructure>>>,
                StructuredArray<StructuredArray<PublicMockStructure>>> builder = get3dBuilderPublic(lengths);
        builder.getStructuredSubArrayBuilder().
                getStructuredSubArrayBuilder().
                elementCtorAndArgsProvider(ctorAndArgsProvider);

        final StructuredArray<StructuredArray<StructuredArray<PublicMockStructure>>> array = builder.build();

        assertCorrectVariableInitialisation(lengths, array);
    }

    @Test
    public void shouldConstructArrayElementsViaLambdas() throws NoSuchMethodException {
//        Uncomment for Java 8, keep commented for Java 7 and 6
//
//        final Constructor<MockStructure> constructor =
//                MockStructure.class.getConstructor(Long.TYPE, Long.TYPE);
//
//        final long length = 8;
//        final StructuredArray<MockStructure> array =
//                StructuredArray.newInstance(MockStructure.class,
//                        context -> new CtorAndArgs<MockStructure>(
//                                constructor,
//                                context.getIndex(), context.getIndex() * 2),
//                        length);
//
//        assertCorrectVariableInitialisation(new long[] {length}, array);
//
//        final long[] lengths = {7, 8, 9};
//
//        final StructuredArrayBuilder<StructuredArray<StructuredArray<StructuredArray<MockStructure>>>,
//                StructuredArray<StructuredArray<MockStructure>>> builder = get3dBuilder(lengths);
//        builder.getSubArrayBuilder().getSubArrayBuilder().elementCtorAndArgsProvider(
//                context -> {
//                    long indexSum = 0;
//                    for (ConstructionContext c = context; c != null; c = c.getContainingContext()) {
//                        indexSum += c.getIndex();
//                    }
//                    return new CtorAndArgs<MockStructure>(constructor,
//                            indexSum, (indexSum * 2L));
//                }
//        );
//
//        final StructuredArray<StructuredArray<StructuredArray<MockStructure>>> array2 = builder.build();
//
//        assertCorrectVariableInitialisation(lengths, array2);
    }

    @Test
    public void shouldSetAndGetCorrectValueAtGivenIndex() throws NoSuchMethodException {
        final long[] lengths = {11, 10, 3};
        final StructuredArray<StructuredArray<StructuredArray<MockStructure>>> array = get3dBuilder(lengths).build();

        initValues(lengths, array);
        assertCorrectVariableInitialisation(lengths, array);
    }

    @Test
    public void shouldIterateOverArray() throws NoSuchMethodException {
        final long[] lengths = {11};
        final StructuredArray<MockStructure> array =
                StructuredArray.newInstance(lookup, MockStructure.class, lengths[0]);

        initValues(lengths, array);

        StructuredArray<MockStructure>.ElementIterator iter = array.iterator();

        long sum = 0;
        long elementCount = 0;
        while (iter.hasNext()) {
            final long index = iter.getCursor();
            final MockStructure mockStructure = iter.next();

            assertThat(valueOf(mockStructure.getIndex()), is(valueOf(index)));
            assertThat(valueOf(mockStructure.getTestValue()), is(valueOf(index * 2)));
            sum += index;
            elementCount++;
        }

        long sum2 = 0;
        long elementCount2 = 0;
        for (final MockStructure mockStructure : array) {
            sum2 += mockStructure.getIndex();
            elementCount2++;
        }

        assertThat(valueOf(elementCount), is(valueOf(array.getLength())));
        assertThat(valueOf(sum), is(valueOf(sum2)));
        assertThat(valueOf(elementCount), is(valueOf(elementCount2)));
    }

    @Test
    public void shouldIterateOverArrayAndResetAgain() throws NoSuchMethodException {
        final long length = 11;
        final StructuredArray<MockStructure> array =
                StructuredArray.newInstance(lookup, MockStructure.class, length);

        initValues(new long[] {length}, array);

        int i = 0;
        final StructuredArray<MockStructure>.ElementIterator iter = array.iterator();
        while (iter.hasNext()) {
            final long index = iter.getCursor();
            final MockStructure mockStructure = iter.next();
            assertThat(valueOf(mockStructure.getIndex()), is(valueOf(index)));
            assertThat(valueOf(mockStructure.getTestValue()), is(valueOf(index * 2)));
            i++;
        }

        iter.reset();
        i = 0;
        while (iter.hasNext()) {
            final long index = iter.getCursor();
            final MockStructure mockStructure = iter.next();
            assertThat(valueOf(mockStructure.getIndex()), is(valueOf(index)));
            assertThat(valueOf(mockStructure.getTestValue()), is(valueOf(index * 2)));
            i++;
        }

        assertThat(valueOf(i), is(valueOf(length)));
    }

    @Test
    public void shouldConstructCopyOfArray() throws NoSuchMethodException {
        final long length = 15;
        final DefaultMockCtorAndArgsProvider ctorAndArgsProvider = new DefaultMockCtorAndArgsProvider();
        final StructuredArray<MockStructure> sourceArray =
                StructuredArray.newInstance(MockStructure.class, ctorAndArgsProvider, length);

        assertThat(valueOf(sourceArray.getLength()), is(valueOf(length)));
        assertTrue(sourceArray.getElementClass() == MockStructure.class);

        final StructuredArray<MockStructure> newArray = StructuredArray.copyInstance(lookup, sourceArray);

        // We expect MockStructure elements to be initialized with index = index, and testValue = index * 2:
        assertCorrectVariableInitialisation(new long[]{length}, newArray);
    }

    @Test
    public void shouldConstructCopyOfArrayPublic() throws NoSuchMethodException {
        final long length = 15;
        final DefaultPublicMockCtorAndArgsProvider ctorAndArgsProvider = new DefaultPublicMockCtorAndArgsProvider();
        final StructuredArray<PublicMockStructure> sourceArray =
                StructuredArray.newInstance(PublicMockStructure.class, ctorAndArgsProvider, length);

        assertThat(valueOf(sourceArray.getLength()), is(valueOf(length)));
        assertTrue(sourceArray.getElementClass() == PublicMockStructure.class);

        final StructuredArray<PublicMockStructure> newArray = StructuredArray.copyInstance(sourceArray);

        // We expect PublicMockStructure elements to be initialized with index = index, and testValue = index * 2:
        assertCorrectVariableInitialisation(new long[] {length}, newArray);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldConstructCopyOfArray3D() throws NoSuchMethodException {
        final long[] lengths = {15, 7, 5};
        final DefaultMockCtorAndArgsProvider ctorAndArgsProvider = new DefaultMockCtorAndArgsProvider();

        final StructuredArrayBuilder<StructuredArray<StructuredArray<StructuredArray<MockStructure>>>,
                StructuredArray<StructuredArray<MockStructure>>> builder = get3dBuilder(lengths);
        builder.getStructuredSubArrayBuilder().
                getStructuredSubArrayBuilder().
                elementCtorAndArgsProvider(ctorAndArgsProvider);

        final StructuredArray<StructuredArray<StructuredArray<MockStructure>>> sourceArray = builder.build();

        StructuredArray<StructuredArray<MockStructure>> subArray1 = sourceArray.get(0);
        StructuredArray<MockStructure> subArray2 = subArray1.get(0);

        assertThat(valueOf(sourceArray.getLength()), is(valueOf(lengths[0])));
        assertThat(valueOf(subArray1.getLength()), is(valueOf(lengths[1])));
        assertThat(valueOf(subArray2.getLength()), is(valueOf(lengths[2])));
        assertTrue(subArray2.getElementClass() == MockStructure.class);

        final StructuredArray<StructuredArray<StructuredArray<MockStructure>>> newArray =
                StructuredArray.copyInstance(lookup, sourceArray);

        // We expect MockStructure elements to be initialized with index = index, and testValue = index * 2:
        assertCorrectVariableInitialisation(lengths, newArray);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldConstructCopyOfArray3DPublic() throws NoSuchMethodException {
        final long[] lengths = {15, 7, 5};
        final DefaultPublicMockCtorAndArgsProvider ctorAndArgsProvider = new DefaultPublicMockCtorAndArgsProvider();

        final StructuredArrayBuilder<StructuredArray<StructuredArray<StructuredArray<PublicMockStructure>>>,
                StructuredArray<StructuredArray<PublicMockStructure>>> builder = get3dBuilderPublic(lengths);
        builder.getStructuredSubArrayBuilder().
                getStructuredSubArrayBuilder().
                elementCtorAndArgsProvider(ctorAndArgsProvider);

        final StructuredArray<StructuredArray<StructuredArray<PublicMockStructure>>> sourceArray = builder.build();

        StructuredArray<StructuredArray<PublicMockStructure>> subArray1 = sourceArray.get(0);
        StructuredArray<PublicMockStructure> subArray2 = subArray1.get(0);

        assertThat(valueOf(sourceArray.getLength()), is(valueOf(lengths[0])));
        assertThat(valueOf(subArray1.getLength()), is(valueOf(lengths[1])));
        assertThat(valueOf(subArray2.getLength()), is(valueOf(lengths[2])));
        assertTrue(subArray2.getElementClass() == PublicMockStructure.class);

        final StructuredArray<StructuredArray<StructuredArray<PublicMockStructure>>> newArray =
                StructuredArray.copyInstance(sourceArray);

        // We expect PublicMockStructure elements to be initialized with index = index, and testValue = index * 2:
        assertCorrectVariableInitialisation(lengths, newArray);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldConstructCopyOfArrayRange() throws NoSuchMethodException {
        final long[] lengths = {15, 7, 5};
        final DefaultMockCtorAndArgsProvider ctorAndArgsProvider = new DefaultMockCtorAndArgsProvider();

        final StructuredArrayBuilder<StructuredArray<StructuredArray<StructuredArray<MockStructure>>>,
                StructuredArray<StructuredArray<MockStructure>>> builder = get3dBuilder(lengths);
        builder.getStructuredSubArrayBuilder().
                getStructuredSubArrayBuilder().
                elementCtorAndArgsProvider(ctorAndArgsProvider);

        final StructuredArray<StructuredArray<StructuredArray<MockStructure>>> sourceArray = builder.build();

        StructuredArray<StructuredArray<MockStructure>> subArray1 = sourceArray.get(0);
        StructuredArray<MockStructure> subArray2 = subArray1.get(0);

        assertThat(valueOf(sourceArray.getLength()), is(valueOf(lengths[0])));
        assertThat(valueOf(subArray1.getLength()), is(valueOf(lengths[1])));
        assertThat(valueOf(subArray2.getLength()), is(valueOf(lengths[2])));
        assertTrue(subArray2.getElementClass() == MockStructure.class);

        long[] offsets = {2, 2, 2};
        long[] counts = {13, 5, 3};
        final StructuredArray<StructuredArray<StructuredArray<MockStructure>>> newArray =
                StructuredArray.copyInstance(lookup, sourceArray, offsets, counts);

        assertCorrectVariableInitialisation(counts, newArray, 2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void shouldThrowOutOfBoundExceptionForAccessesOutOfBounds() throws NoSuchMethodException {
        final long length = 11;
        final StructuredArray<MockStructure> array =
                StructuredArray.newInstance(lookup, MockStructure.class, length);

        array.get(length);
    }

    @Test
    public void shouldNotThrowIncompatibleTypeExceptionForGetsOfProperTypes() throws NoSuchMethodException {
        final long[] lengths = {11, 7, 4};
        final StructuredArray<StructuredArray<StructuredArray<MockStructure>>> array = get3dBuilder(lengths).build();

        // Step by step gets of the correct type (array vs. element) per dimension:
        StructuredArray<StructuredArray<MockStructure>> subArray1 = array.get(2);
        StructuredArray<MockStructure> subArray2 = subArray1.get(2);
        subArray2.get(2);

    }

    @Test
    public void shouldCopyRegionLeftInArray() throws NoSuchMethodException {
        final long length = 11;
        final StructuredArray<MockStructure> array =
                StructuredArray.newInstance(lookup, MockStructure.class, length);

        initValues(new long[]{length}, array);

        StructuredArray.shallowCopy(array, 4, array, 3, 2, false);

        assertThat(valueOf(array.get(3).getIndex()), is(valueOf(4)));
        assertThat(valueOf(array.get(4).getIndex()), is(valueOf(5)));
        assertThat(valueOf(array.get(5).getIndex()), is(valueOf(5)));
    }

    @Test
    public void shouldCopyRegionRightInArray() throws NoSuchMethodException {
        final long length = 11;
        final StructuredArray<MockStructure> array =
                StructuredArray.newInstance(lookup, MockStructure.class, length);

        initValues(new long[]{length}, array);

        StructuredArray.shallowCopy(array, 5, array, 6, 2, false);

        assertThat(valueOf(array.get(5).getIndex()), is(valueOf(5)));
        assertThat(valueOf(array.get(6).getIndex()), is(valueOf(5)));
        assertThat(valueOf(array.get(7).getIndex()), is(valueOf(6)));
    }

    @Test
    public void shouldCopyEvenWithFinalFields() throws NoSuchMethodException {
        final long length = 11;
        final StructuredArray<MockStructureWithFinalField> array =
                StructuredArray.newInstance(lookup, MockStructureWithFinalField.class, length);

        StructuredArray.shallowCopy(array, 1, array, 3, 1, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFinalFieldWouldBeCopied() throws NoSuchMethodException {
        final long length = 11;
        final StructuredArray<MockStructureWithFinalField> array =
                StructuredArray.newInstance(lookup, MockStructureWithFinalField.class, length);

        StructuredArray.shallowCopy(array, 1, array, 3, 1);
    }

    @Test
    public void moveObjectsIntoNewArray() throws NoSuchMethodException {
        int length = 100;
        ArrayList<MockStructure> sourceMocks = new ArrayList<MockStructure>(length);
        for (int i = 0; i < length; i++) {
            sourceMocks.add(new MockStructure(i, i * 2));
        }

        @SuppressWarnings("unchecked")
        StructuredArrayBuilder<StructuredArray<MockStructure>, MockStructure> builder =
                new StructuredArrayBuilder(
                        lookup,
                        StructuredArray.class,
                        MockStructure.class,
                        length
                );
        builder.elementCtorAndArgsProvider(new CopyFromArrayListProvider(sourceMocks));
        StructuredArray<MockStructure> mocks = builder.build();
        assertThat(valueOf(mocks.get(5).getIndex()), is(valueOf(5)));
        assertThat(valueOf(mocks.get(5).getTestValue()), is(valueOf(10)));
        MockStructure mock = mocks.get(7);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Test support below
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private <T extends MockStructure> void assertCorrectFixedInitialisation(
            final long expectedIndex,
            final long expectedValue,
            final long[] lengths,
            final StructuredArray<T> array) {
        StructuredArray a = array;
        for (int i = 0; i < lengths.length - 1; i++) {
            assertThat(valueOf(a.getLength()), is(valueOf(lengths[i])));
            a = (StructuredArray) a.get(0);
        }
        assertThat(valueOf(a.getLength()), is(valueOf(lengths[lengths.length - 1])));

        assertTrue(MockStructure.class.isAssignableFrom(a.getElementClass()));

        long totalElementCount = 1;
        for (long l : lengths) {
            totalElementCount *= l;
        }

        final long[] cursors = new long[lengths.length];

        long elementCountToCursor = 0;

        while (elementCountToCursor < totalElementCount) {
            // Check element at cursors:
            // Check element at cursors:
            a = array;
            for (int i = 0; i < cursors.length - 1; i++) {
                a = (StructuredArray) a.get(cursors[i]);
            }
            MockStructure mockStructure = (MockStructure) a.get(cursors[cursors.length - 1]);

            assertThat(valueOf(mockStructure.getIndex()), is(valueOf(expectedIndex)));
            assertThat(valueOf(mockStructure.getTestValue()), is(valueOf(expectedValue)));

            // Increment cursors from inner-most dimension out:
            for (int cursorDimension = cursors.length - 1; cursorDimension >= 0; cursorDimension--) {
                if ((++cursors[cursorDimension]) < lengths[cursorDimension]) {
                    break;
                }
                // This dimension wrapped. Reset to zero and continue to one dimension higher
                cursors[cursorDimension] = 0;
            }
            elementCountToCursor++;
        }
    }

    private void assertCorrectVariableInitialisation(final long[] lengths,
                                                     final StructuredArray array) {
        assertCorrectVariableInitialisation(lengths, array, 0);
    }

    private void assertCorrectVariableInitialisation(final long[] lengths,
                                                     final StructuredArray array, long indexOffset) {
        StructuredArray a = array;
        for (int i = 0; i < lengths.length - 1; i++) {
            assertThat(valueOf(a.getLength()), is(valueOf(lengths[i])));
            a = (StructuredArray) a.get(0);
        }
        assertThat(valueOf(a.getLength()), is(valueOf(lengths[lengths.length - 1])));

        assertTrue(MockStructure.class.isAssignableFrom(a.getElementClass()));

        long totalElementCount = 1;
        for (long l : lengths) {
            totalElementCount *= l;
        }

        final long[] cursors = new long[lengths.length];

        long elementCountToCursor = 0;

        while (elementCountToCursor < totalElementCount) {
            // Check element at cursors:
            a = array;
            for (int i = 0; i < cursors.length - 1; i++) {
                a = (StructuredArray) a.get(cursors[i]);
            }
            MockStructure mockStructure = (MockStructure) a.get(cursors[cursors.length - 1]);

            long indexSum = 0;
            String cursorsString = "";
            for (long index : cursors) {
                indexSum += index + indexOffset;
                cursorsString += index + ",";
            }

            assertThat("elementCountToCursor: " + elementCountToCursor + " cursors: " + cursorsString,
                    valueOf(mockStructure.getIndex()), is(valueOf(indexSum)));
            assertThat("elementCountToCursor: " + elementCountToCursor + " cursors: " + cursorsString,
                    valueOf(mockStructure.getTestValue()), is(valueOf(indexSum * 2)));

            // Increment cursors from inner-most dimension out:
            for (int cursorDimension = cursors.length - 1; cursorDimension >= 0; cursorDimension--) {
                if ((++cursors[cursorDimension]) < lengths[cursorDimension]) {
                    break;
                }
                // This dimension wrapped. Reset to zero and continue to one dimension higher
                cursors[cursorDimension] = 0;
            }
            elementCountToCursor++;
        }
    }

    private void initValues(final long[] lengths, final StructuredArray array) {
        final long[] cursors = new long[lengths.length];
        long totalElementCount = 1;
        for (long l : lengths) {
            totalElementCount *= l;
        }

        long elementCountToCursor = 0;

        while (elementCountToCursor < totalElementCount) {
            // Check element at cursors:
            StructuredArray a = array;
            for (int i = 0; i < cursors.length - 1; i++) {
                a = (StructuredArray) a.get(cursors[i]);
            }
            MockStructure mockStructure = (MockStructure) a.get(cursors[cursors.length - 1]);

            long indexSum = 0;
            for (long index : cursors) {
                indexSum += index;
            }

            mockStructure.setIndex(indexSum);
            mockStructure.setTestValue(indexSum * 2);

            // Increment cursors from inner-most dimension out:
            for (int cursorDimension = cursors.length - 1; cursorDimension >= 0; cursorDimension--) {
                if ((++cursors[cursorDimension]) < lengths[cursorDimension]) {
                    break;
                }

                // This dimension wrapped. Reset to zero and continue to one dimension higher
                cursors[cursorDimension] = 0;
            }
            elementCountToCursor++;
        }
    }

    public static class PublicMockStructure extends MockStructure {
        public PublicMockStructure() {
        }

        public PublicMockStructure(final long index, final long testValue) {
            super(index, testValue);
        }

        public PublicMockStructure(final PublicMockStructure src) {
            super(src);
        }
    }

    private static class MockStructure {

        private long index = -1;
        private long testValue = Long.MIN_VALUE;

        public MockStructure() {
        }

        public MockStructure(final long index, final long testValue) {
            this.index = index;
            this.testValue = testValue;
        }

        public MockStructure(final MockStructure src) {
            this.index = src.index;
            this.testValue = src.testValue;
        }

        public long getIndex() {
            return index;
        }

        public void setIndex(final long index) {
            this.index = index;
        }

        public long getTestValue() {
            return testValue;
        }

        public void setTestValue(final long testValue) {
            this.testValue = testValue;
        }

        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final MockStructure that = (MockStructure)o;

            return index == that.index && testValue == that.testValue;
        }

        public int hashCode() {
            int result = (int)(index ^ (index >>> 32));
            result = 31 * result + (int)(testValue ^ (testValue >>> 32));
            return result;
        }

        public String toString() {
            return "MockStructure{" +
                    "index=" + index +
                    ", testValue=" + testValue +
                    '}';
        }
    }

    public static class MockStructureWithFinalField {
        private final int value = 888;
    }

    private static class DefaultPublicMockCtorAndArgsProvider implements CtorAndArgsProvider<PublicMockStructure> {
        private final Class[] argsTypes = {Long.TYPE, Long.TYPE};

        public CtorAndArgs<PublicMockStructure> getForContext(
                ConstructionContext<PublicMockStructure> context) throws NoSuchMethodException {
            long indexSum = 0;
            for (ConstructionContext c = context; c != null; c = c.getContainingContext()) {
                indexSum += c.getIndex();
            }
            Object[] args = {indexSum, indexSum * 2};
            // We could do this much more efficiently with atomic caching of a single allocated CtorAndArgs,
            // as CopyCtorAndArgsProvider does, but no need to put in the effort in a test...
            return new CtorAndArgs<PublicMockStructure>(PublicMockStructure.class, argsTypes, args);
        }
    }

    private static class DefaultMockCtorAndArgsProvider implements CtorAndArgsProvider<MockStructure> {

        private final Class[] argsTypes = {Long.TYPE, Long.TYPE};

        public CtorAndArgs<MockStructure> getForContext(ConstructionContext<MockStructure> context) throws NoSuchMethodException {
            long indexSum = 0;
            for (ConstructionContext c = context; c != null; c = c.getContainingContext()) {
                indexSum += c.getIndex();
            }
            Object[] args = {indexSum, indexSum * 2};
            // We could do this much more efficiently with atomic caching of a single allocated CtorAndArgs,
            // as CopyCtorAndArgsProvider does, but no need to put in the effort in a test...
            return new CtorAndArgs<MockStructure>(lookup, MockStructure.class, argsTypes, args);
        }
    }


    private static class CopyFromArrayListProvider implements CtorAndArgsProvider<MockStructure> {

        static final Constructor<MockStructure> copyCtor;

        static {
            try {
                copyCtor = MockStructure.class.getConstructor(new Class[]{MockStructure.class});
                copyCtor.setAccessible(true); // When constructor is passed in, setAccessible is the caller responsibility...
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }

        private final ArrayList<MockStructure> fromList;

        public CopyFromArrayListProvider(ArrayList<MockStructure> fromList) {
            this.fromList = fromList;
        }

        public CtorAndArgs<MockStructure> getForContext(ConstructionContext<MockStructure> context) throws NoSuchMethodException {
            return new CtorAndArgs<MockStructure>(copyCtor, fromList.get((int) context.getIndex()));
        }
    }

    StructuredArrayBuilder<StructuredArray<StructuredArray<StructuredArray<MockStructure>>>,
            StructuredArray<StructuredArray<MockStructure>>> get3dBuilder(long... lengths) {
        @SuppressWarnings("unchecked")
        StructuredArrayBuilder<StructuredArray<StructuredArray<StructuredArray<MockStructure>>>,
                StructuredArray<StructuredArray<MockStructure>>> builder =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        new StructuredArrayBuilder(
                                StructuredArray.class,
                                new StructuredArrayBuilder(
                                        lookup,
                                        StructuredArray.class,
                                        MockStructure.class,
                                        lengths[2]
                                ),
                                lengths[1]
                        ),
                        lengths[0]
                );
        return builder;
    }

    StructuredArrayBuilder<StructuredArray<StructuredArray<StructuredArray<PublicMockStructure>>>,
            StructuredArray<StructuredArray<PublicMockStructure>>> get3dBuilderPublic(long... lengths) {
        @SuppressWarnings("unchecked")
        StructuredArrayBuilder<StructuredArray<StructuredArray<StructuredArray<PublicMockStructure>>>,
                StructuredArray<StructuredArray<PublicMockStructure>>> builder =
                new StructuredArrayBuilder(
                        StructuredArray.class,
                        new StructuredArrayBuilder(
                                StructuredArray.class,
                                new StructuredArrayBuilder(
                                        StructuredArray.class,
                                        PublicMockStructure.class,
                                        lengths[2]
                                ),
                                lengths[1]
                        ),
                        lengths[0]
                );
        return builder;
    }
}
