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

package it.water.permission;

import it.water.core.api.action.ResourceAction;
import it.water.core.api.service.Service;
import it.water.core.api.service.integration.PermissionIntegrationClient;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.testing.utils.junit.WaterTestExtension;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;

@ExtendWith(WaterTestExtension.class)
class PermissionIntegrationRestClientTest implements Service {

    @Inject
    @Setter
    private PermissionIntegrationClient permissionIntegrationClient;

    @Test
    void testPermissionIntegrationRestClient() {
        Assertions.assertNotNull(permissionIntegrationClient);
        Assertions.assertNull(permissionIntegrationClient.findByRoleAndResourceNameAndResourceId(0, "", 0));
        Assertions.assertNull(permissionIntegrationClient.findByRoleAndResourceName(0, ""));
        Assertions.assertNull(permissionIntegrationClient.findByUserAndResource(0, null));
        Assertions.assertNull(permissionIntegrationClient.findByUserAndResourceNameAndResourceId(0, "", 0));
        Assertions.assertNull(permissionIntegrationClient.findByUserAndResourceName(0, ""));
        Assertions.assertEquals(0, permissionIntegrationClient.findByRole(0).size());
        List<ResourceAction<?>> list = Collections.emptyList();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> permissionIntegrationClient.checkOrCreatePermissions(0, list));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> permissionIntegrationClient.checkOrCreatePermissionsSpecificToEntity(0, 0, list));
        Assertions.assertFalse(permissionIntegrationClient.permissionSpecificToEntityExists("", 0));
    }
}
