
package it.water.permission.service.rest;

import it.water.core.api.service.BaseEntityApi;
import it.water.core.api.service.rest.FrameworkRestController;
import it.water.core.interceptors.annotations.Inject;
import it.water.permission.api.PermissionApi;
import it.water.permission.api.rest.PermissionRestApi;
import it.water.permission.model.WaterPermission;
import it.water.service.rest.persistence.BaseEntityRestApi;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


/**
 * @Generated by Water Generator
 * Rest Api Class for Permission entity.
 */
@FrameworkRestController(referredRestApi = PermissionRestApi.class)
public class PermissionRestControllerImpl extends BaseEntityRestApi<WaterPermission> implements PermissionRestApi {
    private static Logger log = LoggerFactory.getLogger(PermissionRestControllerImpl.class.getName());

    @Inject
    @Setter
    private PermissionApi permissionApi;

    @Override
    protected BaseEntityApi<WaterPermission> getEntityService() {
        return permissionApi;
    }

    @Override
    public Map<String, Map<String, Map<String, Boolean>>> elaboratePermissionMap(Map<String, List<Long>> entityPks) {
        this.log.debug("Invoking elaboratePermissionMap from rest service for {}", this.getEntityService().getEntityType().getSimpleName());
        return permissionApi.entityPermissionMap(entityPks);
    }
}
