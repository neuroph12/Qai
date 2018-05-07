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

import org.openrdf.annotations.Iri;
import qube.qai.data.TimeSequence;
import qube.qai.persistence.MapDataProvider;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.services.QaiInjectorService;

import java.util.*;

import static qube.qai.main.QaiConstants.BASE_URL;

@Iri(BASE_URL + "AverageSequence")
public class AverageSequence extends Procedure {

    private String descTemplate = "Dates from: %s to %s with childUUID: '%s'";

    private String nameTemplate = "(Avg: %s)";

    @Iri(BASE_URL + "childEntity")
    private StockEntity childEntity;

    @Iri(BASE_URL + "sequences")
    private TimeSequence[] sequences;

    @Iri(BASE_URL + "startDate")
    private Date startDate;

    @Iri(BASE_URL + "endDate")
    private Date endDate;

    @Iri(BASE_URL + "allDates")
    private Date[] allDates;

    /**
     * this class takes a collection of time-sequences and creates a new
     * one with the average value of individual inputs- an average for each input
     * in each collection
     */
    public AverageSequence() {
        super("AverageSequence");
        this.sequences = new TimeSequence[0];
        this.allDates = new Date[0];
    }

    @Override
    public void execute() {

        if (getInputs() == null || getInputs().isEmpty()) {
            info("No inputs to process- terminating execution");
            return;
        }

        TreeMap<String, TimeSequence> sequenceMap = new TreeMap<>();
        TreeSet<Date> allDatesTmp = new TreeSet<>();
        childEntity = new StockEntity();
        //childEntity.setName(getDisplayName());
        childEntity.setTickerSymbol(getDisplayName());
        childEntity.setUuid(getUuid());

        // first collect all dates
        //sequenceMap = new TreeMap<>();
        StringBuffer tickersBuffer = new StringBuffer();
        for (Iterator<QaiDataProvider> it = getInputs().iterator(); it.hasNext(); ) {

            // @TODO update the stock-data first
            QaiDataProvider<StockEntity> provider = it.next();
            QaiInjectorService.getInstance().injectMembers(provider);
            StockEntity entity = provider.getData();

            // first begin with noting the ticker symbol
            //allUUIDs.add(entity.getUuid());
            tickersBuffer.append(entity.getTickerSymbol());
            if (it.hasNext()) {
                tickersBuffer.append(", ");
            }

            // create the time-sequences which will be used
            TimeSequence sequence = new TimeSequence(entity.getTickerSymbol());

            Collection<StockQuote> quotes = entity.getQuotes();
            for (StockQuote quote : quotes) {
                sequence.add(quote.getQuoteDate(), quote.adjustedClose);
                if (!allDatesTmp.contains(quote.getQuoteDate())) {
                    allDatesTmp.add(quote.getQuoteDate());
                }
            }

            //allDates.addAll(sequence.dates());
            sequenceMap.put(entity.getTickerSymbol(), sequence);
        }

        // now we can go through all dates and all quotes on a given day
        for (Date date : allDates) {

            double count = 0.0;
            double sum = 0.0;
            for (TimeSequence sequence : sequenceMap.values()) {
                if (!Double.isNaN(sequence.getValue(date))) {
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
        QaiDataProvider provider = new MapDataProvider(STOCK_ENTITY, childEntity.getUuid());
        QaiInjectorService.getInstance().injectMembers(provider);
        provider.putData(childEntity.getUuid(), childEntity);


        allDates = new Date[allDatesTmp.size()];
        allDatesTmp.toArray(allDates);

        sequences = new TimeSequence[sequenceMap.size()];
        int index = 0;
        for (TimeSequence sequence : sequenceMap.values()) {
            sequences[index] = sequence;
            index++;
        }

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

    public Collection<Date> getAllDatesCollection() {
        return Arrays.asList(allDates);
    }

    public Date[] getAllDates() {
        return allDates;
    }

    public TimeSequence[] getSequences() {
        return sequences;
    }

    public StockEntity getChildEntity() {
        return childEntity;
    }

}
