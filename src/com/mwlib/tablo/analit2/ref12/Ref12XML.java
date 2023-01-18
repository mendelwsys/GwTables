package com.mwlib.tablo.analit2.ref12;

import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.analit2.UtilsData;
import com.mwlib.tablo.analit2.NNodeBuilder;
import com.smartgwt.client.types.ListGridFieldType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 23.10.14
 * Time: 12:53
 * Описатель данных табдицы задержек поездов
 */
public class Ref12XML
{
    public static final String xml="<?xml version='1.0' encoding='UTF-8' ?>\n" +
            "<project>\n" +
            "<tuple>\n" +
            "\t<col tid='"+TablesTypes.PLACE_ID+"' title='ИД' hide='true' ftype='"+ ListGridFieldType.TEXT.getValue()+"'/>\n" +
            "\t<col tid='"+TablesTypes.DOR_CODE+"' title='ИД Дороги' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='"+TablesTypes.DOR_NAME+"' title='Дорога'  hide='true'/>\n" +
            "\t<col tid='NUM' title='Номер в отображении' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='"+TablesTypes.POLG_ID+"' title='ИД Участка'  ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='"+TablesTypes.POLG_NAME+"' title='Участок'/>\n" +

            "\t<col tid='CNT' title='Кол. шт.' ftype='"+ ListGridFieldType.INTEGER.getValue()+"' zval=''/>\n" + //Тип ожидаемый в этом поле
            "</tuple>\n" +


            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.REFUSES+"#TOT_ALL_D' title='Всего' tblName='"+ TablesTypes.REFUSES+"' " +
            "filter='{" +
            "    \"_constructor\":\"AdvancedCriteria\", \n" +
            "    \"operator\":\"and\", \n" +
            "    \"criteria\":[\n" +
            "        {\n" +
            "            \"fieldName\":\"ID_KIND\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":2\n" +
            "        }, \n" +
            "        {\n" +
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
             "\t<NVAL colid='CNT' colN='2'/>\n" +
            "</NNode>\n" +


            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.REFUSES+"' title='Устр. с нач. сут.'>\n" +

            "\t\t<NNode colid='A' val='TOT_D' title='Всего' " +
            "filter='{" +
            "    \"_constructor\":\"AdvancedCriteria\", \n" +
            "    \"operator\":\"and\", \n" +
            "    \"criteria\":[\n" +
            "        {\n" +
            "            \"fieldName\":\"ID_KIND\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":2\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"ND\", \n" +
            "            \"operator\":\"greaterOrEqual\", \n" +
            "            \"value\":{\n" +
            "                \"_constructor\":\"RelativeDate\", \n" +
            "                \"value\":\"$today\"\n" +
            "            }\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"TIMESTAMP_FIELD\", \n" +
            "            \"operator\":\"greaterOrEqualField\", \n" +
            "            \"value\":\"KD\"\n" +
            "        }, \n" +
            "        {\n" +
            "            \"operator\":\"notNull\", \n" +
            "            \"fieldName\":\"KD\"\n" +
            "        }\n" +
            "    ]"+
            "}'>\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='LONG_D' title='Длит.> &lt;br&gt;1 часа' " +
            "filter='{" +
            "    \"_constructor\":\"AdvancedCriteria\", \n" +
            "    \"operator\":\"and\", \n" +
            "    \"criteria\":[\n" +
            "        {\n" +
            "            \"fieldName\":\"KD_ND\", \n" +
            "            \"operator\":\"greaterThan\", \n" +
            "            \"value\":60\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"ID_KIND\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":2\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"ND\", \n" +
            "            \"operator\":\"greaterOrEqual\", \n" +
            "            \"value\":{\n" +
            "                \"_constructor\":\"RelativeDate\", \n" +
            "                \"value\":\"$today\"\n" +
            "            }\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"TIMESTAMP_FIELD\", \n" +
            "            \"operator\":\"greaterOrEqualField\", \n" +
            "            \"value\":\"KD\"\n" +
            "        }, \n" +
            "        {\n" +
            "            \"operator\":\"notNull\", \n" +
            "            \"fieldName\":\"KD\"\n" +
            "        }\n" +
            "    ]" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t</NNode>\n" +
            "</NNode >\n" +


            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.REFUSES+"' title='Не Устр. с нач. сут.'>\n" +

