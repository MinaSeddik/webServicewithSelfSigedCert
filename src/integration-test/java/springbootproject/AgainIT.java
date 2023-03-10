package springbootproject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@RunWith(SpringRunner.class)
public class AgainIT {


    @Test
    @DisplayName("just xxxxx integration test")
    public void integration_testing() {

        List<String> list = new ArrayList<>();

        boolean result = list.add("something");


        assertTrue(result);
    }

    @Test
    void name() {
        List<String> list = new ArrayList<>();

        boolean result = list.add("something");


        assertTrue(result);

    }
}
