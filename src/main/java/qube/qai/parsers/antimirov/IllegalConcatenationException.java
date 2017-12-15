/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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

package qube.qai.parsers.antimirov;


import qube.qai.parsers.antimirov.nodes.Concatenable;

/**
 * Occurs if the concatenation of linear forms is applied
 * to an illegal input. A concatenation of linear forms is defined for
 * type pairs (cf. rules CL1 and CL2) and sets (cf. rules CL3-CL7).
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see Concatenable
 */
public class IllegalConcatenationException
        extends Exception {


    /**
     * Sole Constructor for class IllegalConcatenationException
     *
     * @param msg Error message to be printed.
     */
    public IllegalConcatenationException(String msg) {

        super(msg);
    }


}//class
