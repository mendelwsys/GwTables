package com.mycompany.client.updaters;

import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.operations.ICliFilter;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.DiagramDesc;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.DataBoundComponent;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;


import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 04.09.14
 * Time: 12:53
 * Апдейтер данных грида
 */
public class BGridConstructor extends BMetaConstructor implements IGridConstructor
{


    public boolean isMetaWasSet() {
        return getGrid().isMetaWasSet();
    }

    public void setMetaWasSet(boolean metaWasSet) {
        super.setMetaWasSet(metaWasSet);
        getGrid().setMetaWasSet(metaWasSet);
    }

    public Record toMyRecord2(Record record)
    {
//        ListGridRecord newRecord = new ListGridRecord(record.getJsObj());
        Object _link=record.getAttributeAsObject(TablesTypes.CRDURL);
        if (_link!=null && !(_link instanceof String))
        {
            Map link=record.getAttributeAsMap(TablesTypes.CRDURL);
            record.setAttribute(TablesTypes.CRDURL, link.get("link"));
            record.setAttribute(TablesTypes.LINKTEXT, link.get(TablesTypes.LINKTEXT));
        }
        return record;
    }

    public void setLinkAndStyle2Record(Record recordOld, Record recordNew)
    {

//        Map link=recordOld.getAttributeAsMap(TablesTypes.CRDURL);
        Object _link=recordOld.getAttributeAsObject(TablesTypes.CRDURL);
        if (_link!=null && !(_link instanceof String))
        {
            Map link=recordOld.getAttributeAsMap(TablesTypes.CRDURL);
            recordNew.setAttribute(TablesTypes.CRDURL, link.get("link"));
            recordNew.setAttribute(TablesTypes.LINKTEXT, link.get(TablesTypes.LINKTEXT));
        }
        recordNew.setAttribute(TablesTypes.ROW_STYLE,recordOld.getAttribute(TablesTypes.ROW_STYLE));
    }





    private ListGridWithDesc grid;

    public BGridConstructor(ListGridWithDesc grid) {
        this.grid = grid;
    }

    public BGridConstructor() {
    }

    @Override
    public  void setDataBoundComponent(DataBoundComponent grid)
    {
        this.grid = (ListGridWithDesc)grid;
//        grid.addFormulaField();
//        grid.getOrCreateJsObj();
//        grid.addSummaryField();
    }

    @Override
    public ListGridField[] getAllFields()
    {
        return getGrid().getAllFields();
    }

    public DataBoundComponent getDataBoundComponent()
    {
        return getGrid();
    }

    public ListGridWithDesc getGrid()
    {
        return grid;
    }


    @Override
    public void setListGridFields(ListGridField[] fields)
    {
        final ListGridWithDesc listGridWithDesc = getGrid();
        if (listGridWithDesc!=null)
            fields=listGridWithDesc.addFormulaFields(fields);

        super.setListGridFields(fields);
        if (listGridWithDesc!=null)
        {
            listGridWithDesc.setFilterDS(this.getFilterDS());
            listGridWithDesc.setFieldMetaDS(this.getFieldsMetaDS());
            listGridWithDesc.setFields(fields);
        }
    }

    protected void setOptions(Record gridOptions)
    {
        Boolean fixedRecordHeights = gridOptions.getAttributeAsBoolean("fixedRecordHeights");
        final ListGridWithDesc listGridWithDesc = getGrid();
        if (listGridWithDesc!=null)
        {
            listGridWithDesc.setFixedRecordHeights(fixedRecordHeights);
            Integer headerHeight = gridOptions.getAttributeAsInt("headerHeight");
            if (headerHeight!=null && headerHeight > 0)
                listGridWithDesc.setHeaderHeight(headerHeight);
            Integer cellHeight = gridOptions.getAttributeAsInt("cellHeight");
            if (cellHeight!=null && cellHeight > 0)
                listGridWithDesc.setCellHeight(cellHeight);
        }

    }

