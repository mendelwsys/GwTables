package com.mwlib.tablo.analit2;



import com.mycompany.common.analit2.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 23.10.14
 * Time: 12:54
 *
 */
public class NNodeBuilder
{

        //Описатель кортежа
    public void buildColDef(NodeList nodeTuple,Map<String,ColDef> ll) throws Exception
    {
        if (nodeTuple.getLength()>0)
        {
            NodeList nodeCol = ((Element) nodeTuple.item(0)).getElementsByTagName("col");
            for (int i=0;i<nodeCol.getLength();i++)
            {
                Element eCol = (Element) nodeCol.item(i);
                String hide = eCol.getAttribute("hide");

                String ftype=null;
                if (eCol.hasAttribute("ftype"))
                    ftype=eCol.getAttribute("ftype");
                String format=null;
                if (eCol.hasAttribute("format"))
                    format=eCol.getAttribute("format");

                String zval=null;
                if (eCol.hasAttribute("zval"))
                    zval = eCol.getAttribute("zval");
                String nval=null;
                if (eCol.hasAttribute("nval"))
                    nval = eCol.getAttribute("nval");

                ColDef colDef = new ColDef
                (
                        eCol.getAttribute("tid"), eCol.getAttribute("title"),
                        (hide != null) && "true".equalsIgnoreCase(hide),format,
                        ftype
                );

                if (zval!=null)
                {
                    colDef.setZval(zval);
                    if (nval==null)
                        colDef.setNval(zval);
                }

                if (nval!=null)
                {
                    colDef.setNval(nval);
                    if (zval==null)
                        colDef.setZval(nval);
                }

                ll.put(colDef.getColName(),colDef);
            }
        }
    }




    private void parseGrp(NodeList nodeGrpX,List<GrpDef> grp)
    {
        if (nodeGrpX.getLength()>0)
        {
            Element item = (Element) nodeGrpX.item(0);

            Integer colN=getIntegerParam(item,"colN",null);

            NodeList nodeGrp = item.getElementsByTagName("fld");
            for (int i=0;i<nodeGrp.getLength();i++)
            {
                Element eFName = (Element) nodeGrp.item(i);

                Integer _colN=getIntegerParam(eFName,"colN",null);

                String tid = eFName.getAttribute("tid");
                String tColId = eFName.getAttribute("tColId");
                if (tColId==null)
                    tColId=tid;
                if (_colN!=null)
                    grp.add(new GrpDef(tid,tColId,_colN));
                else
                    grp.add(new GrpDef(tid,tColId,colN));
            }
        }
    }


    public IAnalisysDesc xml2Desc(String xml) throws Exception
    {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document res = db.parse(
                new ByteArrayInputStream(xml.getBytes("utf-8"))
        );


        NodeList nodeMaps = res.getElementsByTagName("project");
        nodeMaps = nodeMaps.item(0).getChildNodes();

        List<NNode2> nNode2s=new LinkedList<NNode2>();
        buildNodes(nodeMaps, nNode2s,new Integer[]{0});

        List<GrpDef> grp = new LinkedList<GrpDef>();
        parseGrp(res.getElementsByTagName("grpX"), grp);

        Map<String,ColDef> colDefs = new HashMap<String,ColDef>();
        NodeList nodeTuple = res.getElementsByTagName("tuple");
        buildColDef(nodeTuple,colDefs);

        return new IAnalisysDescImpl(grp,colDefs,nNode2s);
    }

    public void buildNodes(NodeList nodeMaps,List<NNode2> ll,Integer[] startDefColoN) throws Exception
    {
        for (int i=0;i<nodeMaps.getLength();i++)
        {
            Node node = nodeMaps.item(i);
            if (node.getNodeType()!=Node.ELEMENT_NODE)
                continue;
            String nodeName = node.getNodeName();

            String colId=((Element) node).getAttribute("colid");
            String val=((Element) node).getAttribute("val");
            String title=((Element) node).getAttribute("title");
            String tblName=((Element) node).getAttribute("tblName");
            String filter=((Element) node).getAttribute("filter");
            String noDrill=((Element) node).getAttribute("noDrill");


            Boolean rotate = getBooleanParam(((Element) node), "rotate", false);
            if (nodeName.equals("NNode"))
            {
                NNode2 nnode = new NNode2(title, val, colId,"NNode",getIntegerParam((Element)node,"colN",null), rotate,tblName,filter);
                nnode.setNoDrill(noDrill);
                LinkedList<NNode2> chldNodes = new LinkedList<NNode2>();
                NodeList childNodes = node.getChildNodes();
                if (childNodes.getLength()>0)
                    buildNodes(childNodes, chldNodes,startDefColoN);
                else
                {
                    childNodes = node.getChildNodes();
                    buildNodes(childNodes, chldNodes,startDefColoN);
                }
                NNode2[] nodes = chldNodes.toArray(new NNode2[chldNodes.size()]);
                for (NNode2 nNode2 : nodes)
                    nNode2.setParent(nnode);
                nnode.setNodes(nodes);
                ll.add(nnode);
            }
            else if (nodeName.equals("NVAL"))
            {

                Integer colN = getIntegerParam((Element) node,"colN",startDefColoN[0]);
                startDefColoN[0]=colN;
                startDefColoN[0]++;

                NNode2 nval = new NNode2(title, val, colId,"NVAL",colN, rotate,tblName,filter);
                nval.setNoDrill(noDrill);
                ll.add(nval);
            }
        }
    }

    private Integer getIntegerParam(Element node,String attrName,Integer defСolN) {
        Integer colN=defСolN;
        try
        {
            if (node.hasAttribute(attrName))
                colN=Integer.parseInt(node.getAttribute(attrName));
        } catch (NumberFormatException e)
        {
            //
        }
        return colN;
    }


    private Boolean getBooleanParam(Element node,String attrName,Boolean defСolN) {
        Boolean colN=defСolN;
        if (node.hasAttribute(attrName))
           colN="true".equalsIgnoreCase(node.getAttribute(attrName));
        return colN;
    }

}
