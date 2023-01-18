package com.mwlib.tablo.analit2.warnact;

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
public class WarnACTXML
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
            "\t<col tid='TLN' title='Длинна км.' ftype='"+ListGridFieldType.FLOAT.getValue()+"' format='###0.0' zval=''/>\n" + //Тип ожидаемый в этом поле и формат данных
            "</tuple>\n" +


            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.WARNINGS+"#TOT_ALL_D' title='Действует&lt;br&gt;всего' tblName='"+ TablesTypes.WARNINGS+"' " +
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
             "\t<NVAL colid='CNT' title='шт.' colN='2'/>\n" +
             "\t<NVAL colid='TLN' title='км.' />\n" +
            "</NNode>\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.WARNINGS+"' title='Из общего количества действет&lt;br&gt;    по скоростям'>\n" +

            "\t\t<NNode colid='A' val='15' title='15' " +
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
            "\t\t\t<NVAL colid='CNT' title='шт.' />\n" +
            "\t\t\t<NVAL colid='TLN' title='км.' />\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='25' title='25' " +
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
            "\t\t\t<NVAL colid='CNT' title='шт.' />\n" +
            "\t\t\t<NVAL colid='TLN' title='км.' />\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='40' title='40' " +
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
            "\t\t\t<NVAL colid='CNT' title='шт.' />\n" +
            "\t\t\t<NVAL colid='TLN' title='км.' />\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.WARNINGS+"' title='Всего' >" +

            "\t\t<NNode colid='A' val='LONG' title='Длит.' " +
            "filter='" +
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
            "           \"operator\":\"equals\", \n" +
            "           \"fieldName\":\"FIXED_END_DATE\", \n" +
            "           \"value\":0 \n" +
            "        }'"+
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +
//
            "\t\t<NNode colid='A' val='SHORT' title='Краткоср.' " +
            "filter='" +
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
            "           \"operator\":\"notEqual\", \n" +
            "           \"fieldName\":\"FIXED_END_DATE\", \n" +
            "            \"value\":0 \n" +
            "        }'"+
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +

            "</NNode>\n" +


            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.WARNINGS+"' title='П' >" +

            "\t\t<NNode colid='A' val='PLONG' title='Длит.' " +
            "filter='" +
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
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\"FIXED_END_DATE\", \n" +
            "                    \"value\":0 \n" +
            "        }, \n" +
            "        {" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.VID_NAME+"\", \n" +
            "                    \"value\":\"П\" \n" +
            "        }'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +
//
            "\t\t<NNode colid='A' val='PSHORT' title='Краткоср.' " +
            "filter='" +
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
            "                    \"operator\":\"notEqual\", \n" +
            "                    \"fieldName\":\"FIXED_END_DATE\", \n" +
            "                    \"value\":0 \n" +
            "        }, \n" +
            "        {" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.VID_NAME+"\", \n" +
            "                    \"value\":\"П\" \n" +
            "        }'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +

            "</NNode>\n" +

            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.WARNINGS+"' title='Э' >" +

            "\t\t<NNode colid='A' val='ELONG' title='Длит.' " +
            "filter='" +
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
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\"FIXED_END_DATE\", \n" +
            "                    \"value\":0 \n" +
            "        }, \n" +
            "        {" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.VID_NAME+"\", \n" +
            "                    \"value\":\"Э\" \n" +
            "        }'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +
//
            "\t\t<NNode colid='A' val='ESHORT' title='Краткоср.' " +
            "filter='" +
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
            "                    \"operator\":\"notEqual\", \n" +
            "                    \"fieldName\":\"FIXED_END_DATE\", \n" +
            "                    \"value\":0 \n" +
            "        }, \n" +
            "        {" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.VID_NAME+"\", \n" +
            "                    \"value\":\"Э\" \n" +
            "        }'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +

            "</NNode>\n" +

            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.WARNINGS+"' title='Ш' >" +

            "\t\t<NNode colid='A' val='HLONG' title='Длит.' " +
            "filter='" +
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
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\"FIXED_END_DATE\", \n" +
            "                    \"value\":0 \n" +
            "        }, \n" +
            "        {" +
            "            \"operator\":\"equals\", \n" +
            "            \"fieldName\":\""+TablesTypes.VID_NAME+"\", \n" +
            "            \"value\":\"Ш\" \n" +
            "        }'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +
//
            "\t\t<NNode colid='A' val='HSHORT' title='Краткоср.' " +
            "filter='" +
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
            "                    \"operator\":\"notEqual\", \n" +
            "                    \"fieldName\":\"FIXED_END_DATE\", \n" +
            "                    \"value\":0 \n" +
            "        }, \n" +
            "        {" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.VID_NAME+"\", \n" +
            "                    \"value\":\"Ш\" \n" +
            "        }'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +

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
