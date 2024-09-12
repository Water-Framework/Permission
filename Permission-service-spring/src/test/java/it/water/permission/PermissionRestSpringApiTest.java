
package it.water.permission;

import com.intuit.karate.junit5.Karate;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PermissionApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PermissionRestSpringApiTest {

    @Karate.Test
    Karate restInterfaceTest() {
        return Karate.run("../Permission-service/src/test/resources/karate");
    }

}
