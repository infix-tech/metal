package io.parsingdata.metal;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Selection;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class ImmutableObjectTest {

    @Test
    void checkHashCode() {
        final ImmutableObject immutableObject = new ImmutableObject() {
            @Override
            public String toString() {
                return "test";
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }

            @Override
            public int cachingHashCode() {
                return 25;
            }
        };
        assertEquals(25, immutableObject.hashCode());
    }

    @Test
    void calculateHashOnlyOnce() {
        final ImmutableObject mock = mock(ImmutableObject.class);
        final ImmutableObject immutableObject = new ImmutableObject() {
            @Override
            public String toString() {
                return "";
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }

            @Override
            public int cachingHashCode() {
                return mock.cachingHashCode();
            }
        };
        immutableObject.hashCode();
        immutableObject.hashCode();
        immutableObject.hashCode();
        verify(mock, times(1)).cachingHashCode();
    }

    @Test
    @Timeout(value=500, unit = TimeUnit.MILLISECONDS)
    void performanceTest() {
        // This test would take way too much time without hash caching.
        byte[] input = new byte[8*256];
        Token deep = repn(
            seq(
                def("data", 256),
                tie(
                    seq("token",
                        def("byte", 1),
                        opt(token("token"))
                    ),
                    last(ref("data"))
                )
            ),
            con(input.length / 256)
        );
        Optional<ParseState> result = deep.parse(env(createFromByteStream(new InMemoryByteStream(input))));
        assertTrue(result.isPresent());

        ImmutableList<ParseValue> allValues = Selection.getAllValues(result.get().order, x -> true);
        assertThat(allValues.size, equalTo(2056L));

        final Map<ParseValue, Value> values = new HashMap<>();
        while (allValues != null) {
            values.put(allValues.head, allValues.head);
            allValues = allValues.tail;
        }
    }

}