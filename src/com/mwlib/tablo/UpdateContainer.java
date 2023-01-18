package com.mwlib.tablo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 28.05.15
 * Time: 13:09
 * содержит информацию которую необходимо передать клиенту (в т.ч. набор параметров при апдейте)
 */
public class UpdateContainer
{
    public UpdateContainer(){

        dataRef=new HashMap<Object, long[]>();
        paramRef=new HashMap<String, Object>();

    }

    public UpdateContainer(UpdateContainer container)
    {
           this.dataRef=new HashMap<Object, long[]>(container.dataRef);
           this.paramRef=new HashMap<String, Object>(container.paramRef);
    }

    public UpdateContainer(Map<Object, long[]> dataRef)
    {
        this.dataRef=dataRef;
        this.paramRef=new HashMap<String, Object>();
    }

    public UpdateContainer(Map<Object, long[]> dataRef,Map<String, Object> paramRef)
    {
        this.dataRef=dataRef;
        this.paramRef = paramRef;
    }



    public final Map<Object, long[]> dataRef;
    public final Map<String, Object> paramRef;


}