    public void setDiagramDesc(Map mapDesc) throws SetGridException {

        if (mapDesc != null)
        {

            DiagramDesc ddesc = new DiagramDesc();
            ddesc.setType((String) mapDesc.get("type"));
            ArrayList tuples = (ArrayList) mapDesc.get("tuples");
            ddesc.setTuples((Map[]) tuples.toArray(new Map[tuples.size()]));

            ArrayList columnDesc = (ArrayList) mapDesc.get("columnDesc");
            String[][] arr2title = new String[columnDesc.size()][];
            for (int i = 0, titleSize = columnDesc.size(); i < titleSize; i++) {
                Object o = columnDesc.get(i);
                ArrayList attr2title = (ArrayList) o;
                arr2title[i] = (String[]) attr2title.toArray(new String[attr2title.size()]);
            }
            ddesc.setColumnDesc(arr2title);
            ddesc.setTitle((String) mapDesc.get("title"));
            ddesc.setwType((String) mapDesc.get("wType"));
            final ListGridWithDesc listGridWithDesc = getGrid();
            if (listGridWithDesc!=null)
                listGridWithDesc.setDesc(ddesc);
        }
    }

//    private int deb_ix=0;
    private boolean isCalcFilter=false;

    private TimeUpdaterCtrl timerCtrl= new TimeUpdaterCtrl(3,0);

