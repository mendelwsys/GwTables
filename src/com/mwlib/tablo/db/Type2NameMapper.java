package com.mwlib.tablo.db;

import com.mycompany.common.Pair;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 30.09.14
 * Time: 14:40
 * Типы событий и связанные с ними групперовки событий
 */
public class Type2NameMapper implements ITypes2NameMapper {


    @Override
    public BaseTableDesc[] getAllTableDesc()
    {
        return names2Desc.values().toArray(new BaseTableDesc[names2Desc.values().size()]);
    }

    @Override
    public BaseTableDesc getTableDescByTblName(String name)
    {
        return names2Desc.get(name);
    }

    @Override
    public BaseTableDesc[] getTableDescByEventType(int data_type) {

        List<BaseTableDesc> ll = new LinkedList<BaseTableDesc>();
        String[] names = types2names.get(data_type);
        for (String name : names)
            ll.add(getTableDescByTblName(name));
        return ll.toArray(new BaseTableDesc[ll.size()]);
    }



    public Type2NameMapper()
    {
    }


    public Type2NameMapper(BaseTableDesc[] descs)
    {
        setType2NameMapper(descs);

    }

    private void setType2NameMapper(BaseTableDesc[] descs) {
        Pair<String,int[]>[] prs = new Pair[descs.length];
        for (int i = 0; i < prs.length; i++)
        {
            prs[i]=new Pair<String,int[]>(descs[i].getTableType(),descs[i].getDataTypes());
            names2Desc.put(prs[i].first,descs[i]);
        }
        setByTypesWithName(prs);
    }


    public Type2NameMapper(String name,int[] types)
    {
        this(new Pair[]{new Pair<String,int[]>(name,types)});
    }

    public Type2NameMapper(Map<String,int[]> names2types)
    {
        setByTypes2Names(names2types);
    }

    public Type2NameMapper(Pair<String,int[]>[] names2types)
    {
        setByTypesWithName(names2types);
    }

    private void setByTypesWithName(Pair<String, int[]>[] names2types) {
        Map<String,int[]> _names2types = new HashMap<>();
        for (Pair<String, int[]> names2type : names2types) {
            _names2types.put(names2type.first,names2type.second);
        }
        setByTypes2Names(_names2types);
    }

    public Type2NameMapper(Object[][] names2types)
    {
        Map<String,int[]> _names2types = new HashMap<>();
        for (Object[] names2type : names2types) {
            _names2types.put((String)names2type[0],(int[])names2type[1]);
        }
        setByTypes2Names(_names2types);
    }


    public Map<Integer, String[]> getTypes2names() {
        return types2names;
    }

    public void setTypes2names(Map<Integer, String[]> types2names) {
        this.types2names = types2names;
    }

    protected Map<Integer,String[]> types2names = new HashMap<Integer,String[]>();

    protected Map<String,BaseTableDesc> names2Desc = new HashMap<String,BaseTableDesc>();



    public void setByTypes2Names(Map<String,int[]> names2types)
    {
        Map<Integer,List<String>> _types2Name = new HashMap<Integer,List<String>>();

        for (String name : names2types.keySet())
        {
            int [] types=names2types.get(name);



            for (int type : types)
            {

                List<String> names=_types2Name.get(type);
                if (names==null)
                    _types2Name.put(type,names=new LinkedList<String>());
                names.add(name);
            }
        }

        for (Integer type : _types2Name.keySet())
        {
            List<String> names = _types2Name.get(type);
            types2names.put(type, names.toArray(new String[names.size()]));
        }

    }

    @Override
    public String[] getNames()
    {
        Collection<String> values = new HashSet<String>();

        for (String[] strings : types2names.values())
            values.addAll(Arrays.asList(strings));
        //Collection<String> values = new HashSet<String>(types2names.values());
        return values.toArray(new String[values.size()]);
    }

    @Override
    public Integer[] getTypes()
    {
        return types2names.keySet().toArray(new Integer[types2names.size()]);
    }

    @Override
    public String[] getNameFromType(int type)
    {
        return types2names.get(type);
    }
}
