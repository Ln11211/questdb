/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2022 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.griffin.engine.functions.math;

import io.questdb.cairo.CairoConfiguration;
import io.questdb.cairo.sql.Function;
import io.questdb.cairo.sql.Record;
import io.questdb.griffin.FunctionFactory;
import io.questdb.griffin.SqlExecutionContext;
import io.questdb.griffin.engine.functions.BinaryFunction;
import io.questdb.griffin.engine.functions.DoubleFunction;
import io.questdb.griffin.engine.functions.TernaryFunction;
import io.questdb.griffin.engine.functions.UnaryFunction;
import io.questdb.std.IntList;
import io.questdb.std.ObjList;

public class WidthBucketDouble implements FunctionFactory {
    @Override
    public String getSignature() {
        return "width_bucket(Dddi)";}

    @Override
    public Function newInstance(int position, ObjList<Function> args, IntList argPositions, CairoConfiguration configuration, SqlExecutionContext sqlExecutionContext) {
        return new widthbucket(args.getQuick(0), args.getQuick(1), args.getQuick(2), args.getQuick(3));
    }

    private static final class widthbucket extends DoubleFunction implements TernaryFunction,UnaryFunction {
        private final Function left;
        private final Function right;
        private final Function center;
        private final Function function;

        public widthbucket(Function left, Function center, Function right, Function function) {
            this.left = left;
            this.center = center;
            this.right =right;
            this.function=function;
        }

        public Function getArg() {
            return function;
        }

        @Override
        public double getDouble(Record rec) {
            double op= left.getDouble(rec);
            double b1= center.getDouble(rec);
            double b2= right.getDouble(rec);
            double count= function.getDouble(rec);

           if(op>=b1 && op<=b2 ) {
               return (int)( op / ( (b2-b1)/count ) )+1;
           }
           else if(op>b2)
               return count+1;
           else return 0;
        }

        @Override
        public Function getLeft() {
            return left;
        }
        @Override
        public Function getCenter() {
            return center;
        }
        @Override
        public Function getRight() {
            return right;
        }

    }
}
