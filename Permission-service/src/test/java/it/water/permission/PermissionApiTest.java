package it.water.permission;

import it.water.core.api.action.Action;
import it.water.core.api.action.ActionsManager;
import it.water.core.api.action.ResourceAction;
import it.water.core.api.bundle.Runtime;
import it.water.core.api.model.PaginableResult;
import it.water.core.api.model.Resource;
import it.water.core.api.model.Role;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.repository.query.Query;
import it.water.core.api.role.RoleManager;
import it.water.core.api.service.Service;
import it.water.core.api.service.integration.PermissionIntegrationClient;
import it.water.core.api.user.UserManager;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.model.exceptions.ValidationException;
import it.water.core.model.exceptions.WaterRuntimeException;
import it.water.core.permission.action.ActionFactory;
import it.water.core.permission.action.CrudActions;
import it.water.core.permission.action.DefaultActionList;
import it.water.core.permission.exceptions.UnauthorizedException;
import it.water.core.testing.utils.bundle.TestRuntimeInitializer;
import it.water.core.testing.utils.junit.WaterTestExtension;
import it.water.core.testing.utils.runtime.TestRuntimeUtils;
import it.water.permission.api.PermissionApi;
import it.water.permission.api.PermissionRepository;
import it.water.permission.api.PermissionSystemApi;
import it.water.permission.model.WaterPermission;
import it.water.repository.entity.model.exceptions.DuplicateEntityException;
import lombok.Setter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generated with Water Generator.
 * Test class for Permission Services.
 * <p>
 * Please use PermissionRestTestApi for ensuring format of the json response
 */
