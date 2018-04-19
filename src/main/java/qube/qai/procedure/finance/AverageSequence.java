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
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.services.QaiInjectorService;

import javax.inject.Inject;
import java.util.*;

public class AverageSequence extends Procedure {

    private String descTemplate = "Dates from: %s to %s with childUUID: '%s'";

    private String nameTemplate = "(Avg: %s)";

    private StockEntity childEntity;

    //private SelectForEach select;

    private TreeMap<String, TimeSequence> sequenceMap;

    private Date startDate;

    private Date endDate;

    private TreeSet<Date> allDates;

    private TreeSet<String> allUUIDs;

    @Inject
    private HazelcastInstance hazelcastInstance;

    /**
     * this class takes a collection of time-sequences and creates a new
     * one with the average value of individual inputs- an average for each input
     * in each collection
     */
    public AverageSequence() {
    }

    @Override
    public void execute() {

        if (getInputs() == null || getInputs().isEmpty()) {
            info("No inputs to process- terminating execution");
            return;
        }

        allUUIDs = new TreeSet<>();
        allDates = new TreeSet<>();
        childEntity = new StockEntity();
        //childEntity.setName(getDisplayName());
        childEntity.setTickerSymbol(getDisplayName());
        childEntity.setUuid(getUuid());

        // first collect all dates
        sequenceMap = new TreeMap<>();
        StringBuffer tickersBuffer = new StringBuffer();
        for (Iterator<QaiDataProvider> it = getInputs().iterator(); it.hasNext(); ) {

            QaiDataProvider<StockEntity> provider = it.next();
            QaiInjectorService.getInstance().injectMembers(provider);
            StockEntity entity = provider.getData();

            // first begin with noting the ticker symbol
            allUUIDs.add(entity.getUuid());
            tickersBuffer.append(entity.getTickerSymbol());
            if (it.hasNext()) {
                tickersBuffer.append(", ");
            }

            // create the time-sequences which will be used
            TimeSequence<Double> sequence = new TimeSequence<>();

            // this does troubles- first update the quotes
            /*StockQuoteUpdater updater = new StockQuoteUpdater();
            QaiInjectorService.getInstance().injectMembers(updater);
            updater.addInputs(new DataProvider(entity));
            updater.execute();
            if (updater.getQuotes() == null || updater.getQuotes().isEmpty()) {
                info("Quotes for: '" + entity.getTickerSymbol() + "' could not be updated- is symbol correct? Skipping.");
                break;
            }
            Collection<StockQuote> quotes = updater.getQuotes();*/
            Collection<StockQuote> quotes = entity.getQuotes();
            for (StockQuote quote : quotes) {
                sequence.add(quote.getQuoteDate(), quote.adjustedClose);
                if (!allDates.contains(quote.getQuoteDate())) {
                    allDates.add(quote.getQuoteDate());
                }
            }

            //allDates.addAll(sequence.dates());
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
            avgQuote.setTickerSymbol(getDisplayName());
            avgQuote.setParentUUID(getUuid());
            avgQuote.setQuoteDate(date);
            avgQuote.setAdjustedClose(avg);
            avgQuote.setClose(avg);
            childEntity.addQuote(avgQuote);
        }

        boolean first = true;
        for (Date date : allDates) {
            if (first) {
                startDate = date;
                first = false;
            }
            endDate = date;
        }
        // when all done and said, save the child stock-entity
        String name = String.format(nameTemplate, tickersBuffer.toString());
        childEntity.setName(name);
        String desc = String.format(descTemplate, startDate, endDate, getUuid());
        childEntity.setSecFilings(desc);
        IMap<String, StockEntity> entityIMap = hazelcastInstance.getMap(QaiConstants.STOCK_ENTITIES);
        entityIMap.put(getUuid(), childEntity);

        hasExecuted = true;
    }

    public String getDisplayName() {
        return String.format(nameTemplate, getUuid());
    }

    @Override
    public Procedure createInstance() {
        return new AverageSequence();
    }

    @Override
    protected void buildArguments() {

    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public TreeSet<String> getAllUUIDs() {
        return allUUIDs;
    }

    public void setAllUUIDs(TreeSet<String> allUUIDs) {
        this.allUUIDs = allUUIDs;
    }

    public TreeSet<Date> getAllDates() {
        return allDates;
    }

    public void setAllDates(TreeSet<Date> allDates) {
        this.allDates = allDates;
    }

    public TreeMap<String, TimeSequence> getSequenceMap() {
        return sequenceMap;
    }

    public void setSequenceMap(TreeMap<String, TimeSequence> sequenceMap) {
        this.sequenceMap = sequenceMap;
    }

    public StockEntity getChildEntity() {
        return childEntity;
    }

    public void setChildEntity(StockEntity childEntity) {
        this.childEntity = childEntity;
    }

   /* public SelectForEach getSelect() {
        return select;
    }

    public void setSelect(SelectForEach select) {
        this.select = select;
    }*/
}
