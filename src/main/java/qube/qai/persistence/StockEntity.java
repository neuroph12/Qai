/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
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

import qube.qai.data.AcceptsVisitors;
import qube.qai.data.DataVisitor;
import qube.qai.services.implementation.UUIDService;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rainbird on 11/19/15.
 */
@Entity
public class StockEntity implements Serializable, AcceptsVisitors {

    @Id
    @thewebsemantic.Id
    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "tickerSymbol")
    private String tickerSymbol;

    @Column(name = "tradedIn")
    private String tradedIn;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "security")
    private String security;

    @Column(name = "secFilings")
    private String secFilings;

    @Column(name = "gicsSector")
    private String gicsSector;

    @Column(name = "gicsSubIndustry")
    private String gicsSubIndustry;

    @Column(name = "address")
    private String address;

    @Column(name = "dateFirstAdded")
    private Date dateFirstAdded;

    @Column(name = "CIK")
    private String CIK;

    @Column(name = "yield")
    private double yield;

    @Column(name = "capital")
    private double capital;

    @Column(name = "weeklyHigh")
    private double weeklyHigh;

    @Column(name = "bookingValue")
    private double bookingValue;

    @Column(name = "weeklyLow")
    private double weeklyLow;

    @Column(name = "share")
    private double share;

    @Column(name = "marketPrice")
    private double marketPrice;

    @Column(name = "EBITDA")
    private double ebitda;

    @Column(name = "earnings")
    private double earnings;

    @Column(name = "sales")
    private double sales;

    // eager fetch so that they can be serialized along in the hazelcast-maps
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tickerSymbol", fetch = FetchType.EAGER)
    @OrderBy("quoteDate")
    private Set<StockQuote> quotes;

    public StockEntity() {
        this.uuid = UUIDService.uuidString();
        this.quotes = new HashSet<>();
    }

    public void addQuote(StockQuote quote) {
        quote.setParentUuid(uuid);
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
