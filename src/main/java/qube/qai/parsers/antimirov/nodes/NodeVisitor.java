package qube.qai.parsers.antimirov.nodes;

/**
 * Created by rainbird on 1/26/17.
 */
public interface NodeVisitor {

    void visit(AlternationNode node);

    void visit(ConcatenationNode node);

    void visit(EmptyNode node);

    void visit(IterationNode node);

    void visit(Node node);

    void visit(NameNode node);

    void visit(NoneNode node);

    void visit(PrimitiveNode node);

}
