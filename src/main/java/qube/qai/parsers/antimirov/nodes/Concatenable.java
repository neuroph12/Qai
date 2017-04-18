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

package qube.qai.parsers.antimirov.nodes;


import qube.qai.parsers.antimirov.IllegalConcatenationException;

/**
 * Interface <code>Concatenable</code> represents an entity on which a
 * concatenation of linear forms can be performed. A linear form is
 * defined as a set of type pairs. Its concatenation rules are defined
 * on type pairs (cf. rules CL1, CL2) and sets (cf. rules CL3-CL7).
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see NodeSet
 * @see NodePair
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
    NodeSet concatenate(BaseNode r)
            throws IllegalConcatenationException;

}//class
