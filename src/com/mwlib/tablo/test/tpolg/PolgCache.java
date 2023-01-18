package com.mwlib.tablo.test.tpolg;

import com.mwlib.tablo.derby.DerbyCache;
import com.mwlib.tablo.derby.DerbyTableOperations;
import com.mycompany.common.cache.CacheException;
import com.mwlib.tablo.SQLUtils;
import com.mwlib.tablo.UpdateContainer;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 04.06.15
 * Time: 13:46
 * Специализированый кеш данных для обновления места событий
 */
public class PolgCache extends DerbyCache
{
    public PolgCache(Map<String, Object> params) {
        super(params);
    }

    public PolgCache(String tblName) {
        super(tblName);
    }



    Map<Object,Set<Object>> id2TOIds;
    Map<Object,Set<Object>> idTOIds2;

    Object currentCounter=-1l;

    DerbyTableOperations derbyTableOperations=DerbyTableOperations.getDefDerbyTableOperations();

    public Map<Object,long[]> removeAll(Set<Object> keys4Remove) throws CacheException
    {
        Map<Object, long[]> removedIDs=new HashMap<Object, long[]>();
        try
        {
            if (keys4Remove!=null && keys4Remove.size()>0)
            { //Первичное наполнение


                boolean isJustInit=false;
                if (this.idTOIds2==null)
                {
                    String pSQL1 = "select DATA_OBJ_ID || '##' || trim(char(DATATYPE_ID)) || '##' || trim(char(POLG_ID)) as ID,tdp.KEY_DERBY00,tdp.KEY_DERBY_UPD00 from "+this.tblName+" tdp,POLG_OBJ po \n" +
                            "where po.OBJ_OSN_ID=tdp.OBJ_OSN_ID and tdp.KEY_DERBY_UPD00>" + String.valueOf(currentCounter) + "  order by tdp.KEY_DERBY00";

                    Object[][] id2ToIdTuples;
                    id2ToIdTuples = SQLUtils.execQuery(derbyTableOperations.getDerbyConnection(),pSQL1,null);



                    idTOIds2=new HashMap<>();
                    id2TOIds=new HashMap<>();
                    isJustInit=true;

                    for (Object[] id2TOId : id2ToIdTuples)
                    {
                        {
                            Set<Object> ids=id2TOIds.get(id2TOId[0]);
                            if (ids==null)
                                id2TOIds.put(id2TOId[0],ids=new HashSet<>());
                            ids.add(id2TOId[1]);
                        }

                        {
                            Set<Object> ids=idTOIds2.get(id2TOId[1]);
                            if (ids==null)
                                idTOIds2.put(id2TOId[1],ids=new HashSet<>());
                            ids.add(id2TOId[0]);
                        }

                        if (currentCounter==null || ((Long)currentCounter)<((Long)id2TOId[2]))
                            currentCounter=id2TOId[2];
                    }

                }

                removedIDs = super.removeAll(keys4Remove);
                if (removedIDs!=null && removedIDs.size()>0)
                { //процедура трансляции удалямых из таблиц(ы) ключей  в удаляемые ключи клиента

                    Object[][] id2ToIdTuples;
                    if (isJustInit)
                        id2ToIdTuples=new Object[0][];
                    else
                    {
                        String pSQL1 = "select DATA_OBJ_ID || '##' || trim(char(DATATYPE_ID)) || '##' || trim(char(POLG_ID)) as ID,tdp.KEY_DERBY00,tdp.KEY_DERBY_UPD00 from "+this.tblName+" tdp,POLG_OBJ po \n" +
                                "where po.OBJ_OSN_ID=tdp.OBJ_OSN_ID and tdp.KEY_DERBY_UPD00>" + String.valueOf(currentCounter) + "  order by tdp.KEY_DERBY00";
                        id2ToIdTuples = SQLUtils.execQuery(DerbyTableOperations.getDefDerbyTableOperations().getDerbyConnection(), pSQL1, null);
                    }

                    {
                                Set<Object> delsId = new HashSet<>(removedIDs.keySet());
                                System.out.println("size of new keys:" + removedIDs.size());
                                removedIDs.clear();



                                Set<Object> delsId2 = new HashSet<>();
                                {
                                    for (Object id : delsId)
                                    {
                                        Set<Object> ids2=idTOIds2.remove(id);
                                        if (ids2!=null)
                                        {
                                            for (Object id2 : ids2)
                                            {
                                                Set<Object> ids= id2TOIds.get(id2);
                                                ids.remove(id);
                                                if (ids == null || ids.size()==0)
                                                {
                                                    delsId2.add(id2);
                                                    id2TOIds.remove(id2);
                                                }
                                            }
                                        }
                                    }
                                }

                                for (Object[] id2TOId : id2ToIdTuples)
                                {
                                    {
                                        Set<Object> ids=id2TOIds.get(id2TOId[0]);
                                        if (ids==null)
                                            id2TOIds.put(id2TOId[0],ids=new HashSet<>());
                                        ids.add(id2TOId[1]);
                                    }

                                    {
                                        Set<Object> ids=idTOIds2.get(id2TOId[1]);
                                        if (ids==null)
                                            idTOIds2.put(id2TOId[1],ids=new HashSet<>());
                                        ids.add(id2TOId[0]);
                                    }

                                    if (currentCounter==null || ((Long)currentCounter)<((Long)id2TOId[2]))
                                        currentCounter=id2TOId[2];

                                    delsId2.remove(id2TOId[0]);
                                }

                                for (Object id2 : delsId2)
                                    removedIDs.put(id2,null);
                        }

                        verifyIt(idTOIds2,id2TOIds);
                }
            }
        } catch (Exception e) {
            throw new CacheException(e);
        }

        return removedIDs;
    }

