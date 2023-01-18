package com.mycompany.client.apps.App.api;

import com.mycompany.client.GridUtils;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.SimpleNewPortlet;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.OperationCtx;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.common.DescOperation;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.Portlet;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 17:28
 * Стандарная операция создания исторической таблицы
 */
public class CreateEventTableH extends CreateEventTable
{
    public CreateEventTableH() {
    }

    public CreateEventTableH(int operationId, int parentOperationId, String viewName, TypeOperation type, String tableType) {
        super(operationId, parentOperationId, viewName, type, tableType);
    }

    protected ListGridWithDesc createGrid(String dataURL, String headerURL, Criteria criteria)
    {
        ListGridWithDesc gridTable = GridUtils.createGridTable(new BGridConstructor()
        {

                    protected RecordList extractData4Update(Record[] data,int[] countWasChanged)
                    {
                        ListGridWithDesc gridWithDesc = getGrid();
                        if (gridWithDesc==null)
                            return new RecordList();

                        //RecordList rl = gridWithDesc.isGrouped()?gridWithDesc.getOriginalRecordList():gridWithDesc.getDataAsRecordList();

                        RecordList rl = gridWithDesc.getCacheData();
                        if (rl==null)
                        {
                            rl = gridWithDesc.isGrouped()?gridWithDesc.getOriginalRecordList():gridWithDesc.getDataAsRecordList();
                            gridWithDesc.setCacheData(rl);
                        }

                        boolean notSearch=rl.isEmpty();
                        List<Integer> toAddList = new LinkedList<Integer>();

                        for (int i = 0; i < data.length; i++)
                        {
                            Record inRecord = data[i];

                            String id = inRecord.getAttributeAsString(TablesTypes.KEY_FNAME);
                            Integer actual = inRecord.getAttributeAsInt(TablesTypes.ACTUAL);
                            if (actual==null) actual=0;

                            int inRlIx=-1;
                            if (!notSearch)
                                inRlIx = rl.findIndex(TablesTypes.KEY_FNAME, id);

                            if (inRlIx >= 0)
                            {
                                if (actual > 0)
                                {
                                    {
                                        Record rrl = rl.get(inRlIx);
                                        Record newRect = toMyRecord2(inRecord);
                                        String[] atrs=newRect.getAttributes();
                                        for (String key : atrs)
                                        {
                                            Object value = newRect.getAttributeAsObject(key);
                                            rrl.setAttribute(key, value);
                                        }

                                    }
                                }
                                else
                                {
                                    Record rrl = rl.get(inRlIx);
                                    rrl.setAttribute(TablesTypes.ACTUAL,actual);
                                }
                            }
                            else //if (actual > 0)
                            {
                                toAddList.add(i);
                                countWasChanged[0]=1;
                            }
                        }

                        for (Integer i : toAddList)
                            rl.add(toMyRecord2(data[i]));

//        int recCnt=rl.toArray().length;
//        for (int i=0;i<recCnt;i++)
//            rl.get(i).setAttribute(TablesTypes.ORDIX,i);

                        return rl;
                    }


                },NodesHolder.gridMetaProvider, criteria, headerURL, dataURL, false, true);

        gridTable.setGroupByMaxRecords(30000);
        if (isMultiGroup!=null) gridTable.setCanMultiGroup(isMultiGroup);
        if (isMultiSort!=null) gridTable.setCanMultiSort(isMultiSort);
        return gridTable;
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateEventTableH();
    }

    public String getDataURL() {
        return "transport/hdata2";
    }
}
