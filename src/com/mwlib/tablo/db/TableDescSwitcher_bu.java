package com.mwlib.tablo.db;

import com.mwlib.tablo.db.desc.*;
import com.mycompany.common.TablesTypes;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 10.10.14
 * Time: 13:14
 * Класс для получения метаинофрмации о таблицах
 */
public class TableDescSwitcher_bu
{
    public ITypes2NameMapper getMapper() {
        return mapper;
    }

    private ITypes2NameMapper mapper;
    private static boolean test = true;




    private TableDescSwitcher_bu(ITypes2NameMapper mapper)
    {
        this.mapper=mapper;
    }


        //EventTypeDistributer metaProvider = new EventTypeDistributer(new Type2NameMapperAuto(new BaseTableDesc[]{new WindowsDesc(),new ViolationDesc(),new RefuseDesc()}));
    //TODO Задаются таблицы с наполнением кэша

    private static TableDescSwitcher_bu instance = new TableDescSwitcher_bu
            (
//                new Type2NameMapperAuto(
//                    new BaseTableDesc[]
//                    {
//                            new WindowsDesc(test),
//                            new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
//                            new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61})
//                    })

                new Type2NameMapperAuto
                (
                  new BaseTableDesc[]
                  {
                        new DelayABGDDesc(test),new ZMDesc(test),new KMODesc(test),
                        new LostTrDesc(test),new VagInTORDesc(test),new VIPGidDesc(test),new DelayGIDTDesc(test),
                        new WarningInTimeDesc(test),new TechDesc(test),new ViolationDesc(test),new RefuseDesc(test),
                        new WindowsDesc(test),
                        new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                        new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),
                        new WindowsCurrentDesc(test),
                        new WindowsOverTimeDesc(test),
                        new WarningDesc(test),new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79}),
                        new PlacesDesc(test),new PlacesPOLGDesc(test),
                        new LocReqDesc(test)
                  }
                )
            );

    static private List<TableDescSwitcher_bu> instances= new LinkedList<>();
    static
    {
        instances.add(instance);
    }


    public static TableDescSwitcher_bu getInstance()
    {
        return instance;
    }

    public static List<TableDescSwitcher_bu> getInstances()
    {
        return instances;
    }


    public boolean isTest()
    {
        return test;
    }

    public BaseTableDesc[] getDescInstance(Map mapParams) throws Exception
    {
        String[] params=(String[])mapParams.get(TablesTypes.TTYPE);

        List<BaseTableDesc> rv= new LinkedList<BaseTableDesc>();
        if (params!=null && params.length>0 && params[0]!=null)
        {
            for (String param : params)
            {

                BaseTableDesc desc = mapper.getTableDescByTblName(param);
                if (desc!=null)
                    rv.add(desc);
                else
                    System.out.println("desc getting error : params: " + param);
            }
            return rv.toArray(new BaseTableDesc[rv.size()]);
        }
        else
            throw new Exception("Can't define table type for instance ");
    }

}
