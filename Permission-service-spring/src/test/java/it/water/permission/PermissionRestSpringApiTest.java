
package it.water.permission;

import com.intuit.karate.junit5.Karate;
import it.water.core.api.bundle.Runtime;
import it.water.core.api.model.User;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.user.UserManager;
import it.water.core.security.model.principal.UserPrincipal;
import it.water.core.testing.utils.runtime.TestRuntimeUtils;
import it.water.implementation.spring.security.SpringSecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

@SpringBootTest(classes = PermissionApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "water.rest.security.jwt.validate=false",
        "water.testMode=true"
})
public class PermissionRestSpringApiTest {
    @Autowired
    private ComponentRegistry componentRegistry;

    @BeforeEach
    void impersonateAdmin(){
        //jwt token service is disabled, we just inject admin user for bypassing permission system
        //just remove this line if you want test with permission system working
        TestRuntimeUtils.impersonateAdmin(componentRegistry);
    }
    @Karate.Test
    Karate restInterfaceTest() {
        return Karate.run("../Permission-service/src/test/resources/karate");
    }

}
