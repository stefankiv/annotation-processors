import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

public class HelloWorldVisitor extends TreeTranslator {
    private final TreeMaker make;
    private final Names names;
    private final Symtab symbols;

    public HelloWorldVisitor(final TreeMaker make, final Names names, final Symtab symbols) {
        this.make = make;
        this.names = names;
        this.symbols = symbols;
    }

    @Override
    public void visitClassDef(final JCTree.JCClassDecl tree) {
        super.visitClassDef(tree);
        tree.defs = tree.defs.append(createHelloWorldMethod());
    }

    /**
     * Generate:
     * <code>
     *  public static void main(final String[] args) {
     *      System.out.println("Hello, World!");
     *  }
     * </code>
     */
    private JCTree createHelloWorldMethod() {
        // println:
        //  System.out.println
        final JCTree.JCFieldAccess println = make.Select(
                make.Select(
                        make.Ident(names.fromString("System")),
                        names.fromString("out")
                ),
                names.fromString("println")
        );

        // methodBody:
        //  {
        //      System.out.println("Hello, World!");
        //  }
        final JCTree.JCBlock methodBody = make.Block(0,                         // create block
                List.of(                                                        // list of statements, included in the block
                        make.Exec(                                              // execute some expression
                                make.Apply(                                     // create method (or variable accessing) invocation expression
                                        List.nil(),                             // method type arguments
                                        println,                                // method expression
                                        List.of(make.Literal("Hello, World!"))  // method arguments
                                )
                        )
                )
        );

        // params:
        //  [final String[] args]
        final List<JCTree.JCVariableDecl> params = List.of(                                     // list of parameters
                make.VarDef(                                                                    // variable declaration
                        make.Modifiers(Flags.FINAL | Flags.PARAMETER),                          // variable modifiers
                        names.fromString("args"),                                               // variable name
                        make.Type(new Type.ArrayType(symbols.stringType, symbols.arrayClass)),  // variable type
                        null                                                                    // variable init expression
                )
        );

        // method:
        //  public static void main(+params+) +methodBody+
        return make.MethodDef(                                  // create method declaration
                make.Modifiers(Flags.PUBLIC | Flags.STATIC),    // method modifiers
                names.fromString("main"),                       // method name
                make.Type(symbols.voidType),                    // method return type
                List.nil(),                                     // method type arguments
                params,                                         // method arguments
                List.nil(),                                     // method thrown exceptions
                methodBody,                                     // method body
                null                                            // method default value (for annotations)
        );
    }
}
