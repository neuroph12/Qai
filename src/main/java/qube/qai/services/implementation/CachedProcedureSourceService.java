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

package qube.qai.services.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.analysis.*;
import qube.qai.services.ProcedureSourceInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 12/16/15.
 */
public class CachedProcedureSourceService implements ProcedureSourceInterface {

    private Logger logger = LoggerFactory.getLogger("CachedProcedureSourceService");

    private String[] procedureNames = {ChangePointAnalysis.NAME,
            MatrixStatistics.NAME,
            NetworkStatistics.NAME,
            NeuralNetworkAnalysis.NAME,
            NeuralNetworkForwardPropagation.NAME,
            SortingPercentilesProcedure.NAME,
            TimeSequenceAnalysis.NAME};

    private static CachedProcedureSourceService instance;

    private Map<String, Procedure> procedureMap;

    private CachedProcedureSourceService() {
        this.procedureMap = new HashMap<String, Procedure>();
    }

    private void initProcedures() {
        ProcedureSourceService sourceService = new ProcedureSourceService();
        for (String name : procedureNames) {
            Procedure procedure = sourceService.getProcedureWithName(name);
            if (procedure != null && !procedure.hasExecuted()) {
                //logger.info("procedure: " + name + " is running as part of initialization...");
                //procedure.run();
                procedureMap.put(name, procedure);
            }

        }
    }

    public String[] getProcedureNames() {
        return procedureNames;
    }

    public Procedure getProcedureWithName(String name) {
        return procedureMap.get(name);
    }

    public static CachedProcedureSourceService getInstance() {
        if (instance == null) {
            instance = new CachedProcedureSourceService();
            instance.initProcedures();
        }

        return instance;
    }
}
