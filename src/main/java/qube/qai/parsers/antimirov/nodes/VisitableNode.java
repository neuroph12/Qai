package qube.qai.parsers.antimirov.nodes;

/**
 * Created by rainbird on 1/26/17.
 */
public interface VisitableNode {

    public void accept(NodeVisitor visitor);
}
