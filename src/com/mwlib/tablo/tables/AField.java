package com.mwlib.tablo.tables;

import com.mycompany.common.FieldException;
import com.mycompany.common.tables.ColumnHeadBean;
import com.smartgwt.client.types.ListGridFieldType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.05.14
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class AField implements FieldTranslator
{
    protected String name;
    protected String title;
    protected String type;
    protected boolean visible;
    protected boolean autofit;

    public boolean isAlwaysSend() {
        return alwaysSend;
    }

    public void setAlwaysSend(boolean alwaysSend) {
        this.alwaysSend = alwaysSend;
    }

    protected boolean alwaysSend;

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    protected String alignment;


    //protected

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    protected String linkText;

    static int ix;


    public AField(String title) {
        this("i"+String.valueOf(ix++), title);
    }

    public AField(String name, String title) {
        this(name, title, ListGridFieldType.TEXT.toString(),true);
    }

    public AField(String name, String title,boolean autofit) {
        this(name, title, ListGridFieldType.TEXT.toString(),true,autofit);
    }

    public AField(String name,String title,String type,boolean visible)
    {
        this.name = name;
        this.title = title;
        this.type = type;
        this.visible = visible;
    }

    public AField(String name,String title,String type,boolean visible,boolean autofit)
    {
        this(name,title,type,visible);
        this.autofit=autofit;
    }
    public AField(String name,String title,String type,boolean visible,boolean autofit,boolean alwaysSend)
    {
        this(name,title,type,visible);
        this.autofit=autofit;
        this.alwaysSend=alwaysSend;
    }


    public ColumnHeadBean getColumnHeadBean() throws FieldException
    {
        ColumnHeadBean columnHeadBean = new ColumnHeadBean(title, name, type, visible, linkText, autofit);
        columnHeadBean.setAlignment(alignment);
        return columnHeadBean;
    }

    public Object getS(Map<String,ColumnHeadBean> columns, Map tuple, Map<String, Object> _outTuple) throws FieldException
    {
        throw new UnsupportedOperationException("Can't call method for abstract class");
    }
    public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
    {
        return getS(translateHeads(column),tuple,outTuple);
    }

    public Map<String, ColumnHeadBean> translateHeads(ColumnHeadBean[] meta)
    {
        Map<String, ColumnHeadBean> mapMeta = new HashMap<String, ColumnHeadBean>();
        for (ColumnHeadBean aMeta : meta)
            mapMeta.put(aMeta.getName(), aMeta);
        return mapMeta;
    }

}
