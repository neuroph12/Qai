/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.parsers.antimirov.log;

import qube.qai.parsers.antimirov.Inequality;
import qube.qai.parsers.antimirov.nodes.BaseNode;
import qube.qai.parsers.antimirov.nodes.Name;
import qube.qai.parsers.antimirov.nodes.NodeSet;
import qube.qai.parsers.antimirov.nodes.NodeSetElement;


/**
 * Provides a convenient interface for using the protocol functions of
 * class <code>Logger</code>.  <code>Logger</code> is a line-oriented
 * device, so each line is caused by a single command. To keep the use
 * of the <code>Logger</code> convenient and simple,
 * <code>LogManager</code> provides the user with methods for formatted
 * output. This also keeps the logging commands in the code thin.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Logger
 * @see Inequality
 */
public class LogManager {


    /**
     * Symbol for corners.
     */
    protected final String BOX = "+";


    /**
     * Symbol for separation of calls.
     */
    protected final String SEP = "------------";


    /**
     * Symbol for logging the call identifcation.
     */
    protected final String CALL = "Call: ";


    /**
     * Symbol for logical AND.
     */
    protected final String AND = "/\\";


    /**
     * Symbol for logical OR.
     */
    protected final String OR = "\\/";


    /**
     * Internal <code>Logger</code>.
     */
    protected Logger logger;


    /**
     * Constructor of class <code>LogManager</code>. Requires further
     * configuring of the <code>LogManager</code> with a
     * <code>Logger</code>.
     */
    public LogManager() {
    }


    /**
     * Constructor of class <code>LogManager</code>. The internal
     * <code>Logger</code> is activated by default. The separation
     * facility is activated by default.
     *
     * @param log The internal <code>Logger</code> to use.
     */
    public LogManager(Logger log) {

        this.logger = log;
        this.logger.setActive(true);
        this.logger.setSeparation(true);
    }//constructor


    /**
     * Switches internal <code>Logger</code> on or off.
     *
     * @param onOff TRUE switches internal <code>Logger</code> on,
     *              FALSE off.
     */
    public void setActive(boolean onOff) {

        this.logger.setActive(onOff);
    }//setActive


    /**
     * Returns activation status of internal <code>Logger</code>.
     *
     * @return TRUE if <code>Logger</code> is active, otherwise
     * FALSE.
     */
    public boolean isActive() {

        return this.logger.isActive();
    }//isActive


    /**
     * Returns the internal <code>Logger</code>.
     *
     * @return Internal <code>Logger</code>.
     */
    public Logger getLogger() {

        return this.logger;
    }//getLogger


    /**
     * Sets internal <code>Logger</code> to <code>log</code>.
     *
     * @param log New internal <code>Logger</code>.
     */
    public void setLogger(Logger log) {

        this.logger = log;
    }//setLogger


    /**
     * Adds one indent step.
     *
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    private void addStep()
            throws LoggingException {

        this.logger.markPos(this.logger.getIndent());
        this.logger.incIndent();
        this.logger.emptyLn();
    }//addStep


    /**
     * Removes one indent step.
     */
    private void removeStep() {

        this.logger.decIndent();
    }//removeStep


    /**
     * Starts the log of a new call. Logs the upper bound of a call log
     * box, adds one indent step and the input types.
     *
     * @param id call number
     * @param r  Input type for r.
     * @param s  Input type for s.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void openCall(long id, BaseNode r, BaseNode s)
            throws LoggingException {

        this.logCall(id);
        this.addStep();
        this.logInput(r, s);
    }//openCall


    /**
     * Logs a call log separator.
     *
     * @param id call number
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    protected void logCall(long id)
            throws LoggingException {

        StringBuffer buf = new StringBuffer(BOX);
        buf.append(SEP);
        buf.append(CALL);
        this.logger.log(buf.toString() + id);
    }


    /**
     * Closes a log of a non-trivial call. Logs a call log separator,
     * the result and removes one indent step.
     *
     * @param id     Call number.
     * @param result Calculation result.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void closeCall(long id, boolean result)
            throws LoggingException {

        this.logger.emptyLn();
        this.logger.log("Result: " + result);
        this.logger.decIndent();
        this.logger.unmarkPos(this.logger.getIndent());
        this.logCall(id);
        //this.logger.emptyLn();
    }//closeCall


    /**
     * Logs a call where one or both parameters are <code>null</code>.
     *
     * @param id Call number.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void logNullCase(long id, BaseNode r, BaseNode s)
            throws LoggingException {

        openCall(id, r, s);
        this.logTrivCase("One or both instances are null.", false, id);
    }//logNullCase


    /**
     * Closes a log of a trivial call. Logs a case message, a call log
     * separator, the result and removes one indent step.
     *
     * @param str    Case message.
     * @param id     Call number.
     * @param result Computation result.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void logTrivCase(String str, boolean result, long id)
            throws LoggingException {

        this.logger.log(str);
        this.closeCall(id, result);
    }//logTrivCase


    /**
     * Logs input types r and s.
     *
     * @param r Input for r.
     * @param s Input for s.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void logInput(BaseNode r, BaseNode s)
            throws LoggingException {

        this.logger.log("Input r: " + ((r != null) ?
                r.toString() :
                Name.NULL));
        this.logger.log("Input s: " + ((s != null) ?
                s.toString() :
                Name.NULL));
        this.logger.emptyLn();
    }//logInput


    /**
     * Logs all leading names in <code>lNames</code>.
     *
     * @param lNames Set of leading names.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void logNames(NodeSet lNames)
            throws LoggingException {

        this.logger.log("Leading names of r: " + lNames.toString());
        this.logger.emptyLn();
    }


    /**
     * Logs Partial Derivatives of a regular expression.
     *
     * @param lefts  Left Clause.
     * @param rights Right Clause.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void logPD(NodeSetElement[] lefts, NodeSetElement[] rights)
            throws LoggingException {

        this.logger.log(
                "PD of r <: s for all leading names of r (added to A):");
        this.logger.incIndent();
        this.logger.emptyLn();
        this.logger.log("TypePairs in left clause :");
        this.logger.incIndent();
        for (int i = 0; i < lefts.length; i++)
            logger.log(i + ": " + ((lefts[i] != null) ?
                    lefts[i].toString() :
                    Name.NULL));
        this.logger.emptyLn();
        this.logger.decIndent();
        this.logger.log("TypePairs in right clause:");
        this.logger.incIndent();
        for (int i = 0; i < rights.length; i++)
            logger.log(i + ": " + ((rights[i] != null) ?
                    rights[i].toString() :
                    Name.NULL));
        this.logger.emptyLn();
        this.logger.decIndent();
        this.logger.decIndent();
    }//logPD


    /**
     * Logs a logical AND.
     */
    public void logAND()
            throws LoggingException {

        this.logger.emptyLn();
        this.logger.log(this.AND);
        this.logger.emptyLn();
    }//logAND


