/*
 * Copyright 2024 Aristide Cittadino
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package it.water.permission.manager;

import it.water.core.api.model.PaginableResult;
import it.water.core.api.repository.query.Query;
import it.water.core.api.repository.query.QueryBuilder;
import it.water.core.api.repository.query.QueryOrder;
import it.water.core.interceptors.annotations.FrameworkComponent;
import it.water.repository.entity.model.PaginatedResult;

import java.util.Collections;

/**
 * System Api for test purpose just to support
 */
@FrameworkComponent(services = TestResourceSystemApi.class)
public class TestResourceSystemImpl implements TestResourceSystemApi {
    private TestResource returnEntity;
    @Override
    public TestResource save(TestResource testResource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestResource update(TestResource testResource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestResource find(long l) {
        return returnEntity;
    }

    @Override
    public TestResource find(Query query) {
        return returnEntity;
    }

    @Override
    public PaginableResult<TestResource> findAll(Query query, int i, int i1, QueryOrder queryOrder) {
        return new PaginatedResult<>(1,1,1,1, Collections.singleton(returnEntity));
    }

    @Override
    public long countAll(Query query) {
        return 1;
    }

    @Override
    public Class<TestResource> getEntityType() {
        return TestResource.class;
    }

    @Override
    public QueryBuilder getQueryBuilderInstance() {
        return null;
    }

    @Override
    public void returnEntity(TestResource resource) {
        this.returnEntity = resource;
    }
}
