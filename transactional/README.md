# Transactional annotation processor
Since well-known Spring annotation `@Transactional` doesn't work on non-public method this processor is used for preventing compilation, if that rule is broken.

Compile `TransactionalTest.java` with `javac -cp ../transactional.jar`.