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
 * Occurs if a type instance is not constructed as a regular
 * expression, for instance if a <code>RAlternationType</code> does not
 * have two children, or a <code>RNameType</code> has more than one
 * child.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see BaseNode
 */
public class IncompleteTypeException extends TypeException {


    /**
     * Sole Constructor for class <code>IncompleteTypeException</code>.
     *
     * @param msg Error message to be printed.
     */
    public IncompleteTypeException(String msg) {

        super(msg);
    }


}//class
