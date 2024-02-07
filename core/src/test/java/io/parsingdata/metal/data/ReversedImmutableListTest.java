/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.parsingdata.metal.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


/**
 * Note: the same tests are performed at {@link ImmutableListTest}. Updates in this class may also be valuable there.
 */
public class ReversedImmutableListTest {

    public static Stream<Arguments> addHeadTest() {
        return Stream.of(
            arguments(List.of(), 4, List.of(4)),
            arguments(List.of(1), 4, List.of(4, 1)),
            arguments(List.of(1, 2), 4, List.of(4, 2, 1)),
            arguments(List.of(1, 2, 3), 4, List.of(4, 3, 2, 1))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void addHeadTest(final List<Integer> originalToReverse, final int head, final List<Integer> expected) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(originalToReverse).reverse().addHead(head);
        final List<Integer> actual = new ArrayList<>(reverse);
        assertIterableEquals(expected, actual);
        assertEquals(head, reverse.head());
    }

    public static Stream<Arguments> addListTest() {
        return Stream.of(
            arguments(List.of(), List.of(), List.of()),
            arguments(List.of(1), List.of(), List.of(1)),
            arguments(List.of(1, 2), List.of(), List.of(2, 1)),
            arguments(List.of(1, 2, 3), List.of(), List.of(3, 2, 1)),

            arguments(List.of(), List.of(4), List.of(4)),
            arguments(List.of(1), List.of(4), List.of(1, 4)),
            arguments(List.of(1, 2), List.of(4), List.of(2, 1, 4)),
            arguments(List.of(1, 2, 3), List.of(4), List.of(3, 2, 1, 4)),

            arguments(List.of(), List.of(4, 5), List.of(4, 5)),
            arguments(List.of(1), List.of(4, 5), List.of(1, 4, 5)),
            arguments(List.of(1, 2), List.of(4, 5), List.of(2, 1, 4, 5)),
            arguments(List.of(1, 2, 3), List.of(4, 5), List.of(3, 2, 1, 4, 5))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void addListTest(final List<Integer> listToReverse, final List<Integer> listToAdd, final List<Integer> expected) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse().addList(new ImmutableList<>(listToAdd));
        final List<Integer> actual = new ArrayList<>(reverse);
        assertIterableEquals(expected, actual);
    }

    public static Stream<Arguments> getTest() {
        return Stream.of(
            arguments(List.of(1), 0, 1),
            arguments(List.of(1, 2), 0, 2),
            arguments(List.of(1, 2), 1, 1),
            arguments(List.of(1, 2, 3), 0, 3),
            arguments(List.of(1, 2, 3), 1, 2),
            arguments(List.of(1, 2, 3), 2, 1)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getTest(final List<Integer> listToReverse, final int index, final Integer expected) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertEquals(expected, reverse.get(index));
    }


    public static Stream<Arguments> getOutOfBoundsTest() {
        return Stream.of(
            arguments(List.of(), -1),
            arguments(List.of(), 0),
            arguments(List.of(), 1),

            arguments(List.of(1), -1),
            arguments(List.of(1), 1),

            arguments(List.of(1, 2), -1),
            arguments(List.of(1, 2), 2),

            arguments(List.of(1, 2, 3), -1),
            arguments(List.of(1, 2, 3), 3)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getOutOfBoundsTest(final List<Integer> listToReverse, final int index) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertThrows(IndexOutOfBoundsException.class, () -> reverse.get(index));
    }

    @Test
    public void toArrayTest() {
        final ImmutableList<Integer> reverse = new ImmutableList<>(List.of(1, 2, 3, 4, 5, 6)).reverse();
        assertArrayEquals(new Integer[]{6, 5, 4, 3, 2, 1}, reverse.toArray());
        assertArrayEquals(new Integer[]{6, 5, 4, 3, 2, 1}, reverse.toArray(new Integer[6]));
        assertArrayEquals(new Integer[]{6, 5, 4, 3, 2, 1}, reverse.toArray(new Integer[3]));
        assertArrayEquals(new Integer[]{6, 5, 4, 3, 2, 1, null, null, null}, reverse.toArray(new Integer[9]));
    }

    public static Stream<Arguments> headAndTailTest() {
        return Stream.of(
            arguments(List.of(), null, List.of()),
            arguments(List.of(1), 1, List.of()),
            arguments(List.of(1, 2), 2, List.of(1)),
            arguments(List.of(1, 2, 3), 3, List.of(2, 1)),
            arguments(List.of(1, 2, 3, 4), 4, List.of(3, 2, 1))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void headAndTailTest(final List<Integer> originalToReverse, final Integer head, final List<Integer> tail) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(originalToReverse).reverse();
        assertEquals(head, reverse.head());
        assertIterableEquals(tail, reverse.tail());
    }

    @Test
    public void reverseTest() {
        final List<Integer> listToReverse = List.of(1, 2, 3, 4, 5);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertEquals(listToReverse, reverse.reverse());
    }

    @Test
    public void streamTest() {
        final List<Integer> listToReverse = List.of(1, 2, 3, 4, 5);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        final Stream<Integer> actual = reverse.stream();
        assertEquals(List.of(5, 4, 3, 2, 1), actual.collect(Collectors.toList()));
    }

    @Test
    public void iteratorTest() {
        final List<Integer> listToReverse = List.of(1, 2, 3, 4, 5);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        final Iterator<Integer> actual = reverse.iterator();
        assertEquals(5, actual.next());
        assertEquals(4, actual.next());
        assertEquals(3, actual.next());
        assertEquals(2, actual.next());
        assertEquals(1, actual.next());
        assertFalse(actual.hasNext());
    }

    @Test
    public void indexOfTest() {
        final List<Integer> listToReverse = List.of(1, 1, 2, 2, 3, 3);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertEquals(4, reverse.indexOf(1));
        assertEquals(2, reverse.indexOf(2));
        assertEquals(0, reverse.indexOf(3));
    }


    @Test
    public void lastIndexOfTest() {
        final List<Integer> listToReverse = List.of(1, 1, 2, 2, 3, 3);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertEquals(5, reverse.lastIndexOf(1));
        assertEquals(3, reverse.lastIndexOf(2));
        assertEquals(1, reverse.lastIndexOf(3));
    }

    @Test
    public void containsTest() {
        final List<Integer> listToReverse = List.of(1, 1, 2, 2, 3, 3);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertTrue(reverse.contains(2));
        assertFalse(reverse.contains(5));
        assertFalse(reverse.contains(1L));
    }

    public static Stream<Arguments> containsAllTest() {
        return Stream.of(
            arguments(true, List.of(), List.of()),
            arguments(false, List.of(), List.of(1)),
            arguments(false, List.of(), List.of(1, 2)),

            arguments(true, List.of(1), List.of()),
            arguments(true, List.of(1), List.of(1)),
            arguments(false, List.of(1), List.of(2)),
            arguments(false, List.of(1), List.of(1, 2)),

            arguments(true, List.of(1, 2), List.of()),
            arguments(true, List.of(1, 2), List.of(1)),
            arguments(true, List.of(1, 2), List.of(2)),
            arguments(true, List.of(1, 2), List.of(1, 2)),
            arguments(false, List.of(1, 2), List.of(1, 2, 3)),

            arguments(true, List.of(1, 2, 3), List.of()),
            arguments(true, List.of(1, 2, 3), List.of(1)),
            arguments(true, List.of(1, 2, 3), List.of(2)),
            arguments(true, List.of(1, 2, 3), List.of(3)),
            arguments(true, List.of(1, 2, 3), List.of(1, 2)),
            arguments(true, List.of(1, 2, 3), List.of(2, 3)),
            arguments(true, List.of(1, 2, 3), List.of(3, 1)),
            arguments(true, List.of(1, 2, 3), List.of(3, 2)),
            arguments(true, List.of(1, 2, 3), List.of(1, 2, 3)),

            arguments(false, List.of(1, 2, 3), List.of(4)),
            arguments(false, List.of(1, 2, 3), List.of(4, 1)),
            arguments(false, List.of(1, 2, 3), List.of(4, 5, 1, 2, 3))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void containsAllTest(final boolean contains, final List<Integer> listToReverse, final List<Integer> listToTest) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertEquals(contains, reverse.containsAll(listToTest));
    }

    // All methods that modifies the list should throw an UnsupportedOperationException.
    public static Stream<Arguments> unsupportedOperationException() {
        return Stream.of(
            arguments(unsupported(list -> list.add(0, 9))),
            arguments(unsupported(list -> list.add( 9))),
            arguments(unsupported(list -> list.set(0, 9))),
            arguments(unsupported(list -> list.remove(0))),
            arguments(unsupported(list -> list.remove(Integer.valueOf(9)))),
            arguments(unsupported(list -> list.addAll(List.of(8, 9)))),
            arguments(unsupported(list -> list.addAll(0, List.of(8, 9)))),
            arguments(unsupported(list -> list.removeAll(List.of(1, 2)))),
            arguments(unsupported(list -> list.retainAll(List.of(1, 2)))),
            arguments(unsupported(list -> list.clear())),
            arguments(unsupported(list -> list.iterator().remove())),
            arguments(unsupported(list -> list.listIterator().add(0))),
            arguments(unsupported(list -> list.listIterator().set(0))),
            arguments(unsupported(list -> list.listIterator().remove()))
        );
    }

    private static Consumer<ImmutableList<Integer>> unsupported(final Consumer<ImmutableList<Integer>> immutableListConsumer) {
        return immutableListConsumer;
    }

    @ParameterizedTest
    @MethodSource
    public void unsupportedOperationException(final Consumer<ImmutableList<Integer>> method) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(List.of(1, 3, 2, 5, 4, 6)).reverse();
        assertThrows(UnsupportedOperationException.class, () -> method.accept(reverse));
    }

    public static Stream<Arguments> toStringTest() {
        return Stream.of(
            arguments(List.of(), ""),
            arguments(List.of(1), ">1"),
            arguments(List.of(1, 2), ">2>1"),
            arguments(List.of(1, 2, 3), ">3>2>1")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void toStringTest(final List<Integer> listToReverse, final String expected) {
        assertEquals(expected, new ImmutableList<>(listToReverse).reverse().toString());
    }

    @Test
    public void listIteratorTest() {
        final ImmutableList<Integer> reverse = new ImmutableList<>(List.of(1, 2, 3)).reverse();
        final ListIterator<Integer> actual = reverse.listIterator();
        assertEquals(3, actual.next());
        assertEquals(2, actual.next());
        assertEquals(1, actual.next());
        assertFalse(actual.hasNext());
    }

    @Test
    public void listIteratorWithIndexTest() {
        final ImmutableList<Integer> reverse = new ImmutableList<>(List.of(1, 2, 3)).reverse();
        final ListIterator<Integer> actual = reverse.listIterator(2);
        assertEquals(1, actual.next());
        assertFalse(actual.hasNext());
    }

}