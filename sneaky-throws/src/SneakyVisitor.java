import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

public class SneakyVisitor extends TreeTranslator {
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
