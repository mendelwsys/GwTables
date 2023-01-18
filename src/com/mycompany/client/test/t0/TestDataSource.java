package com.mycompany.client.test.t0;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.utils.JScriptUtils;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 12.09.14
 * Time: 17:24
 * Тестовый источник данных
 */
public class TestDataSource extends DataSource
{
    public boolean isForHead() {
        return forHead;
    }

    public void setForHead(boolean forHead) {
        this.forHead = forHead;
    }

    private boolean forHead=true;

    public TestDataSource() {
    }

    public TestDataSource(JavaScriptObject jsObj) {
        super(jsObj);
    }

    public TestDataSource(String dataURL) {
        super(dataURL);
    }

    public void fetchData(Criteria criteria, DSCallback callback, DSRequest requestProperties)
    {
        String tableType = criteria.getAttribute(TablesTypes.TTYPE);
        String[] head2data=TestData.data4Tests.get(tableType);
        if (head2data!=null)
        {
            String rawData = forHead ? head2data[0] : head2data[1];
            callback.execute(JScriptUtils.getDSResponse(rawData),rawData,null);
        }
    }
}
