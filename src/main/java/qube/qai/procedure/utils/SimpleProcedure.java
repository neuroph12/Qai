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

package qube.qai.procedure.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.procedure.Procedure;

/**
 * Created by zenpunk on 12/27/15.
 */
public class SimpleProcedure extends Procedure {

    private Logger logger = LoggerFactory.getLogger("SimpleProcedure");

    public static String NAME = "Simple Procedure";

    public SimpleProcedure() {
        super(NAME);
    }

    @Override
    public void execute() {
        logger.info("SimpleProcedure: '" + getUuid() + "' successfully executed!");
    }

    @Override
    public Procedure createInstance() {
        return new SimpleProcedure();
    }

    @Override
    public void buildArguments() {
//        arguments = new Arguments();
    }

}
