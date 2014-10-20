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

package nl.minvenj.nfi.ddrx.util;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.data.ParsedValueList;

public class EnvironmentFactory {

    public static Environment stream(final int... bytes) {
        return new Environment(new ParsedValueList(), new InMemoryByteStream(toByteArray(bytes)), 0);
    }

    public static Environment stream(final URI resource) throws IOException {
        return new Environment(new ParsedValueList(), new InMemoryByteStream(Files.readAllBytes(Paths.get(resource))), 0L);
    }

    public static Environment stream(final String value, final Charset charset) {
        return new Environment(new ParsedValueList(), new InMemoryByteStream(value.getBytes(charset)), 0L);
    }

    public static byte[] toByteArray(final int... bytes) {
        final byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            out[i] = (byte) bytes[i];
        }
        return out;
    }

}