    /**
     * Logs a logical OR.
     */
    public void logOR()
            throws LoggingException {

        this.logger.log(this.OR);
    }//logOR


    /**
     * Logs partial derivatives of regular inequality.
     *
     * @param iq Inequality from which partial derivatives are computed.
     * @param pd Partial derivatives.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void logPDIQ(Inequality iq, Inequality[] pd)
            throws LoggingException {

        String indent = "  ";
        int i = 0;

        this.logger.log(pd.length
                + " Inequalities in the Partial Derivatives of:");
        this.logger.log(iq.toString());

        this.logger.emptyLn();

        // log first disjunction
        this.logger.log(indent + "(" + i + ") "
                + ((pd[i] != null) ? pd[i].toString() : Name.NULL)
                + " \\/ ");
        this.logger.log(indent + "(" + (i + 1) + ") "
                + ((pd[i + 1] != null) ? pd[i + 1].toString() : Name.NULL));

        // log all further disjunctions
        for (i = 2; i < pd.length; i++) {

            this.logger.log(" /\\");

            this.logger.log(indent + "(" + i + ") "
                    + ((pd[i] != null) ? pd[i].toString() : Name.NULL)
                    + " \\/ ");
            this.logger.log(indent + "(" + (i + 1) + ") "
                    + ((pd[i + 1] != null) ? pd[i + 1].toString() : Name.NULL));

            i++;
        }

        this.logger.emptyLn();
    }//logPDIQ


    /**
     * Logs an exception with message and stack trace.
     *
     * @param e     The Exception to log.
     * @param calls Call number.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void logException(Exception e, long calls) {

        Class eClass = e.getClass();
        String eName = eClass.getName();

        StringBuffer msg = new StringBuffer("No result because of ");
        msg.append(eName);

        if (e instanceof LoggingException)
            this.logger.resetIndent();
        else
            msg.append(" in call: " + calls);

        // make LoggingException impossible
        if (this.logger.getIndent() >= this.logger.getMaxIndent())
            this.logger.resetIndent();

        try {

            this.logger.log(eName + ": ");
            this.logger.log("-> " + e.getMessage());

            this.logger.emptyLn();

            StackTraceElement[] stackTrace = e.getStackTrace();
            for (int i = 0; i < stackTrace.length; i++) {
                this.logger.log(">  " + stackTrace[i].toString());
            }

            this.logger.emptyLn();

            this.logger.decIndent();
            this.logger.unmarkPos(this.logger.getIndent());
            //this.logCall(calls);
            this.logger.emptyLn();

            this.logger.log(msg.toString());

        } catch (LoggingException le) {
        }

        if (e instanceof LoggingException)
            this.logger.close();
    }//logException


    /**
     * Logs the result of a prove process with number of calls.
     *
     * @param result The result of the prove process.
     * @param calls  The number of recursive calls needed.
     * @throws LoggingException Occurrs if some illegal indent
     *                          position is tried to be marked.
     */
    public void logResult(boolean result, long calls)
            throws LoggingException {

        this.logger.emptyLn();

        StringBuffer buf = new StringBuffer("r <: s is ");
        buf.append(result ? "true." : "false.");
        this.logger.log(buf.toString());

        this.logger.log("Number of recursive calls: " + calls);
    }//logResult


    /**
     * Closes the logger and finishes logging.
     */
    public void close() {

        this.logger.close();
    }//close


}//class
