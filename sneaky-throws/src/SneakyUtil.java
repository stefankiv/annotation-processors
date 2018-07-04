public class SneakyUtil {
    public static RuntimeException sneakyThrow(Throwable t) {
        sneakyThrow0(t);
        return null;
    }

    public static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }
}
