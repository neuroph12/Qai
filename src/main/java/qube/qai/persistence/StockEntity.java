package qube.qai.persistence;

import org.apache.commons.lang3.builder.EqualsBuilder;
import qube.qai.services.implementation.UUIDService;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by rainbird on 11/19/15.
 */
@Entity
public class StockEntity implements Serializable {

    @Id
    private String uuid;

    private String name;

    private String tickerSymbol;

    private String security;

    private String secFilings;

    private String gicsSector;

    private String gicsSubIndustry;

    private String address;

    private Date dateFirstAdded;

    public StockEntity() {
        this.uuid = UUIDService.uuidString();
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StockEntity) {
            StockEntity other = (StockEntity) obj;
            return uuid.equals(other.uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
