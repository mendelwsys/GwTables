package com.mycompany.common.cache;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 22.09.14
 * Time: 18:46
 * простой ключ
 */
public class SimpleKeyGenerator implements IKeyGenerator
{
    private String[] cols;
    private int[] ixCols;

    private INm2Ix mapper;

    public SimpleKeyGenerator(String[] cols,INm2Ix mapper) throws CacheException
    {
        this.mapper = mapper;
        setKeyCols(cols);
    }

    public Object getKeyByTuple(Map tuple) throws CacheException
    {
        StringBuffer rv=new StringBuffer(100);//формируем ключ
        for (String key : cols)
        {
            Object colVal=tuple.get(key);
            if (colVal==null)
                throw new CacheException("The key can not be null in tuple:"+key);
            rv=rv.append("#").append(colVal.toString());
        }
        if (rv.length()>0)
            return rv.substring(1);
        return null;
    }

    @Override
    public Object getKeyByTuple(Object[] tuple) throws CacheException
    {
        StringBuffer rv=new StringBuffer(100);//формируем ключ
        for (int ixCol : ixCols)
        {
            if (tuple[ixCol]==null)
                throw new CacheException("The key can not be null ix in tuple:"+ixCol+" col name is:"+cols[ixCol]);
            rv=rv.append("#").append(tuple[ixCol].toString());
        }
        if (rv.length()>0)
            return rv.substring(1);
        return null;
    }

    @Override
    public String[] getKeyCols() {
        return cols;
    }

    @Override
    public void setKeyCols(int[] ixCols) throws CacheException
    {
        this.cols=new String[ixCols.length];
        Map<Integer, String> ix2ColName = mapper.getIx2ColName();
        for (int i = 0, ixColsLength = ixCols.length; i < ixColsLength; i++)
        {
            int col = ixCols[i];
            this.cols[i]= ix2ColName.get(col);
            if (this.cols[i] == null)
                throw new CacheException();
        }
        this.ixCols=ixCols;
    }

    @Override
    public void setKeyCols(String[] cols) throws CacheException
    {
        this.ixCols=new int[cols.length];
        Map<String,Integer> colName2Ix = mapper.getColName2Ix();
        for (int i = 0, colsLength = cols.length; i < colsLength; i++)
        {
            Integer ix = colName2Ix.get(cols[i]);
            if (ix == null)
                throw new CacheException();
            this.ixCols[i]=ix;
        }
        this.cols=cols;
    }

}
