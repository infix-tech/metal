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

package nl.minvenj.nfi.ddrx.format;

import static nl.minvenj.nfi.ddrx.Shorthand.cat;
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.eqNum;
import static nl.minvenj.nfi.ddrx.Shorthand.expTrue;
import static nl.minvenj.nfi.ddrx.Shorthand.not;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;

import java.util.zip.CRC32;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.expression.value.UnaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.Value;
import nl.minvenj.nfi.ddrx.expression.value.ValueOperation;
import nl.minvenj.nfi.ddrx.token.Token;

public class PNG {

    private static final Token HEADER = seq(def("highbit", con(1), eq(con(0x89))),
                                            seq(def("PNG", con(3), eq(con("PNG"))),
                                                def("controlchars", con(4), eq(con(0x0d0a1a0a)))));
    private static final Token FOOTER = seq(def("footerlength", con(4), eqNum(con(0))),
                                            seq(def("footertype", con(4), eq(con("IEND"))),
                                                def("footercrc32", con(4), eq(con(0xae426082l)))));
    private static final Token STRUCT = seq(def("length", con(4), expTrue()),
                                            seq(def("chunktype", con(4), not(eq(con("IEND")))),
                                                seq(def("chunkdata", ref("length"), expTrue()),
                                                    def("crc32", con(4), eq(new UnaryValueExpression(cat(ref("chunktype"), ref("chunkdata"))) {
                                                        @Override
                                                        public Value eval(final Environment env) {
                                                            return _op.eval(env).operation(new ValueOperation() {
                                                                @Override
                                                                public Value execute(byte[] value) {
                                                                    CRC32 crc = new CRC32();
                                                                    crc.update(value);
                                                                    final long crcValue = crc.getValue();
                                                                    return new Value(new byte[] { (byte)((crcValue & 0xff000000) >> 24), (byte)((crcValue & 0xff0000) >> 16), (byte)((crcValue & 0xff00) >> 8), (byte)(crcValue & 0xff) }, env.getEncoding());
                                                                }
                                                            });
                                                        }
                                                    })))));
    public static final Token FORMAT = seq(HEADER,
                                           seq(rep(STRUCT),
                                               FOOTER));

}