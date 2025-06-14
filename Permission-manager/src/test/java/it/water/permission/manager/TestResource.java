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

import java.util.Date;

import it.water.core.api.entity.shared.SharedEntity;
import it.water.core.api.permission.ProtectedEntity;
import it.water.core.permission.action.CrudActions;
import it.water.core.permission.annotations.AccessControl;
import it.water.core.permission.annotations.DefaultRoleAccess;
import jakarta.persistence.Id;

/**
 * Test entity used just for saving permission and test them.
 */

@AccessControl(availableActions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE},
        rolesPermissions = {
                //Admin role can do everything
                @DefaultRoleAccess(roleName = TestResource.TEST_ROLE_MANAGER, actions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE}),
                //Viwer has read only access
                @DefaultRoleAccess(roleName = TestResource.TEST_ROLE_VIEWER, actions = {CrudActions.FIND, CrudActions.FIND_ALL}),
                //Editor can do anything but remove
                @DefaultRoleAccess(roleName = TestResource.TEST_ROLE_EDITOR, actions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL})
        })
public class TestResource implements ProtectedEntity, SharedEntity {

    public static final String TEST_ROLE_MANAGER = "test_manager";
    public static final String TEST_ROLE_VIEWER = "test_viewer";
    public static final String TEST_ROLE_EDITOR = "test_editor";

    private long id;
    private Integer version;

    private Long ownerUserId;

    public void setId(long id) {
        this.id = id;
    }

    @Override
    @Id
    public long getId() {
        return id;
    }

    @Override
    public Date getEntityCreateDate() {
        return null;
    }

    @Override
    public Date getEntityModifyDate() {
        return null;
    }

    @Override
    public Integer getEntityVersion() {
        return version;
    }

    @Override
    public void setEntityVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String getResourceName() {
        return TestResource.class.getName();
    }

    @Override
    public Long getOwnerUserId() {
        return ownerUserId;
    }

    @Override
    public void setOwnerUserId(Long aLong) {
        this.ownerUserId = aLong;
    }
}
