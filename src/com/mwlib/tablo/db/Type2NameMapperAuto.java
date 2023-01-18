package com.mwlib.tablo.db;

import com.mycompany.common.Pair;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 30.09.14
 * Time: 14:40
 *
 */
public class Type2NameMapperAuto extends Type2NameMapper
{

    public Type2NameMapperAuto()
    {
        super();
    }

    public Type2NameMapperAuto(BaseTableDesc[] descs)
    {
        super(descs);
    }


    public Type2NameMapperAuto(String name,int[] types)
    {
        super(name,types);
    }


    public Type2NameMapperAuto(Map<String, int[]> names2types) {
        super(names2types);
    }

    public Type2NameMapperAuto(Pair<String, int[]>[] names2types)
    {
        super(names2types);
    }

    public Type2NameMapperAuto(Object[][] names2types) {
        super(names2types);
    }

    @Override
    public String[] getNameFromType(int type)
    {
        String[] name = super.getNameFromType(type);
        if (name==null)
            types2names.put(type,name=new String[]{String.valueOf(type)});
        return name;
    }
}
