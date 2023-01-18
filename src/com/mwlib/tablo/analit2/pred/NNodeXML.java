package com.mwlib.tablo.analit2.pred;

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
 * Date: 23.10.14
 * Time: 12:53
 * Описатель данных консолидационной таблицы
 */
public class NNodeXML
{
    /*
        Если тип не определен, тогда он  может определяется  типом первого прешедшего поля.
        Для ключевых полей, таким образом тип определен всегда, чего не скажешь для
        полей значений, корме того необходимо тогда запрашивать данные, для того что бы
        определять тип в таблицы, опять же можно но сложене

        Если определен zval, но не опреден nval тогда они становится одинаковым и наоборот
        Тип поля должен быть определен до запроса данных, когда формируется заголовки таблиц, по умолчанию тип ftype='ListGridFieldType.TEXT'.
    */


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
            "\t<col tid='TLN' title='Длинна км.' ftype='"+ListGridFieldType.FLOAT.getValue()+"' format='###0.0' zval=''/>\n" + //Тип ожидаемый в этом поле и формат данных
            "</tuple>\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.WARNINGS+"' title='Предупреждения'>\n" +

            "\t\t<NNode colid='A' val='GRP' tblName='"+TablesTypes.WARNINGSINTIME+"'  title='Заложено&lt;br&gt;графиком' noDrill='true'>\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.' colN='2'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.' />\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='REAL' title='Действуют'>\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +
            "\t\t<NNode colid='A' val='LONG' title='Длительные' " +
            "filter='" +
            "        {\n" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\"FIXED_END_DATE\", \n" +
            "                    \"value\":0 \n" +
            "        }'"+
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +

            "\t\t<NNode colid='A' val='NP' tblName='"+TablesTypes.WARNINGS_NP+"' title='C нарушением&lt;br&gt;приказа' rotate='true'>\n" +
            "\t\t\t<NVAL colid='CNT' />\n" +
            "\t\t</NNode>\n" +

            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.WINDOWS+"' title='Технологические окна'>\n" +
            "\t<NNode colid='A' val='TECH'  tblName='"+TablesTypes.TECH+"'  title='Подход&lt;br&gt;техники' rotate='true' noDrill='true'>\n" +
            "\t\t<NVAL colid='CNT'/>\n" +
            "\t</NNode >\n" +

            "\t<NNode title='Все'>\n" +
            "\t\t<NNode colid='A' val='PLAN' title='Запланированные' rotate='true'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='CURR' tblName='"+TablesTypes.WINDOWS_CURR+"'  title='Предоставленные' rotate='true'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='OVERTIME' tblName='"+TablesTypes.WINDOWS_OVERTIME+"' title='Передержанные' rotate='true'>\n" +
            "\t\t\t<NVAL colid='CNT' />\n" +
            "\t\t</NNode >\n" +
            "\t</NNode >\n"
            +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.REFUSES+"' title='Отказы&lt;br&gt;технических&lt;br&gt;средств'>\n" +
            "\t\t<NNode colid='A' val='Y' title='Принятыe&lt;br&gt;к учету' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"notEqual\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":73 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='N' title='Непринятыe&lt;br&gt;к учету' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":73 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.VIOLATIONS+"' title='Технологические&lt;br&gt;нарушения'>\n" +
            "\t\t<NNode colid='A' val='Y' title='Принятыe&lt;br&gt;к учету' rotate='true' filter='" +
            "{" +
            "                    \"operator\":\"notEqual\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":72 \n" +
            "}'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='N' title='Непринятыe&lt;br&gt;к учету' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":72 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.DELAYS_GID+"' title='Задержки поездов&lt;br&gt;ГИД'>\n" +

            "\t\t<NNode colid='A' val='"+ DelayGIDTDesc.PASS+"' title='Пассажирские' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":74 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode>\n" +

            "\t\t<NNode colid='A' val='"+ DelayGIDTDesc.REG+"' title='Пригородные' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":75 \n" +
            "}'" +
            ">\n" +
             "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode>\n" +
            "\t\t<NNode colid='A' val='"+ DelayGIDTDesc.CRG+"' title='Грузовые' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":76 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode>\n" +

            "</NNode>\n" +


            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.DELAYS_ABVGD+"' title='Задержки поездов&lt;br&gt;ИХ АВГД'>\n" +

            "\t\t<NNode colid='A' val='"+ DelayGIDTDesc.PASS+"' title='Пассажирские' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":62 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='"+ DelayGIDTDesc.REG+"' title='Пригородные' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":63 \n" +
            "}'" +
            ">\n" +
             "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode>\n" +
            "\t\t<NNode colid='A' val='"+ DelayGIDTDesc.CRG+"' title='Грузовые' rotate='true' " +
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

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.KMOTABLE+"' title='Замечания КМО'>\n" +
            "\t\t<NNode colid='A' val='Y' title='Неустранено&lt;br&gt;просрочено' rotate='true'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='N' title='В т.ч. с&lt;br&gt;запретными&lt;br&gt;мерами' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"notNull\", \n" +
            "                    \"fieldName\":\"MERA_BEZ\", \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +


            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.ZMTABLE+"' title='ЕКАСУИ' >\n" +
            "\t\t<NNode colid='A' val='Y' title='ЗМ&lt;br&gt;Незакрытые' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":84 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='N' title='ЗМ&lt;br&gt;просроченные' rotate='true' " +
            "filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":85 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +



            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.LOST_TRAIN+"' title='Брошенные поезда' rotate='true'>\n" +
            "\t<NVAL colid='CNT'/>\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.VAGTOR+"' title='Вагоны находящиеся&lt;br&gt;в ТОР' rotate='true'>\n" +
            "\t<NVAL colid='CNT'/>\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.VIP_GID+"' title='Важные&lt;br&gt;пометки ГИД' rotate='true'>\n" +
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
