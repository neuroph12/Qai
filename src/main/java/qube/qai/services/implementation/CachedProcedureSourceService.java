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

    private String[] procedureNames = { ChangePointAnalysis.NAME,
                                        MatrixStatistics.NAME,
                                        NetworkStatistics.NAME,
                                        NeuralNetworkAnalysis.NAME,
                                        NeuralNetworkForwardPropagation.NAME,
                                        SortingPercentilesProcedure.NAME,
                                        TimeSequenceAnalysis.NAME };

    private static CachedProcedureSourceService instance;

    private Map<String, Procedure> procedureMap;

    private CachedProcedureSourceService() {
        this.procedureMap = new HashMap<String, Procedure>();
    }

    private void initProcedures() {
        ProcedureSourceService sourceService = new ProcedureSourceService();
        for (String name : procedureNames) {
            Procedure procedure = sourceService.getProcedureWithName(name);
            if (!procedure.hasExecuted()) {
                //logger.info("procedure: " + name + " is running as part of initialization...");
                //procedure.run();
            }
            procedureMap.put(name, procedure);
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
