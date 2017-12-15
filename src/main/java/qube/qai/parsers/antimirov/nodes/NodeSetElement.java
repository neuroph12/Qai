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

package qube.qai.parsers.antimirov.nodes;

/**
 * Interface <code>SetElement</code> provides method
 * <code>equals</code>() for comparison by equality between
 * <code>SetElement</code> instances.  <code>SetElements</code> are
 * aggregated by class <code>Set</code>.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see NodeSet
 */
public interface NodeSetElement {


    /**
     * Returns TRUE if <code>e</code> is equal to the instance.
     *
     * @return TRUE, if e is equal to the instance.
     */
    boolean equals(NodeSetElement e);


}//interface
