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


import qube.qai.parsers.antimirov.nodes.BaseNode;

/**
 * Occurs if a type instance is analyzed that does not fulfill the
 * two wellformedness constraints of types.
 * <p> <b>
 * 1) every recursive occurrence must be in a tail position within a
 * concatenation,
 * </p>
 * <p> <b>
 * 2) every recursive occurrence must be preceded by at least one
 * non-nullable term.
 * </b></p>
 * See also thesis, Section 3.3, p.27.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see BaseNode
 */
public class NoWellformedTypeException
        extends TypeException {


    /**
     * Sole Constructor for class NoWellformedTypeException.
     *
     * @param msg Error message to be printed.
     */
    public NoWellformedTypeException(String msg) {

        super(msg);
    }


}//class
