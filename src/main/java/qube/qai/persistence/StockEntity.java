package qube.qai.persistence;

import qube.qai.data.AcceptsVisitors;
import qube.qai.data.DataVisitor;
import qube.qai.services.implementation.UUIDService;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by rainbird on 11/19/15.
 */
@Entity
public class StockEntity implements Serializable, AcceptsVisitors {

//    @EmbeddedId
//    private StockEntityId id;

    @Id
    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "tickerSymbol", nullable = false)
    private String tickerSymbol;

    @Column(name = "tradedIn", nullable = false)
    private String tradedIn;

    @Column(name = "name")
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

    public StockEntity() {
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
        if(uuid != null) {
            return uuid.hashCode();
        } else {
            return super.hashCode();
        }
    }
}
