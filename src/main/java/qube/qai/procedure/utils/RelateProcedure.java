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

package qube.qai.procedure.utils;

import qube.qai.data.Arguments;
import qube.qai.data.SelectionOperator;
import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 3/11/17.
 */
public class RelateProcedure extends Procedure {

    public static String NAME = "Relate Procedure";

    public static String DESCRIPTION = "Attaches two resources to each other so that their relation can be persisted ";

    public static String RELATE_TO = "relateTo";

    public static String RELATED_RESOURCE = "relatedResource";

    private SelectionOperator relateTo;

    private SelectionOperator relatedResource;

    /**
     * this class is mainly for relating two resources with each other
     * so that their relation with each other can be persisted
     * the first example will be attaching wiki-articles to selected
     * stock-entities, and charts and plots of data which have already been
     * generated and so on.
     */
    public RelateProcedure() {
        super(NAME);
    }

    @Override
    public void execute() {
        // nothing to do here
    }

    @Override
    public void buildArguments() {
        arguments = new Arguments();
    }

    public SelectionOperator getRelateTo() {
        return relateTo;
    }

    public void setRelateTo(SelectionOperator relateTo) {
        this.relateTo = relateTo;
    }

    public SelectionOperator getRelatedResource() {
        return relatedResource;
    }

    public void setRelatedResource(SelectionOperator relatedResource) {
        this.relatedResource = relatedResource;
    }
}
