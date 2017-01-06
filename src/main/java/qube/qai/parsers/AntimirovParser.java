package qube.qai.parsers;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.misc.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.parsers.antimirov.IncompleteTypeException;
import qube.qai.parsers.antimirov.nodes.*;

import java.util.Iterator;
import java.util.List;

/**
 * Created by rainbird on 9/27/16.
 */
public class AntimirovParser {

    private static Logger logger = LoggerFactory.getLogger("AntimirovParser");

    protected BaseNode currentNode;

    public AntimirovParser() {
    }

    public BaseNode parseExpression(String expressionString) {
        return null;
    }

    public Parser<BaseNode> name() {
        return Scanners.IDENTIFIER
                .map(new org.codehaus.jparsec.functors.Map<String, BaseNode>() {
            @Override
            public BaseNode map(String s) {
                Name name = new Name(s);
                BaseNode node = null;
                if (Name.BOOLEAN.equals(s)
                        || "int".equals(s)
                        || Name.INTEGER.equals(s)
                        || Name.DECIMAL.equals(s)
                        || Name.DOUBLE.equals(s)
                        || Name.STRING.equals(s)) {
                    node = new PrimitiveNode(name);
                    if (currentNode != null)  {
                        currentNode.setFirstChild(node);
                    }
                } else if ("e".equals(s)) {
                    node = new EmptyNode();
                    currentNode = node;
                } else {
                    try {
                        node = new Node(name);
                        currentNode = node;
                    } catch (IncompleteTypeException e) {
                        logger.error("AntimirovParser.name() threw IncompleteTypeException:", e);
                    }
                }

                return node;
            }
        });
    }

    public Parser<BaseNode> typedName() {
        return name().followedBy(type());
    }

    public Parser<BaseNode> type() {
        return Parsers.between(Scanners.string("["),
                name(),
                Scanners.string("]"));
    }

    public Parser<BaseNode> element() {
        return Parsers.or(typedName(), name());
    }

    public Parser<BaseNode> expr() {
        return Parsers.or(concatenation(), alternation(), iteration());
    }

    public Parser<BaseNode> paranthesis(Parser<BaseNode> base) {

        final Parser.Reference<BaseNode> baseRef = base.newReference();
        Parser<BaseNode> parser = base.between(Scanners.string("("), Scanners.string(")")).or(base);
        baseRef.set(base);

        return parser;
    }

    public Parser<BaseNode> spaceElement() {
        return Scanners.WHITESPACES.followedBy(element()).map(new Map<Void, BaseNode>() {
            @Override
            public BaseNode map(Void aVoid) {
                return currentNode;
            }
        });
    }

    public Parser<BaseNode> concatenation() {
        return new Mapper<BaseNode>() {
            public BaseNode map(BaseNode node1, List<BaseNode> children) {
                BaseNode node = null;
                try {
                    boolean useFirst = true;
                    for (Iterator<BaseNode> it = children.iterator(); it.hasNext(); ) {
                        BaseNode child = it.next();
                        if (useFirst) {
                            node = new ConcatenationNode(node1, child);
                            useFirst = false;
                        } else {
                            node = new ConcatenationNode(currentNode, child);
                        }
                        currentNode = node;
                    }

                } catch (IncompleteTypeException e) {
                    logger.error("AntimirovParser.concatenation() threw IncompleteTypeException");
                }
                return node;
            }
        }.sequence(element(), spaceElement().many());
    }

    public Parser<BaseNode> iterationShort() {
        return element().followedBy(Scanners.string("*")).map(new Map<BaseNode, BaseNode>() {
            @Override
            public BaseNode map(BaseNode baseNode) {
                IterationNode node = null;
                try {
                    node = new IterationNode(baseNode);
                } catch (IncompleteTypeException e) {
                    logger.error("AntimirovParser.alternation() threw IncompleteTypeException", e);
                }
                popNode(node);
                return node;
            }
        });
    }

    public Parser<BaseNode> iteration() {
        return Parsers.or(iterationLong(), iterationShort());
    }

    public Parser<BaseNode> iterationLong() {
        return new Mapper<BaseNode>() {
            public BaseNode map(BaseNode node, Void aVoid, String min, Void bVoid, String max, Void cVoid) {
                int minimum = Integer.parseInt(min);
                int maximum = Integer.parseInt(max);
                return new IterationNode(node, minimum, maximum);
            }
        }.sequence(element(),
                Scanners.string("{"),
                Scanners.INTEGER,
                Scanners.string("-"),
                Scanners.INTEGER,
                Scanners.string("}"));
    }

    private Parser<BaseNode> altElement() {
        return Scanners.WHITESPACES.followedBy(Scanners.string("|"))
                .followedBy(Scanners.WHITESPACES)
                .followedBy(element()).map(new Map<Void, BaseNode>() {
            @Override
            public BaseNode map(Void aVoid) {
                return currentNode;
            }
        });
    }

    public Parser<BaseNode> alternation() {
        return new Mapper<BaseNode>() {
            public BaseNode map(BaseNode node1, List<BaseNode> children) {
                BaseNode node = null;
                try {
                    boolean useFirst = true;
                    for (Iterator<BaseNode> it = children.iterator(); it.hasNext(); ) {
                        BaseNode child = it.next();
                        if (useFirst) {
                            node = new AlternationNode(node1, child);
                            useFirst = false;
                        } else {
                            node = new AlternationNode(currentNode, child);
                        }
                        currentNode = node;
                    }

                } catch (IncompleteTypeException e) {
                    logger.error("AntimirovParser.concatenation() threw IncompleteTypeException");
                }
                return node;
            }
        }.sequence(element(), altElement().many());
    }

    private void popNode(BaseNode node) {
        currentNode = node;
    }
}
