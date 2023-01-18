package com.mwlib.tablo.analit2.oindex;

import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.analit2.UtilsData;
import com.mwlib.tablo.EventUtils;
import com.mwlib.tablo.analit2.NNodeBuilder;
import com.mwlib.tablo.db.desc.DelayGIDTDesc;
import com.smartgwt.client.types.ListGridFieldType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.08.2015
 * Time: 16:54
 * Описатель данных оперативнх показателей
 */
public class OIndxXML
{


    public static final String xml="<?xml version='1.0' encoding='UTF-8' ?>\n" +
            "<project>\n" +
            "<tuple>\n" +
            "\n" +
            "\t<col tid='DOR_KOD' title='ИД Дороги' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='VID_ID' title='ИД службы' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='PRED_ID' title='ИД' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\n" +
            "\t<col tid='DOR_NAME' title='Дорога'  hide='true'/>\n" +
            "\t<col tid='PRED_NAME' title='Служба/предприятие'/>\n" +
            "\t<col tid='VID_NAME' title='Служба'  hide='true'/>\n" +
            "\n" +
            "\t<col tid='CNT' title='Кол. шт.' ftype='"+ ListGridFieldType.INTEGER.getValue()+"' zval=''/>\n" + //Тип ожидаемый в этом поле
            "</tuple>\n" +


            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.REFUSES+"' title='ОТС 1,2,3 кат' " +
            "filter='{" +
            "    \"_constructor\":\"AdvancedCriteria\", \n" +
            "    \"operator\":\"and\", \n" +
            "    \"criteria\":[\n" +
            "       {\n" +
            "            \"fieldName\":\"ND\", \n" +
            "            \"operator\":\"greaterOrEqual\", \n" +
            "            \"value\":{\n" +
            "                \"_constructor\":\"RelativeDate\", \n" +
            "                \"value\":\"$today\"\n" +
            "            }\n" +
            "        }\n" +
            "    ]" +
            "}'" +
            ">\n" +
            "\t<NVAL colid='CNT'  colN='2'/>\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.VIOLATIONS+"' title='ТН' " +
            "filter='{" +
            "    \"_constructor\":\"AdvancedCriteria\", \n" +
            "    \"operator\":\"and\", \n" +
            "    \"criteria\":[\n" +
            "       {\n" +
            "            \"fieldName\":\"ND\", \n" +
            "            \"operator\":\"greaterOrEqual\", \n" +
            "            \"value\":{\n" +
            "                \"_constructor\":\"RelativeDate\", \n" +
            "                \"value\":\"$today\"\n" +
            "            }\n" +
            "        }\n" +
            "    ]" +
            "}'" +
            ">\n" +
            "\t<NVAL colid='CNT'/>\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.DELAYS_ABVGD+"' title='Задержки поездов'>\n" +

            "\t\t<NNode colid='A' val='"+ DelayGIDTDesc.PASS+"' title='Пас.' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":62 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='"+ DelayGIDTDesc.REG+"' title='Пр.' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":63 \n" +
            "}'" +
            ">\n" +
             "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode>\n" +
            "\t\t<NNode colid='A' val='"+ DelayGIDTDesc.CRG+"' title='Гр.' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":64 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode>\n" +

            "</NNode>\n" +                                                                    //  notNull



            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.ZMTABLE+"' title='Просроченные&lt;br&gt;зам. и инц' \n" +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":85 \n" +
            "}'" +
            ">\n" +
            "\t<NVAL colid='CNT'/>\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.WARNINGS+"' title='Пред.&lt;br&gt; &lt;=25' " +

            "filter='{" +
            "    \"_constructor\":\"AdvancedCriteria\", \n" +
            "    \"operator\":\"and\", \n" +
            "    \"criteria\":[\n" +
            "        {\n" +
            "            \"fieldName\":\"TIMESTAMP_FIELD\", \n" +
            "            \"operator\":\"greaterOrEqualField\", \n" +
            "            \"value\":\"TIM_BEG\"\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"TIMESTAMP_FIELD\", \n" +
            "            \"operator\":\"lessOrEqualField\", \n" +
            "            \"value\":\"TIM_OTM\"\n" +
            "        },\n" +
            "        {" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":25\n" +
            "       }" +
            "    ]" +
            "}'" +

            ">\n" +
            "\t<NVAL colid='CNT'/>\n" +
            "</NNode >\n" +


            "<grpX colN='1'>\n" +
            "\t<fld tid='DOR_KOD' tColId='DOR_NAME'>\n" +
                "\t\t<fld tid='VID_ID' tColId='VID_NAME'>\n" +
                    "\t\t<fld tid='PRED_ID' tColId='PRED_NAME'/>\n" +
                "\t</fld>\n" +
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

        Map<String, Integer> key2Number = new HashMap<String, Integer>();
        UtilsData.getKey2key2Number(nodes,"", key2Number,0);
        Map<Integer, String> number2Key = UtilsData.number2Key(key2Number);


        Map<String,Object> testTuple=new HashMap<String,Object>();

        testTuple.put("PRED_ID",1);
        testTuple.put("PRED_NAME", "ПЧ-1");

        testTuple.put("EVENT_TYPE", "WR");
        testTuple.put("EVENT_NAME", "Предупреждения");
        testTuple.put("A", "REAL");

        testTuple.put("CNT", 5);
        testTuple.put("LN", 100);

        HashMap<String, Object> resTuple = new HashMap<String, Object>();
        UtilsData.conVertMapByNode("",nodes,testTuple, resTuple,key2Number, new LinkedList<String>());

        for (String ix : resTuple.keySet())
        {
            System.out.println("ix = " + ix);
        }

        String res=EventUtils.toJson(nodes);
        System.out.println("res = " + res);
    }

}
