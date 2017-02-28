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


/**
 * Abstract superclass for all exceptions concerning inherent parts or
 * behaviour of type classes.
 *
 * @author Stefan Hohenadel
 * @version 1.0
 */
public abstract class TypeException
        extends Exception {


    /**
     * Constructor for class <code>TypeException</code>.
     *
     * @param msg The error message to be printed.
     */
    public TypeException(String msg) {

        super(msg);
    }//constructor


}//class
