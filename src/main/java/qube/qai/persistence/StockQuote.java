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

package qube.qai.persistence;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openrdf.annotations.Iri;
import qube.qai.data.AcceptsVisitors;
import qube.qai.data.DataVisitor;
import qube.qai.services.implementation.UUIDService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

import static qube.qai.main.QaiConstants.BASE_URL;

/**
 * Created by rainbird on 11/19/15.
 */
@Entity
@Iri(BASE_URL + "StockQuote")
public class StockQuote implements Serializable, AcceptsVisitors {

    @Id
    @Column(name = "uuid")
    @Iri(BASE_URL + "uuid")
    private String uuid;

    @Column(name = "parentUUID", nullable = false)
    @Iri(BASE_URL + "parentUUID")
    private String parentUUID;

    @Column(name = "tickerSymbol", nullable = false)
    @Iri(BASE_URL + "tickerSymbol")
    private String tickerSymbol;

    @Column(name = "quoteDate", nullable = false)
    @Iri(BASE_URL + "quoteDate")
    private Date quoteDate;

    @Column(name = "adjustedClose")
    @Iri(BASE_URL + "adjuestedClose")
    public double adjustedClose;

    @Column(name = "close")
    @Iri(BASE_URL + "close")
    public double close;

    @Column(name = "high")
    @Iri(BASE_URL + "high")
    public double high;

    @Column(name = "low")
    @Iri(BASE_URL + "low")
    public double low;

    @Column(name = "open")
    @Iri(BASE_URL + "open")
    public double open;

    @Column(name = "volume")
    @Iri(BASE_URL + "volume")
    public double volume;

    public StockQuote() {
        this.uuid = UUIDService.uuidString();
    }

    public StockQuote(String tickerSymbol, Date date, double adjustedClose, double close, double high, double low, double open, double volume) {
        this.adjustedClose = adjustedClose;
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
        this.volume = volume;
    }

    public Object accept(DataVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getParentUUID() {
        return parentUUID;
    }

    public void setParentUUID(String parentUUID) {
        this.parentUUID = parentUUID;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public Date getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(Date quoteDate) {
        this.quoteDate = quoteDate;
    }

    public double getAdjustedClose() {
        return adjustedClose;
    }

    public void setAdjustedClose(double adjustedClose) {
        this.adjustedClose = adjustedClose;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder b = new HashCodeBuilder(17, 17);
        return b.append(quoteDate).toHashCode(); // append(tickerSymbol)
    }

    /**
     * in this case equality is only when ticker-symbol and dates are equal
     * and not checked by using uuid, as it might be expected.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StockQuote)) {
            return false;
        }
        StockQuote other = (StockQuote) obj;
        return new EqualsBuilder().append(uuid, other.uuid).build();
    }

}
