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


/**
 * Represents a logger for logging
 * protocol messages to standard output. The instance of the
 * <code>ScreenLogger</code> can be assigned to a
 * <code>LogManager</code> for more convenient use.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see LogManager
 */
public class ScreenLogger
        extends Logger {


    /**
     * Sole constructor for <code>ScreenLogger</code>.
     */
    public ScreenLogger() {

        super();
    }//constructor


    /**
     * Worker for log().
     *
     * @param line Message to log.
     */
    protected void wlog(String line) {

        System.out.print(line);
    }//wlog


    /**
     * Starts a new line.
     */
    public void newLn() {

        System.out.println();
    }//newLn


    /**
     * Closes the logger.
     */
    public void close() {
    }//close


    /**
     * Returns a <code>String</code> representation of the
     * <code>ScreenLogger</code>.
     *
     * @return A <code>String</code> representation of the
     * <code>ScreenLogger</code>. brief status overview.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer("ScreenLogger");
        buf.append((this.isActive) ? " (active) " : " (inactive) ");

        return buf.toString();
    }//toString


}//class
