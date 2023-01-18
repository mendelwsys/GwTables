package com.mwlib.tablo.analit2.warnagr;

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
public class WarnAGRXML
{
    public static final String xml="<?xml version='1.0' encoding='UTF-8' ?>\n" +
            "<project>\n" +
            "<tuple>\n" +
            "\n" +
            "\t<col tid='DOR_KOD' title='ИД Дороги' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='DOR_NAME' title='Дорога' />\n" +
            "\t<col tid='CNT' title='шт.' ftype='"+ ListGridFieldType.INTEGER.getValue()+"' zval='0' nval='0'/>\n" + //Тип ожидаемый в этом поле
            "\t<col tid='TLN' title='км.' ftype='"+ListGridFieldType.FLOAT.getValue()+"' format='###0.0' zval='0' />\n" + //Тип ожидаемый в этом поле и формат данных
            "</tuple>\n" +

            "\t\t<NNode colid='EVENT_TYPE' val='"+TablesTypes.WARNINGSINTIME+"#ALL'  title='Заложено&lt;br&gt;графиком' tblName='"+TablesTypes.WARNINGSINTIME+"' noDrill='true'>\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.' colN='2'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.' />\n" +
            "\t\t</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.WARNINGS+"#ALL' title='Действует&lt;br&gt;всего' tblName='"+ TablesTypes.WARNINGS+"' " +
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
            "        }\n" +
            "    ]" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "</NNode>\n" +

            "\t\t<NNode colid='EVENT_TYPE' val='GvsF'  title='+/- к графику' noDrill='true'>\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.' />\n" +
            "\t\t</NNode >\n" +


            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.WARNINGS+"' title='В том числе со скоростью'>\n" +

            "\t\t<NNode title='15' >" +
            "\t\t\t<NNode colid='A' val='G15' title='граф.' tblName='"+TablesTypes.WARNINGSINTIME+"' noDrill='true'>\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t\t<NNode colid='A' val='F15' title='факт.' " +
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
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":15\n" +
            "        } \n" +
            "    ]" +
            "}" +
            "'>\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +
            "\t\t</NNode >\n" +



            "\t\t<NNode title='25' >" +
            "\t\t\t<NNode colid='A' val='G25' title='граф.' tblName='"+TablesTypes.WARNINGSINTIME+"' noDrill='true'>\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t\t<NNode colid='A' val='F25' title='факт.' " +
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
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":25\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"greaterThan\", \n" +
            "            \"value\":15\n" +
            "        }\n" +
            "    ]" +
            "}'" +
            ">\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t</NNode >\n" +



            "\t\t<NNode title='40' >" +
            "\t\t\t<NNode colid='A' val='G40' title='граф.' tblName='"+TablesTypes.WARNINGSINTIME+"' noDrill='true'>\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t\t<NNode colid='A' val='F40' title='факт.' " +
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
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":40\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"greaterThan\", \n" +
            "            \"value\":25\n" +
            "        }\n" +
            "    ]" +
            "}'" +
            ">\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t</NNode >\n" +


            "\t\t<NNode title='50' >" +
            "\t\t\t<NNode colid='A' val='G50' title='граф.' tblName='"+TablesTypes.WARNINGSINTIME+"' noDrill='true'>\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t\t<NNode colid='A' val='F50' title='факт.' " +
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
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":50\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"greaterThan\", \n" +
            "            \"value\":40\n" +
            "        }\n" +
            "    ]" +
            "}'" +
            ">\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t</NNode >\n" +

            "\t\t<NNode title='60' >" +
            "\t\t\t<NNode colid='A' val='G60' title='граф.' tblName='"+TablesTypes.WARNINGSINTIME+"' noDrill='true'>\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t\t<NNode colid='A' val='F60' title='факт.' " +
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
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":60\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"greaterThan\", \n" +
            "            \"value\":50\n" +
            "        }\n" +
            "    ]" +
            "}'" +
            ">\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t</NNode >\n" +


            "\t\t<NNode title='&gt; 60' >" +
            "\t\t\t<NNode colid='A' val='GOTHER' title='граф.' tblName='"+TablesTypes.WARNINGSINTIME+"' noDrill='true'>\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t\t<NNode colid='A' val='FOTHER' title='факт.' " +
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
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"V\", \n" +
            "            \"operator\":\"greaterThan\", \n" +
            "            \"value\":60\n" +
            "        }\n" +
            "    ]" +
            "}'>\n" +
            "\t\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t\t</NNode >\n" +

            "\t\t</NNode >\n" +


            "</NNode >\n" +


            "<grpX colN='1'>\n" +
                "\t<fld tid='DOR_KOD' tColId='DOR_NAME'/>\n" +
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
