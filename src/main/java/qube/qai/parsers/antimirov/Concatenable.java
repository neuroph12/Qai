package qube.qai.parsers.antimirov;


/**
 * Interface <code>Concatenable</code> represents an entity on which a
 * concatenation of linear forms can be performed. A linear form is
 * defined as a set of type pairs. Its concatenation rules are defined
 * on type pairs (cf. rules CL1, CL2) and sets (cf. rules CL3-CL7).
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Set
 * @see TypePair
 */
public interface Concatenable {


    /**
     * Concatenates a type <code>r</code> to the implementing class and
     * returns a <code>Set</code> with the resulting linear form.
     *
     * @param r The type to concatenate the instance with.
     * @throws IllegalConcatenationException Occurs if the input entities of a concatenation does
     *                                       not obey the rules for concatenation of linear forms.
     */
    public Set concatenate(RType r)
            throws IllegalConcatenationException;

}//class
