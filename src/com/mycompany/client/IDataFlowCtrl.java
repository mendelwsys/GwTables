package com.mycompany.client;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 28.11.14
 * Time: 13:34
 * Интерфейс для управления подкачкой данных
 */
public interface IDataFlowCtrl
{
    int DEF_DELAY_DATA_MILLIS = 2000;
    int DEF_DELAY_HEADER_MILLIS = 0;

    Criteria getCriteria();

    void setCriteria(Criteria criteria);

    int getPeriod();

    void setPeriod(int period);

    void stopUpdateData();

    void startUpdateData(boolean dynamicUpdate, DSCallback afterDataUpdate, int delayMillis);

    void startUpdateMeta(DSCallback afterHeaderUpdate, int delayMillis);

    void updateMetaAndData(boolean dynamicUpdate, boolean noData);

    void updateMetaAndData(boolean dynamicUpdate, boolean noData, DSCallback afterDataUpdate, DSCallback afterHeaderUpdate);

    void updateMetaAndData(boolean dynamicUpdate, int delayHeaderMillis, boolean noData, DSCallback afterDataUpdate, DSCallback afterHeaderUpdate, int delayDataMillis);

    void updateMeta(DSCallback afterHeaderUpdate);

    void updateData();

    void startUpdateData(boolean dynamicUpdate);

    void startUpdateData(boolean dynamicUpdate, DSCallback afterDataUpdate);

    void setFullDataUpdate();

    void removeAfterUpdater(DSCallback updater);

    void addAfterUpdater(DSCallback updater);

    boolean isTimer();
}
