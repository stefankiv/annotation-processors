import java.util.Optional;

public class TransactionalTest {
    @Transactional
    public Optional<String> findNameById(final Long id) {
        return findById(id).map(user -> user.name);
    }

    @Transactional
    private Optional<User> findById(final Long id) {
        return Optional.empty();
    }

    @Transactional
    private Optional<User> findById1(final Long id) {
        return Optional.empty();
    }

    static class User {
        String name;
    }
}