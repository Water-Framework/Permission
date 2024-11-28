package it.water.permission.manager;

import it.water.core.api.action.ActionList;
import it.water.core.api.action.ActionsManager;
import it.water.core.api.bundle.Runtime;
import it.water.core.api.model.Resource;
import it.water.core.api.model.Role;
import it.water.core.api.model.User;
import it.water.core.api.permission.PermissionManager;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.role.RoleManager;
import it.water.core.api.service.Service;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.permission.action.CrudActions;
import it.water.core.testing.utils.api.TestPermissionManager;
import it.water.core.testing.utils.api.TestUserManager;
import it.water.core.testing.utils.bundle.TestRuntimeInitializer;
import it.water.core.testing.utils.junit.WaterTestExtension;
import it.water.permission.api.PermissionApi;
import it.water.permission.api.PermissionSystemApi;
import it.water.permission.model.WaterPermission;
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
class PermissionManagerDefaultTest implements Service {

    @Inject
    @Setter
    private ComponentRegistry componentRegistry;

    @Inject
    @Setter
    private Runtime runtime;

    @Inject
    @Setter
    //test role manager
    private RoleManager roleManager;

    @Inject
    @Setter
    private PermissionManager permissionManager;

    @Inject
    @Setter
    private PermissionApi permissionApi;

    @Inject
    @Setter
    private PermissionSystemApi permissionSystemApi;

    @Inject
    @Setter
    //test role manager
    private TestUserManager testUserManager;

    @Inject
    @Setter
    private ActionsManager actionsManager;

    //admin user
    private it.water.core.api.model.User adminUser;
    private User viewerUser;
    private User managerUser;
    private User editorUser;
    private User noPermissionUser;
    private User noRoleUser;
    private Map<String, ActionList<Resource>> actionsMap;

    private TestResource testResource;
    private TestResourceChild testResourceChild;
    private NotProtectedTestResource notProtectedTestResource;
    private NotProtectedTestResource2 notProtectedTestResource2;

    @BeforeAll
    public void beforeAll() {
        //impersonate admin so we can test the happy path
        adminUser = testUserManager.findUser("admin");
        viewerUser = testUserManager.addUser(TestResource.TEST_ROLE_VIEWER, TestResource.TEST_ROLE_VIEWER, TestResource.TEST_ROLE_VIEWER, "viewer@mail.com", "Password1_", "salt", false);
        managerUser = testUserManager.addUser(TestResource.TEST_ROLE_MANAGER, TestResource.TEST_ROLE_MANAGER, TestResource.TEST_ROLE_MANAGER, "manager@mail.com", "Password1_", "salt", false);
        editorUser = testUserManager.addUser(TestResource.TEST_ROLE_EDITOR, TestResource.TEST_ROLE_EDITOR, TestResource.TEST_ROLE_EDITOR, "editor@mail.com", "Password1_", "salt", false);
        noPermissionUser = testUserManager.addUser("noPermissionUser", "noPermissionUser", "noPermissionUser", "no-permission-user@mail.com", "Password1_", "salt", false);
        noRoleUser = testUserManager.addUser("noRoleUser", "noRoleUser", "noRoleUser", "no-role-user@mail.com", "Password1_", "salt", false);
        actionsMap = actionsManager.getActions();
        testResource = new TestResource();
        testResource.setUserOwner(viewerUser);
        testResource.setId(1);
        testResourceChild = new TestResourceChild(testResource);
        notProtectedTestResource = new NotProtectedTestResource();
        notProtectedTestResource.setUserOwner(managerUser);
        notProtectedTestResource2 = new NotProtectedTestResource2();
        //forcing a mock of the system api
        TestResourceSystemApi systemApi = componentRegistry.findComponent(TestResourceSystemApi.class, null);
        systemApi.returnEntity(testResource);
        Assertions.assertTrue(actionsMap.containsKey(TestResource.class.getName()));
        Assertions.assertEquals(5, actionsMap.get(TestResource.class.getName()).getList().size());
        Role noPermissionRole = roleManager.createIfNotExists("noPermissionRole");
        roleManager.addRole(viewerUser.getId(), roleManager.getRole(TestResource.TEST_ROLE_VIEWER));
        roleManager.addRole(managerUser.getId(), roleManager.getRole(TestResource.TEST_ROLE_MANAGER));
        roleManager.addRole(editorUser.getId(), roleManager.getRole(TestResource.TEST_ROLE_EDITOR));
        roleManager.addRole(noPermissionUser.getId(), noPermissionRole);
    }