    public UpdateContainer update(Map[] inTuples, boolean insertAllNotFound) throws CacheException
    {
        return super.update(inTuples,insertAllNotFound);
    }


    private void verifyIt(Map<Object,Set<Object>> _idTOIds2,Map<Object,Set<Object>> _id2TOIds) throws Exception
    {
        String pSQL1 = "select DATA_OBJ_ID || '##' || trim(char(DATATYPE_ID)) || '##' || trim(char(POLG_ID)) as ID,tdp.KEY_DERBY00,tdp.KEY_DERBY_UPD00 from "+this.tblName+" tdp,POLG_OBJ po \n" +
                "where po.OBJ_OSN_ID=tdp.OBJ_OSN_ID and tdp.KEY_DERBY_UPD00> -1 order by tdp.KEY_DERBY00";

        Object[][] id2ToIdTuples;
        id2ToIdTuples = SQLUtils.execQuery(derbyTableOperations.getDerbyConnection(),pSQL1,null);



        Map<Object,Set<Object>> idTOIds2=new HashMap<>();
        Map<Object,Set<Object>> id2TOIds=new HashMap<>();

        for (Object[] id2TOId : id2ToIdTuples)
        {
            {
                Set<Object> ids=id2TOIds.get(id2TOId[0]);
                if (ids==null)
                    id2TOIds.put(id2TOId[0],ids=new HashSet<>());
                ids.add(id2TOId[1]);
            }

            {
                Set<Object> ids=idTOIds2.get(id2TOId[1]);
                if (ids==null)
                    idTOIds2.put(id2TOId[1],ids=new HashSet<>());
                ids.add(id2TOId[0]);
            }
        }

        Map<Object, Set<Object>>[][] cmpArr=new Map[][]
        {
                {_idTOIds2, idTOIds2},
                {idTOIds2, _idTOIds2},
                {id2TOIds, _id2TOIds},
                {_id2TOIds, id2TOIds},
        };


        br:
        {
        int i=0;
        while (
                compare(cmpArr[i][0],cmpArr[i][1])==0)
        {
            i++;
            if (i>=cmpArr.length)
                break br;
        }
            System.out.println("Error definition of data");
        }
    }

    private int compare(Map<Object, Set<Object>> _idTOIds2, Map<Object, Set<Object>> idTOIds2) {
        for (Object key : idTOIds2.keySet())
        {
           if (!_idTOIds2.containsKey(key))
           {
               System.out.println("lost key = " + key+" in our cahce");
               return -1;
           }
            Set<Object> _ids2 = _idTOIds2.get(key);

            Set<Object> ids2 = idTOIds2.get(key);

            final int size = ids2.size();
            if (size !=_ids2.size())
            {
                System.out.println("different key size = " + key+" in our cahce");
                return -1;
            }
            _ids2.addAll(ids2);
            if (_ids2.size()!=size)
            {
                System.out.println("different keys for key = " + key+" in our cahce");
                return -1;
            }
        }
        return 0;
    }
}
