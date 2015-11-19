package qube.qai.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rainbird on 11/19/15.
 */
public class StockEntity implements Serializable {

    private String uuidString;

    private String tickerSymbol;

    private String security;

    private String secFilings;

    private String gicsSector;

    private String gicsSubIndustry;

    private String address;

    private Date dateFirstAdded;
}
