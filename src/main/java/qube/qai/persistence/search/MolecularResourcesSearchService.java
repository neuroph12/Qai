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

package qube.qai.persistence.search;

import qube.qai.main.QaiConstants;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import java.util.ArrayList;
import java.util.Collection;

public class MolecularResourcesSearchService implements SearchServiceInterface, QaiConstants {

    public MolecularResourcesSearchService() {
    }

    @Override
    public String getContext() {
        return MOLECULAR_RESOURCES;
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        Collection<SearchResult> results = new ArrayList<>();

        results.add(new SearchResult(MOLECULAR_RESOURCES, "Crambin", "1crm", "Crambin structure", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "1BLU", "1blu", "Ferredoxin structure", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "1IHM", "1ihm", "Structure of the Noro virus capsid", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "3J3Y", "3j3y", "HIV-1 capsid structure", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "3PQR", "3pqr", "Structure of light-activated rhodopsin in complex with a peptide derived from the C-terminus of transducin", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "1D66", "1d66", "System of a peptide derived from the C-terminus of transducin", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "1HMS", "1hms", "Muscle Fatty Acid Binding Protein", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "1J1E", "4j1e", "Beta-secretase 1", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "HEMOGLOBIN-1hgb", "1hgb", "HEMOGLOBIN (AQUO MET) (ALPHA CHAIN)", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "HEMOGLOBIN-1hga", "1hga", "HEMOGLOBIN (DEOXY) (ALPHA CHAIN)", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "HEMOGLOBIN-1hgc", "1hgc", "HEMOGLOBIN (OXY) (ALPHA CHAIN)", 1.0));
        results.add(new SearchResult(MOLECULAR_RESOURCES, "5JDO", "5jdo", "Haptoglobin-haemoglobin receptor", 1.0));

        return results;
    }
}
