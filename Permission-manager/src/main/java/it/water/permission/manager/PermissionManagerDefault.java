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
import it.water.core.api.model.Role;
import it.water.core.api.model.User;
import it.water.core.api.permission.Permission;
import it.water.core.api.permission.PermissionManager;
import it.water.core.api.permission.PermissionManagerComponentProperties;
import it.water.core.api.permission.ProtectedEntity;
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
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@FrameworkComponent(properties = {PermissionManagerComponentProperties.PERMISSION_MANAGER_IMPLEMENTATION_PROP + "=" + PermissionManagerComponentProperties.PERMISSION_MANAGER_DEFAILT_IMPLEMENTATION})
public class PermissionManagerDefault implements PermissionManager {
    private Logger log = LoggerFactory.getLogger(PermissionManagerDefault.class.getName());

    //supporting spring properties bind with bean properties
    @Setter
    @Getter
    private String implementation = PermissionManagerComponentProperties.PERMISSION_MANAGER_DEFAILT_IMPLEMENTATION;
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
        log.debug(
                "invoking checkPermission User {} Entity Resource Name: {}", username, entity);

        if (entity != null && !PermissionManager.isProtectedEntity(entity.getResourceName()))
            return true;

        if (entity != null && !PermissionManager.isProtectedEntity(entity))
            return true;

        if (username == null || entity == null || action == null)
            return false;

        User user = this.userIntegrationClient.fetchUserByUsername(username);
        if (user == null)
            return false;
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
        log.debug("invoking checkPermission User {} Entity Resource Name: {} Action: {} ", username, resourceName, action);
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
        if (username == null || resource == null || action == null)
            return false;

        if (!PermissionManager.isProtectedEntity(resource.getName()))
            return true;

        log.debug(
                "invoking checkPermission User {} Entity Resource Name: {} Action Name: {}  actionId: {}", username, resource.getName(), action.getActionName(), action.getActionId());
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
                hasPermission = user != null && entities[i] != null && checkUserOwnsResource(user, entities[i]);
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
                hasPermission = user != null && entities[i] != null && checkUserOwnsResource(user, entities[i]);
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
        if (user.isAdmin())
            return true;

        Collection<Role> userRoles = roleIntegrationClient.fetchUserRoles(user.getId());

        if (userRoles.isEmpty())
            return false;

