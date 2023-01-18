package com.mycompany.client.test.informer;

import com.smartgwt.client.widgets.viewer.DetailViewerField;

/**
 * Created by Anton.Pozdnev on 05.08.2015.
 */
public interface IInformerCli {


    void setRoadFieldName(String road);

    void setEventFieldName(String event);

    void setExcludedFieldsArray(String[] excluded);

    void setFields(DetailViewerField[] fields);

    // void onExpand(int width,int height);
    // void onCollapse(int width, int height);
    void setParamsNum(String id, int paramsNum);

    void setEventTitle(String id, String longEventName, String shortEventName);

    void setRoadTitle(String id, String longRoadTitle, String shortRoadTitle);

    void setOverAllStatus(String id, int status);

    void setParamValueAndStatus(String id, int paramNum, String paramValue, int status);

    int getInitialHeight(String id);

    int getInitialWidth(String id);
}
