package com.mycompany.client.apps.App;

import com.mycompany.client.apps.App.api.DorOperationFactory;
import com.mycompany.client.apps.OperationNode;
import com.mycompany.client.operations.IOperation;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 11.12.14
 * Time: 17:25
 * TODO  Все это должно тянуться с сервера
 */

public class NSI
{

    public static final String DOR_KOD = "DOR_KOD";
    public static final String DOR_NAME_S = "DOR_NAME_S";
    public static final String DOR_NAME = "DOR_NAME";

    public static List<OperationNode> createAddDataNode(int ParentId)
    {
        List<OperationNode> operationNodes= new LinkedList<OperationNode>();

        List<IOperation> operations = createAddDataOperation(ParentId);
        for (IOperation operation : operations) {
            operationNodes.add(new OperationNode(operation));
        }
        return operationNodes;
    }

    public static List<IOperation> createAddDataOperation(int ParentId)
    {
        List<IOperation> operations= new LinkedList<IOperation>();
        DataSource ds = getWayNSI();

        Record recAll=null;
//        final List<Pair<String,String>> allDors=new LinkedList<Pair<String,String>>();

        Record[] recs = ds.getCacheData();
        for (Record rec : recs)
        {
            final Integer dor_kod = rec.getAttributeAsInt(DOR_KOD);

            if (dor_kod>0)
            {
                operations.add(new DorOperationFactory(ParentId*100+ dor_kod, ParentId, rec.getAttribute(DOR_NAME))
                {
                    {
                        //this.getParams().add(new Pair<String,String>(DOR_KOD,dor_kod.toString()));
                        this.getParams().add(new Criterion(DOR_KOD,OperatorId.EQUALS,dor_kod.toString()));
                    }
                });
//                allDors.add(new Pair<String,String>(DOR_KOD,dor_kod.toString()));
            }
            else
                recAll=rec;
        }

        if (recAll!=null)
        {
            operations.add(new DorOperationFactory(ParentId*100, ParentId, recAll.getAttribute(DOR_NAME))
            {
                {
//                    this.getParams().addAll(allDors);
                    //this.getParams().add(new Pair<String, String>("1", "1"));
                    this.getParams().add(new Criterion(DOR_KOD,OperatorId.NOT_NULL));
                }
            });
        }

        return operations;
    }

    public static void fillMapByRoad(Map<Integer,String> typeMap)
    {
        DataSource ways = NSI.getWayNSI();
        Record[] recs = ways.getCacheData();
        for (Record rec : recs)
            typeMap.put(rec.getAttributeAsInt(NSI.DOR_KOD),rec.getAttribute(NSI.DOR_NAME));
        typeMap.remove(-1);
    }

    public static void fillMapByRoadEx(Map<Integer,String> typeMap)
    {
        DataSource ways = NSI.getWayNSI();
        Record[] recs = ways.getCacheData();
        for (Record rec : recs)
            typeMap.put(rec.getAttributeAsInt(NSI.DOR_KOD),rec.getAttribute(NSI.DOR_NAME));
    }


    static DataSource ways;
    public static DataSource getWayNSI()
    {
        if (ways!=null)
            return ways;
        ways = new DataSource();
        ways.setClientOnly(true);
        ways.setCacheAllData(true);
        DataSourceField dor_kod = new DataSourceField(DOR_KOD, FieldType.INTEGER);
        dor_kod.setValidOperators(OperatorId.EQUALS);
        ways.addField(dor_kod);

        DataSourceField dor_name = new DataSourceField(DOR_NAME_S, FieldType.TEXT);
        dor_name.setValidOperators();
        ways.addField(dor_name);

        DataSourceField dor_name_l = new DataSourceField(DOR_NAME, FieldType.TEXT);
        dor_name.setValidOperators();
        ways.addField(dor_name_l);

        List<ListGridRecord> recordList = new LinkedList<ListGridRecord>();

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,1);
            newRecord.setAttribute(DOR_NAME_S,"Окт");
            newRecord.setAttribute(DOR_NAME,"Октябрьская");
            recordList.add(newRecord);
        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,10);
            newRecord.setAttribute(DOR_NAME_S,"Клг");
            newRecord.setAttribute(DOR_NAME,"Калининградская");
            recordList.add(newRecord);
        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,17);
            newRecord.setAttribute(DOR_NAME_S,"Мск");
            newRecord.setAttribute(DOR_NAME,"Московская");
            recordList.add(newRecord);
        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,24);
            newRecord.setAttribute(DOR_NAME_S,"Гор");
            newRecord.setAttribute(DOR_NAME,"Горьковская");
            recordList.add(newRecord);
        }


        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,28);
            newRecord.setAttribute(DOR_NAME_S,"Сев");
            newRecord.setAttribute(DOR_NAME,"Северная");
            recordList.add(newRecord);
        }


        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,51);
            newRecord.setAttribute(DOR_NAME_S,"Скв");
            newRecord.setAttribute(DOR_NAME,"Северо-Кавказская");
            recordList.add(newRecord);
        }


        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,58);
            newRecord.setAttribute(DOR_NAME_S,"Ювс");
            newRecord.setAttribute(DOR_NAME,"Юго-Восточная");
            recordList.add(newRecord);
        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,61);
            newRecord.setAttribute(DOR_NAME_S,"Прв");
            newRecord.setAttribute(DOR_NAME,"Приволжская");
            recordList.add(newRecord);

        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,63);
            newRecord.setAttribute(DOR_NAME_S,"Кбш");
            newRecord.setAttribute(DOR_NAME,"Куйбышевская");
            recordList.add(newRecord);

        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,76);
            newRecord.setAttribute(DOR_NAME_S,"Свр");

            newRecord.setAttribute(DOR_NAME,"Свердловская");
            recordList.add(newRecord);

        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,80);
            newRecord.setAttribute(DOR_NAME_S,"Юур");

            newRecord.setAttribute(DOR_NAME,"Южно-Уральская");
            recordList.add(newRecord);

        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,83);
            newRecord.setAttribute(DOR_NAME_S,"Зсб");

            newRecord.setAttribute(DOR_NAME,"Западно-Сибирская");
            recordList.add(newRecord);

        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,88);
            newRecord.setAttribute(DOR_NAME_S,"Крс");
            newRecord.setAttribute(DOR_NAME,"Красноярская");
            recordList.add(newRecord);

        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,92);
            newRecord.setAttribute(DOR_NAME_S,"Всб");
            newRecord.setAttribute(DOR_NAME,"Восточно-Сибирская");
            recordList.add(newRecord);

        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,94);
            newRecord.setAttribute(DOR_NAME_S,"Заб");
            newRecord.setAttribute(DOR_NAME,"Забайкальская");
            recordList.add(newRecord);

        }

        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,96);
            newRecord.setAttribute(DOR_NAME_S,"Двс");
            newRecord.setAttribute(DOR_NAME,"Дальневосточная");
            recordList.add(newRecord);
        }


        {
            ListGridRecord newRecord = new ListGridRecord();
            newRecord.setAttribute(DOR_KOD,-1);
            newRecord.setAttribute(DOR_NAME_S,"Все");
            newRecord.setAttribute(DOR_NAME,"Все дороги");
            recordList.add(newRecord);
        }


        ways.setCacheData(recordList.toArray(new ListGridRecord[recordList.size()]));
        return ways;
    }

}