    public void updateDataGrid(Record[] data, boolean resetAll) throws SetGridException
    {
        ListGridWithDesc listGridWithDesc = getGrid();
        if (listGridWithDesc==null)
            return;

        boolean filterWasChanged = listGridWithDesc.resetCliWasChanged();

        if (filterWasChanged)
            isCalcFilter=listGridWithDesc.isContainsFieldInCliFilter(TablesTypes.TIMESTAMP_FIELD);

        {
            boolean l_needToRecalcFilter=false;
            if (isCalcFilter)
                l_needToRecalcFilter=timerCtrl.isNeedCalcFilter();


            if (!resetAll && isCalcFilter && l_needToRecalcFilter)
            {
                RecordList rl=listGridWithDesc.getCacheData();
                if (rl!=null)
                {
                    int ln=rl.getLength();
                    for (int i=0;i<ln;i++)
                        rl.get(i).setAttribute(TablesTypes.TIMESTAMP_FIELD,timerCtrl.getCurrentDT());
                }
            }

            if (isCalcFilter && data!=null && data.length>0)
                for (Record record : data)
                    record.setAttribute(TablesTypes.TIMESTAMP_FIELD,timerCtrl.getCurrentDT());

            filterWasChanged=filterWasChanged || l_needToRecalcFilter;
        }


        if (resetAll)
        {
            listGridWithDesc.setData(new Record[0]);
            listGridWithDesc.setCacheData(new RecordList(new Record[0]));
        }

////Тест удаления
//        if (deb_ix%5==0 && (data==null || data.length==0))
//        {
//            RecordList rl = listGridWithDesc.getCacheData();
//            Record rec=rl.get(2);
//            rec.setAttribute(TablesTypes.ACTUAL,0);
//            data=new Record[]{rec};
//        }
////Тест обновления
//        if (deb_ix>5 && (data==null || data.length==0))
//        {
//            RecordList rl = listGridWithDesc.getCacheData();
//
//            //String[] ids=new String[]{"1;887167##54","1;887247##59","1;887107##56"};
//            String[] ids=new String[]{"1;888560##56","1;888441##54","1;888636##54"};
//            data=new Record[ids.length];
//            for (int i = 0, idsLength = ids.length; i < idsLength; i++)
//            {
//                int inRlIx1 = rl.findIndex(TablesTypes.KEY_FNAME, ids[i]);
//                if (inRlIx1<0)
//                    continue;
//                data[i] = rl.get(inRlIx1);
//                String pred_name1 = data[i].getAttribute("PRED_NAME");
//                int ix=pred_name1.lastIndexOf("_");
//                if (ix<0)
//                    pred_name1 = pred_name1 + "_" + deb_ix;
//                else
//                    pred_name1=pred_name1.substring(0,ix) + "_" + deb_ix;
//                data[i].setAttribute("PRED_NAME", pred_name1);
//
//                {
//                    Date dt=data[i].getAttributeAsDate("DT_KD");
//                    dt.setTime(dt.getTime()+60*60*1000);
//                    data[i].setAttribute("DT_KD", dt);
//                }
//
//                {
//                    Date dt=data[i].getAttributeAsDate("ND");
//                    dt.setTime(dt.getTime()-60*60*1000);
//                    data[i].setAttribute("ND", dt);
//                }
//
//            }
//        }
//        deb_ix++;


        if (data==null || data.length==0)
        {
            if (!resetAll && filterWasChanged)
            {
                RecordList rl=listGridWithDesc.getCacheData();
                List<ICliFilter> filters = listGridWithDesc.getCliFilters();
                if (filters!=null && filters.size()>0)
                {
                    Record[] rf = applyFilters(rl, filters);
                    listGridWithDesc.setData(rf);
                }
                else
                   listGridWithDesc.setData(rl);
            }
            if (!resetAll)
                recalcWindowsTable(listGridWithDesc);
            return;
        }

        if (resetAll)
              windUpdateInterval =System.currentTimeMillis();

        int[] countWasChanged=new int[]{0};
        RecordList rl = extractData4Update(data,countWasChanged);

        if (rl!=null)
        {
            listGridWithDesc.setCacheData(rl);

            List<ICliFilter> filters = listGridWithDesc.getCliFilters();
            if (filters!=null && filters.size()>0)
            {
                Record[] rf = applyFilters(rl, filters);
                rl=new RecordList(rf);
                countWasChanged[0]=1; //TODO предложить новый - более рациональный алгоритм предполагаю, что этот будет моргать при большом колличестве данных
            }
            else if (filterWasChanged) //Потому что в любом случае изменились данные иди нет надо переустановить грид
                countWasChanged[0]=1; //TODO предложить новый - более рациональный алгоритм предполагаю, что этот будет моргать при большом колличестве данных

            listGridWithDesc.setGroupByAsyncThreshold(30000);//TODO проработать проццесс считывания и установки данного правила
            if (listGridWithDesc.isGrouped())
            {
                    Tree tree=listGridWithDesc.getGroupTree();
                    Set<String> openFolders=new HashSet<String>();
                    TreeNode[] nodes = tree.getDescendantFolders();
                    for (TreeNode node : nodes)
                        if (tree.isOpen(node))
                            openFolders.add(node.getAttribute("groupValue"));

                    if (countWasChanged[0]!=0)
                        listGridWithDesc.setData(rl);
                    else
                        listGridWithDesc.groupBy(listGridWithDesc.getGroupByFields());

                    Tree tree1=listGridWithDesc.getGroupTree();
                    nodes = tree1.getDescendantFolders();
                    for (TreeNode node : nodes)
                    {
                            if(openFolders.contains(node.getAttribute("groupValue")))
                                tree1.openFolder(node);
                            else
                                tree1.closeFolder(node);
                    }
            }
            else
            if (countWasChanged[0]!=0)
                listGridWithDesc.setData(rl);



            listGridWithDesc.resort();
            if (countWasChanged[0]==0)
                listGridWithDesc.markForRedraw();
        }

    }


