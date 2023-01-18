package com.mycompany.client.test.t5GridApp;

import com.mycompany.common.Pair;
import com.mycompany.common.analit.NNode;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.cache.INm2Ix;
import com.mycompany.common.cache.SimpleKeyGenerator;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 18.10.14
 * Time: 10:59
 *
 */
public class TestData_bu
{

    static Map<String, Pair<String, Object>[]> domDef = getDomDef();
    static String[] hierarchy=getDimHierarchy();
    static Pair<Set<String>,String[]>[] prs2Cols =getTests();

    public static Pair<Set<String>,String[]>[] getTests()
    {
        Pair<Set<String>,String[]>[] prs=new Pair[]
              {
                    new Pair<Set<String>,String[]>(new HashSet<String>(Arrays.asList("WIND","I","*")),new String[]{"CNT","LN","ADL"}),
                    new Pair<Set<String>,String[]>(new HashSet<String>(Arrays.asList("WIND","II","*")),new String[]{"CNT","LN","ADL","XXX"}),
                    new Pair<Set<String>,String[]>(new HashSet<String>(Arrays.asList("WR","*")),new String[]{"CNT","LN"}),
                    new Pair<Set<String>,String[]>(new HashSet<String>(Arrays.asList("VIOL","*")),new String[]{"CNT"})
              };
        return prs;
    }

    public static String[] getDimHierarchy()
    {
        return new String[]{"CATEGORY_ID","EVENT_TYPE"};
    }


    public static String [] getColFigNamesByNodes(Pair<Set<String>,String[]>[] criteria,NNode node)
    {
        for (Pair<Set<String>, String[]> setPair : criteria)
        {
            String nm=node.name;
            if (setPair.first.contains(nm))
            {
                if (setPair.first.size()==1 || (setPair.first.size()==2 && setPair.first.contains("*")))
                    return setPair.second;
                else
                {
                    NNode node1=node.parent;
                    HashSet<String> prNext=new HashSet<String>(setPair.first);
                    prNext.remove(nm);
                    while (node1!=null)
                    {
                        if (prNext.contains(node1.name))
                        {
                            if (prNext.size()==1 || (prNext.size()==2 && prNext.contains("*")))
                                return setPair.second;
                            else
                            {
                                prNext.remove(node1.name);
                                node1=node1.parent;
                            }
                        }
                        else if (setPair.first.contains("*"))
                            node1=node1.parent;
                        else
                            break;
                    }
                }
            }
            else if (setPair.first.contains("*") && node.parent!=null)
            {
                String[] res=getColFigNamesByNodes(criteria,node.parent);
                if (res!=null && res.length>0)
                    return res;
            }
        }
        return new String[0];
    }

    public static Map[] getTestDatas()
    {
        List<Map> rv=new LinkedList<Map>();

        Map<String,Object> map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","WIND");
        map.put("EVENT_NAME","ОКНА");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",5);
        map.put("LN",100);
        map.put("ADL",3.5);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","WIND");
        map.put("EVENT_NAME","ОКНА");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",11);
        map.put("LN",112);
        map.put("ADL",5);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","WIND");
        map.put("EVENT_NAME","ОКНА");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",101);
        map.put("LN",2000);
        map.put("ADL",8);
        map.put("XXX",321123);

        rv.add(map);


        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","WIND");
        map.put("EVENT_NAME","ОКНА");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",111);
        map.put("LN",20);
        map.put("ADL",8);
        map.put("XXX",2112);

        rv.add(map);


        map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","WR");
        map.put("EVENT_NAME","ПРЕД.");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",15);
        map.put("LN",300);

        rv.add(map);



        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","WR");
        map.put("EVENT_NAME","ПРЕД.");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",10);
        map.put("LN",30);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","WR");
        map.put("EVENT_NAME","ПРЕД.");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",13);
        map.put("LN",120);

        rv.add(map);


        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","WR");
        map.put("EVENT_NAME","ПРЕД.");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",130);
        map.put("LN",12);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","VIOL");
        map.put("EVENT_NAME","НАР.");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",13);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","VIOL");
        map.put("EVENT_NAME","НАР.");

        map.put("CATEGORY_ID","I");
        map.put("CATEGORY_NAME","ПЕРВАЯ КАТ.");

        map.put("CNT",14);

        rv.add(map);

        map=new HashMap<String,Object>();

        map.put("PRED_ID",1);
        map.put("PRED_NAME","ПЧ-1");

        map.put("EVENT_TYPE","VIOL");
        map.put("EVENT_NAME","НАР.");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",13);

        rv.add(map);


        map=new HashMap<String,Object>();

        map.put("PRED_ID",2);
        map.put("PRED_NAME","ПЧ-2");

        map.put("EVENT_TYPE","VIOL");
        map.put("EVENT_NAME","НАР.");

        map.put("CATEGORY_ID","II");
        map.put("CATEGORY_NAME","ВТОРАЯ КАТ.");

        map.put("CNT",113);

        rv.add(map);


