import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.io.IOException;
import static org.antlr.v4.runtime.CharStreams.fromFileName;

public class Test {
    public static void main(String[] args) throws IOException {
        String source = "p2files/illegal-01.dcf";
        CharStream charstream = fromFileName(source);
        DecafLexer lexer = new DecafLexer(charstream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DecafParser parser = new DecafParser(tokens);
        ParseTree tree = parser.program();

//        System.out.println(tree.toStringTree(parser));
        Trees.inspect(tree, parser);
        ParseTreeWalker walker = new ParseTreeWalker();
        DefSymbols def = new DefSymbols();
        walker.walk(def, tree);

//        System.out.println(def.globals.symbols);
    }
}

