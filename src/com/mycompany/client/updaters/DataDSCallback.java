package com.mycompany.client.updaters;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.mycompany.client.operations.ICliFilter;
import com.mycompany.client.utils.DSRegisterImpl;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 13.03.15
 * Time: 12:17
 * Апдейтер данных
 */
abstract public class DataDSCallback extends MyDSCallback
{
    public DataDSCallback(int period)
    {
        super(period);
    }


//    public static com.smartgwt.client.data.Record[] arrayOfRecord(JavaScriptObject nativeArray) {
//        if (nativeArray == null) {
//            return new com.smartgwt.client.data.Record[]{};
//        }
//
//		if (JSOHelper.isArray(nativeArray)==false && com.smartgwt.client.data.ResultSet.isResultSet(nativeArray)==false) {
//			com.smartgwt.client.data.Record[] ret = new com.smartgwt.client.data.Record[1];
//            ret[0] = (nativeArray == null ? null : new com.smartgwt.client.data.Record(nativeArray));
//	        return ret;
//		}
//
//        JavaScriptObject[] components = JSOHelper.toArray(nativeArray);
//        com.smartgwt.client.data.Record[] objects = new com.smartgwt.client.data.Record[components.length];
//
//        for (int i = 0; i < components.length; i++) {
//            com.smartgwt.client.data.Record obj = (com.smartgwt.client.data.Record) RefDataClass.getRef(components[i]);
//            if (obj == null) {
//                obj = (components[i] == null ? null : new MyRecord(components[i]));
//            }
//
//            objects[i] = obj;
//        }
//        return objects;
//    }

    public void execute(DSResponse response, Object rawData, DSRequest request)
    {
            try {

                int status=response.getStatus();//getErrors();

                if (status!= RPCResponse.STATUS_SUCCESS)
                {
                    SC.say("ERROR DATA GETTING: " + status + " " + response.getAttribute("httpResponseText"));
                    return;
                }


                JavaScriptObject jsObject = (JavaScriptObject) response.getAttributeAsObject(DSRegisterImpl.TRANS);
                Object[] objs= JSOHelper.convertToArray(jsObject);


                Record data1=new Record((Map)objs[0]);

                this.timeStamp = data1.getAttributeAsLong("updateStamp");
                this.timeStampN = data1.getAttributeAsInt("updateStampN");
                this.lastTimeStamp = data1.getAttributeAsLong("lastUpdateStamp");
                Integer cliCnt=data1.getAttributeAsInt("cliCnt");
                this.tblId=data1.getAttributeAsString(TablesTypes.TBLID);


//                Record[] data = arrayOfRecord(response.getAttributeAsJavaScriptObject("data"));


                Record[] data = response.getData();

//TODO                    this.timeStamp = data[0].getAttributeAsLong("updateStamp");
//TODO                    this.timeStampN = data[0].getAttributeAsInt("updateStampN");
//TODO                    Integer cliCnt=data[0].getAttributeAsInt("cliCnt");
//TODO                    this.tblId=data[0].getAttributeAsString(TablesTypes.TBLID);

//TODO                    gridConstructor.setDiagramDesc(data[0].getAttributeAsMap("desc"));
//TODO                    data = data[0].getAttributeAsRecordArray("tuples");

//                    dst.setCacheData(data);
//                    dst.fetchData(new Criteria(),new DSCallback() {
//                        @Override
//                        public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
//                            Record[] rec = dsResponse.getData();
//                            for (Record record : rec) {
//                                record.toMap();
//                            }
//                        }
//                    });


                data = applyCliFilters(data);

                boolean resetAll = false;
                if (cliCnt!=null)
                {
                    resetAll=(cliCnt-this.cliCnt <=0);
                    this.cliCnt =cliCnt;
                }

                updateData(data, resetAll);


                Timer timer1 = getTimer();
                if (timer1 != null)
                    timer1.schedule(this.period);
            }
            catch (SetGridException e) {
                e.printStackTrace(); //TODO Что делать на это исключение
            }
            catch (Exception e)
            {
                e.printStackTrace(); //TODO Что делать на это исключение
            }
    }

    protected Record[] applyCliFilters(Record[] data)
    {
        filters=getFilters();
        if (filters!=null)
            for (ICliFilter filter : filters)
                data=filter.filter(data);
        return data;
    }

    abstract protected void updateData(Record[] data, boolean resetAll) throws SetGridException;
//    {
//        gridConstructor.updateDataGrid(data, resetAll);
//    }
}
