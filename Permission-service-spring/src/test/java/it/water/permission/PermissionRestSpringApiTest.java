
package it.water.permission;

import com.intuit.karate.junit5.Karate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = PermissionApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "water.rest.security.jwt.validate=false",
        "water.testMode=true"
})
public class PermissionRestSpringApiTest {

    @Karate.Test
    Karate restInterfaceTest() {
        return Karate.run("../Permission-service/src/test/resources/karate");
    }

}