            "\t\t<NNode colid='A' val='ACT_TOT_D' title='Всего' " +
            "filter='{\n" +
            "    \"_constructor\":\"AdvancedCriteria\", \n" +
            "    \"operator\":\"and\", \n" +
            "    \"criteria\":[\n" +
            "        {\n" +
            "            \"fieldName\":\"ID_KIND\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":2\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"ND\", \n" +
            "            \"operator\":\"greaterOrEqual\", \n" +
            "            \"value\":{\n" +
            "                \"_constructor\":\"RelativeDate\", \n" +
            "                \"value\":\"$today\"\n" +
            "            }\n" +
            "        }, \n" +
            "        {\n" +
            "            \"_constructor\":\"AdvancedCriteria\", \n" +
            "            \"operator\":\"or\", \n" +
            "            \"criteria\":[\n" +
            "                {\n" +
            "                    \"operator\":\"isNull\", \n" +
            "                    \"fieldName\":\"KD\"\n" +
            "                }, \n" +
            "                {\n" +
            "                    \"fieldName\":\"TIMESTAMP_FIELD\", \n" +
            "                    \"operator\":\"lessOrEqualField\", \n" +
            "                    \"value\":\"KD\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='ACT_LONG_D' title='Длит.> &lt;br&gt;1 часа' " +
            "filter='{" +
            "    \"_constructor\":\"AdvancedCriteria\", \n" +
            "    \"operator\":\"and\", \n" +
            "    \"criteria\":[\n" +
            "        {\n" +
            "            \"fieldName\":\"ID_KIND\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":2\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"ND\", \n" +
            "            \"operator\":\"greaterOrEqual\", \n" +
            "            \"value\":{\n" +
            "                \"_constructor\":\"RelativeDate\", \n" +
            "                \"value\":\"$today\"\n" +
            "            }\n" +
            "        }, \n" +
            "        {\n" +
            "            \"_constructor\":\"AdvancedCriteria\", \n" +
            "            \"operator\":\"or\", \n" +
            "            \"criteria\":[\n" +
            "                {\n" +
            "                    \"operator\":\"isNull\", \n" +
            "                    \"fieldName\":\"KD\"\n" +
            "                }, \n" +
            "                {\n" +
            "                    \"fieldName\":\"TIMESTAMP_FIELD\", \n" +
            "                    \"operator\":\"lessOrEqualField\", \n" +
            "                    \"value\":\"KD\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }, \n" +
            "        {\n" +
            "            \"fieldName\":\"zIntervalVal_\", \n" +
            "            \"operator\":\"greaterThan\", \n" +
            "            \"value\":1\n" +
            "        }\n" +
            "    ]" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t</NNode>\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.REFUSES+"#ACT_TOT_ALL' title='Не Устр.&lt;br&gt;всего' tblName='"+ TablesTypes.REFUSES+"' " +
            "filter='{" +
            "    \"_constructor\":\"AdvancedCriteria\", \n" +
            "    \"operator\":\"and\", \n" +
            "    \"criteria\":[\n" +
            "        {\n" +
            "            \"fieldName\":\"ID_KIND\", \n" +
            "            \"operator\":\"lessOrEqual\", \n" +
            "            \"value\":2\n" +
            "        }, \n" +
            "        {\n" +
            "            \"_constructor\":\"AdvancedCriteria\", \n" +
            "            \"operator\":\"or\", \n" +
            "            \"criteria\":[\n" +
            "                {\n" +
            "                    \"operator\":\"isNull\", \n" +
            "                    \"fieldName\":\"KD\"\n" +
            "                }, \n" +
            "                {\n" +
            "                    \"fieldName\":\"TIMESTAMP_FIELD\", \n" +
            "                    \"operator\":\"lessOrEqualField\", \n" +
            "                    \"value\":\"KD\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]" +
            "}'" +
            ">\n" +
             "\t<NVAL colid='CNT'/>\n" +
            "</NNode>\n" +

            "<grpX colN='1'>\n" +
            "\t<fld tid='"+TablesTypes.DOR_CODE+"' tColId='"+TablesTypes.DOR_NAME+"'>\n" +
                "\t\t<fld tid='"+TablesTypes.PLACE_ID+"' tColId='"+TablesTypes.POLG_NAME+"'/>\n" +
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
