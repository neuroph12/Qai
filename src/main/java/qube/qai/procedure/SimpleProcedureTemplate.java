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

package qube.qai.procedure;


public class SimpleProcedureTemplate<P extends Procedure> implements ProcedureTemplate<P> {

    private P procedure;

    public SimpleProcedureTemplate() {
    }

    public SimpleProcedureTemplate(P procedure) {
        this.procedure = procedure;
    }

    @Override
    public P createProcedure() {
        return (P) procedure.createInstance();
    }

    @Override
    public String getProcedureName() {
        return procedure.NAME;
    }

    @Override
    public String getProcedureDescription() {
        return procedure.DESCRIPTION;
    }

    public P getProcedure() {
        return procedure;
    }

    public void setProcedure(P procedure) {
        this.procedure = procedure;
    }
}
