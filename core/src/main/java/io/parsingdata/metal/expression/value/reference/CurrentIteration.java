/*
 * Copyright 2013-2016 Netherlands Forensic Institute
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

package io.parsingdata.metal.expression.value.reference;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.expression.value.ConstantFactory.createFromNumeric;
import static java.util.Collections.nCopies;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link ValueExpression} that represents the current iteration in an
 * iterable {@link io.parsingdata.metal.token.Token} (e.g. when inside a
 * {@link io.parsingdata.metal.token.Rep} or
 * {@link io.parsingdata.metal.token.RepN}).
 */
public class CurrentIteration implements ValueExpression {

    private final ValueExpression level;

    public CurrentIteration(final ValueExpression level) {
        this.level = level;
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseState parseState, final Encoding encoding) {
        final int level = getLevel(parseState, encoding);
        final ParseGraph currentIterable = findCurrentIterable(parseState.order, new ParseGraphCandidates(level + 1)).computeResult().head();
        if (currentIterable.isEmpty()) { return ImmutableList.create(Optional.empty()); }

        final BigInteger currentIteration = countIterable(currentIterable, ZERO).computeResult();
        return ImmutableList.create(Optional.of(createFromNumeric(currentIteration, new Encoding())));
    }

    private int getLevel(final ParseState parseState, final Encoding encoding) {
        ImmutableList<Optional<Value>> evaluatedLevel = level.eval(parseState, encoding);
        if (evaluatedLevel.size != 1 || !evaluatedLevel.head.isPresent()) {
            throw new IllegalArgumentException("Level must evaluate to a single non-empty value.");
        }
        return evaluatedLevel.head.get().asNumeric().intValueExact();
    }

    private Trampoline<ParseGraphCandidates> findCurrentIterable(final ParseItem item, final ParseGraphCandidates iterableCandidates) {
        if (!item.isGraph()) { return complete(() -> iterableCandidates); }
        if (item.getDefinition().isIterable()) {
            return intermediate(() -> findCurrentIterable(item.asGraph().head, iterableCandidates.add(item.asGraph())));
        }
        return intermediate(() -> findCurrentIterable(item.asGraph().head, iterableCandidates));
    }

    private Trampoline<BigInteger> countIterable(final ParseGraph graph, final BigInteger count) {
        if (!graph.isEmpty()) { return intermediate(() -> countIterable(graph.tail, count.add(ONE))); }
        return complete(() -> count.subtract(ONE));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
                && Objects.equals(level, ((CurrentIteration)obj).level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), level);
    }

    /**
     * ParseGraphCandidates keeps track of the last n ParseGraphs.
     */
    private class ParseGraphCandidates {

        private final Queue<ParseGraph> list;

        ParseGraphCandidates(final int n) {
            list = new ArrayDeque<>(nCopies(n, ParseGraph.EMPTY));
        }

        ParseGraphCandidates add(ParseGraph parseGraph) {
            list.add(parseGraph);
            list.remove();
            return this;
        }

        ParseGraph head() {
            return list.element();
        }
    }

}
