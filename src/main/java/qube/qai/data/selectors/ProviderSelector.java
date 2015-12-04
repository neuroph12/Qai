package qube.qai.data.selectors;

import qube.qai.data.DataProvider;

import javax.inject.Inject;

/**
 * Created by rainbird on 12/4/15.
 */
public class ProviderSelector {

    @Inject
    private DataProvider provider;

    /**
     * a selector which employs a DataProvider to retrieve
     * required data
     */
}
