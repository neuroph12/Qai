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

package qube.qai.parsers.antimirov;


import qube.qai.parsers.antimirov.nodes.BaseNode;

/**
 * Occurrs if a content is requested on a type instance
 * for which a content is not defined.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 * @see BaseNode
 */
public class IrregularContentRequestException
        extends TypeException {


    /**
     * Sole constructor for class
     * <code>IrregularContentRequestException</code>.
     *
     * @param msg Error message.
     */
    public IrregularContentRequestException(String msg) {

        super(msg);
    }


}//class