    //-----------------TODO Все это перенести в отдельный апдейтер для окон или формировать на сервере спец. вычислительную функцию!!!!!
    private Long windUpdateInterval =null;
    public static String getTimeInterval(Date nd, long current, int m)
    {
        long min = m * (nd.getTime() - current) / 60000;
        if (min > 60)
            return " " + (min / 60) + "ч :" + min % 60 + " мин";
        else
            return " " + min + " мин";
    }
    private static boolean setRowStyle(Record rec,long current)
    {
        Integer status_fact=rec.getAttributeAsInt("STATUS_FACT");
        Pair<String,String> pr=new Pair<String,String>();

        if (status_fact!=null && (1==status_fact || 0==status_fact))
        {


            Date nd = rec.getAttributeAsDate("ND");
            Date kd =rec.getAttributeAsDate("KD");

            Date gnd = rec.getAttributeAsDate("DT_ND");
            Date gkd = rec.getAttributeAsDate("DT_KD");

            if (gnd != null && gnd.getTime() > 0)
                nd = gnd;
            if (gkd != null && gkd.getTime() > 0)
                kd = gkd;

            if (0==status_fact)
            {
                if (nd.getTime() > current)
                {

                    final long l = nd.getTime() - current;
                    if (l / 60000 < 30)
                    {
                        pr.first = "Ожидание открытия:" + getTimeInterval(nd, current, 1);
                        if (l>=0)
                            pr.second="background-color:#55FFFF;";
                        else
                            pr.second="background-color:#DD8800;";
                    }
                    else
                        pr.first = "До начала:" + getTimeInterval(nd, current, 1);
                    rec.setAttribute(TablesTypes.LINKTEXT,pr.first);
                    rec.setAttribute(TablesTypes.ROW_STYLE,pr.second);
                    return true;
                }
            }
            else
            {
                if (kd.getTime() < current)
                {
                    if (kd.getTime() < current + 5*60000)
                        pr.first = "Передержка:" + getTimeInterval(kd, current, -1);
                    else
                        pr.first = "Ожидание закрытия:" + getTimeInterval(kd, current, -1);
                    pr.second="background-color:#FF3300;";
                }
                else
                {
                    final long l = kd.getTime() - current;
                    if (l / 60000 < 30)
                    {
                        pr.first = "Ожидание закрытия:" + getTimeInterval(kd, current, 1);
                        pr.second="background-color:#DDDD00;";
                    }
                    else
                    {
                        pr.first = "До окончания:" + getTimeInterval(kd, current, 1);
                        pr.second="background-color:#77DD00;";
                    }
                }
                rec.setAttribute(TablesTypes.LINKTEXT,pr.first);
                rec.setAttribute(TablesTypes.ROW_STYLE, pr.second);
                return true;
            }
        }
        return false;
    }

    private void recalcWindowsTable(ListGridWithDesc listGridWithDesc)
    {
        //TODO Добавить обработку окна
        {
            IDataFlowCtrl ctrl = listGridWithDesc.getCtrl();
            if (ctrl!=null)
            {
                Criteria criteria=ctrl.getCriteria();
                if (criteria!=null)
                {
                    Map vals=criteria.getValues();
                    if (vals!=null)
                    {
                        String ttype = String.valueOf(vals.get(TablesTypes.TTYPE));
                        if (ttype!=null && ttype.contains(TablesTypes.WINDOWS))
                        { //Добавляем к серверному фильтру критерий
                            final long l = System.currentTimeMillis();
                            if (windUpdateInterval !=null && l- windUpdateInterval >60000)
                            {
//                                ctrl.setFullDataUpdate();
                                RecordList rl = listGridWithDesc.getCacheData();
                                int len = rl.getLength();
                                boolean redraw=false;
                                long current = System.currentTimeMillis()+TablesTypes.tMSK;
                                for (int i=0;i<len;i++)
                                {

                                    final Record rec = rl.get(i);
                                    redraw|=setRowStyle(rec,current);
                                }
                                windUpdateInterval =l;
                                if (redraw)
                                    listGridWithDesc.markForRedraw();
                            }
                            else if (windUpdateInterval ==null)
                                windUpdateInterval =l;
                        }
                    }
                }
            }
        }
    }

