package com.mwlib.tablo.test.analit2.ref12;

import com.mwlib.tablo.analit2.ref12.Ref12EventProviderTImpl;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.analit2.UtilsData;
import com.mwlib.tablo.analit2.NNodeBuilder;
import com.mwlib.tablo.analit2.ref12.Ref12XML;
import com.mwlib.tablo.db.IMetaProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.07.15
 * Time: 15:48
 * Тестируем консолидационный провайдер
 */
public class TestRef12EventProvider {

    public static void main(String[] args) throws Exception
    {

        Map<String,Integer> key2Number = new HashMap<String,Integer>();
        Map<String,NNode2> key2NNode = new HashMap<String,NNode2>();

        IAnalisysDesc desc=new NNodeBuilder().xml2Desc(Ref12XML.xml);//PlacesXML.xml);
        NNode2 root=new NNode2("ROOT","","ROOT",NNode2.NNodeType,null,false,null,desc.getNodes(),null);
        UtilsData.getKey2key2Number2(root.getNodes(), "", key2Number, key2NNode, 0);

        boolean  test = true;
        String dsName = DbUtil.DS_JAVA_CACHE_NAME;
        Pair<IMetaProvider, Map[]> res = Ref12EventProviderTImpl.getConsolidateProvider(dsName, new String[]{TablesTypes.REFUSES,TablesTypes.REFUSES+"_"+TablesTypes.PLACES}, test).getUpdateTable(null, null);


         Map[] map=res.second;
        for (Map map1 : map)
        {
            final String place_id = map1.get("PLACE_ID").toString();
            if (map1.get("0")!=null && !String.valueOf(map1.get("0")).equals("0") && !place_id.endsWith("##00") && !place_id.endsWith("##"))// && place_id.startsWith("1##"))
                System.out.println("map1 = " + map1);
        }

        System.out.println("res = " + res.second.length);
    }
}
