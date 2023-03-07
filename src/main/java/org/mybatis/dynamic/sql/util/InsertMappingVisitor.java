/*
 *    Copyright 2016-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.dynamic.sql.util;

import org.mybatis.dynamic.sql.insert.render.FieldAndValueAndParameters;

import java.util.Optional;

public abstract class InsertMappingVisitor<R> implements ColumnMappingVisitor<R> {
    @Override
    public final <T> R visit(ValueMapping<T> mapping) {
        throw new UnsupportedOperationException(Messages.getInternalErrorString(5));
    }

    @Override
    public final <T> R visit(ValueOrNullMapping<T> mapping) {
        throw new UnsupportedOperationException(Messages.getInternalErrorString(6));
    }

    @Override
    public final <T> R visit(ValueWhenPresentMapping<T> mapping) {
        throw new UnsupportedOperationException(Messages.getInternalErrorString(7));
    }

    @Override
    public final R visit(SelectMapping mapping) {
        throw new UnsupportedOperationException(Messages.getInternalErrorString(8));
    }

    @Override
    public final R visit(ColumnToColumnMapping columnMapping) {
        throw new UnsupportedOperationException(Messages.getInternalErrorString(9));
    }

    @Override
    public R visit(RowMapping mapping) {
        // TODO - fix error number
        throw new UnsupportedOperationException(Messages.getInternalErrorString(99));
    }
}
