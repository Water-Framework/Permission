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

import it.water.core.api.action.Action;
import it.water.core.api.action.ActionsManager;
import it.water.core.api.action.ResourceAction;
import it.water.core.api.entity.owned.OwnedChildResource;
import it.water.core.api.entity.owned.OwnedResource;
import it.water.core.api.entity.shared.SharedEntity;
import it.water.core.api.model.BaseEntity;
import it.water.core.api.model.Resource;
import it.water.core.api.model.User;
import it.water.core.api.permission.*;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.service.BaseEntitySystemApi;
import it.water.core.api.service.integration.PermissionIntegrationClient;
import it.water.core.api.service.integration.RoleIntegrationClient;
import it.water.core.api.service.integration.SharedEntityIntegrationClient;
import it.water.core.api.service.integration.UserIntegrationClient;
import it.water.core.interceptors.annotations.FrameworkComponent;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.permission.action.ActionFactory;
import it.water.core.permission.action.UserActions;
import it.water.repository.entity.model.exceptions.NoResultException;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@FrameworkComponent(properties = {PermissionManagerComponentProperties.PERMISSION_MANAGER_IMPLEMENTATION_PROP+"="+PermissionManagerComponentProperties.PERMISSION_MANAGER_DEFAILT_IMPLEMENTATION})
public class PermissionManagerDefault implements PermissionManager {
    private Logger log = LoggerFactory.getLogger(PermissionManagerDefault.class.getName());

    /**
     * Injecting the PermissionSystemService to use methods in PermissionSystemApi
     * interface
     */
    @Inject
    @Setter
    private PermissionIntegrationClient permissionIntegrationClient;

    @Inject
    @Setter
    private UserIntegrationClient userIntegrationClient;

    @Inject
    @Setter
    private SharedEntityIntegrationClient sharedEntityIntegrationClient;

    @Inject
    @Setter
    private RoleIntegrationClient roleIntegrationClient;

    @Inject
    @Setter
    private ActionsManager actionsManager;

    @Inject
    @Setter
    private ComponentRegistry componentRegistry;

    /**
     * @param username
     * @param rolesNames
     * @return
     */
    @Override
    public boolean userHasRoles(String username, String[] rolesNames) {
        if (username == null || username.length() == 0)
            return false;
        Collection<String> rolesNamesCollection = Arrays.asList(rolesNames);
        User u = userIntegrationClient.fetchUserByUsername(username);
        Collection<Role> roles = roleIntegrationClient.fetchUserRoles(u.getId());
        //find user Roles
        return roles.stream().anyMatch(r -> rolesNamesCollection.contains(r.getName()));
    }

    /**
     * Checks if an existing user has permissions for action of HyperIoTAction.
     * Moreover every user, if protected, is set as a base entity of the HyperIoT
     * platform.
     *
     * @param username parameter that indicates the username of user
     * @param entity   parameter that indicates the resource name of user
     * @param action   interaction of the user with HyperIoT platform
     */
    @Override
    public boolean checkPermission(String username, Resource entity,
                                   Action action) {
        if (!PermissionManager.isProtectedEntity(entity.getResourceName()))
            return true;
        log.debug(
                "invoking checkPermission User {} Entity Resource Name: {}", new Object[]{username, entity.getResourceName(), action.getActionName(), action.getActionId()});
        if (!PermissionManager.isProtectedEntity(entity))
            return true;

        if (username == null || entity == null || action == null)
            return false;

        User user = this.userIntegrationClient.fetchUserByUsername(username);

        // every protected entity is a base entity
        ProtectedEntity entityResource = (ProtectedEntity) entity;

        return hasPermission(user, entityResource, action);
    }

