import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
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

public class SneakyAnnotationProcessor extends AbstractProcessor {

    private Trees trees;
    private TreeMaker make;
    private Names names;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
        final Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        make = TreeMaker.instance(context);
        names = Names.instance(context);
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "@SneakyThrows processing finished.");
        } else {
            for (final Element element : roundEnv.getElementsAnnotatedWith(Sneaky.class)) {
                if (element.getKind() == ElementKind.METHOD) {
                    final JCTree tree = (JCTree) trees.getTree(element);
                    final SneakyVisitor visitor = new SneakyVisitor(make, names);
                    tree.accept(visitor);
                }
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Sneaky.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    private static class SneakyVisitor extends TreeTranslator {
        private final TreeMaker make;
        private final Names names;

        public SneakyVisitor(final TreeMaker make, final Names names) {
            this.make = make;
            this.names = names;
        }

        @Override
        public void visitMethodDef(final JCTree.JCMethodDecl tree) {
            super.visitMethodDef(tree);

            final List<JCTree.JCStatement> originalMethodBody = tree.getBody().getStatements();

            final JCTree.JCBlock tryBlock = make.Block(0, originalMethodBody);

            final List<JCTree.JCCatch> catchers = List.of(
                    make.Catch(
                            make.VarDef(
                                    make.Modifiers(0),
                                    names.fromString("t"),
                                    make.Ident(names.fromString("Throwable")),
                                    null
                            ),
                            make.Block(
                                    0,
                                    List.of(
                                            make.Throw(
                                                    make.Apply(
                                                            List.nil(),
                                                            make.Select(
                                                                    make.Ident(names.fromString("SneakyUtil")),
                                                                    names.fromString("sneakyThrow")
                                                            ),
                                                            List.of(
                                                                    make.Ident(names.fromString("t"))
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );

            tree.body.stats = List.of(
                    make.Try(tryBlock, catchers, null)
            );
        }
    }
}
