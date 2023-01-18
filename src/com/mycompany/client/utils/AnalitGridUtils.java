package com.mycompany.client.utils;

import com.mycompany.common.analit.NNode;
import com.mycompany.common.analit2.NNode2;
import com.smartgwt.client.widgets.grid.HeaderSpan;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 22.10.14
 * Time: 10:54
 * Утилиты преобразования для постраения аналитического грида
 */
public class AnalitGridUtils
{
    public static int buildSpans(HeaderSpan span,NNode root,String fname)
    {
        int rv=0;
        List<String> fields= null;
        List<HeaderSpan> headerSpans = null;

        for (NNode node : root.nodes.values())
        {

             if (node.nodes.size()==0)
             {
                 if (fields==null)
                     fields=new LinkedList<String>();
                 fields.add(fname+"#"+node.name);
             }
             else
             {
                 if (headerSpans==null)
                     headerSpans=new LinkedList<HeaderSpan>();

                 HeaderSpan span1 = new HeaderSpan();
                 int lrv=buildSpans(span1,node,fname+"#"+node.name);
                 if (rv<lrv)
                     rv=lrv;
                 headerSpans.add(span1);
             }
        }

        span.setTitle(root.header);
        if (fields!=null)
            span.setFields(fields.toArray(new String[fields.size()]));
        else //if (headerSpans!=null)
            span.setSpans(headerSpans.toArray(new HeaderSpan[headerSpans.size()]));

        return rv+10;
    }

    public static void removeEdgeSpans(HeaderSpan span,List<HeaderSpan> removeSpans) throws Exception
    {
        HeaderSpan[] spansArr = span.getSpans();
        List<String> removeFields=new LinkedList<String>();

        for (HeaderSpan headerSpan : spansArr)
        {
            final String[] fields = headerSpan.getFields();
            if (fields!=null && fields.length>0)
            {
                removeSpans.add(headerSpan);
                removeFields.addAll(Arrays.asList(fields));
            }
            else
                removeEdgeSpans(headerSpan,removeSpans);
        }

        if (removeFields.size()>0)
        {
            span.setSpans(null);
            span.setFields(removeFields.toArray(new String[removeFields.size()]));
        }
    }



    public static int buildSpans2(HeaderSpan span,NNode2 root,String fname,Map<String,Integer> key2Number,int dHeight) throws Exception {

        int maxHeight=0;

        List<HeaderSpan> headerSpans =new LinkedList<HeaderSpan>();
        {
            String title = root.getTitle();
            span.setTitle(title);
        }


        for (NNode2 node : root.getNodes())
        {

            NNode2[] nodes = node.getNodes();
            if (nodes==null || nodes.length==0)
            {
                 //Генерировать еще один span и к нему филл
                HeaderSpan nextSpan = new HeaderSpan();
//                nextSpan.setAlign(Alignment.LEFT);
                String title = node.getTitle();
                nextSpan.setTitle(title);

                String key = fname + "#" + node.getColId();
                Integer ix = key2Number.get(key);
                if (ix==null)
                    throw new Exception("Error indexing of nodes name: "+key);
                nextSpan.setFields(String.valueOf(ix));
                nextSpan.setHeight(dHeight);
                headerSpans.add(nextSpan);

//                if (title!=null && node.isRotate())
//                {
//                    nextSpan.setHeaderBaseStyle(nextSpan.getHeaderBaseStyle()!=null?nextSpan.getHeaderBaseStyle()+" headerButton rtext":"headerButton rtext" );
//                    dHeight*=4;
//                }

                maxHeight= dHeight;
            }
            else
            {
                 HeaderSpan nextSpan = new HeaderSpan();
//                 nextSpan.setAlign(Alignment.LEFT);
                 //Проверим надо ли генерировать выполняется ли условие отсутсвия загловка
                 int lHeight;
                 String _prefix;

                if (node.getVal()!=null && node.getVal().length()>0)
                    _prefix="#" + node.getVal();
                 else
                    _prefix ="";

                if (nodes.length==1 && (nodes[0].getType().equals(NNode2.NVALType))
                     && (nodes[0].getTitle()==null || nodes[0].getTitle().length()==0))
                {  //Не генерируем заголовок к полю
                    String key = fname + _prefix + "#" + nodes[0].getColId();
                    Integer ix = key2Number.get(key);
                    if (ix==null)
                        throw new Exception("Error indexing of nodes name: "+key);

                    String title = node.getTitle();
                    nextSpan.setTitle(title);
                    nextSpan.setFields(String.valueOf(ix));


//                    if (title!=null && node.isRotate())
//                    {
//                        nextSpan.setHeaderBaseStyle(nextSpan.getHeaderBaseStyle()!=null?nextSpan.getHeaderBaseStyle()+" headerButton rtext":"headerButton rtext" );
//                        lHeight= 5*dHeight;
//                    }
//                    else
                        lHeight= dHeight;
                    nextSpan.setHeight(lHeight);


                }
                else
                {

                    lHeight=buildSpans2(nextSpan,node, fname + _prefix,key2Number,dHeight);

                }


                if (maxHeight==0)
                     maxHeight=lHeight;

                if (lHeight>maxHeight)
                {
                    for (HeaderSpan headerSpan : headerSpans)
                    {
                        Integer height = headerSpan.getHeight();
                        if (height!=null)
                            headerSpan.setHeight(height +(lHeight-maxHeight));
                        else
                          throw new Exception("Can't define height of column : "+headerSpan.getTitle());
                    }
                    maxHeight=lHeight; //Подтягиваем все спаны которые были сформированы
                }
                else if (lHeight<maxHeight)
                { //Подтягиваем текущий спан до уровня всех спанов
                    Integer height = nextSpan.getHeight();
                    if (height!=null)
                        nextSpan.setHeight(height +(maxHeight-lHeight)); //Подтягиваем текущий спан
                    else
                        throw new Exception("Can't define height of column : "+nextSpan.getTitle());
                }


                headerSpans.add(nextSpan);
            }
        }



        span.setHeight(dHeight);
        if (headerSpans.size()!=0)
        {
            HeaderSpan[] spans = headerSpans.toArray(new HeaderSpan[headerSpans.size()]);
            for (HeaderSpan headerSpan : spans)
            {
                String[] flds = headerSpan.getFields();
                HeaderSpan[] sans = headerSpan.getSpans();
                if ((flds==null || flds.length==0) &&
                        (sans==null || sans.length==0))
                    throw new Exception("Wrong span id="+ root.getColId()+" title="+root.getTitle());
            }
            span.setSpans(spans);
        }
        else
            System.out.println("headerSpans = " + headerSpans.size());

        return maxHeight+ dHeight;
    }



    public static HeaderSpan findEdgeSpansByName(HeaderSpan rooSpan,String fname) throws Exception
    {

        String[] fields = rooSpan.getFields();
        if (fields.length>0)
            for (String field : fields) {
                if (field.equals(fname))
                    return rooSpan;
            }

        HeaderSpan[] spansArr = rooSpan.getSpans();
        for (HeaderSpan headerSpan : spansArr)
        {
            HeaderSpan rv = findEdgeSpansByName(headerSpan, fname);
            if (rv!=null)
                return rv;
        }

        return null;
    }


}
