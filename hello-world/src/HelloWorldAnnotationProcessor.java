import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.Set;

public class HelloWorldAnnotationProcessor extends AbstractProcessor {

    private Trees trees;
    private TreeMaker make;
    private Names names;
    private Symtab symbols;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
        final Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        make = TreeMaker.instance(context);
        names = Names.instance(context);
        symbols = Symtab.instance(context);
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "@HelloWorld processing finished.");
        } else {
            for (final Element element : roundEnv.getElementsAnnotatedWith(HelloWorld.class)) {
                if (element.getKind() == ElementKind.CLASS) {
                    final JCTree tree = (JCTree) trees.getTree(element);
                    final HelloWorldVisitor visitor = new HelloWorldVisitor(make, names, symbols);
                    tree.accept(visitor);
                }
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(HelloWorld.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
