package it.water.permission.manager;

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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

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
    //test role manager
    private TestUserManager testUserManager;

    @Inject
    @Setter
    private ActionsManager actionsManager;

    //admin user
    private it.water.core.api.model.User adminUser;
    private User viewerUser;
    private User managerUser;
    private User editoriUser;
    private Map<String, ActionList<Resource>> actionsMap;
    @BeforeAll
    public void beforeAll() {
        //impersonate admin so we can test the happy path
        adminUser = testUserManager.findUser("admin");
        actionsManager.registerActions(TestResource.class);
        actionsMap = actionsManager.getActions();
        Assertions.assertTrue(actionsMap.containsKey(TestResource.class.getName()));
        Assertions.assertEquals(5,actionsMap.get(TestResource.class.getName()).getList().size());
    }
    /**
     * Testing basic injection of basic component for permission entity.
     */
    @Test
    @Order(1)
    public void testPermissionManagerIsOverriden() {
        Assertions.assertFalse(permissionManager instanceof TestPermissionManager);
    }
    
    private WaterPermission createPermission(int seed,long roleId,long userId){
        WaterPermission entity = new WaterPermission("exampleName"+seed,2,"entityResourceName"+seed,(long)seed,roleId,userId);
        return entity;
    }
}