    /**
     * Testing basic injection of basic component for permission entity.
     */
    @Test
    @Order(1)
    void testPermissionManagerIsOverriden() {
        Assertions.assertFalse(permissionManager instanceof TestPermissionManager);
    }

    @Test
    @Order(2)
    void testActionsRegisteredCorrectly() {
        Assertions.assertTrue(actionsMap.containsKey(TestResource.class.getName()));
        Assertions.assertEquals(5, actionsMap.get(TestResource.class.getName()).getList().size());
    }

    @Test
    @Order(3)
    void testAssignPermissions() {
        TestRuntimeInitializer.getInstance().impersonate(adminUser, runtime);
        Assertions.assertFalse(permissionManager.userHasRoles(null, new String[]{TestResource.TEST_ROLE_VIEWER}));
        Assertions.assertFalse(permissionManager.userHasRoles("", new String[]{TestResource.TEST_ROLE_VIEWER}));
        Assertions.assertTrue(permissionManager.userHasRoles(viewerUser.getUsername(), new String[]{TestResource.TEST_ROLE_VIEWER}));
        Assertions.assertTrue(permissionManager.userHasRoles(managerUser.getUsername(), new String[]{TestResource.TEST_ROLE_MANAGER}));
        Assertions.assertTrue(permissionManager.userHasRoles(editorUser.getUsername(), new String[]{TestResource.TEST_ROLE_EDITOR}));
    }

