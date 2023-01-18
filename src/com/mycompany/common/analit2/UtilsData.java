package com.mycompany.common.analit2;

import com.mycompany.common.FieldException;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 23.10.14
 * Time: 16:54
 *
 */
public class UtilsData
{


    public static void getValuesByNodeTree(NNode2 node2,List<String> names)
    {
        if (node2!=null)
        {

            if ("ROOT".equals(node2.getColId()))
                return;

            String title=node2.getTitle();
            if (title!=null && title.length()>0)
                names.add(0,title);
            getValuesByNodeTree(node2.getParent(),names);
        }
    }

    public static String getNameByList(List<String> names, String delimiter)
    {
        StringBuilder blder = new StringBuilder();
        for (int i = 0, namesSize = names.size(); i < namesSize; i++)
        {
            String name = names.get(i);
            if (i>0)
                blder.append(delimiter);
            blder.append(name);
        }
        return blder.toString();
    }


    public static final String KEY_DELIMITER = "#";

    public static class BusyKeyException extends Exception
    {

        public BusyKeyException() {
        }

        public String getBusyKey() {
            return busyKey;
        }

        private String busyKey;

        public BusyKeyException(String message,String busyKey) {
            super(message);
            this.busyKey = busyKey;
        }
    }

    public static void conVertMapByNode(String prefix, NNode2[] nodes, Map<String, Object> tuple, Map<String, Object> res, Map<String, Integer> key2Number, List<String> filledKeysByTuple) throws Exception
    {
        for (NNode2 nnode : nodes)
        {
            String colId = nnode.getColId();
            if (NNode2.NNodeType.equals(nnode.getType()))
            {
                String val = nnode.getVal();
                Object tupleVal=tuple.get(colId);
                if (tupleVal!=null && tupleVal.equals(val))
                {
                    //prefix+= KEY_DELIMITER +val;
                    conVertMapByNode(prefix+KEY_DELIMITER +val,nnode.getNodes(),tuple,res,key2Number, filledKeysByTuple);
                }
            }
            else  if (NNode2.NVALType.equals(nnode.getType()))
            {
                String keyColumn = prefix + KEY_DELIMITER + nnode.getColId();
                Integer key1 = key2Number.get(keyColumn);
                if (key1==null)
                {
                    Set<Map.Entry<String, Object>> ess = res.entrySet();
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, Object> es: ess)
                       sb.append(es.getKey()).append(" ").append(es.getValue().toString()).append("\n");
                    throw new Exception("Can't find keyColumn by keyColumn:"+keyColumn+" for tuple:"+sb.toString());
                }
                String key = String.valueOf(key1);
                filledKeysByTuple.add(key);

                if (res.containsKey(key))
                    throw new BusyKeyException("the key:"+key1+" is busy",key);
                res.put(key, tuple.get(colId));

            }
            else
                throw new Exception("NodesTree Error");
        }

    }

    public static NNode2[] removeEmptyNodes(NNode2[] nodes) throws FieldException
    {
        List<NNode2> rv=new LinkedList<NNode2>();
        for (NNode2 nnode : nodes)
        {
            if (NNode2.NNodeType.equals(nnode.getType()))
            {
                NNode2[] rv1 = removeEmptyNodes(nnode.getNodes());
                String colId = nnode.getColId();
                if (colId==null || colId.length()==0)
                    rv.addAll(Arrays.asList(rv1));
                else
                {
                    nnode.setNodes(rv1);
//                    for (NNode2 nNode2 : rv1)
//                        nNode2.setParent(nnode);
                    rv.add(nnode);
                }
            }
            else if (NNode2.NVALType.equals(nnode.getType()))
                rv.add(nnode);
            else
                throw new FieldException("NodesTree Error");
        }
        return rv.toArray(new NNode2[rv.size()]);
    }

    public static Map<Integer,String> number2Key(Map<String,Integer> key2Number) throws FieldException
    {
        Map<Integer,String> rv= new TreeMap<Integer,String>();
        for (String key : key2Number.keySet())
        {
            Integer s = key2Number.get(key);
            if (rv.containsKey(s))
                throw new FieldException("Double key");
            rv.put(s,key);
        }
        return rv;
    }



    public static int getKey2key2Number(NNode2[] nodes,String prefix,Map<String,Integer> key2Number,int beg) throws FieldException
    {
        for (NNode2 nnode : nodes)
        {
            if (NNode2.NNodeType.equals(nnode.getType()))
            {
                String val = nnode.getVal();
                String _prefix=prefix;
                if (val!=null && val.length()>0)
                    _prefix=prefix+ KEY_DELIMITER +val;
//                else
//                    System.out.println("_prefix = " + _prefix);
                beg=getKey2key2Number(nnode.getNodes(),_prefix,key2Number,beg);
            }
            else  if (NNode2.NVALType.equals(nnode.getType()))
            {
                key2Number.put(prefix+ KEY_DELIMITER +nnode.getColId(),beg);
                beg++;
            }
            else
                throw new FieldException("NodesTree Error");
        }
        return beg;
    }


    public static int getKey2key2Number2(NNode2[] nodes,String prefix,Map<String,Integer> key2Number,Map<String,NNode2> key2NNode,int beg) throws FieldException
    {
        for (NNode2 nnode : nodes)
        {
            if (NNode2.NNodeType.equals(nnode.getType()))
            {
                String val = nnode.getVal();
                String _prefix=prefix;
                if (val!=null && val.length()>0)
                    _prefix=prefix+ KEY_DELIMITER +val;
//                else
//                    System.out.println("_prefix = " + _prefix);
                beg=getKey2key2Number2(nnode.getNodes(),_prefix,key2Number,key2NNode,beg);
            }
            else  if (NNode2.NVALType.equals(nnode.getType()))
            {
                key2Number.put(prefix+ KEY_DELIMITER +nnode.getColId(),beg);
                key2NNode.put(prefix+ KEY_DELIMITER +nnode.getColId(),nnode);
                beg++;
            }
            else
                throw new FieldException("NodesTree Error");
        }
        return beg;
    }


}
