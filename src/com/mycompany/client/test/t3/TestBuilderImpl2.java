package com.mycompany.client.test.t3;

import com.mycompany.client.GridUtils;
import com.mycompany.client.test.Demo.MyRecord;
import com.mycompany.client.test.Demo.DemoApp01;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.data.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.09.14
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */


public class TestBuilderImpl2 implements TestBuilder
{
    public MyRecord toMyRecord(Record record)
    {
        Map recordProperties = record.toMap();
        Object v = recordProperties.remove(TablesTypes.ROW_STYLE);
        MyRecord myRecord = new MyRecord();
        for (Object o : recordProperties.keySet()) {
            Object value = recordProperties.get(o);


            if (value instanceof Map)
            {
                Map value1 = (Map) value;
                myRecord.setAttribute((String) o, value1.get("link"));
                myRecord.setLinkText((String) value1.get("linkText"));
            } else
                myRecord.setAttribute((String) o, value);
            //myRecord.setLinkText();

//            grid.getField()
//            if (record.getAttribute())
//            myRecord.setLinkText();
        }

        if (v != null)
            myRecord.setRowStyle((String) v);
        return myRecord;
    }


    @Override
    public void setComponents(Layout mainLayout)
    {
        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();


        final String dataURL = "transport/tdata2";
        final String headerURL = "theadDesc.jsp";

        {
            Portlet portlet = new Portlet();

            final DataSource ds=new DataSource();
            ds.setClientOnly(true);

            final Criteria criteria =
            new AdvancedCriteria(OperatorId.AND, new Criterion[]
                    {
                            new Criterion("o_serv", OperatorId.EQUALS, "Э"),
                    });


            BGridConstructor gridConstructor = new BGridConstructor()
            {
                public void setHeaderGrid(Record gridOptions) throws SetGridException
                {
                    setOptions(gridOptions);

                    ListGridField[] fields = extractFields(gridOptions);

                    for (ListGridField field : fields)
                        ds.addField(new DataSourceTextField(field.getName()));
                    getGrid().setFields(fields);
                    getGrid().setShowAllRecords(false);
                }

                public void updateDataGrid(Record[] data, boolean resetAll) throws SetGridException
                {
                    int[] countWasChanged=new int[]{0};
                    RecordList rl = extractData4Update(data,countWasChanged);
                    if (rl!=null)
                    {
                        ds.setCacheData(rl.toArray());
                        ds.fetchData(criteria, new DSCallback() {
                            @Override
                            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                                getGrid().setData(dsResponse.getData());
                            }
                        });
                    }
                }

                protected RecordList extractData4Update(Record[] data,int[] countWasChanged)
                {
                    RecordList rl = new RecordList(ds.getCacheData());


                    List<Integer> toAddList = new LinkedList<Integer>();

                    for (int i = 0; i < data.length; i++)
                    {
                        Record record = data[i];

                        String id = record.getAttributeAsString(TablesTypes.KEY_FNAME);
                        Integer actual = record.getAttributeAsInt(TablesTypes.ACTUAL);
                        if (actual==null) actual=0;

                        int inRlIx = rl.findIndex(TablesTypes.KEY_FNAME, id);

                        if (inRlIx >= 0)
                        {
                            if (actual > 0)
                            {
//                    if (countWasChanged[0]==0)
                                {
                                    MyRecord rrl = (MyRecord)rl.get(inRlIx);

                                    MyRecord newRect = toMyRecord(record);
                                    Map mp=newRect.toMap();

                                    for (Object key : mp.keySet())
                                    {
                                        Object value = mp.get(key);

//                            if ("3".equals(key)) //Тест ощибочных изменений в результате рек. сета
//                            {
//                                Object v1=rrl.getAttribute((String)key);
//                                if ((v1==null && value!=null) || !v1.equals(value))
//                                    System.out.println("was value = " + v1+" value; "+value);
//                            }


                                        rrl.setAttribute((String) key, value);
                                    }
                                    rrl.setRowStyle(newRect.getRowStyle());
                                    rrl.setLinkText(newRect.getLinkText());
                                }
//                    else
//                       rl.set(inRlIx, toMyRecord(record));
                            }
                            else
                            {
                                rl.removeAt(inRlIx);
                                countWasChanged[0]=1;
                            }
                        }
                        else if (actual > 0)
                        {
                            toAddList.add(i);
                            countWasChanged[0]=1;
                        }
                    }

                    for (Integer i : toAddList)
                        rl.add(toMyRecord(data[i]));

                    int recCnt=rl.toArray().length;
                    for (int i=0;i<recCnt;i++)
                        rl.get(i).setAttribute(TablesTypes.ORDIX,i);

                    return rl;
                }



            };

            final ListGrid windows = GridUtils.createGridTable(gridConstructor, DemoApp01.gridMetaProvider, TablesTypes.WINDOWS, headerURL, dataURL, true, false);
            windows.setWidth100();
            windows.setHeight100();

            portlet.addItem(windows);
            portalLayout.addPortlet(portlet);
        }

        mainLayout.addMembers(portalLayout);

    }
}
