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

import it.water.core.api.entity.owned.OwnedChildResource;
import it.water.core.api.model.BaseEntity;
import it.water.core.api.permission.ProtectedEntity;

import java.util.Date;

public class TestResourceChild implements ProtectedEntity, OwnedChildResource {

    public TestResource parent;

    public TestResourceChild(TestResource parent) {
        this.parent = parent;
    }

    @Override
    public BaseEntity getParent() {
        return this.parent;
    }

    @Override
    public long getId() {
        return 1l;
    }

    @Override
    public Date getEntityCreateDate() {
        return new Date();
    }

    @Override
    public Date getEntityModifyDate() {
        return new Date();
    }

    @Override
    public Integer getEntityVersion() {
        return 1;
    }

    @Override
    public void setEntityVersion(Integer integer) {
        throw new UnsupportedOperationException();
    }

    public void setParent(TestResource parent) {
        this.parent = parent;
    }

    @Override
    public Long getOwnerUserId() {
        return this.parent.getOwnerUserId();
    }

    @Override
    public void setOwnerUserId(Long aLong) {
        //do nothing
    }
}
