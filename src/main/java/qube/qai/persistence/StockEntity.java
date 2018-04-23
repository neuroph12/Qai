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

import org.openrdf.annotations.Iri;
import qube.qai.data.AcceptsVisitors;
import qube.qai.data.DataVisitor;
import qube.qai.services.implementation.UUIDService;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static qube.qai.main.QaiConstants.BASE_URL;

/**
 * Created by rainbird on 11/19/15.
 */
@Entity
@Iri(BASE_URL + "StockEntity")
public class StockEntity implements Serializable, AcceptsVisitors {

    @Id
    @Column(name = "uuid", nullable = false)
    @Iri(BASE_URL + "uuid")
    private String uuid;

    @Column(name = "tickerSymbol", nullable = false, unique = true)
    @Iri(BASE_URL + "tickerSymbol")
    private String tickerSymbol;

    @Column(name = "tradedIn")
    @Iri(BASE_URL + "tradedIn")
    private String tradedIn;

    @Column(name = "name", nullable = false)
    @Iri(BASE_URL + "name")
    private String name;

    @Column(name = "security")
    @Iri(BASE_URL + "security")
    private String security;

    @Column(name = "secFilings")
    @Iri(BASE_URL + "secFilings")
    private String secFilings;

    @Column(name = "gicsSector")
    @Iri(BASE_URL + "gicsSector")
    private String gicsSector;

    @Column(name = "gicsSubIndustry")
    @Iri(BASE_URL + "gicsSubIndustry")
    private String gicsSubIndustry;

    @Column(name = "address")
    @Iri(BASE_URL + "address")
    private String address;

    @Column(name = "dateFirstAdded")
    @Iri(BASE_URL + "dateFirstAdded")
    private Date dateFirstAdded;

    @Column(name = "CIK")
    @Iri(BASE_URL + "CIK")
    private String CIK;

    @Column(name = "yield")
    @Iri(BASE_URL + "yield")
    private double yield;

    @Column(name = "capital")
    @Iri(BASE_URL + "capital")
    private double capital;

    @Column(name = "weeklyHigh")
    @Iri(BASE_URL + "weeklyHigh")
    private double weeklyHigh;

    @Column(name = "bookingValue")
    @Iri(BASE_URL + "bookingvalue")
    private double bookingValue;

    @Column(name = "weeklyLow")
    @Iri(BASE_URL + "weeklyLow")
    private double weeklyLow;

    @Column(name = "share")
    @Iri(BASE_URL + "share")
    private double share;

    @Column(name = "marketPrice")
    @Iri(BASE_URL + "marketPrice")
    private double marketPrice;

    @Column(name = "EBITDA")
    @Iri(BASE_URL + "EBITDA")
    private double ebitda;

    @Column(name = "earnings")
    @Iri(BASE_URL + "earnings")
    private double earnings;

    @Column(name = "sales")
    @Iri(BASE_URL + "sales")
    private double sales;

    // eager fetch so that they can be serialized along in the hazelcast-maps
    @OrderBy("quoteDate")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "parentUUID")
    @Iri(BASE_URL + "quotes")
    private Set<StockQuote> quotes;

    public StockEntity() {
        this.uuid = UUIDService.uuidString();
        this.quotes = new HashSet<>();
    }

    public void addQuote(StockQuote quote) {
        quotes.add(quote);
    }

    public Object accept(DataVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public String getTradedIn() {
        return tradedIn;
    }

    public void setTradedIn(String tradedIn) {
        this.tradedIn = tradedIn;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getSecFilings() {
        return secFilings;
    }

    public void setSecFilings(String secFilings) {
        this.secFilings = secFilings;
    }

    public String getGicsSector() {
        return gicsSector;
    }

    public void setGicsSector(String gicsSector) {
        this.gicsSector = gicsSector;
    }

    public String getGicsSubIndustry() {
        return gicsSubIndustry;
    }

    public void setGicsSubIndustry(String gicsSubIndustry) {
        this.gicsSubIndustry = gicsSubIndustry;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateFirstAdded() {
        return dateFirstAdded;
    }

    public void setDateFirstAdded(Date dateFirstAdded) {
        this.dateFirstAdded = dateFirstAdded;
    }

    public String getCIK() {
        return CIK;
    }

    public void setCIK(String CIK) {
        this.CIK = CIK;
    }

    public double getYield() {
        return yield;
    }

    public void setYield(double yield) {
        this.yield = yield;
    }

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public double getWeeklyHigh() {
        return weeklyHigh;
    }

    public void setWeeklyHigh(double weeklyHigh) {
        this.weeklyHigh = weeklyHigh;
    }

    public double getBookingValue() {
        return bookingValue;
    }

    public void setBookingValue(double bookingValue) {
        this.bookingValue = bookingValue;
    }

    public double getWeeklyLow() {
        return weeklyLow;
    }

    public void setWeeklyLow(double weeklyLow) {
        this.weeklyLow = weeklyLow;
    }

    public double getShare() {
        return share;
    }

    public void setShare(double share) {
        this.share = share;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public double getEbitda() {
        return ebitda;
    }

    public void setEbitda(double ebitda) {
        this.ebitda = ebitda;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }

    public Set<StockQuote> getQuotes() {
        return quotes;
    }

    public void setQuotes(Set<StockQuote> quotes) {
        this.quotes = quotes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StockEntity) {
            StockEntity other = (StockEntity) obj;
            if (uuid != null && other.uuid != null) {
                return uuid.equals(other.uuid);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (uuid != null) {
            return uuid.hashCode();
        } else {
            return super.hashCode();
        }
    }
}