@ExtendWith(WaterTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PermissionApiTest implements Service {

    @Inject
    @Setter
    private ComponentRegistry componentRegistry;

    @Inject
    @Setter
    private PermissionApi permissionApi;

    @Inject
    @Setter
    private PermissionIntegrationClient permissionIntegrationClient;

    @Inject
    @Setter
    private PermissionSystemApi permissionSystemApi;

    @Inject
    @Setter
    private Runtime runtime;

    @Inject
    @Setter
    private PermissionRepository permissionRepository;

    @Inject
    @Setter
    private ActionsManager actionsManager;

    @Inject
    @Setter
    //test role manager
    private RoleManager roleManager;

    @Inject
    @Setter
    //test role manager
    private UserManager userManager;

    //admin user
    private it.water.core.api.model.User adminUser;
    private it.water.core.api.model.User permissionManagerUser;
    private it.water.core.api.model.User permissionViewerUser;
    private it.water.core.api.model.User permissionEditorUser;

    private Role permissionManagerRole;
    private Role permissionViewerRole;
    private Role permissionEditorRole;

    @BeforeAll
    void beforeAll() {
        //getting user
        permissionManagerRole = roleManager.getRole(WaterPermission.DEFAULT_MANAGER_ROLE);
        permissionViewerRole = roleManager.getRole(WaterPermission.DEFAULT_VIEWER_ROLE);
        permissionEditorRole = roleManager.getRole(WaterPermission.DEFAULT_EDITOR_ROLE);
        Assertions.assertNotNull(permissionManagerRole);
        Assertions.assertNotNull(permissionViewerRole);
        Assertions.assertNotNull(permissionEditorRole);
        //impersonate admin so we can test the happy path
        adminUser = userManager.findUser("admin");
        permissionManagerUser = userManager.addUser("manager", "name", "lastname", "manager@a.com", "Password!_", "salt", false);
        permissionViewerUser = userManager.addUser("viewer", "name", "lastname", "viewer@a.com", "Password!_", "salt", false);
        permissionEditorUser = userManager.addUser("editor", "name", "lastname", "editor@a.com", "Password!_", "salt", false);
        //starting with admin permissions
        roleManager.addRole(permissionManagerUser.getId(), permissionManagerRole);
        roleManager.addRole(permissionViewerUser.getId(), permissionViewerRole);
        roleManager.addRole(permissionEditorUser.getId(), permissionEditorRole);
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
    }

    /**
     * Testing basic injection of basic component for permission entity.
     */
    @Test
    @Order(1)
    void componentsInsantiatedCorrectly() {
        this.permissionApi = this.componentRegistry.findComponent(PermissionApi.class, null);
        Assertions.assertNotNull(this.permissionApi);
        Assertions.assertNotNull(this.componentRegistry.findComponent(PermissionSystemApi.class, null));
        this.permissionRepository = this.componentRegistry.findComponent(PermissionRepository.class, null);
        Assertions.assertNotNull(this.permissionRepository);
    }

    /**
     * Testing simple save and version increment
     */
    @Test
    @Order(2)
    void saveOk() {
        WaterPermission entity = createPermission(0, 0, 0);
        entity = this.permissionApi.save(entity);
        Assertions.assertEquals(1, entity.getEntityVersion());
        Assertions.assertTrue(entity.getId() > 0);
        Assertions.assertEquals("exampleName0", entity.getName());
    }

    /**
     * Testing update logic, basic test
     */
    @Test
    @Order(3)
    void updateShouldWork() {
        Query q = this.permissionRepository.getQueryBuilderInstance().createQueryFilter("name=exampleName0");
        WaterPermission entity = this.permissionApi.find(q);
        Assertions.assertNotNull(entity);
        entity.setActionIds(1);
        entity = this.permissionApi.update(entity);
        Assertions.assertEquals(1, entity.getActionIds());
        Assertions.assertEquals(2, entity.getEntityVersion());
    }

    /**
     * Testing update logic, basic test
     */
    @Test
    @Order(4)
    void updateShouldFailWithWrongVersion() {
        Query q = this.permissionRepository.getQueryBuilderInstance().createQueryFilter("name=exampleName0");
        WaterPermission errorEntity = this.permissionApi.find(q);
        Assertions.assertEquals(1, errorEntity.getActionIds());
        Assertions.assertEquals(2, errorEntity.getEntityVersion());
        errorEntity.setEntityVersion(1);
        Assertions.assertThrows(WaterRuntimeException.class, () -> this.permissionApi.update(errorEntity));
    }

    /**
     * Testing finding all entries with no pagination
     */
    @Test
    @Order(5)
    void findAllShouldWork() {
        PaginableResult<WaterPermission> all = this.permissionApi.findAll(null, -1, -1, null);
        Assertions.assertEquals(1, all.getResults().size());
    }

    /**
     * Testing finding all entries with settings related to pagination.
     * Searching with 5 items per page starting from page 1.
     */
    @Test
    @Order(6)
    void findAllPaginatedShouldWork() {
        for (int i = 2; i < 11; i++) {
            WaterPermission u = createPermission(i, 0, 0);
            this.permissionApi.save(u);
        }
        PaginableResult<WaterPermission> paginated = this.permissionApi.findAll(null, 7, 1, null);
        Assertions.assertEquals(7, paginated.getResults().size());
        Assertions.assertEquals(1, paginated.getCurrentPage());
        Assertions.assertEquals(2, paginated.getNextPage());
        paginated = this.permissionApi.findAll(null, 7, 2, null);
        Assertions.assertEquals(3, paginated.getResults().size());
        Assertions.assertEquals(2, paginated.getCurrentPage());
        Assertions.assertEquals(1, paginated.getNextPage());
    }

    /**
     * Testing removing all entities using findAll method.
     */
    @Test
    @Order(7)
    void removeAllShouldWork() {
        PaginableResult<WaterPermission> paginated = this.permissionApi.findAll(null, -1, -1, null);
        paginated.getResults().forEach(entity -> {
            this.permissionApi.remove(entity.getId());
        });
        Assertions.assertEquals(0, this.permissionApi.countAll(null));
    }

    /**
     * Testing failure on duplicated entity
     */
    @Test
    @Order(8)
    void saveShouldFailOnDuplicatedEntity() {
        WaterPermission entity = createPermission(1, 0, 0);
        this.permissionApi.save(entity);
        WaterPermission duplicated = this.createPermission(1, 0, 0);
        //cannot insert new entity wich breaks unique constraint
        Assertions.assertThrows(DuplicateEntityException.class, () -> this.permissionApi.save(duplicated));
    }

    /**
     * Testing failure on validation failure for example code injection
     */
    @Test
    @Order(9)
    void updateShouldFailOnValidationFailure() {
        WaterPermission newEntity = new WaterPermission("<script>function(){alert('ciao')!}</script>", 2, "entityResourceName", 0l, 0, 0);
        Assertions.assertThrows(ValidationException.class, () -> this.permissionApi.save(newEntity));
    }

    /**
     * Testing Crud operations on manager role
     */
    @Order(10)
    @Test
    void managerCanDoEverything() {
        TestRuntimeInitializer.getInstance().impersonate(permissionManagerUser, runtime);
        final WaterPermission entity = createPermission(101, 0, 0);
        WaterPermission savedEntity = Assertions.assertDoesNotThrow(() -> this.permissionApi.save(entity));
        savedEntity.setActionIds(8);
        Assertions.assertDoesNotThrow(() -> this.permissionApi.update(entity));
        Assertions.assertDoesNotThrow(() -> this.permissionApi.find(savedEntity.getId()));
        Assertions.assertDoesNotThrow(() -> this.permissionApi.remove(savedEntity.getId()));

    }

    @Order(11)
    @Test
    void viewerCannotSaveOrUpdateOrRemove() {
        TestRuntimeInitializer.getInstance().impersonate(permissionViewerUser, runtime);
        final WaterPermission entity = createPermission(201, 0, 0);
        Assertions.assertThrows(UnauthorizedException.class, () -> this.permissionApi.save(entity));
        //viewer can search
        WaterPermission found = Assertions.assertDoesNotThrow(() -> this.permissionApi.findAll(null, -1, -1, null).getResults().stream().findFirst()).get();
        Assertions.assertDoesNotThrow(() -> this.permissionApi.find(found.getId()));
        //viewer cannot update or remove
        found.setActionIds(8);
        long foundId = found.getId();
        Assertions.assertThrows(UnauthorizedException.class, () -> this.permissionApi.update(entity));
        Assertions.assertThrows(UnauthorizedException.class, () -> this.permissionApi.remove(foundId));
    }

    @Order(12)
    @Test
    void editorCannotRemove() {
        TestRuntimeInitializer.getInstance().impersonate(permissionEditorUser, runtime);
        final WaterPermission entity = createPermission(301, 0, 0);
        WaterPermission savedEntity = Assertions.assertDoesNotThrow(() -> this.permissionApi.save(entity));
        savedEntity.setActionIds(8);
        Assertions.assertDoesNotThrow(() -> this.permissionApi.update(entity));
        Assertions.assertDoesNotThrow(() -> this.permissionApi.find(savedEntity.getId()));
        long savedId = savedEntity.getId();
        Assertions.assertThrows(UnauthorizedException.class, () -> this.permissionApi.remove(savedId));
    }

    @Order(13)
    @Test
    void testPermissionSystemApi() {
        WaterPermission waterPermission = createPermission(10001, 0, adminUser.getId(), 0);
        WaterPermission waterRolePermission = createPermission(10001, permissionViewerRole.getId(), 0, 0);
        TestResource t = new TestResource(waterPermission.getEntityResourceName());
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
        Assertions.assertNull(permissionIntegrationClient.findByUserAndResourceName(adminUser.getId(), waterPermission.getEntityResourceName()));
        Assertions.assertNull(permissionIntegrationClient.findByUserAndResourceNameAndResourceId(adminUser.getId(), waterPermission.getEntityResourceName(), 0));
        Assertions.assertNull(permissionIntegrationClient.findByUserAndResource(adminUser.getId(), t));

        Assertions.assertEquals(0, permissionIntegrationClient.findByRole(permissionViewerRole.getId()).size());
        Assertions.assertNull(permissionSystemApi.findByRoleAndResource(permissionViewerRole.getId(), t));
        Assertions.assertNull(permissionIntegrationClient.findByRoleAndResourceName(permissionViewerRole.getId(), waterRolePermission.getEntityResourceName()));
        Assertions.assertNull(permissionIntegrationClient.findByRoleAndResourceNameAndResourceId(permissionViewerRole.getId(), waterRolePermission.getEntityResourceName(), 0));
        permissionSystemApi.save(waterPermission);
        permissionSystemApi.save(waterRolePermission);
        Assertions.assertDoesNotThrow(() -> permissionIntegrationClient.findByUserAndResourceName(adminUser.getId(), waterPermission.getEntityResourceName()));
        Assertions.assertDoesNotThrow(() -> permissionIntegrationClient.findByUserAndResourceNameAndResourceId(adminUser.getId(), waterPermission.getEntityResourceName(), 0));
        Assertions.assertDoesNotThrow(() -> permissionIntegrationClient.findByUserAndResource(adminUser.getId(), t));

        Assertions.assertEquals(1, permissionIntegrationClient.findByRole(permissionViewerRole.getId()).size());
        Assertions.assertNotNull(permissionSystemApi.findByRoleAndResource(permissionViewerRole.getId(), t));
        Assertions.assertNotNull(permissionIntegrationClient.findByRoleAndResourceName(permissionViewerRole.getId(), waterRolePermission.getEntityResourceName()));
        Assertions.assertNotNull(permissionIntegrationClient.findByRoleAndResourceNameAndResourceId(permissionViewerRole.getId(), waterRolePermission.getEntityResourceName(), 0));

        Map<String, List<Long>> pks = new HashMap<>();
        pks.put(waterPermission.getEntityResourceName(), new ArrayList<>());
        pks.get(waterPermission.getEntityResourceName()).add(waterPermission.getResourceId());
        runtime.fillSecurityContext(null);
        Assertions.assertThrows(UnauthorizedException.class, () -> permissionApi.entityPermissionMap(pks));
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
        //In this test we use in memory test permission manager which does not support this operations
        //in future this test may fail if the test permission manager is modified
        Assertions.assertThrows(UnsupportedOperationException.class, () -> permissionApi.entityPermissionMap(pks));

        long permissionViewerId = permissionViewerRole.getId();
        WaterPermission resourcePermission = createPermission(10002,TestResource.class,permissionViewerId,0,0);
        permissionSystemApi.save(resourcePermission);

        DefaultActionList<?> actionList = ActionFactory.createBaseCrudActionList(TestResource.class);
        ResourceAction<?> action = actionList.getList().get(0);
        List<ResourceAction<?>> createActions = new ArrayList<>();
        createActions.add(action);
        Assertions.assertDoesNotThrow(() -> permissionIntegrationClient.checkOrCreatePermissionsSpecificToEntity(permissionViewerId, 0, createActions));
    }

    private WaterPermission createPermission(int seed,Class<?> resourceClass, long roleId, long userId,long resourceId) {
        WaterPermission entity = new WaterPermission("exampleName" + seed, 2, resourceClass.getName(), resourceId, roleId, userId);
        return entity;
    }

    private WaterPermission createPermission(int seed, long roleId, long userId) {
        WaterPermission entity = new WaterPermission("exampleName" + seed, 2, "entityResourceName" + seed, (long) seed, roleId, userId);
        return entity;
    }

    private WaterPermission createPermission(int seed, long roleId, long userId, long resourceId) {
        WaterPermission entity = new WaterPermission("exampleName" + seed, 2, "entityResourceName" + seed, resourceId, roleId, userId);
        return entity;
    }
}
