package qube.qai.parsers.antimirov;


import qube.qai.parsers.antimirov.log.LogManager;
import qube.qai.parsers.antimirov.log.LoggingException;

import java.util.Hashtable;


/**
 * Represents a regular inequality of two regular types r <: s.
 * <code>Inequalitie</code>s can be aggregated in <code>Set</code>s and
 * can prove theirselves for being TRUE or FALSE.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see RType
 */
public final class Inequality
        implements SetElement {


    /**
     * Type on left side of inequality r <: s.
     */
    private RType r;


    /**
     * Type on right side of inequality r <: s.
     */
    private RType s;


    /**
     * Log generator.
     */
    private LogManager logmanager;


    /**
     * Flag for using short circuit evaluation of operators. If flag is
     * set, recursive invocations will only be performed, if they
     * can change the result. Default value is FALSE.
     */
    private boolean shortCircuit = false;


    /**
     * Constructs a representation of a regular inequality r <: s.
     *
     * @param r Type on left side of r <: s.
     * @param s Type on right side of r <: s.
     */
    public Inequality(RType r, RType s) {

        this.r = r;
        this.s = s;
        this.shortCircuit = false;

    }//constructor


    /**
     * Constructs a representation of a regular inequality r <: s, using
     * <code>LogManager</code> <code>lm</code>.
     *
     * @param r  Type on left side of r <: s.
     * @param s  Type on right side of r <: s.
     * @param lm <code>LogManager</code> instance to generate a protocol
     *           of the subtyping process.
     */
    public Inequality(RType r, RType s, LogManager lm) {

        this.r = r;
        this.s = s;
        this.shortCircuit = false;
        this.logmanager = lm;

    }//constructor


    /**
     * Constructs a representation of a regular inequality r <: s,
     * configuring evaluation mode. Passing TRUE will cause turning short
     * circuit evaluation on.
     *
     * @param r         Type on left side of r <: s.
     * @param s         Type on right side of r <: s.
     * @param shortCirc If true, subtyping will use short
     *                  circuit evaluation.
     */
    public Inequality(RType r, RType s, boolean shortCirc) {

        this.r = r;
        this.s = s;
        this.shortCircuit = shortCirc;

    }//constructor


    /**
     * Returns the internal <code>LogManager</code> instance.
     *
     * @return The internal <code>LogManager</code> instance.
     */
    public LogManager getLogManager() {

        return this.logmanager;

    }//getLogManager


    /**
     * Sets the internal <code>LogManager</code> instance.
     *
     * @param log The new internal <code>LogManager</code>.
     */
    public void setLogManager(LogManager log) {

        this.logmanager = log;

    }//setLogManager


    /**
     * Returns the short circuit evaluation status. If short circuit
     * evaluation is set to TRUE, recursive invocations are only caused,
     * if they can change the result. Default is FALSE.
     *
     * @return Status of the short circuit evaluation.
     */
    public boolean getShortCircuit() {

        return this.shortCircuit;

    }//getShortCircuit


    /**
     * Turns short circuit evaluation on (TRUE) or off (FALSE).
     *
     * @param shortCirc TRUE turns short circuit evaluation on, FALSE
     *                  off.
     */
    public void setShortCircuit(boolean shortCirc) {

        this.shortCircuit = shortCirc;

    }//setShortCircuit


    /**
     * Sets internal type r in r <: s to <code>r</code>.
     *
     * @param r Sets internal type r in r <: s to <code>r</code>.
     */
    public void setR(RType r) {

        this.r = r;

    }//setR


    /**
     * Returns type r of regular inequality r <: s.
     *
     * @return Type r of regular inequality r <: s.
     */
    public RType getR() {

        return this.r;

    }//getR


    /**
     * Sets internal type s in r <: s to <code>s</code>.
     *
     * @param s Sets internal type s in r <: s to <code>s</code>.
     */
    public void setS(RType s) {

        this.s = s;

    }//setR


    /**
     * Returns type s of regular inequality r <: s.
     *
     * @return Type s of regular inequality r <: s.
     */
    public RType getS() {

        return this.s;

    }//getS


    /**
     * Returns a copy of the inequality with unfolded member types.
     * Unfolding replaces all recursive occurrences in instances of
     * <code>RNameType</code> by their definitions.
     *
     * @return A copy of the instance with unfolded member types.
     */
    public Inequality unfold() {

        return new Inequality(
                this.r.unfold(new Hashtable()),
                this.s.unfold(new Hashtable()),
                this.logmanager
        );

    }//unfold


    /**
     * Returns partial derivatives of <code>r</code> <: <code>s</code>
     * according to the settheoretic observation of Hosoya.
     * Be pd(ln(r), r) the partial derivatives of r for all leading
     * names of r. Then the length of the resulting array is always
     * |delta(ln(r), r)| * 2^(|delta(ln(s), s)|+1).
     * Between even and odd indices a disjunction operator is assumed,
     * between odd and even indices a conjunction operator is assumed.
     * Hence indices 0,1 represent a single disjunction, indices 2,3
     * represent a single disjunction etc.  The array as a whole
     * represents a conjunction of such disjunctions. The inequalities are
     * not returned as a <code>Set</code> because we need ensurement about
     * the order of inequalities. This is because it is not arbitrary
     * which inequalities are connected by a disjunction. This method does
     * logging as a side effect.
     *
     * @param names The <code>Set</code> of leading names.
     * @return Partial derivatives of <code>r</code> <: <code>s</code>.
     * @throws IllegalConcatenationException Occurs if some illegal
     *                                       concatenation is tried to be performed.
     * @throws IncompleteTypeException       Occurs if at least one input
     *                                       type is no regular expression.
     * @throws NoWellformedTypeException     Occurs if at least one input
     *                                       type does not fulfill the wellformedness
     *                                       constraints.
     */
    public Inequality[] getPartialDerivatives(Set names) throws
            IllegalConcatenationException,
            IncompleteTypeException,
            NoWellformedTypeException {

        // if we have leading names
        if (names.isEmpty() == false && names != null) {

            int m = 0;
            int n = 0;
            int index = 0;

            //compute partial derivatives
            Set pdR = this.r.getPartialDerivatives(names);
            Set pdS = this.s.getPartialDerivatives(names);

            if (pdR.isEmpty() || pdS.isEmpty())
                return null;

            //make arrays from sets of partial derivatives
            SetElement[] pdOfR = pdR.toArray();
            SetElement[] pdOfS = pdS.toArray();

            // detailed log of partial derivatives
            if (this.logmanager != null) {
                try {
                    this.logmanager.logPD(pdOfR, pdOfS);
                } catch (LoggingException le) {
                    this.logmanager.logException(le, 0);
                    this.logmanager.setActive(false);
                }
            }

            // m = |delta(r)|
            m = (pdR.isEmpty()) ? 0 : pdOfR.length;
            // n = |delta(s)|
            n = (pdS.isEmpty()) ? 0 : pdOfS.length;

            // compute number of inequalities in partial derivatives
            // = m * 2^(n+1)
            int numberOfIq = m * (int) (Math.pow(2, n + 1));
            if (numberOfIq < 1)
                return null; //no inequalities, no resulting set

            Inequality[] result = new Inequality[numberOfIq];

            // synchronize length of bit patterns and type clauses
            BitArray s1Subset = new BitArray(pdOfS.length);
            BitArray s2Subset = new BitArray(pdOfS.length);

            // iterate partial derivatives of r
            for (int i = 0; i < m; i++) {

                s1Subset.setAll(false); // type s1 starts as nothing
                s2Subset.setAll(true);  // type s2 starts with the disjunction
                // of all second elements of all pairs

                // construct initial types r1, r2, s1, s2
                RType r1 = (RType)
                        (((TypePair) pdOfR[i]).getFirstElement());
                RType r2 = (RType)
                        (((TypePair) pdOfR[i]).getSecondElement());
                RType s1, s2;

                try {

                    s1 = s1Subset.construct(pdOfS, true);
                    s2 = s2Subset.construct(pdOfS, false);

                } catch (BitArrayIndexException baie) {
                    throw new IncompleteTypeException(
                            "Error while constructing initial types."
                    );
                }

                // iterate right side of partial derivatives
                // (construct power set of first and second elements
                // of right side)
                for (int j = 0; j < Math.pow(2, n); j++) {

                    /*
                   Index positions in the resulting array represent the
                   following conjunction of disjunctions:

                            (0 \/ 1) /\ (2 \/ 3) /\  (4 \/ 5) /\ ...
                */

                    result[index] = new Inequality(r1, s1, this.logmanager);
                    index++;
                    result[index] = new Inequality(r2, s2, this.logmanager);
                    index++;

                    // modify the bitpattern
                    s1Subset.add();
                    s2Subset.subtract();

                    try {

                        // construct new types
                        s1 = s1Subset.construct(pdOfS, true);
                        s2 = s2Subset.construct(pdOfS, false);

                    } catch (BitArrayIndexException baie) {
                        throw new IncompleteTypeException(
                                "Error while constructing types."
                        );
                    }

                }// for j (right side iteration)

            }// for i (left side iteration)

            return result;

            // no leading names, no resulting set
        } else

            return null;

    }//getPartialDerivatives

// - - - The following 9 methods represent the tests for trivial cases
// in which the subtyping process can be stopped without further
// derivation (cf. thesis, Section 6.4, p.64).

    /**
     * Returns TRUE, if <code>r</code> and <code>s</code> are the same
     * type by reference or by equality. In case of TRUE, this method does
     * the logging of this trivial case as a side effect.
     *
     * @param r      The first type to be analyzed.
     * @param s      The second type to be analyzed.
     * @param callID ID of the current call for correct logging.
     * @return TRUE, if <code>r</code> and <code>s</code> are the same
     *         type by reference or by content.
     */
    private boolean sameType(RType r, RType s, long callID) {

        //1) r and s of same type (case T3)
        boolean result = ((r == s) || (r.equals(s)));

        if (result && this.logmanager != null) {

            try {

                this.logmanager.logTrivCase("r same type as s.",
                        true,
                        callID);

            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }

        return result;

    }//sameType


    /**
     * Returns TRUE, if <code>r</code> <: <code>s</code> has been already
     * analyzed. In case of TRUE, this method does the logging of this
     * trivial case as a side effect.
     *
     * @param iq              The inequality to test.
     * @param assumptionTable A table containing all previously analyzed
     *                        inequalities.
     * @param callID          ID of the current call for correct logging.
     * @return TRUE, if <code>iq</code> has already been analyzed.
     */
    private boolean alreadyTested(Inequality iq,
                                  Set assumptionTable,
                                  long callID) {

        // 2) r <: s already analyzed  (case T2)
        boolean result = assumptionTable.contains(iq);

        if (result && this.logmanager != null) {

            try {

                this.logmanager.logTrivCase("r<:s already analyzed.",
                        true,
                        callID);

            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }

        return result;

    }//alreadyTested


    /**
     * Returns TRUE if <code>r</code> is <code>none</code>, otherwise
     * FALSE.  In case of TRUE, this method does the logging of this
     * trivial case as a side effect.
     *
     * @param r      The type to be analyzed.
     * @param callID ID of the current call for correct logging.
     * @return TRUE, if <code>r</code> is an instance of
     *         <code>none</code>, otherwise FALSE.
     */
    private boolean rIsNone(RType r, long callID) {

        // 3) r == none (case T4)
        boolean result = (r instanceof RNoneType);

        if (result && this.logmanager != null) {

            try {

                this.logmanager.logTrivCase("r is none.", true, callID);

            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }//if

        return result;

    }//rIsNone


    /**
     * Returns TRUE if <code>s</code> is <code>none</code>, otherwise
     * FALSE. In case of TRUE, this method does the logging of this
     * trivial case as a side effect.
     *
     * @param r      The type which determines the result to log.
     * @param s      The type to be analyzed.
     * @param callID ID of the current call for correct logging.
     * @return TRUE, if <code>s</code> is an instance of RNoneType,
     *         otherwise FALSE.
     */
    private boolean sIsNone(RType r, RType s, long callID) {

        // 4) s == none (case T5)
        // presupposes T3 (check no. 1) or T4 (check no. 3)
        boolean result = (s instanceof RNoneType);

        if (result && this.logmanager != null) {

            try {

                this.logmanager.logTrivCase("s is none.",
                        r instanceof RNoneType,
                        callID);
            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }

        return result;

    }//sIsNone


    /**
     * Returns TRUE if <code>r</code> is nullable and <code>s</code> is
     * not nullable. In case of TRUE, this method does the logging of
     * this trivial case as a side effect.
     *
     * @param r      The first type to be analyzed.
     * @param s      The second type to be analyzed.
     * @param callID ID of the current call for correct logging.
     * @return TRUE, if <code>r</code> is nullable and <code>s</code> is
     *         not nullable, otherwise FALSE.
     */
    private boolean rNullableSNotNullable(RType r, RType s, long callID)
            throws NoWellformedTypeException {

        boolean result = true;

        // 5) nullable(r) /\ !nullable(s) (case T1)
        result = (r.isNullable() && !(s.isNullable()));

        if (result && this.logmanager != null) {

            try {

                this.logmanager.logTrivCase("r nullable, s not nullable.",
                        false,
                        callID);

            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }//if

        return result;

    }//rNullableSNotNullable


    /**
     * Returns TRUE if <code>r</code> is <code>epsilon</code>. In case of
     * TRUE, this method does the logging of this trivial case as a side
     * effect.
     *
     * @param r      The type to be analyzed.
     * @param s      The type which determines the result to log.
     * @param callID ID of the current call for correct logging.
     * @return TRUE, if <code>r</code> is <code>epsilon</code>, otherwise
     *         FALSE
     */
    private boolean rIsEpsilon(RType r, RType s, long callID) throws
            NoWellformedTypeException {

        // 6) r == epsilon  (cases T6, T7)
        boolean result = (r instanceof REmptyType);

        if (result && this.logmanager != null) {

            try {

                this.logmanager.logTrivCase("r is epsilon.",
                        s.isNullable(),
                        callID);
            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }//if

        return result;

    }//rIsEpsilon


    /**
     * Returns TRUE if <code>lNames</code> is empty. In case of TRUE,
     * this method does the logging of this trivial case as a side
     * effect.
     *
     * @param lNames The set of names to be tested.
     * @param callID ID of the current call for correct logging.
     * @return TRUE if <code>lNames</code> is empty.
     */
    private boolean noLeadingNames(Set lNames, long callID) {

        // 7) no leading names
        // presupposes T1 (check no. 5)
        boolean result = lNames.isEmpty();

        if (result && this.logmanager != null) {

            try {

                this.logmanager.logTrivCase("No leading names.",
                        true,
                        callID);

            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }//if

        return result;

    }//noLeadingNames


    /**
     * Returns TRUE if <code>s</code> is <code>epsilon</code>. In case of
     * TRUE, this method does the logging of this trivial case as a side
     * effect.
     *
     * @param r      The type which determines the result to log.
     * @param s      The type to be analyzed.
     * @param callID ID of the current call for correct logging.
     * @return TRUE, if <code>s</code> is <code>epsilon</code>, otherwise
     *         FALSE
     */
    private boolean sIsEpsilon(RType r, RType s, long callID) throws
            NoWellformedTypeException {

        // 8) s == epsilon
        // presupposes T9 (check no.7)
        boolean result = (s instanceof REmptyType);

        if (result && this.logmanager != null) {

            try {

                this.logmanager.logTrivCase("s is epsilon.",
                        false,
                        callID);
            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }//if

        return result;

    }//sIsEpsilon


    /**
     * Returns TRUE if <code>pd</code> does not contain partial
     * derivatives. In case of TRUE, this method does the logging of this
     * trivial case as a side effect.
     *
     * @param pd     Partial Derivatives to be tested.
     * @param callID ID of the current call for correct logging.
     * @return TRUE if not both sets contain partial derivatives.
     */
    private boolean noPartialDerivatives(Inequality[] pd, long callID) {

        // 9) no partial derivatives
        boolean result = (pd == null);

        if (result && this.logmanager != null) {

            try {

                this.logmanager.logTrivCase("No partial derivatives.",
                        false,
                        callID);

            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }

        }//if

        return result;

    }//noPartialDerivatives


    /**
     * Returns TRUE iff both member types of the inequality fulfill
     * wellformedness constraints 1 and 2, otherwise FALSE.
     *
     * @return TRUE iff both member types of the inequality are
     *         wellformed, otherwise FALSE.
     */
    public boolean isWellformed()
            throws NoWellformedTypeException {

        return this.r.isWellformed() && this.s.isWellformed();
    }//isWellformed


    /**
     * Returns TRUE, if the inequality is TRUE, otherwise FALSE.
     *
     * @return TRUE, if the inequality is TRUE, otherwise FALSE
     */
    public boolean prove()
            throws NoWellformedTypeException,
            IncompleteTypeException,
            IllegalConcatenationException {

        // The actual subtype test is done by worker method wprove().
        // prove() is a wrapper which provides
        // reinitializing and convenient logging.

        boolean result = true;
        Set assumptionSet = new Set();
        Counter counter = new Counter(-1);

        try {

            // pipe input types to the recursive worker method
            result = this.wprove(assumptionSet, counter);

        } catch (NoWellformedTypeException nwte) {

            // log exception if logmanager is activated, otherwise throw it
            if (this.logmanager != null) {
                this.logmanager.logException(nwte, counter.counts());
            }

            throw nwte;

        }//try catch

        // log result
        if (this.logmanager != null) {
            try {
                this.logmanager.logResult(result, counter.counts());
                this.logmanager.close();
            } catch (LoggingException le) {

                this.logmanager.logException(le, 0);
                this.logmanager.setActive(false);
            }
        }//if

        return result;

    }//prove


    /**
     * Worker for prove().
     *
     * @param assumptionSet Table for storing previously handled
     *                      inequalities.
     * @param calls         Counter for number of recursive calls.
     * @return TRUE, if r <: s holds, otherwise FALSE
     */
    private boolean wprove(Set assumptionSet, Counter calls)
            throws NoWellformedTypeException,
            IncompleteTypeException,
            IllegalConcatenationException {

        calls.plus();
        // store current call count for later logging
        long callID = calls.counts();

        // if one or both input types are null, stop
        if ((this.r == null) || (this.s == null)) {

            IncompleteTypeException ite = new IncompleteTypeException(
                    "One or both input types are " + RName.NULL
            );
            if (this.logmanager != null)
                this.logmanager.logException(ite, callID);

            throw ite;
        }//if null

        //log initial test situation: r <: s
        if (this.logmanager != null) {
            try {
                this.logmanager.openCall(callID, this.r, this.s);
            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }

        // unfold types within inequality
        Inequality iq = this.unfold();

        // check wellformedness
        this.isWellformed();

//   I) handle trivial cases

        // 1) r and s same type
        if (sameType(iq.r, iq.s, callID))
            return true;

        // 2) r <: s already contained in assumption table
        if (alreadyTested(iq, assumptionSet, callID))
            return true;

        // 3) r == none
        if (rIsNone(iq.r, callID))
            return true;

        // 4) s == none
        if (sIsNone(iq.r, iq.s, callID))
            return (iq.r instanceof RNoneType);

        // 5) nullable(r) /\ !nullable(s)
        if (rNullableSNotNullable(iq.r, iq.s, callID))
            return false;

        // 6) r == e
        if (rIsEpsilon(iq.r, iq.s, callID))
            return iq.s.isNullable();

//   II) take leading names of r and partial derivatives of all these
        // names on r <: s

        Set lNames = iq.r.leadingNames();

        // 7) no leading names
        if (noLeadingNames(lNames, callID))
            return true;

        // 8) s is epsilon
        if (sIsEpsilon(r, s, callID)) {
            return false;
        }

        // log leading names
        if (this.logmanager != null) {
            try {
                this.logmanager.logNames(lNames);

            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }//

        Inequality[] pd = iq.getPartialDerivatives(lNames);

        // 9) no Partial Derivatives
        if (noPartialDerivatives(pd, callID))
            return false;

        // log partial derivatives of this inequality
        if (this.logmanager != null) {
            try {
                this.logmanager.logPDIQ(iq, pd);

            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }//

//   III) if there are valid partial derivatives, add currently
        // handled inequality to the assumption set
        assumptionSet.add(iq);

//   IV) test all partial derivatives    

        // result of computation
        boolean result = true;

        // result of single disjunction clause r1 <: s1 \/  r2 <: s2
        boolean clauseResult = true;

        for (int i = 0; i < pd.length; i++) {

            //logging
            if (i != 0 && this.logmanager != null) {

                try {
                    this.logmanager.logAND();
                } catch (LoggingException le) {

                    this.logmanager.logException(le, callID);
                    this.logmanager.setActive(false);
                }
            }//if

            // first inequality in current disjunction is
            // computed and logged
            clauseResult = pd[i].wprove(assumptionSet, calls);

            //logging
            if (this.logmanager != null) {
                try {
                    this.logmanager.logOR();
                } catch (LoggingException le) {

                    this.logmanager.logException(le, callID);
                    this.logmanager.setActive(false);
                }
            }//if

            // compute and log second inequality in current disjunction
            // in accordance to operator evaluation policy
            if (this.shortCircuit) {
                clauseResult = clauseResult ||
                        pd[i + 1].wprove(assumptionSet, calls);

            } else {
                clauseResult = clauseResult |
                        pd[i + 1].wprove(assumptionSet, calls);
            }//if

            //add result of current disjunction to main result
            result = result && clauseResult;

            i++;
        }//for

        // close logging
        if (this.logmanager != null) {
            try {
                this.logmanager.closeCall(callID, result);

            } catch (LoggingException le) {

                this.logmanager.logException(le, callID);
                this.logmanager.setActive(false);
            }
        }//if

        return result;

    }//wprove


    /**
     * Returns TRUE if <code>e</code> contains the same types for r and s
     * like the instance, otherwise FALSE.
     *
     * @param e The <code>SetElement</code> to compare the inequality to.
     * @return TRUE if <code>e</code> contains same types for r and s
     *         like the instance, otherwise FALSE.
     */
    public boolean equals(SetElement e) {

        if (e instanceof Inequality) {

            return this.r.equals(((Inequality) e).r) &&
                    this.s.equals(((Inequality) e).s);

        } else

            return false;

    }//equals


    /**
     * Returns a <code>String</code> representation of the inequality.
     *
     * @return A <code>String</code> representation of the inequality.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer(this.r.toString());
        buf.append(" <: ");
        buf.append(this.s.toString());

        return buf.toString();
    }


    /**
     * Class <code>Counter</code> represents a counter for counting the
     * calls over different recursive calls and instances.
     *
     * @author Stefan Hohenadel
     * @version 1.0
     */
    private class Counter {


        /**
         * Contains the number of counted entities.
         */
        private long calls;


        /**
         * Constructor for class <code>Counter</code>. Initializes the
         * <code>Counter</code> with value <code>i</code>.
         *
         * @param i Initializes <code>Counter</code> with start value
         *          <code>i</code>.
         */
        public Counter(int i) {
            this.calls = i;
        }


        /**
         * Increases the <code>Counter</code> by factor 1.
         */
        public void plus() {
            this.calls++;
        }


        /**
         * Returns the state of the <code>Counter</code>.
         *
         * @return Counting value.
         */
        public long counts() {
            return this.calls;
        }

    }// class Counter


}//class Inequality
