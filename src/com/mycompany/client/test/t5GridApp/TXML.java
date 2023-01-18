package com.mycompany.client.test.t5GridApp;

import com.mycompany.common.analit.AnalisysXML;
import com.mycompany.common.analit.NNode;
import com.mycompany.common.analit.UtilsData;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.cache.INm2Ix;
import com.mycompany.common.cache.SimpleKeyGenerator;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.10.14
 * Time: 12:29
 * Тест парсера дескриптора xml для построения анализа
 */
public class TXML
{
    static String xml="<?xml version='1.0' encoding='utf-8' ?>\n" +
            "<!-- Это у нас область определения полей которые сворачиваются в таблицу -->\n" +
            "<project>\n" +
            "\n" +
            "<reqs>\n" +
            "<!--- Здесь запросы из таблиц сформированных на основе событий -->\n" +
            "<!-- Будем пока иметь дело с тремя событиями, строим запросы для получения показателей -->\n" +
            "</reqs>\n" +
            "\n" +
            "<tuple>\n" +
            "\n" +
            "\t<col tid='DOR_СODE' title='ИД Дороги' hide='true'/>\n" +
            "\t<col tid='PRED_ID' title='ИД' hide='true'/>\n" +
            "\t<col tid='SERVICE_ID' title='Сервис' hide='true'/>\n" +
            "\n" +
            "\t<col tid='CATEGORY_ID' title='Категория'/>\n" +
            "\t<col tid='CATEGORY_NAME' title='Название категории'/>\n" +
            "\n" +
            "\t<col tid='CNT' title='Кол. шт.'/>\n" +
            "\t<col tid='LN' title='Длинна км.'/>\n" +
            "\t<col tid='ADL' title='СРЕДНЕЕ'/>\n" +
            "\t<col tid='XXXX' title='X'/>\n" +
            "\n" +
            "</tuple>\n" +
            "\n" +
            "<dims>\n" +
            "\n" +
            " <dim name='CATEGORY_ID'>\n" +
            "\t<dom tid='I' ord = '1' title='ПЕРВАЯ КАТ.'/>\n" +
            "\t<dom tid='II' ord = '2' title='ВТОРАЯ КАТ.'/>\n" +
            " </dim>\n" +
            "\n" +
            " <dim name='EVENT_TYPE'>\n" +
            "\t<dom tid='WIND' ord = '1' title='ОКНА'/>\n" +
            "\t<dom tid='WR' ord = '2' title='ПРЕД.'/>\n" +
            "\t<dom tid='VIOL' ord = '3' title='НАРУШ.'/>\n" +
            " </dim>\n" +
            "\n" +
            "</dims>\n" +
            "\n" +
            "\n" +
            "<maps>\n" +
            "\t<map>\n" +
            "\t<keys>\n" +
            "\t  <key tid='WIND'/>\n" +
            "\t  <key tid='I'/>\n" +
            "\t  <key tid='*'/>\n" +
            "\t</keys>\n" +
            "\t<fnames>\n" +
            " \t\t<fname tid='CNT' ord = '1'/>\n" +
            " \t\t<fname tid='LN' ord = '2' />\n" +
            " \t\t<fname tid='ADL' ord = '3'/>\n" +
            "\t</fnames>\n" +
            "\t</map>\n" +
            "\n" +
            "\t<map>\n" +
            "\t<keys>\n" +
            "\t  <key tid='WIND'/>\n" +
            "\t  <key tid='II'/>\n" +
            "\t  <key tid='*'/>\n" +
            "\t</keys>\n" +
            "\t<fnames>\n" +
            " \t\t<fname tid='CNT' ord='1'/>\n" +
            " \t\t<fname tid='LN' ord='2' />\n" +
            " \t\t<fname tid='ADL' ord='3'/>\n" +
            " \t\t<fname tid='XXXX' ord= '4'/>\n" +
            "\t</fnames>\n" +
            "\t</map>\n" +
            "\t\n" +
            "\t<map>\n" +
            "\t<keys>\n" +
            "\t  <key tid='WR'/>\n" +
            "\t  <key tid='*'/>\n" +
            "\t</keys>\n" +
            "\t<fnames>\n" +
            " \t\t<fname tid='CNT' ord='1'/>\n" +
            " \t\t<fname tid='LN' ord='2'/>\n" +
            "\t</fnames>\n" +
            "\t</map>\n" +
            "\n" +
            "\t<map>\n" +
            "\t<keys>\n" +
            "\t  <key tid='VIOL'/>\n" +
            "\t  <key tid='*'/>\n" +
            "\t</keys>\n" +
            "\t<fnames>\n" +
            " \t\t<fname tid='CNT' ord = '1'/>\n" +
            "\t</fnames>\n" +
            "\t</map>\n" +
            "</maps>\n" +
            "\n" +
            "<grpY>\n" +
            "\t<fld tid='CATEGORY_ID'>\n" +
            "\t\t<fld tid='EVENT_TYPE'/>\n" +
            "\t</fld>\n" +
            "</grpY>\n" +
            "\n" +
            "<grpX>\n" +
            "\t<fld tid='DOR_COD'>\n" +
            "\t\t<fld tid='SERVICE'>\n" +
            "\t\t\t<fld tid='PRED_ID'/>\n" +
            "\t\t</fld>\n" +
            "\t</fld>\n" +
            "</grpX>\n" +
            "\n" +
            "</project>";


