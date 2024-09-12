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

package it.water.permission.api.rest.spring;

import com.fasterxml.jackson.annotation.JsonView;
import it.water.core.api.model.PaginableResult;
import it.water.core.api.service.rest.FrameworkRestApi;
import it.water.core.api.service.rest.WaterJsonView;
import it.water.permission.api.rest.PermissionRestApi;
import it.water.permission.model.WaterPermission;
import it.water.service.rest.api.security.LoggedIn;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author Aristide Cittadino
 * Interface exposing same methods of its parent PermissionRestApi but adding Spring annotations.
 * Swagger annotation should be found because they have been defined in the parent PermissionRestApi.
 */
@RequestMapping("/permission")
@FrameworkRestApi
public interface PermissionSpringRestApi extends PermissionRestApi {
    @PostMapping
    @JsonView(WaterJsonView.Public.class)
    WaterPermission save(WaterPermission permission);

    @PutMapping
    @JsonView(WaterJsonView.Public.class)
    WaterPermission update(WaterPermission permission);

    @GetMapping("/{id}")
    WaterPermission find(@PathVariable("id") long id);

    @GetMapping
    PaginableResult<WaterPermission> findAll();

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void remove(@PathVariable("id") long id);

    /**
     * Create a map view of all logged user permissions.
     * Example
     * {
     * "resourceA":{
     * "38":{
     * "save":true,
     * "update":true,
     * "find":true
     * },
     * "54":{
     * "save":false,
     * "update":true,
     * "find":true
     * }
     * }
     * ...
     * }
     *
     * @param entityPks
     * @return
     */
    @PostMapping("/map")
    Map<String, Map<String, Map<String, Boolean>>> elaboratePermissionMap(Map<String, List<Long>> entityPks);
}
