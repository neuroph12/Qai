package qube.qai.persistence;

import qube.qai.data.AcceptsVisitors;
import qube.qai.data.DataVisitor;
import qube.qai.services.implementation.UUIDService;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by rainbird on 11/19/15.
 */
@Entity
public class StockEntity implements Serializable, AcceptsVisitors {

    @Id
    private String idKey;

    private String name;

    private String tickerSymbol;

    private String security;

    private String secFilings;

    private String gicsSector;

    private String gicsSubIndustry;

    private String address;

    private Date dateFirstAdded;

    private String CIK;

    private String tradedIn;

    public StockEntity() {
        this.idKey = UUIDService.uuidString();
    }

    public StockEntity(String name,
                       String tickerSymbol,
                       String security,
                       String secFilings,
                       String gicsSector,
                       String gicsSubIndustry,
                       String address,
                       Date dateFirstAdded) {
        this();
        this.name = name;
        this.tickerSymbol = tickerSymbol;
        this.security = security;
        this.secFilings = secFilings;
        this.gicsSector = gicsSector;
        this.gicsSubIndustry = gicsSubIndustry;
        this.address = address;
        this.dateFirstAdded = dateFirstAdded;
    }

    public Object accept(DataVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
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

    public String getTradedIn() {
        return tradedIn;
    }

    public void setTradedIn(String tradedIn) {
        this.tradedIn = tradedIn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StockEntity) {
            StockEntity other = (StockEntity) obj;
            return idKey.equals(other.idKey);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return idKey.hashCode();
    }
}
