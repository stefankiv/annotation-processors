import java.util.concurrent.TimeUnit;

// compile with SneakyAnnotationProcessor or sneaky.jar in classpath.

public class SneakyTest {
    @Sneaky
    public static void main(String[] args) {
        System.out.println("prepare yourself");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("...");
        TimeUnit.SECONDS.sleep(1);
        throw new Exception("\\ö/");
    }
}
