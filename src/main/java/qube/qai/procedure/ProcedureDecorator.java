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

package qube.qai.procedure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.DataVisitor;

/**
 * Created by rainbird on 12/27/15.
 */
public abstract class ProcedureDecorator extends Procedure implements ProcedureConstants {

    protected Procedure toDecorate;

    protected Logger logger = LoggerFactory.getLogger("Procedure Logger");

    protected boolean debug = true;

    public ProcedureDecorator(String name, Procedure toDecorate) {
        super(name, toDecorate);
        this.toDecorate = toDecorate;
    }

    @Override
    public void execute() {
        toDecorate.execute();
    }

    @Override
    public Object accept(DataVisitor visitor, Object data) {
        data = toDecorate.accept(visitor, data);
        return visitor.visit(this, data);
    }
}
