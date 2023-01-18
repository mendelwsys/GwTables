package com.mycompany.common.analit;

import com.mycompany.common.Pair;
import com.smartgwt.client.types.ListGridFieldType;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 18.10.14
 * Time: 10:59
 *
 */
public class UtilsData
{

    public static FigDef [] getColFigNamesByNodes(Pair<Set<String>, FigDef[]>[] criteria,NNode node)
    {
        for (Pair<Set<String>, FigDef[]> setPair : criteria)
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
                FigDef[] res=getColFigNamesByNodes(criteria,node.parent);
                if (res!=null && res.length>0)
                    return res;
            }
        }
        return new FigDef[0];
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


    public static void addAllY2Node(NNode root, int i,IAnalisysDesc desc)
    {
        GrpDef[] grpYHierarchy = desc.getGrpYHierarchy();
        Pair<Set<String>, FigDef[]>[] prs2Cols = desc.getFigMapping();
        Map<String, Pair<ListGridFieldType, DomainDim[]>> dimDef = desc.getDimDef();
        Map<String, ColDef> tupleDef = desc.getTupleDef();

        if (i>= grpYHierarchy.length)
        {

            FigDef[] res=getColFigNamesByNodes(prs2Cols,root);
            if (res!=null && res.length>0)
            {
                for (FigDef valName : res)
                {
                    NNode cRoot = new NNode(root);
                    cRoot.name = valName.col_id;
                    ColDef colDef = tupleDef.get(cRoot.name);
                    cRoot.header = colDef.title;
                    cRoot.tplColId=valName.col_id;
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
            String colName = grpYHierarchy[i].getTid();

            Pair<ListGridFieldType,  DomainDim[]> name2Vals = dimDef.get(colName);

            for (DomainDim name2Val : name2Vals.second)
            {
                NNode cRoot = new NNode(root);
                cRoot.header = name2Val.title;
                cRoot.name = name2Val.val.toString();
                cRoot.tplColId=colName;
                root.nodes.put(cRoot.name, cRoot);
            }

            Collection<NNode> values = root.nodes.values();
            for (NNode cRoot : values)
                addAllY2Node(cRoot, i + 1, desc);
            Set<String> ks=new HashSet<String>(root.nodes.keySet());
            for (String key : ks)
                if (root.nodes.get(key).nodes.size()==0)
                    root.nodes.remove(key);
        }
    }


    public static String getHeader(String fName)
    {
        int ix=new StringBuilder(fName).lastIndexOf("#");
        return fName.substring(ix+1,fName.length());
    }
}
