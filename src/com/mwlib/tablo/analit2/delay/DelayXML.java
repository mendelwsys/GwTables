package com.mwlib.tablo.analit2.delay;

import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.analit2.UtilsData;
import com.mwlib.tablo.analit2.NNodeBuilder;
import com.smartgwt.client.types.ListGridFieldType;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 23.10.14
 * Time: 12:53
 * Описатель данных табдицы задержек поездов
 */
public class DelayXML
{
    public static final String xml="<?xml version='1.0' encoding='UTF-8' ?>\n" +
            "<project>\n" +
            "<tuple>\n" +
            "\n" +
            "\t<col tid='DEL_ID' title='ИД' hide='true' ftype='"+ ListGridFieldType.TEXT.getValue()+"'/>\n" +
            "\t<col tid='HOZ_ID' title='ИД хозяйства' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='SYS_TYPE' title='ИД Типа события' hide='true'/>\n" +
            "\t<col tid='DATATYPE_ID' title='Ид типа поезда'  hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='EvName' title='Тип События'/>\n" +
            "\t<col tid='TRTYPE' title='Поезд'/>\n" +
            "\t<col tid='SNM' title='Имя Службы'  hide='true'/>\n" +
            "\t<col tid='CNT' title='Кол. шт.' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" + //Тип ожидаемый в этом поле
            "</tuple>\n" +


            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.DELAYS_GID+"' title='Задержки поездов'>\n" +
            "\t\t<NNode colid='A' val='ДИ' title='ДИ'>\n" +
             "\t\t\t<NVAL colid='CNT' colN='2'/>\n" +
            "\t\t</NNode>\n" +
            "\t\t<NNode colid='A' val='В' title='B'>\n" +
             "\t\t\t<NVAL colid='CNT'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.VID_NAME+"\", \n" +
            "                    \"value\":\"В\" \n" +
            "}'" +
            "/>\n" +
            "\t\t</NNode>\n" +
            "\t\t<NNode colid='A' val='П' title='П'>\n" +
            "\t\t\t<NVAL colid='CNT'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.VID_NAME+"\", \n" +
            "                    \"value\":\"П\" \n" +
            "}'" +
            "/>\n" +
            "\t\t</NNode>\n" +
            "\t\t<NNode colid='A' val='Ш' title='Ш'>\n" +
            "\t\t\t<NVAL colid='CNT'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.VID_NAME+"\", \n" +
            "                    \"value\":\"Ш\" \n" +
            "}'" +
            "/>\n" +
            "\t\t</NNode>\n" +
            "\t\t<NNode colid='A' val='Э' title='Э'>\n" +
            "\t\t\t<NVAL colid='CNT'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.VID_NAME+"\", \n" +
            "                    \"value\":\"Э\" \n" +
            "}'" +
            "/>\n" +
            "\t\t</NNode>\n" +
//            "\t\t<NNode colid='A' val='ДПМ' title='ДПМ'>\n" +
//            "\t\t\t<NVAL colid='CNT'/>\n" +
//            "\t\t</NNode>\n" +
            "</NNode>\n" +

            "<grpX colN='1'>\n" +
                "\t\t<fld tid='SYS_TYPE' tColId='EvName'>\n" +
                    "\t\t<fld tid='DEL_ID' tColId='TRTYPE'/>\n" +
                "\t</fld>\n" +
            "</grpX>\n" +

            "</project>";


    public static IAnalisysDesc testHeaders()  throws Exception
    {
         return new NNodeBuilder().xml2Desc(xml);
    }

    public static void main(String[] args) throws Exception
    {
        IAnalisysDesc desc=new NNodeBuilder().xml2Desc(xml);

        NNode2[] nodes1 = desc.getNodes();

        NNode2[] nodes = UtilsData.removeEmptyNodes(nodes1);
//
//        Map<String, Integer> key2Number = new HashMap<String, Integer>();
//        UtilsData.getKey2key2Number(nodes,"", key2Number,0);
//        Map<Integer, String> number2Key = UtilsData.number2Key(key2Number);
//
//
//        Map<String,Object> testTuple=new HashMap<String,Object>();
//
//        testTuple.put("PRED_ID",1);
//        testTuple.put("PRED_NAME", "ПЧ-1");
//
//        testTuple.put("EVENT_TYPE", "WR");
//        testTuple.put("EVENT_NAME", "Предупреждения");
//        testTuple.put("A", "REAL");
//
//        testTuple.put("CNT", 5);
//        testTuple.put("LN", 100);
//
//        HashMap<String, Object> resTuple = new HashMap<String, Object>();
//        UtilsData.conVertMapByNode("",nodes,testTuple, resTuple,key2Number, new LinkedList<String>());
//
//        for (String ix : resTuple.keySet())
//        {
//            System.out.println("ix = " + ix);
//        }
//
//        String res=EventUtils.toJson(nodes);
//        System.out.println("res = " + res);
    }

}
