package com.mwlib.tablo.analit2.loco;

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
 * Описатель данных Машин
 */
public class LOCXML
{
    public static final String xml="<?xml version='1.0' encoding='UTF-8' ?>\n" +
            "<project>\n" +
            "<tuple>\n" +
            "\n" +
            "\t<col tid='DOR_KOD_NAME_P_ID' title='ИД' hide='true' ftype='"+ ListGridFieldType.TEXT.getValue()+"'/>\n" +

            "\t<col tid='"+TablesTypes.DOR_CODE+"' title='ИД Дороги' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='"+TablesTypes.DOR_NAME+"' hide='true' title='Дорога'/>\n" +

//            "\t<col tid='NDAY' hide='true' title='День'/>\n" +

            "\t<col tid='NAME_P' title='Тип локом.' />\n" +
            "\t<col tid='CNT' title='кол-во' ftype='"+ ListGridFieldType.INTEGER.getValue()+"' zval='0' nval='0'/>\n" + //Тип ожидаемый в этом поле

            "</tuple>\n" +

            "\t\t<NNode colid='EVENT_TYPE' val='"+ TablesTypes.LOCREQ+"'  title='Количество локомотивов' noDrill='true'>\n" +

            "\t\t<NNode colid='A' val='DM' title='Заяв.'>\n" +
            "\t\t\t<NVAL colid='CNT' colN='2'/>\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='1Y' title='1 сог.&lt;br&gt;есть'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='2N' title='2 сог.&lt;br&gt;нет'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='REF' title='Отказ.'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +


            "\t\t</NNode >\n" +

            "<grpX colN='1'>\n" +
            "\t<fld tid='DOR_KOD' tColId='DOR_NAME'>\n" +
             "\t\t<fld tid='DOR_KOD_NAME_P_ID' tColId='NAME_P'/>\n" +
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
