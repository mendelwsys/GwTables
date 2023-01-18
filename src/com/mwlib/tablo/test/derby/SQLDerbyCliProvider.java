package com.mwlib.tablo.test.derby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mwlib.tablo.cache.CliProvider;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.db.DbUtils;
import com.mwlib.tablo.derby.DerbyTableOperations;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.Pair;
import com.mycompany.common.REQPARAM;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.EventUtils;
import com.mwlib.tablo.SQLUtils;
import com.mwlib.tablo.UpdateContainer;
import com.mwlib.tablo.db.BaseTableDesc;

import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 28.05.15
 * Time: 17:17
 * получить выборку по SQL запросу
 */
public class SQLDerbyCliProvider  extends CliProvider
        {

            Map<String,List<Integer>> name2pos=new HashMap<>();//Имя в позицию при подготовек выполнения запроса
            DerbyTableOperations derbyTableOperations=DerbyTableOperations.getDefDerbyTableOperations();


            public SQLDerbyCliProvider(Pair<ICache,BaseTableDesc> dataCache, String cliId, String tblId, String[] tType)
            {

                super(dataCache, cliId, tblId, tType);
            }


            final String inTableName = TablesTypes.WARNINGS + "_" + TablesTypes.PLACES;
            final String PARAM = TablesTypes.DB_TRANSACTION_N + "_" + inTableName;

            Map<Object,Set<Object>> id2TOIds;
            Map<Object,Set<Object>> idTOIds2;

            Object currentCounter=-1l;
            Object nextCounter=null;

            public UpdateContainer getNewDataKeys(Map<String, Object> parameters) throws CacheException
            {
                boolean notFullUpdate=this.notFullUpdate;
                UpdateContainer updateContainer = super.getNewDataKeys(parameters);
                Map<String, Object> params = new HashMap<String, Object>();


                if (!notFullUpdate || updateContainer.paramRef.size()>0)
                    params.put(PARAM,currentCounter);
//                else if ()
//                {
//                    params.put(PARAM, nextCounter);
//                    nextCounter = updateContainer.paramRef.get(PARAM);
//                }

                if (params.size()>0)
                { //процедура трансляции удалямых из таблиц(ы) ключей  в удаляемые ключи клиента

                    nextCounter=null;

                    String pSQL1 = "select DATA_OBJ_ID || '##' || trim(char(DATATYPE_ID)) || '##' || trim(char(POLG_ID)) as ID,tdp.KEY_DERBY00,tdp.KEY_DERBY_UPD00 from WARNINGS_PLACES tdp,POLG_OBJ po \n" +
                            "where po.OBJ_OSN_ID=tdp.OBJ_OSN_ID and tdp.KEY_DERBY_UPD00>:" + PARAM + "  order by tdp.KEY_DERBY00";

                    Map<String, List<Integer>> name2pos = new HashMap<>();
                    pSQL1=SQLUtils.constructSQLRequest(pSQL1, name2pos);
                    Object[][] id2ToIdTuples= new Object[0][];
                    try {
                        id2ToIdTuples = SQLUtils.execSQL(derbyTableOperations.getDerbyConnection(), pSQL1, params, name2pos,null);
                    } catch (Exception e) {
                        ;
                    }


                    if (!notFullUpdate)
                    {//Первичное наполнение

                        idTOIds2=new HashMap<>();
                        id2TOIds=new HashMap<>();
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

                            if (nextCounter==null || ((Long)nextCounter)<((Long)id2TOId[2]))
                                nextCounter=id2TOId[2];
                        }
                    }
                    else
                    {

                        Set<Object> delsId = new HashSet<>();
                        {
                            Map<Object, long[]> newKeys = updateContainer.dataRef;
                            System.out.println("size of new keys:" + newKeys.size());
                            for (Object key : newKeys.keySet())
                            { //Сначала удаленные
                                if (newKeys.get(key)==null)
                                {
                                    delsId.add(key);
                                    newKeys.remove(key);
                                }
                            }
                        }

                        Set<Object> delsId2 = new HashSet<>();
                        {
                            for (Object id : delsId)
                            {
                                Set<Object> ids2=idTOIds2.remove(id);
                                for (Object id2 : ids2)
                                {
                                    Set<Object> ids= id2TOIds.get(id2);
                                    ids.remove(id);
                                    if (ids.size()==0)
                                    {
                                        delsId2.add(id2);
                                        id2TOIds.remove(id2);
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

                            if (nextCounter==null || ((Long)nextCounter)<((Long)id2TOId[2]))
                                nextCounter=id2TOId[2];

                            delsId2.remove(id2TOId[0]);
                        }

                        for (Object id2 : delsId2)
                            updateContainer.dataRef.put(id2,null);
                    }
                }

                if (!notFullUpdate)
                {
                    return null;
                }
                return updateContainer;

            }







            @Override
            public Object[][] getTuplesByParameters(Map<String, Object> parameters,UpdateContainer updateContainer) throws CacheException
            {


                if (
                    (updateContainer!=null
                             &&
                            (
                                (updateContainer.dataRef==null || updateContainer.dataRef.size()==0) &&
                                (updateContainer.paramRef==null || updateContainer.paramRef.size()==0)
                            )
                    )
                   )
                {
                    return new Object[0][];
                }

                String pSQL = getSQLString(parameters, updateContainer);

                Map<String,Object> params= new HashMap<>();
                for (String key : name2pos.keySet())
                {
                    if ((updateContainer==null || !updateContainer.paramRef.containsKey(key)) && !parameters.containsKey(key))
                        throw new CacheException("Can't find parameters by name:"+key);
//                    if (updateContainer!=null && updateContainer.paramRef.containsKey(key))
//                        params.put(key,updateContainer.paramRef.get(key));
//                    else
//                         params.put(key,parameters.get(key));
                }

                params.put(PARAM,currentCounter);


                Connection conn= null;
                PreparedStatement stmt=null;
                ResultSet rs = null;
                try
                {
                    conn= derbyTableOperations.getDerbyConnection();
                    stmt=conn.prepareStatement(pSQL);
                    for (String nameP : params.keySet())
                    {
                        List<Integer> lPos = name2pos.get(nameP);
                        for (Integer lPo : lPos)
                            stmt.setObject(lPo,params.get(nameP));
                    }

                    rs=stmt.executeQuery();

                    if (dataCache.first.getMeta()==null || dataCache.first.getMeta().length==0)
                    {
                        ResultSetMetaData meta = rs.getMetaData();
                        int colCnt = meta.getColumnCount();
                        List<ColumnHeadBean> listOfColumn=new LinkedList<ColumnHeadBean>();
                        for (int i = 1; i <= colCnt; i++)
                        {
                            listOfColumn.add(new ColumnHeadBean(meta.getColumnName(i),meta.getColumnName(i), DbUtils.translate2AttrTypeByClassName(meta.getColumnClassName(i))));
                        }
                        dataCache.first.setMeta(listOfColumn.toArray(new ColumnHeadBean[listOfColumn.size()]));
                    }



                    List<Object[]> rv=new LinkedList<Object[]>();

                    int cnt=rs.getMetaData().getColumnCount();
                    if (cnt>0)
                    {
                        while (rs.next())
                        {
                            Object[] objs=new Object[cnt];
                            for (int i=1;i<=cnt;i++)
                                objs[i-1]=rs.getObject(i);
                            rv.add(objs);
                        }
                    }

                    currentCounter=nextCounter;
                    nextCounter=null;


                    return rv.toArray(new Object[rv.size()][]);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return new Object[0][];
                }
                finally
                {
                    DbUtil.closeAll(rs, stmt, conn, true);
                }
            }

            private String rawRequest;
            private String request;

            protected String getSQLString(Map<String, Object> parameters, UpdateContainer updateContainer)
            {
                String reqParams= EventUtils.getParameter(parameters, TablesTypes.REQPARAM);
                Gson gson = new GsonBuilder().serializeNulls().create();
                final REQPARAM params=gson.fromJson(reqParams, REQPARAM.class); //TODO Для ускорения можно в принципе и не парсить хранить объекты в сессии
                if (rawRequest==null || !rawRequest.equals(params.getRequest()))
                {
                    name2pos.clear();
                    request= SQLUtils.constructSQLRequest(rawRequest=params.getRequest(), name2pos);
                }
                return request;
            }



        }

