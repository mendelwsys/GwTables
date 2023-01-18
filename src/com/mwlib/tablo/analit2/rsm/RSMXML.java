package com.mwlib.tablo.analit2.rsm;

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
public class RSMXML
{
    public static final String xml="<?xml version='1.0' encoding='UTF-8' ?>\n" +
            "<project>\n" +
            "<tuple>\n" +
            "\n" +
            "\t<col tid='STATE_ID' title='ИД состояние' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='NAME' title='Состояние' />\n" +
            "\t<col tid='NPP' title='номер' hide='true'  ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" + //Тип ожидаемый в этом поле
            "\t<col tid='CNT' title='кол-во' ftype='"+ ListGridFieldType.INTEGER.getValue()+"' zval='0' nval='0'/>\n" + //Тип ожидаемый в этом поле

            "</tuple>\n" +

            "\t\t<NNode colid='EVENT_TYPE' val='"+ TablesTypes.RSM_DATA+"'  title='кол-во' noDrill='true'>\n" +
            "\t\t\t<NVAL colid='CNT' colN='2'/>\n" +
            "\t\t</NNode >\n" +

            "<grpX colN='1'>\n" +
                "\t<fld tid='STATE_ID' tColId='NAME'/>\n" +
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