    /**
     * Checks if an existing user has permissions for action of HyperIoTAction.
     *
     * @param username     parameter that indicates the username of user
     * @param resourceName parameter that indicates the resource name of action
     * @param action       interaction of the user with HyperIoT platform
     */
    @Override
    public boolean checkPermission(String username, String resourceName, Action action) {
        if (!PermissionManager.isProtectedEntity(resourceName))
            return true;
        log.debug(
                "invoking checkPermission User {} Entity Resource Name: {} Action Name: {}  actionId: {}", new Object[]{username, resourceName, action.getActionName(), action.getActionId()});
        if (username == null || resourceName == null || action == null)
            return false;

        if (!PermissionManager.isProtectedEntity(resourceName))
            return true;

        return hasPermission(username, resourceName, action);
    }

    /**
     * Checks if an existing user has permissions for action of HyperIoTAction.
     *
     * @param username parameter that indicates the username of user
     * @param resource parameter that indicates the resource name of action
     * @param action   interaction of the user with HyperIoT platform
     */
    @Override
    public boolean checkPermission(String username, Class<? extends Resource> resource,
                                   Action action) {
        if (!PermissionManager.isProtectedEntity(resource.getName()))
            return true;
        log.debug(
                "invoking checkPermission User {} Entity Resource Name: {} Action Name: {}  actionId: {}", new Object[]{username, resource.getName(), action.getActionName(), action.getActionId()});
        if (username == null || resource == null || action == null)
            return false;

        return hasPermission(username, resource.getName(), action);
    }

