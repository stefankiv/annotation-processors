# "Sneaky throws" annotation processor
Attempt to reproduce Lombok's `@SneakyThrows` functionality. 

Modifies AST, allowing throwing checked exceptions when method is annotated with `@Sneaky` annotation. 

Compile `SneakyTest.java` with `javac -cp ../sneaky.jar`. Then run with `java -cp ../sneaky.jar SneakyTest`.