import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AppTest {

    public AppTest() {}

    @Test
    @DisplayName("Test test")
    public void test() {
        assertEquals("thing", "thing");
    }
}
