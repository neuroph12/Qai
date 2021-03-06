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

package qube.qai.data;

import qube.qai.network.Network;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.Procedure;

/**
 * Created by zenpunk on 12/27/15.
 */
public interface DataVisitor {

    Object visit(StockEntity visitee, Object data);

    Object visit(StockQuote visitee, Object data);

    Object visit(WikiArticle visitee, Object data);

    Object visit(Network visitee, Object data);

    Object visit(Procedure visitee, Object data);
}
