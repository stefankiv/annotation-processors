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
        tree.body.stats = wrapWithTryCatch(tree.getBody());
    }

    /**
     * Wraps original method body with try-catch.
     *
     * <code>
     *     try {
     *         original method body
     *     } catch (Throwable t) {
     *         throw SneakyUtil.sneakyThrow(t);
     *     }
     * </code>
     */
    private List<JCTree.JCStatement> wrapWithTryCatch(final JCTree.JCBlock originalMethodBlock) {
        // sneakyThrowMethod:
        //  SneakyUtil.sneakyThrow
        final JCTree.JCFieldAccess sneakyThrowMethod = make.Select(
                make.Ident(names.fromString("SneakyUtil")),
                names.fromString("sneakyThrow")
        );

        // catchers:
        //  catch(Throwable t) {
        //      throw SneakyUtil.sneakyThrow(t);
        //  }
        final List<JCTree.JCCatch> catchers = List.of(                                              // list of catch statements
                make.Catch(                                                                         // catch parameter
                        make.VarDef(                                                                // exception variable declaration
                                make.Modifiers(0),                                                  // no modifiers
                                names.fromString("t"),                                              // variable name
                                make.Ident(names.fromString("Throwable")),                          // variable type
                                null                                                                // variable init expression
                        ),
                        make.Block(                                                                 // catch block
                                0,                                                                  // no flags (e.g. static)
                                List.of(                                                            // list of block expressions
                                        make.Throw(                                                 // throw statement
                                                make.Apply(                                         // method invocation
                                                        List.nil(),                                 // method type arguments
                                                        sneakyThrowMethod,                          // method expression
                                                        List.of(make.Ident(names.fromString("t")))  // method arguments
                                                )
                                        )
                                )
                        )
                )
        );

        //  try {
        //      +originalMethodBlock+
        //  } +catchers+
        return List.of(
                make.Try(originalMethodBlock, catchers, null)
        );
    }
}