    public static Map[] getTestDatas()
    {
        List<Map> rv=new LinkedList<Map>();

        Map<String,Object> map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","WIND");
        map.put("EVENT_NAME","ОКНА");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",5);
        map.put("LN",100);
        map.put("ADL",3.5);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","WIND");
        map.put("EVENT_NAME","ОКНА");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",11);
        map.put("LN",112);
        map.put("ADL",5);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","WIND");
        map.put("EVENT_NAME","ОКНА");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",101);
        map.put("LN",2000);
        map.put("ADL",8);
        map.put("XXX",321123);

        rv.add(map);


        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","WIND");
        map.put("EVENT_NAME","ОКНА");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",111);
        map.put("LN",20);
        map.put("ADL",8);
        map.put("XXX",2112);

        rv.add(map);


        map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","WR");
        map.put("EVENT_NAME","ПРЕД.");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",15);
        map.put("LN",300);

        rv.add(map);



        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","WR");
        map.put("EVENT_NAME","ПРЕД.");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",10);
        map.put("LN",30);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","WR");
        map.put("EVENT_NAME","ПРЕД.");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",13);
        map.put("LN",120);

        rv.add(map);


        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","WR");
        map.put("EVENT_NAME","ПРЕД.");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",130);
        map.put("LN",12);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","VIOL");
        map.put("EVENT_NAME","НАР.");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",13);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","VIOL");
        map.put("EVENT_NAME","НАР.");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",14);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","VIOL");
        map.put("EVENT_NAME","НАР.");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",13);

        rv.add(map);


        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","VIOL");
        map.put("EVENT_NAME","НАР.");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",113);

        rv.add(map);


        return rv.toArray(new Map[rv.size()]);
    }

    public static IKeyGenerator getKeyGenrator() throws CacheException
    {
        return new SimpleKeyGenerator(new String[]{"PRED_ID"},new INm2Ix()
        {
            @Override
            public Map<String, Integer> getColName2Ix()
            {
                Map<String, Integer> rv = new HashMap<String, Integer>();
                rv.put("PRED_ID",0);
                return rv;
            }

            @Override
            public Map<Integer, String> getIx2ColName() {
                Map<Integer,String> rv = new HashMap<Integer,String>();
                rv.put(0,"PRED_ID");
                return rv;
            }
        });
    }


    static void test()
    {
        AnalisysXML analisysXML = new AnalisysXML();
        analisysXML.initByXML(xml);

        NNode root = new NNode("");
        UtilsData.addAllY2Node(root, 0, analisysXML);
        Set<String> res=UtilsData.getFieldNamesByNode(root);

        for (String re : res) {
            String head=UtilsData.getHeader(re);
            System.out.println("head = " + head);
        }


        Map[] testData = getTestDatas();

        Map<Object,Map<String,Object>> tuplesInTable= null;
        try {
            tuplesInTable = new HashMap<Object,Map<String,Object>>();
            IKeyGenerator keyGenerator = getKeyGenrator();

            for (Map tuple : testData)
            {
                Object key=keyGenerator.getKeyByTuple(tuple);
                Map<String, Object> newTuple=tuplesInTable.get(key);
                if (newTuple==null)
                    tuplesInTable.put(key,newTuple = new HashMap<String,Object>());
                UtilsData.conVertMapByNode("", root, tuple, newTuple);
            }
        } catch (CacheException cacheException) {

        }
        System.out.println("tuplesInTable = " + tuplesInTable.size());


    }

}
