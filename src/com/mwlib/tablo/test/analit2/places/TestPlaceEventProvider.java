package com.mwlib.tablo.test.analit2.places;

import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.analit2.UtilsData;
import com.mwlib.tablo.analit2.NNodeBuilder;
import com.mwlib.tablo.analit2.places.PlacesEventProviderTImpl;
import com.mwlib.tablo.analit2.pred.NNodeXML;
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
public class TestPlaceEventProvider {

    public static void main(String[] args) throws Exception
    {

        Map<String,Integer> key2Number = new HashMap<String,Integer>();
        Map<String,NNode2> key2NNode = new HashMap<String,NNode2>();

        IAnalisysDesc desc=new NNodeBuilder().xml2Desc(NNodeXML.xml);//PlacesXML.xml);
        NNode2 root=new NNode2("ROOT","","ROOT",NNode2.NNodeType,null,false,null,desc.getNodes(),null);
        UtilsData.getKey2key2Number2(root.getNodes(), "", key2Number, key2NNode, 0);

        boolean  test = true;
        String dsName = DbUtil.DS_JAVA_CACHE_NAME;
        String dsOraName = DbUtil.DS_ORA_NAME;
        Pair<IMetaProvider, Map[]> res = PlacesEventProviderTImpl.getConsolidateProvider(dsName,dsOraName,new String[]{TablesTypes.REFUSES}, test).getUpdateTable(null, null);

        System.out.println("res = " + res.second.length);
    }
}
