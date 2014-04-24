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

package nl.minvenj.nfi.ddrx.expression.value;

import nl.minvenj.nfi.ddrx.data.Environment;

public class Cat extends BinaryValueExpression {

    public Cat(ValueExpression lop, ValueExpression rop) {
        super(lop, rop);
    }

    @Override
    public Value eval(final Environment env) {
        final byte[] l = _lop.eval(env).getValue();
        final byte[] r = _rop.eval(env).getValue();
        byte[] res = new byte[l.length + r.length];
        System.arraycopy(l, 0, res, 0, l.length);
        System.arraycopy(r, 0, res, l.length, r.length);
        return new Value(res, env.getEncoding());
    }

}