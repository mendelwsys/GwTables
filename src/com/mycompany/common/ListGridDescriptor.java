package com.mycompany.common;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.common.tables.GridOptionsSender;
import com.mycompany.common.tables.HeaderSpanMimic;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGridField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton.Pozdnev on 30.03.2015.
 */
public class ListGridDescriptor extends GridOptionsSender {


    public String getTableTitle() {
        return tableTitle;
    }

    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
    }

    String tableTitle;

    public HeaderSpanMimic getGroupedHeader() {
        return groupedHeader;
    }

    public void setGroupedHeader(HeaderSpanMimic groupedHeader) {
        this.groupedHeader = groupedHeader;
    }

    HeaderSpanMimic groupedHeader;

    public boolean isGrouped() {
        return isGrouped;
    }

    public void setGrouped(boolean isGrouped) {
        this.isGrouped = isGrouped;
    }

    boolean isGrouped = false;


    public ListGridDescriptor() {
    }


    public static ListGridDescriptor buildDescriptor(ListGridWithDesc lg) {
        ListGridDescriptor l = new ListGridDescriptor();

        String viewName = lg.getViewName();
        if (viewName==null || viewName.length()==0)
        {
            final Window target = lg.getTarget();
            if (target!=null)
                viewName=target.getTitle();
        }
        l.setTableTitle(viewName);


        l.setCellHeight(lg.getCellHeight());
        ListGridField[] fields = lg.getFields();
        List<FieldDescriptor> al = new ArrayList<FieldDescriptor>();
        l.setGrouped(lg.isGrouped());

        for (int i = 0, fieldnum = 0; i < fields.length; i++) {
            if (fields[i].getHidden() == null || !fields[i].getHidden()) {
                FieldDescriptor fd = new FieldDescriptor();
                fd.setName(fields[i].getName());

                fd.setTitle(fields[i].getTitle());
//fd.setWidth(Integer.parseInt(fields[i].getWidth()));
                if (fields[i].getType() == null) {


                } else if (fields[i].getType().equals(ListGridFieldType.DATETIME)) {
                    fd.setType(ListGridFieldType.DATETIME.getValue());
                    fd.setDateFormat(fields[i].getFormat());

                } else if (fields[i].getType().equals(ListGridFieldType.INTEGER)) {
                    fd.setType(ListGridFieldType.INTEGER.getValue());

                } else if (fields[i].getType().equals(ListGridFieldType.FLOAT)) {
                    fd.setType(ListGridFieldType.FLOAT.getValue());

                } else if (fields[i].getType().equals(ListGridFieldType.LINK)) {
                    fd.setType(ListGridFieldType.LINK.getValue());
                    fd.setLinkNameField(TablesTypes.LINKTEXT);
                    fd.setLinkURLField(fields[i].getName());


                }
fd.setNum(fieldnum);
                fieldnum++;
                al.add(fd);
            }

        }
        l.setChs(al.toArray(new FieldDescriptor[al.size()]));
        return l;

    }

}
