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

package it.water.permission.service.integration;

import it.water.core.api.action.ResourceAction;
import it.water.core.api.model.Resource;
import it.water.core.api.permission.Permission;
import it.water.core.api.service.integration.PermissionIntegrationClient;
import it.water.core.interceptors.annotations.FrameworkComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@FrameworkComponent
public class PermissionIntegrationRestClient implements PermissionIntegrationClient {

    @Override
    public Permission findByUserAndResource(long l, Resource resource) {
        //todo implement rest invocation and caching logic
        return null;
    }

    @Override
    public Permission findByUserAndResourceName(long l, String s) {
        //todo implement rest invocation and caching logic
        return null;
    }

    @Override
    public Permission findByUserAndResourceNameAndResourceId(long l, String s, long l1) {
        //todo implement rest invocation and caching logic
        return null;
    }

    @Override
    public Permission findByRoleAndResourceName(long l, String s) {
        //todo implement rest invocation and caching logic
        return null;
    }

    @Override
    public Collection<Permission> findByRole(long l) {
        //todo implement rest invocation and caching logic
        return Collections.emptyList();
    }

    @Override
    public Permission findByRoleAndResourceNameAndResourceId(long l, String s, long l1) {
        return null;
    }

    @Override
    public void checkOrCreatePermissions(long l, List<ResourceAction<?>> list) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkOrCreatePermissionsSpecificToEntity(long l, long l1, List<ResourceAction<?>> list) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean permissionSpecificToEntityExists(String s, long l) {
        return false;
    }
}
