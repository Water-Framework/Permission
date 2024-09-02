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

package it.water.permission.service;

import it.water.core.api.action.ResourceAction;
import it.water.core.api.model.Resource;
import it.water.core.api.permission.Permission;
import it.water.core.api.service.integration.PermissionIntegrationClient;
import it.water.core.interceptors.annotations.FrameworkComponent;
import it.water.core.interceptors.annotations.Inject;
import it.water.permission.api.PermissionSystemApi;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@FrameworkComponent
public class PermissionIntegrationLocalClient implements PermissionIntegrationClient {

    @Inject
    @Setter
    private PermissionSystemApi permissionSystemApi;

    @Override
    public Permission findByUserAndResource(long userId, Resource resource) {
        return permissionSystemApi.findByUserAndResource(userId, resource);
    }

    @Override
    public Permission findByUserAndResourceName(long userId, String resourceName) {
        return permissionSystemApi.findByUserAndResourceName(userId, resourceName);
    }

    @Override
    public Permission findByUserAndResourceNameAndResourceId(long userId, String resourceName, long resourceId) {
        return permissionSystemApi.findByUserAndResourceNameAndResourceId(userId, resourceName, resourceId);
    }

    @Override
    public Permission findByRoleAndResourceName(long roleId, String resourceName) {
        return permissionSystemApi.findByRoleAndResourceName(roleId, resourceName);
    }

    @Override
    public Collection<Permission> findByRole(long roleId) {
        Set<Permission> permissions = new HashSet<>();
        permissionSystemApi.findByRole(roleId).forEach(waterPermission -> permissions.add(waterPermission));
        return permissions;
    }

    @Override
    public Permission findByRoleAndResourceNameAndResourceId(long roleId, String resourceName, long resourceId) {
        return permissionSystemApi.findByRoleAndResourceNameAndResourceId(roleId, resourceName, resourceId);
    }

    @Override
    public void checkOrCreatePermissions(long roleId, List<ResourceAction> list) {
        permissionSystemApi.checkOrCreatePermissions(roleId, list);
    }

    @Override
    public void checkOrCreatePermissionsSpecificToEntity(long roleId, long entityId, List<ResourceAction> actions) {
        permissionSystemApi.checkOrCreatePermissionsSpecificToEntity(roleId, entityId, actions);
    }

    @Override
    public boolean permissionSpecificToEntityExists(String resourceName, long resourceId) {
        return permissionSystemApi.permissionSpecificToEntityExists(resourceName, resourceId);
    }
}
