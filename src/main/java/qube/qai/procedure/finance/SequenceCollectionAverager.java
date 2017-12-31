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

package qube.qai.procedure.finance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import qube.qai.data.TimeSequence;
import qube.qai.main.QaiConstants;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.utils.ForEach;

import javax.inject.Inject;
import java.util.*;

public class SequenceCollectionAverager extends Procedure {

    private String descTemplate = "Dates from: %s to %s with childUUID: '%s'";

    private String nameTemplate = "(Avg: %s)";

    private StockEntity childEntity;

    private ForEach collectorForEach;

    private Map<String, TimeSequence> sequenceMap;

    private Set<Date> allDates;

    private Set<String> allUUIDs;

    @Inject
    private HazelcastInstance hazelcastInstance;

    /**
     * this class takes a collection of time-sequences and creates a new
     * one with the average value of individual inputs- an average for each input
     * in each collection
     */
    public SequenceCollectionAverager() {
    }

    @Override
    public void execute() {

        if (collectorForEach == null || collectorForEach.getTargetCollectionProvider() == null) {
            throw new IllegalStateException("Procedure has not been set-up right, no data to work with. Have to terminate");
        }

        if (!collectorForEach.haveChildrenExceuted()) {
            collectorForEach.execute();
        }

        Collection<StockEntity> entities = collectorForEach.getTargetCollectionProvider().getData();
        allUUIDs = new LinkedHashSet<>();
        allDates = new TreeSet<>();
        childEntity = new StockEntity();
        childEntity.setUuid(getUuid());

        // first collect all dates
        sequenceMap = new HashMap<>();
        StringBuffer tickersBuffer = new StringBuffer();
        for (Iterator<StockEntity> it = entities.iterator(); it.hasNext(); ) {

            StockEntity entity = it.next();

            // first begin with noting the ticker symbol
            allUUIDs.add(entity.getUuid());
            tickersBuffer.append(entity.getTickerSymbol());
            if (it.hasNext()) {
                tickersBuffer.append(", ");
            }

            // create the time-sequences which will be used
            TimeSequence<Double> sequence = new TimeSequence<>();
            Collection<StockQuote> quotes = entity.getQuotes();
            for (StockQuote quote : quotes) {
                sequence.add(quote.getQuoteDate(), quote.adjustedClose);
            }

            allDates.addAll(sequence.dates());
            sequenceMap.put(entity.getTickerSymbol(), sequence);
        }

        // now we can go through all dates and all quotes on a given day
        for (Date date : allDates) {

            double count = 0.0;
            double sum = 0.0;
            for (TimeSequence<Double> sequence : sequenceMap.values()) {
                if (sequence.getValue(date) != null) {
                    sum += sequence.getValue(date);
                    count++;
                }
            }

            // now we know the sum for the day, we average it
            double avg = sum / count;
            StockQuote avgQuote = new StockQuote();
            avgQuote.setTickerSymbol(getUuid());
            avgQuote.setParentUUID(getUuid());
            avgQuote.setQuoteDate(date);
            avgQuote.setAdjustedClose(avg);
            avgQuote.setClose(avg);
            childEntity.addQuote(avgQuote);
        }

        Iterator<Date> allDatesIt = allDates.iterator();
        Date startDate = allDatesIt.next();
        Date endDate = null;
        while (allDatesIt.hasNext()) {
            endDate = allDatesIt.next();
        }
        // when all done and said, save the child stock-entity
        String desc = String.format(descTemplate, startDate, endDate, getUuid());
        childEntity.setSecFilings(desc);
        String name = String.format(nameTemplate, tickersBuffer.toString());
        IMap<String, StockEntity> entityIMap = hazelcastInstance.getMap(QaiConstants.STOCK_ENTITIES);
        entityIMap.put(getUuid(), childEntity);
    }

    @Override
    protected void buildArguments() {

    }

    public Set<Date> getAllDates() {
        return allDates;
    }

    public void setAllDates(Set<Date> allDates) {
        this.allDates = allDates;
    }

    public Map<String, TimeSequence> getSequenceMap() {
        return sequenceMap;
    }

    public void setSequenceMap(Map<String, TimeSequence> sequenceMap) {
        this.sequenceMap = sequenceMap;
    }

    public StockEntity getChildEntity() {
        return childEntity;
    }

    public void setChildEntity(StockEntity childEntity) {
        this.childEntity = childEntity;
    }

    public ForEach getCollectorForEach() {
        return collectorForEach;
    }

    public void setCollectorForEach(ForEach collectorForEach) {
        this.collectorForEach = collectorForEach;
    }
}
