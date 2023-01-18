package com.mycompany.common.analit;

import com.google.gwt.xml.client.*;
import com.mycompany.common.Pair;
import com.smartgwt.client.types.ListGridFieldType;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.10.14
 * Time: 12:29
 * Имплементация дескриптора для XML парсера
 */
public class AnalisysXML implements IAnalisysDesc
{

    Map<String, Pair<ListGridFieldType, DomainDim[]>> dimDef= new HashMap<String, Pair<ListGridFieldType, DomainDim[]>>();
    List<Pair<Set<String>,FigDef[]>> maps = new LinkedList<Pair<Set<String>,FigDef[]>>();
    List<GrpDef> grpX= new LinkedList<GrpDef>();
    List<GrpDef> grpY= new LinkedList<GrpDef>();
    Map<String,ColDef> colName2Def= new HashMap<String,ColDef>();



    public IAnalisysDesc initByXML(String xml)
    {

        Document res = XMLParser.parse(xml);

        NodeList dims = res.getElementsByTagName("dim");
        for (int i=0;i<dims.getLength();i++)
        {
            Node dim = dims.item(i);
            String attr_id=((Element) dim).getAttribute("name");
            String type_name=((Element) dim).getAttribute("type");
            ListGridFieldType type_id;
            if (type_name==null)
                type_id=ListGridFieldType.TEXT;
            else
                type_id=ListGridFieldType.valueOf(type_name);

            NodeList doms = ((Element) dim).getElementsByTagName("dom");
            List<DomainDim> dDef=new LinkedList<DomainDim>();

            for (int j=0;j<doms.getLength();j++)
            {
                Element dim1 = (Element) doms.item(j);
                dDef.add(new DomainDim(dim1.getAttribute("tid"),dim1.getAttribute("title"),Integer.parseInt(dim1.getAttribute("ord"))));
            }
            dimDef.put(attr_id,new Pair(type_id,dDef.toArray(new DomainDim[dDef.size()])));
        }

        NodeList nodeMaps = res.getElementsByTagName("map");
        for (int i=0;i<nodeMaps.getLength();i++)
        {
            Node map = nodeMaps.item(i);

            NodeList nodeKeys = ((Element) map).getElementsByTagName("key");
            NodeList nodeFNames = ((Element) map).getElementsByTagName("fname");

            Set<String> keys= new HashSet<String>();
            List<FigDef> figDef=new LinkedList<FigDef>();
            for (int j=0;j<nodeKeys.getLength();j++)
            {
                Element eKey = (Element) nodeKeys.item(j);
                keys.add(eKey.getAttribute("tid"));
            }

            for (int j=0;j<nodeFNames.getLength();j++)
            {
                Element eFName = (Element) nodeFNames.item(j);
                figDef.add(new FigDef(eFName.getAttribute("tid"),Integer.parseInt(eFName.getAttribute("ord"))));
            }
            this.maps.add(new Pair(keys,figDef.toArray(new FigDef[figDef.size()])));
        }
        //Иерархии по X и по Y
        parseGrp(res,"grpX",grpX);
        parseGrp(res,"grpY",grpY);

        //Описатель кортежа
        NodeList nodeTuple = res.getElementsByTagName("tuple");
        NodeList nodeCol = ((Element) nodeTuple.item(0)).getElementsByTagName("col");
        for (int i=0;i<nodeCol.getLength();i++)
        {
            Element eCol = (Element) nodeCol.item(i);
            String hide = eCol.getAttribute("hide");
            ColDef colDef=new ColDef(eCol.getAttribute("tid"),eCol.getAttribute("title"), (hide != null) && "true".equalsIgnoreCase(hide));
            colName2Def.put(colDef.colName,colDef);
        }
        return this;
    }

    private void parseGrp(Document res,String tagName,List<GrpDef> grp) {
        NodeList nodeGrpX = res.getElementsByTagName(tagName);
        NodeList nodeGrp = ((Element) nodeGrpX.item(0)).getElementsByTagName("fld");
        for (int i=0;i<nodeGrp.getLength();i++)
        {
            Element eFName = (Element) nodeGrp.item(i);
            String tid = eFName.getAttribute("tid");
            String tColId = eFName.getAttribute("tColId");
            if (tColId==null)
                tColId=tid;
            grp.add(new GrpDef(tid,tColId));
        }
    }

    @Override
    public Map<String, ColDef> getTupleDef() {
        return colName2Def;
    }


    @Override
    public Map<String, Pair<ListGridFieldType, DomainDim[]>> getDimDef() {
        return dimDef;
    }

    @Override
    public Pair<Set<String>,FigDef[]>[] getFigMapping()
    {
        return maps.toArray(new Pair[maps.size()]);
    }

    @Override
    public GrpDef[] getGrpYHierarchy() {
        return grpY.toArray(new GrpDef[grpY.size()]);
    }

    @Override
    public GrpDef[] getGrpXHierarchy() {
        return grpX.toArray(new GrpDef[grpX.size()]);
    }
}
