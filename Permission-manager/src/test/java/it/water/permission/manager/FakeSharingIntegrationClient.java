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

import it.water.core.api.service.integration.SharedEntityIntegrationClient;
import it.water.core.interceptors.annotations.FrameworkComponent;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Simple mock to have more test coverage
 */
@FrameworkComponent(priority = 2, services = SharedEntityIntegrationClient.class)
public class FakeSharingIntegrationClient implements SharedEntityIntegrationClient {

    private Collection<Long> sharedEntityIds = new HashSet<>();

    @Override
    public Collection<Long> fetchSharingUsersIds(String s, long l) {
        //just returning standard id
        return List.of(1l);
    }

    public void addId(long id) {
        sharedEntityIds.add(id);
    }

    public void removeId(long id) {
        sharedEntityIds.remove(id);
    }

    public void clearAll() {
        sharedEntityIds.clear();
    }
}
