/**
 *    Copyright 2016-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.dynamic.sql.update.render;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.FragmentAndParameters;
import org.mybatis.dynamic.sql.util.FragmentCollector;
import org.mybatis.dynamic.sql.util.UpdateMapping;
import org.mybatis.dynamic.sql.where.WhereModel;
import org.mybatis.dynamic.sql.where.render.WhereClauseAndParameters;

public class UpdateRenderer {
    private UpdateModel updateModel;
    private RenderingStrategy renderingStrategy;
    
    private UpdateRenderer(Builder builder) {
        updateModel = Objects.requireNonNull(builder.updateModel);
        renderingStrategy = Objects.requireNonNull(builder.renderingStrategy);
    }
    
    public UpdateStatement render() {
        SetPhraseVisitor visitor = new SetPhraseVisitor(renderingStrategy);

        FragmentCollector fc = updateModel.mapColumnValues(toFragmentAndParameters(visitor))
                .collect(FragmentCollector.collect());
        
        return UpdateStatement.withTableName(updateModel.table().name())
                .withSetClause(calculateSetPhrase(fc))
                .withParameters(fc.parameters())
                .withWhereClause(updateModel.whereModel().map(this::renderWhereModel))
                .build();
    }

    private String calculateSetPhrase(FragmentCollector collector) {
        return collector.fragments()
                .collect(Collectors.joining(", ", "set ", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    private WhereClauseAndParameters renderWhereModel(WhereModel whereModel) {
        return whereModel.render(renderingStrategy);
    }

    private Function<UpdateMapping, FragmentAndParameters> toFragmentAndParameters(SetPhraseVisitor visitor) {
        return updateMapping -> toFragmentAndParameters(visitor, updateMapping);
    }
    
    private FragmentAndParameters toFragmentAndParameters(SetPhraseVisitor visitor, UpdateMapping updateMapping) {
        return updateMapping.accept(visitor);
    }
    
    public static Builder withUpdateModel(UpdateModel updateModel) {
        return new Builder().withUpdateModel(updateModel);
    }
    
    public static class Builder {
        private UpdateModel updateModel;
        private RenderingStrategy renderingStrategy;

        public Builder withUpdateModel(UpdateModel updateModel) {
            this.updateModel = updateModel;
            return this;
        }
        
        public Builder withRenderingStrategy(RenderingStrategy renderingStrategy) {
            this.renderingStrategy = renderingStrategy;
            return this;
        }
        
        public UpdateRenderer build() {
            return new UpdateRenderer(this);
        }
    }
}