        return rv.toArray(new Map[rv.size()]);
    }


    public static IKeyGenerator getKeyGenrator() throws CacheException
    {
        return new SimpleKeyGenerator(new String[]{"PRED_ID"},new INm2Ix()
        {
            @Override
            public Map<String, Integer> getColName2Ix()
            {
                Map<String, Integer> rv = new HashMap<String, Integer>();
                rv.put("PRED_ID",0);
                return rv;
            }

            @Override
            public Map<Integer, String> getIx2ColName() {
                Map<Integer,String> rv = new HashMap<Integer,String>();
                rv.put(0,"PRED_ID");
                return rv;
            }
        });
    }


    /**
     * @return Отдать область определения измерений.
     */
    public static Map<String,Pair<String,Object>[]> getDomDef()
   {
       Map<String,Pair<String,Object>[]> domDef=new HashMap<String,Pair<String,Object>[]>();
       domDef.put("CATEGORY_ID",new Pair[]{new Pair<String,Object>("ПЕРВАЯ КАТ.","I"),new Pair<String,Object>("ВТОРАЯ КАТ.","II")});
       domDef.put("EVENT_TYPE",new Pair[]{new Pair<String,Object>("ОКНА","WIND"),new Pair<String,Object>("ПРЕД.","WR"),new Pair<String,Object>("НАРУШ.","VIOL")});
       return domDef;
   }


    public static void conVertMapByNode(String prefix,NNode root, Map<String,Object> map,Map<String,Object> rv)
    {
        Collection<NNode> values = root.nodes.values();

        Object tupVal;
        Iterator<NNode> iterator = values.iterator();
        if (iterator.hasNext())
        {
            NNode nNode = iterator.next();
            tupVal=map.get(nNode.tplColId);
            if (tupVal==null)
            {//Ощибка
                rv.clear();
                return;
            }

            Collection<NNode> nextVal = root.nodes.values();
            Iterator<NNode> itIt = nextVal.iterator();
            if (itIt.hasNext())
            {
                if (itIt.next().nodes.size()==0)
                {
                    for (NNode tNode : nextVal)
                    {
                        tupVal=map.get(tNode.tplColId);
                        rv.put(prefix + "#" + tNode.name, tupVal);
                    }
                }
                else
                {
                    NNode nextNode = root.nodes.get(tupVal.toString());
                    prefix=prefix+"#"+tupVal.toString();
                    conVertMapByNode(prefix, nextNode, map, rv);
                }

            }
            else
                {//Ощибка
                    rv.clear();
                }


        }
        else
        //Ощибка
            rv.clear();


    }

    public static Set<String> getFieldNamesByNode(NNode root)
    {
        Set<String> rv= new HashSet<String>();
        if (root.nodes.size()==0)
            rv.add(root.name);

        for (NNode node : root.nodes.values())
        {
            Set<String> resSet = getFieldNamesByNode(node);
            for (String name : resSet)
                rv.add(root.name+"#"+name);
        }
        return rv;
    }


    public static void addAllToNode(NNode root, int i)
    {
        if (i>=hierarchy.length)
        {
            String[] res=getColFigNamesByNodes(prs2Cols,root);
            if (res!=null && res.length>0)
            {
                for (String valName : res)
                {
                    NNode cRoot = new NNode(root);
                    cRoot.header = valName;
                    cRoot.name = valName;
                    cRoot.tplColId=valName;
                    root.nodes.put(cRoot.name, cRoot);
                }
            }
            else
            {
                System.out.println("res = " + res);
            }

        }
        else
        {
            String colName = hierarchy[i];

            Pair<String, Object>[] name2Vals = domDef.get(colName);

            for (Pair<String, Object> name2Val : name2Vals)
            {
                NNode cRoot = new NNode(root);
                cRoot.header = name2Val.first;
                cRoot.name = name2Val.second.toString();
                cRoot.tplColId=colName;
                root.nodes.put(cRoot.name, cRoot);
            }

            Collection<NNode> values = root.nodes.values();
            for (NNode cRoot : values)
                addAllToNode(cRoot,i+1);
            Set<String> ks=new HashSet<String>(root.nodes.keySet());
            for (String key : ks)
                if (root.nodes.get(key).nodes.size()==0)
                    root.nodes.remove(key);
        }
    }



//    public static void main(String[] args) throws CacheException {
//        NNode root = new NNode("");
//        addAllToNode(root, 0);
//        Set<String> res=getFieldNamesByNode(root);
//
//        for (String re : res) {
//            String head=getHeader(re);
//            System.out.println("head = " + head);
//        }
//
//        Map[] testData = getTestDatas();
//
//        Map<Object,Map<String,Object>> tuplesInTable=new HashMap<Object,Map<String,Object>>();
//        IKeyGenerator keyGenerator = getKeyGenrator();
//
//        for (Map tuple : testData)
//        {
//            Object key=keyGenerator.getKeyByTuple(tuple);
//            Map<String, Object> newTuple=tuplesInTable.get(key);
//            if (newTuple==null)
//                tuplesInTable.put(key,newTuple = new HashMap<String,Object>());
//            conVertMapByNode("", root, tuple, newTuple);
//        }
//        System.out.println("tuplesInTable = " + tuplesInTable.size());
//    }

    public static String getHeader(String fName)
    {
        int ix=new StringBuilder(fName).lastIndexOf("#");
        return fName.substring(ix+1,fName.length());
    }
}
