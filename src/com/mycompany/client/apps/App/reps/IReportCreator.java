package com.mycompany.client.apps.App.reps;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.cache.CacheException;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.widgets.Window;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 04.08.15
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public interface IReportCreator {


    public static interface IFilterUpdater
    {
        AdvancedCriteria updateFiler(AdvancedCriteria filter);
    }

    boolean isCompletely();

    Map<String, Integer> getKey2Number();

    Map<String, NNode2> getKey2NNode();

    IAnalisysDesc getDesc();


    void allocateHeaders(ListGridWithDesc grid, IAnalisysDesc desc) throws Exception;

    void setHeaders(Window portlet, ListGridWithDesc grid) throws CacheException;

    void setGrid(Window portlet, ListGridWithDesc grid);

    boolean isGroupClickAble(final Integer rowNum, final Integer colNum);

    boolean isCellClickAble(final Integer rowNum, final Integer colNum);

    void openEventsOnGroupClick(final Integer rowNum, final Integer colNum, IFilterUpdater updater);


    void onCellClickEvent(final Integer rowNum, final Integer colNum, IFilterUpdater updater);
}
