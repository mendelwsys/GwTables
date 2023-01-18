package com.mycompany.client.dojoChart;

import com.mycompany.common.FieldException;
import com.mycompany.common.analit2.GrpDef;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.analit2.UtilsData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 03.03.15
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class DescExt
{ //просто контейнер данных для того что бы не распарсивать это второй раз

    private IAnalisysDesc desc;
    private Map<String,Integer> key2Number;
    private Map<String,NNode2> key2NNode;
    private Map<Integer, String> number2Key;
    private GrpDef[] keyCols;
    private NNode2 root;


    public Map<String, Integer> getKey2Number() {
        return key2Number;
    }

    public Map<String, NNode2> getKey2NNode() {
        return key2NNode;
    }

    public Map<Integer, String> getNumber2Key() {
        return number2Key;
    }

    public GrpDef[] getKeyCols() {
        return keyCols;
    }


    public IAnalisysDesc getDesc() {
        return desc;
    }

    public NNode2 getRoot() {
        return root;
    }



    DescExt(IAnalisysDesc desc) throws FieldException
    {
        this.desc=desc;
        root=new NNode2("ROOT","","ROOT",NNode2.NNodeType,null,false,null,desc.getNodes(),null);
        keyCols=desc.getGrpXHierarchy();//Получим ключевые поля по X TODO (Это необходимо для установки описателя иерархии, если добавить еще функции, мы тогда полностью уйдем от использования кода )
        UtilsData.getKey2key2Number2(root.getNodes(), "", key2Number = new HashMap<String, Integer>(), key2NNode = new HashMap<String, NNode2>(), 0);
        number2Key = UtilsData.number2Key(key2Number);
    }
}