    /**
     * Returns a map containing all actiona available for every resource
     *
     * @param username
     * @param entityPks
     * @return
     */
    @Override
    public Map<String, Map<String, Map<String, Boolean>>> entityPermissionMap(String username, Map<String, List<Long>> entityPks) {
        Map<String, Map<String, Map<String, Boolean>>> userPermissionMap = new HashMap<>();
        entityPks.keySet().forEach(entityClass -> {
            userPermissionMap.computeIfAbsent(entityClass, key -> new HashMap<>());
            BaseEntitySystemApi<?> baseEntitySystemApi = componentRegistry.findEntitySystemApi(entityClass);
            if (baseEntitySystemApi != null) {
                entityPks.get(entityClass).forEach(entityId -> {
                    String entityIdsSts = String.valueOf(entityId);
                    userPermissionMap.get(entityClass).computeIfAbsent(entityIdsSts, key -> new HashMap<>());
                    try {
                        BaseEntity entity = baseEntitySystemApi.find(entityId);
                        if (entity != null) {
                            List<ResourceAction<Resource>> actions = actionsManager.getActions().get(entityClass).getList();
                            actions.forEach(resourceAction -> {
                                boolean hasPermission = checkPermission(username, entity, resourceAction.getAction());
                                userPermissionMap.get(entityClass).get(entityIdsSts).put(resourceAction.getAction().getActionName(), hasPermission);
                            });
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            }
        });
        return userPermissionMap;
    }

    /**
     * @param username     parameter that indicates the username of entity
     * @param resourceName parameter that indicates the resource name of action
     * @param action       interaction of the user with HyperIoT platform
     * @param entities     List of entities User must own in order to perform the action
     * @return
     */
    public boolean checkPermissionAndOwnership(String username, String resourceName, Action action, Resource... entities) {
        boolean hasPermission = false;
        if (!PermissionManager.isProtectedEntity(resourceName))
            hasPermission = true;
        else
            hasPermission = checkPermission(username, resourceName, action);

        if (hasPermission && entities != null) {
            User user = this.userIntegrationClient.fetchUserByUsername(username);
            for (int i = 0; i < entities.length && hasPermission; i++) {
                hasPermission = hasPermission && user != null && entities[i] != null && checkUserOwnsResource(user, entities[i]);
            }
        }
        return hasPermission;
    }

    /**
     * @param username parameter that indicates the username of entity
     * @param resource parameter that indicates the resource on which the action should be performed
     * @param action   interaction of the user with HyperIoT platform
     * @param entities List of other entities User must own in order to perform the action
     * @return
     */
    public boolean checkPermissionAndOwnership(String username, Resource resource, Action action, Resource... entities) {
        boolean hasPermission = false;
        if (!PermissionManager.isProtectedEntity(resource.getResourceName()))
            hasPermission = true;
        else
            hasPermission = checkPermission(username, resource.getResourceName(), action);

        if (hasPermission && entities != null) {
            User user = this.userIntegrationClient.fetchUserByUsername(username);
            for (int i = 0; i < entities.length && hasPermission; i++) {
                hasPermission = hasPermission && user != null && entities[i] != null && checkUserOwnsResource(user, entities[i]);
            }
        }
        return hasPermission;
    }


    /**
     * Find an existing user by username. Returns actions permission by user role.
     *
     * @param username     parameter required to find a user by his username
     * @param resourceName parameter that indicates the resource name
     * @param action       interaction of the user with HyperIoT platform
     * @return Actions permission by user
     */
    private boolean hasPermission(String username, String resourceName, Action action) {
        User user = this.userIntegrationClient.fetchUserByUsername(username);
        if (user == null) {
            return false;
        }

        if (user.isAdmin())
            return true;

        Collection<Role> userRoles = roleIntegrationClient.fetchUserRoles(user.getId());

        if (userRoles == null || userRoles.isEmpty())
            return false;

        Iterator<? extends Role> it = userRoles.iterator();

        while (it.hasNext()) {
            Role r = it.next();
            Permission permission = permissionIntegrationClient.findByRoleAndResourceName(r.getId(), resourceName);
            if (permission != null
                    && hasPermission(permission.getActionIds(), action.getActionId()))
                return true;
        }
        return false;
    }

    /**
     * Find an existing user by username. Returns actions permission by user role.
     *
     * @param user   parameter required to find a user by his username
     * @param action interaction of the user with HyperIoT platform
     * @return Actions permission by user
     */
    private boolean hasPermission(User user, ProtectedEntity entity,
                                  Action action) {
        if (user == null) {
            return false;
        }
        if (user.isAdmin())
            return true;

        Collection<Role> userRoles = roleIntegrationClient.fetchUserRoles(user.getId());

        if (userRoles == null || userRoles.isEmpty())
            return false;

        Iterator<? extends Role> it = userRoles.iterator();
        while (it.hasNext()) {
            Role r = it.next();
            Permission permission = null;

            try {
                permission = permissionIntegrationClient.findByRoleAndResourceName(r.getId(), entity.getResourceName());
            } catch (NoResultException e) {
                log.warn("No permission found for: {}  on resource {}"
                        , new Object[]{r.getName(), entity.getResourceName()});
            }

            Permission userPermission = null;
            try {
                userPermission = permissionIntegrationClient.findByUserAndResourceName(user.getId(), entity.getResourceName());
            } catch (NoResultException e) {
                log.warn("No permission found for: {}  on resource {}"
                        , new Object[]{user.getUsername(), entity.getResourceName()});
            }

            Permission permissionSpecific = null;
            try {
                permissionSpecific = permissionIntegrationClient.findByRoleAndResourceNameAndResourceId(r.getId(),
                        entity.getResourceName(), entity.getId());
            } catch (NoResultException e) {
                log.warn("No specific permission found for: {}  on resource {}"
                        , new Object[]{r.getName(), entity.getResourceName()});
            }

            Permission userPermissionSpecific = null;
            try {
                userPermissionSpecific = permissionIntegrationClient.findByUserAndResourceNameAndResourceId(user.getId(),
                        entity.getResourceName(), entity.getId());
            } catch (NoResultException e) {
                log.warn("No specific permission found for: {}  on resource {}"
                        , new Object[]{user.getUsername(), entity.getResourceName()});
            }

            Permission permissionImpersonation = null;
            try {
                permissionImpersonation = permissionIntegrationClient.findByRoleAndResourceName(r.getId(),
                        User.class.getName());
            } catch (NoResultException e) {
                log.warn("No impersonification permission found for: {}  on resource {}"
                        , new Object[]{r.getName(), entity.getResourceName()});
            }
            // it initialize the value with the general value based on resource name
            // general permission is : permission based on the role or permission based on user
            boolean hasGeneralPermission = (permission != null
                    && hasPermission(permission.getActionIds(), action.getActionId())) || (userPermission != null && hasPermission(userPermission.getActionIds(), action.getActionId()));
            // entity permission is specific if it is found on role or user
            boolean hasEntityPermission = (permissionSpecific != null
                    && hasPermission(permissionSpecific.getActionIds(), action.getActionId())) || (userPermissionSpecific != null
                    && hasPermission(userPermissionSpecific.getActionIds(), action.getActionId()));

            boolean existPermissionSpecificToEntity = permissionIntegrationClient.permissionSpecificToEntityExists(entity.getResourceName(), entity.getId());

            Action impersonateAction = actionsManager.getActions().get(User.class.getName()).getAction(UserActions.IMPERSONATE);
            boolean userOwnsResource = checkUserOwnsResource(user, entity);
            boolean userSharesResource = checkUserSharesResource(user, entity);
            boolean hasImpersonationPermission = permissionImpersonation != null && hasPermission(
                    permissionImpersonation.getActionIds(), impersonateAction.getActionId());
            // The value is true only if the entity permission exists and contains the
            // actionId, or if
            // the entity permission doesn't exists then the rule follow the
            // generalPermission
            // AND if the resource is an owned resource is accessed by the right user or the
            // accessing user has the impersonation permission
            if ((((permissionSpecific != null || userPermissionSpecific != null) && hasEntityPermission) || (permissionSpecific == null && userPermissionSpecific == null && hasGeneralPermission))
                    && (userOwnsResource ||
                    ((userSharesResource && !existPermissionSpecificToEntity && hasGeneralPermission) || (userSharesResource && (permissionSpecific != null || userPermissionSpecific != null) && hasEntityPermission)) ||
                    hasImpersonationPermission))
                return true;
        }
        return false;
    }

    /**
     * Performs a bitwise operation between the permissionActionIds and the
     * actionId. It manipulate the bits with & operator used to compare bits of each
     * operand.
     *
     * @param permissionActionIds parameter that indicates the Permission actionIds
     * @param actionId            parameter that indicates the id of HyperIoTAction
     */
    private boolean hasPermission(long permissionActionIds, long actionId) {
        boolean hasPermission = (permissionActionIds & actionId) == actionId;
        log.debug(
                "invoking hasPermission permissionActionIds & actionId == actionId {}",
                new Object[]{permissionActionIds, actionId, hasPermission});
        return hasPermission;
    }

    /**
     * @param user     the current logged user
     * @param resource the current resource
     * @return true if the resource is owned by the current logged user or the
     * resource is not a owned resource, false otherwise.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean checkUserOwnsResource(User user, Object resource) {
        if (user.isAdmin())
            return true;

        BaseEntity entity = (BaseEntity) resource;
        User resourceOwner = null;
        //used when user shares only child entities
        boolean userSharesResource = checkUserSharesResource(user, entity);
        // looks up for a persisted entity in the hierarchy chain
        while (true) {
            // double check if the passed entity is consistent (must belong to `user`)
            if (entity instanceof OwnedResource) {
                resourceOwner = ((OwnedResource) entity).getUserOwner();
                if (!userSharesResource && resourceOwner != null && resourceOwner.getId() != 0
                        && user.getId() != resourceOwner.getId()) {
                    return false;
                } else
                    break;
            } else if (entity instanceof OwnedChildResource) {
                OwnedChildResource child = (OwnedChildResource) entity;
                if (child.getParent() != null)
                    entity = child.getParent();
                else
                    break;
            } else
                break;
        }
        if (entity.getId() != 0) {
            // load the persisted entity
            BaseEntitySystemApi service = componentRegistry.findEntitySystemApi(((BaseEntity) resource).getResourceName());
            if (service != null) {
                BaseEntity persistedEntity = service.find(entity.getId());
                // verify the owner
                if (persistedEntity instanceof OwnedResource) {
                    resourceOwner = ((OwnedResource) persistedEntity).getUserOwner();
                } else if (persistedEntity instanceof OwnedChildResource) {
                    OwnedChildResource persistedChildEntity = (OwnedChildResource) persistedEntity;
                    if (persistedChildEntity != null && persistedChildEntity.getParent() != null) {
                        // retry with the parent resource
                        return (persistedChildEntity.getParent() == null || checkUserOwnsResource(user, persistedChildEntity.getParent()))
                                && checkUserOwnsResource(user, persistedChildEntity.getParent());
                    }
                } else {
                    // resource is not owned so check can pass
                    return (persistedEntity != null);
                }
            }
        } else {
            return true;
        }

        return (user != null && resourceOwner != null && (user.getId() == resourceOwner.getId() || userSharesResource));
    }

    /**
     * @param user     the current logged user
     * @param resource the current resource
     * @return true if the resource is shared to the current logged user or the
     * resource is not a shared resource, false otherwise.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean checkUserSharesResource(User user, Object resource) {
        if (user.isAdmin())
            return true;
        BaseEntity entity = (BaseEntity) resource;
        Collection<Long> sharingUsers = new ArrayList<>();
        // looks up for a persisted entity in the hierarchy chain
        boolean loop = true;
        while (loop) {
            // double check if the passed entity is consistent (must be shared to `user`)
            if (entity instanceof SharedEntity) {
                if(sharedEntityIntegrationClient == null)
                    return false;
                sharingUsers = sharedEntityIntegrationClient.fetchSharingUsersIds(entity.getResourceName(), entity.getId());
                if (sharingUsers.stream().noneMatch(id -> id == user.getId())) {
                    return false;
                } else
                    loop = false;
            } else if (entity instanceof OwnedChildResource) {
                OwnedChildResource child = (OwnedChildResource) entity;
                if (child.getParent() != null)
                    entity = child.getParent();
                else
                    loop = false;
            } else
                loop = false;
        }
        if (entity.getId() != 0) {
            // load the persisted entity
            BaseEntitySystemApi service = componentRegistry.findEntitySystemApi(((BaseEntity) resource).getResourceName());
            if (service != null) {
                BaseEntity persistedEntity = service.find(entity.getId());
                // verify the owner
                if (persistedEntity instanceof SharedEntity) {
                    if(sharedEntityIntegrationClient == null){
                        sharingUsers = Collections.emptyList();
                    } else {
                        sharingUsers = sharedEntityIntegrationClient.fetchSharingUsersIds(entity.getResourceName(), entity.getId());
                    }
                } else if (persistedEntity instanceof OwnedChildResource) {
                    OwnedChildResource persistedChildEntity = (OwnedChildResource) persistedEntity;
                    if (persistedChildEntity != null && persistedChildEntity.getParent() != null) {
                        // retry with the parent resource
                        return (persistedChildEntity.getParent() == null || checkUserOwnsResource(user, persistedChildEntity.getParent()))
                                && checkUserOwnsResource(user, persistedChildEntity.getParent());
                    } else {
                        // resource is not shared so check can pass
                        return (persistedEntity != null);
                    }
                }
            }
        } else {
            return false;
        }
        return (user != null && sharingUsers.stream().anyMatch(id -> id == user.getId()));
    }

    /**
     * @param role
     * @param resourceClass
     * @param action
     */
    @Override
    public void addPermissionIfNotExists(Role role, Class<? extends Resource> resourceClass, Action action) {
        ResourceAction<?> resourceAction = ActionFactory.createResourceAction(resourceClass, action);
        List<ResourceAction> permissionList = Collections.singletonList(resourceAction);
        permissionIntegrationClient.checkOrCreatePermissions(role.getId(), permissionList);
    }
}