import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.Set;

public class TransactionalAnnotationProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "@Transactional processing finished.");
        } else {
            for (final Element element : roundEnv.getElementsAnnotatedWith(Transactional.class)) {
                if (element.getKind() == ElementKind.METHOD) {
                    if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "@Transactional works only with public methods", element);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Transactional.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