        Iterator<? extends Role> it = userRoles.iterator();
        while (it.hasNext()) {
            Role r = it.next();
            Permission permissionSpecific = permissionIntegrationClient.findByRoleAndResourceNameAndResourceId(r.getId(),
                    entity.getResourceName(), entity.getId());
            Permission userPermissionSpecific = permissionIntegrationClient.findByUserAndResourceNameAndResourceId(user.getId(),
                    entity.getResourceName(), entity.getId());
            Permission permissionImpersonation = permissionIntegrationClient.findByRoleAndResourceName(r.getId(),
                    User.class.getName());
            // it initialize the value with the general value based on resource name
            // general permission is : permission based on the role or permission based on user
            boolean hasGeneralPermission = hasGeneralPermission(user, r, entity, action);
            // entity permission is specific if it is found on role or user
            boolean hasEntityPermission = hasEntityPermission(permissionSpecific, action, userPermissionSpecific);
            boolean existPermissionSpecificToEntity = permissionIntegrationClient.permissionSpecificToEntityExists(entity.getResourceName(), entity.getId());
            boolean userActionsAreRegistered = actionsManager.getActions().get(User.class.getName()) != null;
            Action impersonateAction = (userActionsAreRegistered) ? actionsManager.getActions().get(User.class.getName()).getAction(UserActions.IMPERSONATE) : null;
            boolean userOwnsResource = checkUserOwnsResource(user, entity);
            boolean userSharesResource = checkUserSharesResource(user, entity);
            boolean hasImpersonationPermission = impersonateAction != null && permissionImpersonation != null && hasPermission(
                    permissionImpersonation.getActionIds(), impersonateAction.getActionId());
            return calculatePermission(permissionSpecific, userPermissionSpecific, hasEntityPermission, hasGeneralPermission, userOwnsResource, userSharesResource, existPermissionSpecificToEntity) || hasImpersonationPermission;
        }
        return false;
    }

    private boolean calculatePermission(Permission permissionSpecific, Permission userPermissionSpecific, boolean hasEntityPermission, boolean hasGeneralPermission, boolean userOwnsResource, boolean userSharesResource, boolean existPermissionSpecificToEntity) {
        // The value is true only if the entity permission exists and contains the
        // actionId, or if
        // the entity permission doesn't exists then the rule follow the
        // generalPermission
        // AND if the resource is an owned resource is accessed by the right user or the
        // accessing user has the impersonation permission
        return (
                ((permissionSpecific != null || userPermissionSpecific != null) && hasEntityPermission) ||
                        (permissionSpecific == null && userPermissionSpecific == null && hasGeneralPermission)) && (userOwnsResource || ((userSharesResource && !existPermissionSpecificToEntity && hasGeneralPermission) || (userSharesResource && (permissionSpecific != null || userPermissionSpecific != null) && hasEntityPermission)));
    }

    private boolean hasGeneralPermission(User user, Role r, ProtectedEntity entity, Action action) {
        Permission permission = permissionIntegrationClient.findByRoleAndResourceName(r.getId(), entity.getResourceName());
        Permission userPermission = permissionIntegrationClient.findByUserAndResourceName(user.getId(), entity.getResourceName());
        return (permission != null
                && hasPermission(permission.getActionIds(), action.getActionId())) || (userPermission != null && hasPermission(userPermission.getActionIds(), action.getActionId()));
    }

    private boolean hasEntityPermission(Permission permissionSpecific, Action action, Permission userPermissionSpecific) {
        return (permissionSpecific != null
                && hasPermission(permissionSpecific.getActionIds(), action.getActionId())) || (userPermissionSpecific != null
                && hasPermission(userPermissionSpecific.getActionIds(), action.getActionId()));
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
                hasPermission);
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
            boolean mustBreak = false;
            if (entity instanceof OwnedResource ownedResource) {
                resourceOwner = ownedResource.getUserOwner();
                if (resourceOwnerDoesNotMatch(resourceOwner, user, userSharesResource)) {
                    return false;
                }
                mustBreak = true;
            } else if (entity instanceof OwnedChildResource child) {
                if (child.getParent() != null)
                    entity = child.getParent();
                else {
                    mustBreak = true;
                }
            } else {
                mustBreak = true;
            }

            if (mustBreak)
                break;
        }

        return doCheckUserOwnsResource(user, resourceOwner, resource, entity, userSharesResource);
    }

    private boolean resourceOwnerDoesNotMatch(User resourceOwner, User user, boolean userSharesResource) {
        return !userSharesResource && resourceOwner != null && resourceOwner.getId() != 0
                && user.getId() != resourceOwner.getId();
    }

    private boolean doCheckUserOwnsResource(User user, User resourceOwner, Object resource, BaseEntity entity, boolean userSharesResource) {
        if (entity.getId() == 0)
            return true;
        // load the persisted entity
        BaseEntitySystemApi<?> service = componentRegistry.findEntitySystemApi(((BaseEntity) resource).getResourceName());
        if (service != null) {
            BaseEntity persistedEntity = service.find(entity.getId());
            // verify the owner
            if (persistedEntity instanceof OwnedResource ownedResource) {
                resourceOwner = ownedResource.getUserOwner();
            } else if (persistedEntity instanceof OwnedChildResource persistedChildEntity) {
                if (persistedChildEntity.getParent() != null) {
                    // retry with the parent resource
                    return (persistedChildEntity.getParent() == null || checkUserOwnsResource(user, persistedChildEntity.getParent()))
                            && checkUserOwnsResource(user, persistedChildEntity.getParent());
                }
            } else {
                // resource is not owned so check can pass
                return (persistedEntity != null);
            }
        }
        return (resourceOwner != null && (user.getId() == resourceOwner.getId() || userSharesResource));
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

        if (sharedEntityIntegrationClient == null)
            return false;

        BaseEntity entity = (BaseEntity) resource;
        Collection<Long> sharingUsers = new ArrayList<>();
        // looks up for a persisted entity in the hierarchy chain
        boolean loop = true;
        while (loop) {
            // double check if the passed entity is consistent (must be shared to `user`)
            if (entity instanceof SharedEntity) {
                sharingUsers = sharedEntityIntegrationClient.fetchSharingUsersIds(entity.getResourceName(), entity.getId());
                if (sharingUsers.stream().noneMatch(id -> id == user.getId())) {
                    return false;
                } else
                    loop = false;
            } else if (entity instanceof OwnedChildResource child) {
                if (child.getParent() != null)
                    entity = child.getParent();
                else
                    loop = false;
            } else
                loop = false;
        }

        return doCheckUserSharesResource(sharingUsers, user, resource, entity);
    }

    private boolean doCheckUserSharesResource(Collection<Long> sharingUsers, User user, Object resource, BaseEntity entity) {
        if (entity.getId() == 0)
            return false;
        // load the persisted entity
        BaseEntitySystemApi<?> service = componentRegistry.findEntitySystemApi(((BaseEntity) resource).getResourceName());
        if (service != null) {
            BaseEntity persistedEntity = service.find(entity.getId());
            // verify the owner
            if (persistedEntity instanceof SharedEntity) {
                if (sharedEntityIntegrationClient == null) {
                    sharingUsers = Collections.emptyList();
                } else {
                    sharingUsers = sharedEntityIntegrationClient.fetchSharingUsersIds(entity.getResourceName(), entity.getId());
                }
            } else if (persistedEntity instanceof OwnedChildResource persistedChildEntity) {
                if (persistedChildEntity.getParent() != null) {
                    // retry with the parent resource
                    return (persistedChildEntity.getParent() == null || checkUserOwnsResource(user, persistedChildEntity.getParent()))
                            && checkUserOwnsResource(user, persistedChildEntity.getParent());
                }
                // resource is not shared so check can pass
                return true;
            }
        }
        return (sharingUsers.stream().anyMatch(id -> id == user.getId()));
    }

    /**
     * @param role
     * @param resourceClass
     * @param action
     */
    @Override
    public void addPermissionIfNotExists(Role role, Class<? extends Resource> resourceClass, Action action) {
        ResourceAction<?> resourceAction = ActionFactory.createResourceAction(resourceClass, action);
        List<ResourceAction<?>> permissionList = Collections.singletonList(resourceAction);
        permissionIntegrationClient.checkOrCreatePermissions(role.getId(), permissionList);
    }
}