    @Test
    @Order(4)
    void testUserPermissions() {
        TestRuntimeInitializer.getInstance().impersonate(adminUser, runtime);
        TestResource testResource = new TestResource();
        Assertions.assertTrue(permissionManager.userHasRoles(viewerUser.getUsername(), new String[]{TestResource.TEST_ROLE_VIEWER}));
        ActionList<?> actions = actionsManager.getActions().get(TestResource.class.getName());
        Assertions.assertTrue(permissionManager.checkPermission(viewerUser.getUsername(), TestResource.class, actions.getAction(CrudActions.FIND)));
        Assertions.assertTrue(permissionManager.checkPermission(viewerUser.getUsername(), NotProtectedTestResource.class, actions.getAction(CrudActions.FIND)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(), (Class)null, actions.getAction(CrudActions.FIND)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(), TestResource.class, null));
        Assertions.assertTrue(permissionManager.checkPermission(viewerUser.getUsername(), TestResource.class.getName(), actions.getAction(CrudActions.FIND)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(), (String)null, actions.getAction(CrudActions.FIND)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(), TestResource.class.getName(), actions.getAction(CrudActions.REMOVE)));
        Assertions.assertTrue(permissionManager.checkPermission(viewerUser.getUsername(), testResource, actions.getAction(CrudActions.FIND)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(), (Resource)null, actions.getAction(CrudActions.FIND)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(), testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertTrue(permissionManager.checkPermission(adminUser.getUsername(), testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermission(null, testResource, actions.getAction(CrudActions.REMOVE)));

        Assertions.assertFalse(permissionManager.checkPermission(noPermissionUser.getUsername(), testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermission("notExistingUser", (Resource)null, null));
        Assertions.assertFalse(permissionManager.checkPermission(null, (Resource)null, null));
        Assertions.assertFalse(permissionManager.checkPermission(null, (String)null, null));
        Assertions.assertFalse(permissionManager.checkPermission(null, (Class)null, null));
        Assertions.assertFalse(permissionManager.checkPermission(noRoleUser.getUsername(), testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermission(noPermissionUser.getUsername(), testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermission("notExistingUser", testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermission(noRoleUser.getUsername(), testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermission(noPermissionUser.getUsername(), testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermissionAndOwnership(adminUser.getUsername(), testResource, null));
        Assertions.assertFalse(permissionManager.checkPermissionAndOwnership("notExistingUser", testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermissionAndOwnership(noPermissionUser.getUsername(), testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermissionAndOwnership(noRoleUser.getUsername(), testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertTrue(permissionManager.checkPermissionAndOwnership(adminUser.getUsername(), testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertTrue(permissionManager.checkPermissionAndOwnership(adminUser.getUsername(), notProtectedTestResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(), notProtectedTestResource2.getResourceName(), actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(), testResource, null));
        Assertions.assertTrue(permissionManager.checkPermission(viewerUser.getUsername(), notProtectedTestResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertTrue(permissionManager.checkPermission(viewerUser.getUsername(), notProtectedTestResource2, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(), (Resource) null, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertTrue(permissionManager.checkPermission(viewerUser.getUsername(), notProtectedTestResource.getResourceName(), actions.getAction(CrudActions.REMOVE)));
    }

    @Test
    @Order(5)
    void testCheckOwnership() {
        TestRuntimeInitializer.getInstance().impersonate(adminUser, runtime);
        Assertions.assertTrue(permissionManager.checkUserOwnsResource(viewerUser, testResource));
        Assertions.assertTrue(permissionManager.checkUserOwnsResource(adminUser, testResource));
        Assertions.assertFalse(permissionManager.checkUserOwnsResource(managerUser, testResource));
    }

    @Test
    @Order(6)
    void testCheckPermissionAndOwnership() {
        TestRuntimeInitializer.getInstance().impersonate(adminUser, runtime);
        ActionList<?> actions = actionsManager.getActions().get(TestResource.class.getName());
        Assertions.assertTrue(permissionManager.checkPermissionAndOwnership(TestResource.TEST_ROLE_VIEWER, testResource.getResourceName(), actions.getAction(CrudActions.FIND), testResourceChild));
        Assertions.assertFalse(permissionManager.checkUserOwnsResource(viewerUser, notProtectedTestResource));
        Assertions.assertTrue(permissionManager.checkPermissionAndOwnership(TestResource.TEST_ROLE_MANAGER, notProtectedTestResource.getResourceName(), actions.getAction(CrudActions.FIND), null));
        //testing on resource child
        Assertions.assertFalse(permissionManager.checkPermissionAndOwnership(TestResource.TEST_ROLE_VIEWER, testResourceChild, actions.getAction(CrudActions.REMOVE), null));
        Assertions.assertFalse(permissionManager.checkPermissionAndOwnership(TestResource.TEST_ROLE_VIEWER, testResource.getResourceName(), actions.getAction(CrudActions.REMOVE), testResourceChild));
        Assertions.assertTrue(permissionManager.checkPermissionAndOwnership(TestResource.TEST_ROLE_VIEWER, testResource, actions.getAction(CrudActions.FIND), testResource));
    }

    @Test
    @Order(7)
    void testEntityEmptyPermissionMap() {
        Map<String, List<Long>> resourceIds = new HashMap<>();
        resourceIds.put(TestResource.class.getName(), new ArrayList<>());
        resourceIds.get(TestResource.class.getName()).add(1l);
        Assertions.assertNotNull(permissionManager.entityPermissionMap(TestResource.TEST_ROLE_VIEWER, resourceIds));
        Assertions.assertNotNull(permissionApi.entityPermissionMap(resourceIds));
    }


    private WaterPermission createPermission(int seed, long roleId, long userId) {
        WaterPermission entity = new WaterPermission("exampleName" + seed, 2, "entityResourceName" + seed, (long) seed, roleId, userId);
        return entity;
    }
}
