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
package org.mybatis.dynamic.sql.render;

import java.util.concurrent.atomic.AtomicInteger;

import org.mybatis.dynamic.sql.BindableColumn;

/**
 * A rendering strategy is used to generate a platform specific binding.
 *
 * <p>Rendering strategies are used during the rendering phase of statement generation.
 * All generated SQL statements include the generated statement itself, and a map of parameters that
 * should be bound to the statement at execution time. For example, a generated select statement may
 * look like this when rendered for MyBatis:
 *
 * <p><code>select foo from bar where id = #{parameters.p1,jdbcType=INTEGER}</code>
 *
 * <p>In this case, the binding is <code>#{parameters.p1,jdbcType=INTEGER}</code>. MyBatis knows how to interpret this
 * binding - it will look for a value in the <code>parameters.p1</code> property of the parameter object
 * passed to the statement and bind it as a prepared statement parameter when executing the statement.
 */
public abstract class RenderingStrategy {
    public static final String DEFAULT_PARAMETER_PREFIX = "parameters"; //$NON-NLS-1$

    public String formatParameterMapKey(AtomicInteger sequence) {
        return "p" + sequence.getAndIncrement(); //$NON-NLS-1$
    }

    /**
     * This method generates a binding for a parameter to a placeholder in a generated SQL statement.
     *
     * <p>This binding is appropriate when there can be a mapping between a parameter and a known target column,
     * In MyBatis, the binding can specify type information based on the column. The bindings are specific
     * to the target framework.
     *
     * <p>For MyBatis, a binding looks like this: "#{prefix.parameterName,jdbcType=xxx,typeHandler=xxx,javaType=xxx}"
     *
     * <p>For Spring, a binding looks like this: ":parameterName"
     *
     * @param column column definition used for generating type details in a MyBatis binding. Ignored for Spring.
     * @param prefix parameter prefix used for locating the parameters in a SQL provider object. Typically, will be
     *               {@link RenderingStrategy#DEFAULT_PARAMETER_PREFIX}. This is ignored for Spring.
     * @param parameterName name of the parameter. Typically generated by calling
     *     {@link RenderingStrategy#formatParameterMapKey(AtomicInteger)}
     * @return the generated binding
     */
    public abstract String getFormattedJdbcPlaceholder(BindableColumn<?> column, String prefix, String parameterName);

    /**
     * This method generates a binding for a parameter to a placeholder in a generated SQL statement.
     *
     * <p>This binding is appropriate when the parameter is bound to placeholder that is not a known column (such as
     * a limit or offset parameter). The bindings are specific to the target framework.
     *
     * <p>For MyBatis, a binding looks like this: "#{prefix.parameterName}"
     *
     * <p>For Spring, a binding looks like this: ":parameterName"
     *
     * @param prefix parameter prefix used for locating the parameters in a SQL provider object. Typically, will be
     *               {@link RenderingStrategy#DEFAULT_PARAMETER_PREFIX}. This is ignored for Spring.
     * @param parameterName name of the parameter. Typically generated by calling
     *     {@link RenderingStrategy#formatParameterMapKey(AtomicInteger)}
     * @return the generated binding
     */
    public abstract String getFormattedJdbcPlaceholder(String prefix, String parameterName);

    /**
     * This method generates a binding for a parameter to a placeholder in a record based insert statement.
     *
     * <p>This binding is specifically for use with insert, batch insert, and multirow insert statements.
     * These statements bind parameters to properties of a row class. The Spring implementation changes the binding
     * to match values expected for a these insert statements. For MyBatis, the binding is the same
     * as {@link RenderingStrategy#getFormattedJdbcPlaceholder(BindableColumn, String, String)}.
     *
     * <p>For MyBatis, a binding looks like this: "#{prefix.parameterName,jdbcType=xxx,typeHandler=xxx,javaType=xxx}"
     *
     * <p>For Spring, a binding looks like this: ":prefix.parameterName"
     *
     * @param column column definition used for generating type details in a MyBatis binding. Ignored for Spring.
     * @param prefix parameter prefix used for locating the parameters in a SQL provider object. Typically, will be
     *     either "row" or "records[x]" to match the properties of the generated statement object class.
     * @param parameterName name of the parameter. Typically, this is a property in the row class associated with the
     *     insert statement.
     * @return the generated binding
     */
    public String getRecordBasedInsertBinding(BindableColumn<?> column, String prefix, String parameterName) {
        return getFormattedJdbcPlaceholder(column, prefix, parameterName);
    }

    public abstract String getRecordBasedInsertBinding(BindableColumn<?> column, String parameterName);
}
