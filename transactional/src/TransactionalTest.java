import java.util.Optional;

// compile with TransactionalAnnotationProcessor or transactional.jar in classpath.

public class TransactionalTest {
    @Transactional
    public Optional<String> findNameById(final Long id) {
        return findById(id).map(user -> user.name);
    }

    @Transactional
    private Optional<User> findById(final Long id) {
        return Optional.empty();
    }

    static class User {
        String name;
    }
}
