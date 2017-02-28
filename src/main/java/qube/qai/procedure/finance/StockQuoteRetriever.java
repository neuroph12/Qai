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

package qube.qai.procedure.finance;

import qube.qai.data.SelectorProvider;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 11/19/15.
 */
public class StockQuoteRetriever extends Procedure {

    public static String NAME = "Stock Quote Retriever Procedure";

    @Inject
    private SelectorProvider provider;

    public StockQuoteRetriever() {
        super(NAME);
    }

    @Override
    public void execute() {
        Collection<StockQuote> returnValue = new ArrayList<StockQuote>();

        //
    }

    @Override
    public void buildArguments() {

    }
}
