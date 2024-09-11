package it.water.permission.manager;

import it.water.core.api.action.Action;
import it.water.core.api.action.ActionList;
import it.water.core.api.action.ActionsManager;
import it.water.core.api.bundle.Runtime;
import it.water.core.api.model.PaginableResult;
import it.water.core.api.model.Resource;
import it.water.core.api.model.User;
import it.water.core.api.permission.PermissionManager;
import it.water.core.api.permission.Role;
import it.water.core.api.permission.RoleManager;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.repository.query.Query;
import it.water.core.api.service.Service;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.model.exceptions.ValidationException;
import it.water.core.model.exceptions.WaterRuntimeException;
import it.water.core.permission.action.ActionFactory;
import it.water.core.permission.action.CrudActions;
import it.water.core.permission.exceptions.UnauthorizedException;
import it.water.core.testing.utils.api.TestPermissionManager;
import it.water.core.testing.utils.api.TestUserManager;
import it.water.core.testing.utils.bundle.TestRuntimeInitializer;
import it.water.core.testing.utils.junit.WaterTestExtension;
import it.water.permission.api.PermissionApi;
import it.water.permission.api.PermissionRepository;
import it.water.permission.api.PermissionSystemApi;
import it.water.permission.model.WaterPermission;
import it.water.repository.entity.model.exceptions.DuplicateEntityException;
import lombok.Setter;
import org.hsqldb.server.Server;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generated with Water Generator.
 * Test class for Permission Services.
 * 
 * Please use PermissionRestTestApi for ensuring format of the json response
 

 */