    public Record[] applyFilters(RecordList rl, List<ICliFilter> filters)
    {
        Record[] rf=rl.toArray();

//        if (isCalcFilter)
//        {
//            Date value = new Date();
//            for (Record record : rf)
//                record.setAttribute(TablesTypes.TIMESTAMP_FIELD, value);
//        }


        for (ICliFilter filter : filters)
            rf=filter.filter(rf);
        return rf;
    }


//    public Record[] applyFilters(Record[] rf, List<ICliFilter> filters)
//    {
//        for (ICliFilter filter : filters)
//            rf=filter.filter(rf);
//        return rf;
//    }


    protected RecordList extractData4Update(Record[] data,int[] countWasChanged)
    {
        ListGridWithDesc gridWithDesc = getGrid();
        if (gridWithDesc==null)
            return new RecordList();

        //RecordList rl = gridWithDesc.isGrouped()?gridWithDesc.getOriginalRecordList():gridWithDesc.getDataAsRecordList();

        RecordList rl = gridWithDesc.getCacheData();
        if (rl==null)
        {
            rl = gridWithDesc.isGrouped()?gridWithDesc.getOriginalRecordList():gridWithDesc.getDataAsRecordList();
            gridWithDesc.setCacheData(rl);
        }

        boolean notSearch=rl.isEmpty();
        List<Integer> toAddList = new LinkedList<Integer>();

        for (int i = 0; i < data.length; i++)
        {
            Record inRecord = data[i];

            String id = inRecord.getAttributeAsString(TablesTypes.KEY_FNAME);
            Integer actual = inRecord.getAttributeAsInt(TablesTypes.ACTUAL);
            if (actual==null) actual=0;

            int inRlIx=-1;
            if (!notSearch)
                inRlIx = rl.findIndex(TablesTypes.KEY_FNAME, id);

            if (inRlIx >= 0)
            {
                if (actual > 0)
                {
//                    if (countWasChanged[0]==0)
                    {
                        Record rrl = rl.get(inRlIx);
                        Record newRect = toMyRecord2(inRecord);
                        String[] atrs=newRect.getAttributes();
                        for (String key : atrs)
                        {
                            Object value = newRect.getAttributeAsObject(key);
                            rrl.setAttribute(key, value);
                        }

//                        Map newMap=newRect.toMap();
//                        for (Object key : newMap.keySet())
//                        {
//                            Object value = newMap.get(key);
//
////                            if ("3".equals(key)) //Тест ощибочных изменений в результате рек. сета
////                            {
////                                Object v1=rrl.getAttribute((String)key);
////                                if ((v1==null && value!=null) || !v1.equals(value))
////                                    System.out.println("was value = " + v1+" value; "+value);
////                            }
//
//
//                            rrl.setAttribute((String) key, value);
//                        }

//                        rrl.setRowStyle(newRect.getRowStyle());
//                        rrl.setLinkText(newRect.getLinkText());

//TODO ????                        setLinkAndStyle2Record(rrl, newRect);    //Если Link пришел, мы его перекопируем в toMyRecord2() плюс потом копирование аттрибутов, если не пришел тогда копировать нечего
                    }
//                    else
//                       rl.set(inRlIx, toMyRecord(inRecord));
                }
                else
                {
                    rl.removeAt(inRlIx);
                    countWasChanged[0]=1;
                }
            }
            else if (actual > 0)
            {
                toAddList.add(i);
                countWasChanged[0]=1;
            }
        }

        for (Integer i : toAddList)
            rl.add(toMyRecord2(data[i]));

//        int recCnt=rl.toArray().length;
//        for (int i=0;i<recCnt;i++)
//            rl.get(i).setAttribute(TablesTypes.ORDIX,i);

        return rl;
    }

//    protected RecordList extractData4UpdateExp(Record[] data,int[] countWasChanged)
//    {
//        ListGridWithDesc gridWithDesc = getGrid();
//        if (gridWithDesc==null)
//            return new RecordList();
//
//        RecordList rl = gridWithDesc.getCacheData();
//        if (rl==null)
//        {
//            rl = gridWithDesc.isGrouped()?gridWithDesc.getOriginalRecordList():gridWithDesc.getDataAsRecordList();
//            gridWithDesc.setCacheData(rl);
//        }
//
//        Map<String,Integer> key2Ix=new HashMap<String,Integer>();
//        {
//            for (int i=0,ln=rl.getLength();i<ln;i++)
//                key2Ix.put(rl.get(i).getAttributeAsString(TablesTypes.KEY_FNAME),i);
//        }
//
//        for (int i = 0; i < data.length; i++)
//        {
//            Record inRecord = data[i];
//
//            String id = inRecord.getAttributeAsString(TablesTypes.KEY_FNAME);
//            Integer actual = inRecord.getAttributeAsInt(TablesTypes.ACTUAL);
//            if (actual==null) actual=0;
//
//            Integer inRlIx=key2Ix.get(id);
//
//            if (inRlIx!=null && inRlIx >= 0)
//            {
//                if (actual > 0)
//                {
//                    Record rrl = rl.get(inRlIx);
//                    Record newRect = toMyRecord2(inRecord);
//                    String[] atrs=newRect.getAttributes();
//                    for (String key : atrs)
//                    {
//                        Object value = newRect.getAttributeAsObject(key);
//                        rrl.setAttribute(key, value);
//                    }
//                    String wasActual = rrl.getAttribute(TablesTypes.ACTUAL);
//                    if (wasActual!=null)
//                        rrl.setAttribute(TablesTypes.ACTUAL,1);
//                }
//                else
//                {
//                    Record rrl = rl.get(inRlIx);
//                    rrl.setAttribute(TablesTypes.ACTUAL,0);
//                    countWasChanged[0]=1;
//                }
//            }
//            else if (actual > 0)
//            {
//                final Record rrl = toMyRecord2(data[i]);
//                if (key2Ix.containsKey(id))
//                    throw new RuntimeException("Double key while adding");
//                key2Ix.put(id,rl.getLength());
//                rrl.setAttribute(TablesTypes.ACTUAL,1);
//                rl.add(rrl);
//                countWasChanged[0]=1;
//            }
//        }
//
//        for (int i=0;i<rl.getLength();)
//        {
//            Integer actual=rl.get(i).getAttributeAsInt(TablesTypes.ACTUAL);
//            if (actual!=null && actual==0)
//                rl.removeAt(i);
//            else
//                i++;
//        }
//
//        return rl;
//    }


//    public void updateDataGridWk(Record[] data, boolean resetAll) throws SetGridException
//    {
//        ListGridWithDesc gridWithDesc = getGrid();
//        if (resetAll)
//            gridWithDesc.setData(new Record[0]);
//
//        if (data==null || data.length==0)
//            return;
//
//        int[] countWasChanged=new int[]{0};
//        RecordList rl = extractData4UpdateWK(data, countWasChanged);
//
//
//        if (rl!=null)
//        {
//
//            //gridWithDesc.getGroupV
//
//
//            gridWithDesc.setGroupByAsyncThreshold(5000);//TODO проработать проццесс считывания и установки данного правила
//
//            if (gridWithDesc.isGrouped())
//            {
//                    Tree tree=gridWithDesc.getGroupTree();
//                    Set<String> openFolders=new HashSet<String>();
//                    TreeNode[] nodes = tree.getDescendantFolders();
//                    for (TreeNode node : nodes)
//                        if (tree.isOpen(node))
//                            openFolders.add(node.getAttribute("groupValue"));
//
//                    if (countWasChanged[0]!=0)
//                        gridWithDesc.setData(rl);
//                    else
//                        gridWithDesc.groupBy(gridWithDesc.getGroupByFields());
//
//                    Tree tree1=gridWithDesc.getGroupTree();
//                    nodes = tree1.getDescendantFolders();
//                    for (TreeNode node : nodes)
//                    {
//                            if(openFolders.contains(node.getAttribute("groupValue")))
//                                tree1.openFolder(node);
//                            else
//                                tree1.closeFolder(node);
//                    }
//            }
//            else if (countWasChanged[0]!=0)
//                gridWithDesc.setData(rl);
//
//            gridWithDesc.setCacheData(rl);
//
//            gridWithDesc.resort();
//            if (countWasChanged[0]==0)
//                gridWithDesc.markForRedraw();
//        }
//    }




//    protected RecordList extractData4UpdateWK(Record[] data,int[] countWasChanged)
//    {
//        ListGridWithDesc gridWithDesc = getGrid();
//
//        RecordList rl = gridWithDesc.isGrouped()?gridWithDesc.getOriginalRecordList():gridWithDesc.getDataAsRecordList();
//
//
//        List<Integer> toAddList = new LinkedList<Integer>();
//
//        for (int i = 0; i < data.length; i++)
//        {
//            Record record = data[i];
//
//            String id = record.getAttributeAsString(TablesTypes.KEY_FNAME);
//            Integer actual = record.getAttributeAsInt(TablesTypes.ACTUAL);
//            if (actual==null) actual=0;
//
//            int inRlIx = rl.findIndex(TablesTypes.KEY_FNAME, id);
//
//            if (inRlIx >= 0)
//            {
//                if (actual > 0)
//                {
////                    if (countWasChanged[0]==0)
//                    {
//                        MyRecord rrl = (MyRecord)rl.get(inRlIx);
//
//                        MyRecord newRect = toMyRecord(record);
//                        Map mp=newRect.toMap();
//
//                        for (Object key : mp.keySet())
//                        {
//                            Object value = mp.get(key);
//
////                            if ("3".equals(key)) //Тест ощибочных изменений в результате рек. сета
////                            {
////                                Object v1=rrl.getAttribute((String)key);
////                                if ((v1==null && value!=null) || !v1.equals(value))
////                                    System.out.println("was value = " + v1+" value; "+value);
////                            }
//
//
//                            rrl.setAttribute((String) key, value);
//                        }
//                        rrl.setRowStyle(newRect.getRowStyle());
//                        rrl.setLinkText(newRect.getLinkText());
//                    }
////                    else
////                       rl.set(inRlIx, toMyRecord(record));
//                }
//                else
//                {
//                    rl.removeAt(inRlIx);
//                    countWasChanged[0]=1;
//                }
//            }
//            else if (actual > 0)
//            {
//                toAddList.add(i);
//                countWasChanged[0]=1;
//            }
//        }
//
//        for (Integer i : toAddList)
//            rl.add(toMyRecord(data[i]));
//
//        int recCnt=rl.toArray().length;
//        for (int i=0;i<recCnt;i++)
//            rl.get(i).setAttribute(TablesTypes.ORDIX,i);
//
//        return rl;
//    }


//    public void __updateDataGrid(Record[] data) throws SetGridException
//    {
//        int[] ints=new int[]{0};
//        RecordList rl = __extractData4Update(data, ints);
//        if (rl!=null)
//        {
//            final ListGridWithDesc gridWithDesc = getGrid();
//            gridWithDesc.setGroupByAsyncThreshold(300);//TODO проработать проццесс считывания и установки данного правила
//
//
//
//            if (gridWithDesc.isGrouped())
//            {
//                String[] groupByFields = gridWithDesc.getGroupByFields();
//                Tree tree=(Tree)rl;
//
//
////                for (String groupByField : groupByFields)
////                {
////                    if (changed.get(groupByField))
////                    {
////                        Tree tree=gridWithDesc.getGroupTree(); //Нельзя так при добавлении/удалении у нас не сохраняются (открыто или закрыто) состояния папок
//
//                final Set<String> openFolders=new HashSet<String>();
//                {
//                        TreeNode[] nodes = tree.getDescendantFolders();
//                        for (TreeNode node : nodes)
//                            if (tree.isOpen(node))
//                                openFolders.add(node.getAttribute("groupValue"));
//                        if (ints[0]==0)
//                            gridWithDesc.groupBy(groupByFields);
//                }
//
////                gridWithDesc.addGroupByCompleteHandler(new GroupByCompleteHandler() {
////                    @Override
////                    public void onGroupByComplete(GroupByCompleteEvent event) {
//                Tree tree1=gridWithDesc.getGroupTree();
//                TreeNode[] nodes = tree1.getDescendantFolders();
//                for (TreeNode node : nodes)
//                {
//                        if(openFolders.contains(node.getAttribute("groupValue")))
//                            tree1.openFolder(node);
//                        else
//                            tree1.closeFolder(node);
//                }
////                    }
////                });
//
////                        break;
////                    }
////                }
//            }
////            else
//           gridWithDesc.resort();
//           gridWithDesc.markForRedraw();
//        }
//    }

//    protected RecordList __extractData4Update(Record[] data,int[] ints)
//    {
//        ListGridWithDesc gridWithDesc = getGrid();
//
//        RecordList rl = gridWithDesc.getDataAsRecordList();
//
//        List<Integer> toAddList = new LinkedList<Integer>();
//
//
//        for (int i = 0; i < data.length; i++)
//        {
//            Record record = data[i];
//
//            String id = record.getAttributeAsString(TablesTypes.KEY_FNAME);
//            Integer actual = record.getAttributeAsInt(TablesTypes.ACTUAL);
//            if (actual==null) actual=0;
//
//            int inRlIx = rl.findIndex(TablesTypes.KEY_FNAME, id);
//
//            MyRecord rrl=null;
//            if (rl instanceof Tree)
//                rrl= (MyRecord) rl.find(TablesTypes.KEY_FNAME, id);
//
//            if (inRlIx >= 0 || rrl!=null)
//            {
//                if (actual > 0)
//                {
//                    if (rl instanceof Tree)
//                    {
//                        MyRecord newRect = toMyRecord(record);
//                        Map mp=newRect.toMap();
//
//                        for (Object key : mp.keySet())
//                        {
//                            Object value = mp.get(key);
////                            Boolean aBoolean = isChanged.get(key);
////                            if (aBoolean!=null && aBoolean)
////                                rrl.setAttribute((String)key, value);
////                            else if (
////                                     (value!=null &&  !value.equals(rrl.getAttributeAsObject((String) key)))
////                                     ||
////                                     (value==null && rrl.getAttributeAsObject((String) key)!=null)
////                                    )
////                            {
////                                isChanged.put(key, true);
////                                rrl.setAttribute((String)key, value);
////                            }
//                            rrl.setAttribute((String)key, value);
//                        }
//                        rrl.setRowStyle(newRect.getRowStyle());
//                        rrl.setLinkText(newRect.getLinkText());
//                    }
//                    else
//                        rl.set(inRlIx, toMyRecord(record));
//                }
//                else
//                {
//                    if (rrl!=null)
//                        gridWithDesc.removeData(rrl);
//                    else
//                    {
//                        rl.removeAt(inRlIx);
//                        ints[0]=1;
//                    }
//                }
//            }
//            else if (actual > 0)
//            {
//                toAddList.add(i);
//                ints[0]=1;
//            }
//        }
//
//        for (Integer i : toAddList)
//        {
//            //rl.add(toMyRecord(data[i])); //TODO Важно даже не сколько удаление, сколько динамическое добавление в дерево
//            gridWithDesc.addData(toMyRecord(data[i]));
//        }
//
//        int recCnt=rl.toArray().length;
//        for (int i=0;i<recCnt;i++)
//            rl.get(i).setAttribute(TablesTypes.ORDIX,i);
//
////        if (totalChange)
////            for (Object o : isChanged.keySet())
////                isChanged.put(o,true);
//
//        return rl;
//    }

}
