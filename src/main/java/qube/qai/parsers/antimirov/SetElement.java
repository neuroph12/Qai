package qube.qai.parsers.antimirov;

/**
 * Interface <code>SetElement</code> provides method
 * <code>equals</code>() for comparison by equality between
 * <code>SetElement</code> instances.  <code>SetElements</code> are
 * aggregated by class <code>Set</code>.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Set
 */
public interface SetElement {


    /**
     * Returns TRUE if <code>e</code> is equal to the instance.
     *
     * @return TRUE, if e is equal to the instance.
     */
    public boolean equals(SetElement e);


}//interface
