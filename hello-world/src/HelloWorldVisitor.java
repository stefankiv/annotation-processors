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

    private JCTree createHelloWorldMethod() {
        final JCTree.JCFieldAccess println = make.Select(
                make.Select(
                        make.Ident(names.fromString("System")),
                        names.fromString("out")
                ),
                names.fromString("println")
        );

        final JCTree.JCBlock methodBody = make.Block(0,
                List.of(
                        make.Exec(
                                make.Apply(
                                        List.nil(),
                                        println,
                                        List.of(make.Literal("Hello, World!"))
                                )
                        )
                )
        );

        final List<JCTree.JCVariableDecl> params = List.of(
                make.VarDef(
                        make.Modifiers(Flags.FINAL | Flags.PARAMETER),
                        names.fromString("args"),
                        make.Type(new Type.ArrayType(symbols.stringType, symbols.arrayClass)),
                        null
                )
        );
        return make.MethodDef(
                make.Modifiers(Flags.PUBLIC | Flags.STATIC),
                names.fromString("main"),
                make.Type(symbols.voidType),
                List.nil(),
                params,
                List.nil(),
                methodBody,
                null
        );
    }

}