@ExtendWith(WaterTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PermissionManagerDefaultTest implements Service {
    public static final String VIEWER_USERNAME = "viewer";
    public static final String EDITOR_USERNAME = "editor";
    public static final String MANAGER_USERNAME = "manager";
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
    private Map<String, ActionList<Resource>> actionsMap;

    private TestResource testResource;
    private TestResourceChild testResourceChild;
    private NotProtectedTestResource notProtectedTestResource;
    @BeforeAll
    public void beforeAll() {
        //impersonate admin so we can test the happy path
        adminUser = testUserManager.findUser("admin");
        viewerUser = testUserManager.addUser(VIEWER_USERNAME,VIEWER_USERNAME,VIEWER_USERNAME,"viewer@mail.com",false);
        managerUser = testUserManager.addUser(MANAGER_USERNAME,MANAGER_USERNAME,MANAGER_USERNAME,"manager@mail.com",false);
        editorUser = testUserManager.addUser(EDITOR_USERNAME,EDITOR_USERNAME,EDITOR_USERNAME,"editor@mail.com",false);
        actionsMap = actionsManager.getActions();
        testResource = new TestResource();
        testResource.setUserOwner(viewerUser);
        testResource.setId(1);
        testResourceChild = new TestResourceChild(testResource);
        notProtectedTestResource = new NotProtectedTestResource();
        notProtectedTestResource.setUserOwner(managerUser);
        //forcing a mock of the system api
        TestResourceSystemApi systemApi = componentRegistry.findComponent(TestResourceSystemApi.class,null);
        systemApi.returnEntity(testResource);
        Assertions.assertTrue(actionsMap.containsKey(TestResource.class.getName()));
        Assertions.assertEquals(5,actionsMap.get(TestResource.class.getName()).getList().size());
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
    void testActionsRegisteredCorrectly(){
        Assertions.assertTrue(actionsMap.containsKey(TestResource.class.getName()));
        Assertions.assertEquals(5,actionsMap.get(TestResource.class.getName()).getList().size());
    }

    @Test
    @Order(3)
    void testAssignPermissions(){
        TestRuntimeInitializer.getInstance().impersonate(adminUser,runtime);
        roleManager.addRole(viewerUser.getId(),roleManager.getRole(TestResource.TEST_ROLE_VIEWER));
        roleManager.addRole(managerUser.getId(),roleManager.getRole(TestResource.TEST_ROLE_MANAGER));
        roleManager.addRole(editorUser.getId(),roleManager.getRole(TestResource.TEST_ROLE_EDITOR));
        Assertions.assertTrue(permissionManager.userHasRoles(viewerUser.getUsername(),new String[]{TestResource.TEST_ROLE_VIEWER}));
        Assertions.assertTrue(permissionManager.userHasRoles(managerUser.getUsername(),new String[]{TestResource.TEST_ROLE_MANAGER}));
        Assertions.assertTrue(permissionManager.userHasRoles(editorUser.getUsername(),new String[]{TestResource.TEST_ROLE_EDITOR}));
    }

    @Test
    @Order(4)
    void testUserPermissions(){
        TestRuntimeInitializer.getInstance().impersonate(adminUser,runtime);
        TestResource testResource = new TestResource();
        Assertions.assertTrue(permissionManager.userHasRoles(viewerUser.getUsername(),new String[]{TestResource.TEST_ROLE_VIEWER}));
        ActionList<?> actions = actionsManager.getActions().get(TestResource.class.getName());
        Assertions.assertTrue(permissionManager.checkPermission(viewerUser.getUsername(),TestResource.class, actions.getAction(CrudActions.FIND)));
        Assertions.assertTrue(permissionManager.checkPermission(viewerUser.getUsername(),TestResource.class.getName(), actions.getAction(CrudActions.FIND)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(),TestResource.class.getName(), actions.getAction(CrudActions.REMOVE)));
        Assertions.assertTrue(permissionManager.checkPermission(viewerUser.getUsername(),testResource, actions.getAction(CrudActions.FIND)));
        Assertions.assertFalse(permissionManager.checkPermission(viewerUser.getUsername(),testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertTrue(permissionManager.checkPermission(adminUser.getUsername(),testResource, actions.getAction(CrudActions.REMOVE)));
        Assertions.assertFalse(permissionManager.checkPermission(null,testResource, actions.getAction(CrudActions.REMOVE)));
    }

    @Test
    @Order(5)
    void testCheckOwnership(){
        TestRuntimeInitializer.getInstance().impersonate(adminUser,runtime);
        Assertions.assertTrue(permissionManager.checkUserOwnsResource(viewerUser,testResource));
        Assertions.assertFalse(permissionManager.checkUserOwnsResource(managerUser,testResource));
    }

    @Test
    @Order(6)
    void testCheckPermissionAndOwnership(){
        TestRuntimeInitializer.getInstance().impersonate(adminUser,runtime);
        ActionList<?> actions = actionsManager.getActions().get(TestResource.class.getName());
        Assertions.assertTrue(permissionManager.checkPermissionAndOwnership(VIEWER_USERNAME,testResource.getResourceName(),actions.getAction(CrudActions.FIND),testResourceChild));
        Assertions.assertFalse(permissionManager.checkUserOwnsResource(viewerUser,notProtectedTestResource));
        Assertions.assertTrue(permissionManager.checkPermissionAndOwnership(MANAGER_USERNAME,notProtectedTestResource.getResourceName(),actions.getAction(CrudActions.FIND),null));
        //testing on resource child
        Assertions.assertFalse(permissionManager.checkPermissionAndOwnership(VIEWER_USERNAME,testResourceChild,actions.getAction(CrudActions.REMOVE),null));
        Assertions.assertFalse(permissionManager.checkPermissionAndOwnership(VIEWER_USERNAME,testResource.getResourceName(),actions.getAction(CrudActions.REMOVE),testResourceChild));
        Assertions.assertTrue(permissionManager.checkPermissionAndOwnership(VIEWER_USERNAME,testResource,actions.getAction(CrudActions.FIND),testResource));
    }

    @Test
    @Order(7)
    void testEntityEmptyPermissionMap(){
        Map<String, List<Long>> resourceIds = new HashMap<>();
        resourceIds.put(TestResource.class.getName(),new ArrayList<>());
        resourceIds.get(TestResource.class.getName()).add(1l);
        Assertions.assertNotNull(permissionManager.entityPermissionMap(VIEWER_USERNAME,resourceIds));
    }

    
    private WaterPermission createPermission(int seed,long roleId,long userId){
        WaterPermission entity = new WaterPermission("exampleName"+seed,2,"entityResourceName"+seed,(long)seed,roleId,userId);
        return entity;
    }
}
