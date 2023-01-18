package com.mycompany.client.test.updaters;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.test.Demo.MyRecord;
import com.mycompany.client.operations.ICliFilter;
import com.mycompany.client.updaters.BMetaConstructor;
import com.mycompany.client.updaters.IGridConstructor;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.DiagramDesc;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.util.SC;
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
public class BGridConstructor_BR extends BMetaConstructor implements IGridConstructor
{

    public boolean isMetaWasSet() {
        return getGrid().isMetaWasSet();
    }

    public void setMetaWasSet(boolean metaWasSet) {
        super.setMetaWasSet(metaWasSet);
        getGrid().setMetaWasSet(metaWasSet);
    }

    public MyRecord toMyRecord(Record record)
    {
        Object v = record.getAttribute(TablesTypes.ROW_STYLE);
        MyRecord myRecord;
        if (record instanceof MyRecord)
        {
            myRecord=(MyRecord)record;

            Object value = record.getAttributeAsMap(TablesTypes.CRDURL);
            if (value!=null)
            {
                Map value1 = (Map) value;
                myRecord.setAttribute(TablesTypes.CRDURL, value1.get("link"));
                myRecord.setLinkText((String) value1.get("linkText"));
                myRecord.setAttribute(TablesTypes.ROW_STYLE,v);
            }
        }
        else
        {
            Map recordProperties = record.toMap();
            myRecord = new MyRecord();
            for (Object o : recordProperties.keySet())
            {
                Object value = recordProperties.get(o);
                if (value instanceof Map)
                {
                    Map value1 = (Map) value;
                    myRecord.setAttribute((String) o, value1.get("link"));
                    myRecord.setLinkText((String) value1.get("linkText"));
                } else
                    myRecord.setAttribute((String) o, value);
                //myRecord.setLinkText();
    //            grid.getField()
    //            if (record.getAttribute())
    //            myRecord.setLinkText();
            }
            myRecord.setAttribute(TablesTypes.ROW_STYLE,v);
        }
        if (v != null)
            myRecord.setRowStyle((String) v);

        return myRecord;
    }

    private ListGridWithDesc grid;

    public BGridConstructor_BR(ListGridWithDesc grid) {
        this.grid = grid;
    }

    public BGridConstructor_BR() {
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
        super.setListGridFields(fields);
        final ListGridWithDesc listGridWithDesc = getGrid();
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
    public void updateDataGrid(Record[] data, boolean resetAll) throws SetGridException
    {
        ListGridWithDesc listGridWithDesc = getGrid();
        if (listGridWithDesc==null)
            return;

        final boolean filterWasChanged = listGridWithDesc.resetCliWasChanged();




        if (resetAll)
        {
            listGridWithDesc.setData(new Record[0]);
            listGridWithDesc.setCacheData(new RecordList(new Record[0]));
        }

//Тест удаления
//        if (deb_ix%5==0 && (data==null || data.length==0))
//        {
//            RecordList rl = listGridWithDesc.getCacheData();
//            Record rec=rl.get(2);
//            rec.setAttribute(TablesTypes.ACTUAL,0);
//
//            data=new Record[]{rec};
//        }
//Тест обновления
//        if (deb_ix>5 && (data==null || data.length==0))
//        {
//            RecordList rl = listGridWithDesc.getCacheData();
//
//            String[] ids=new String[]{"1;887167##54","1;887247##59","1;887107##56"};
//            data=new Record[ids.length];
//            for (int i = 0, idsLength = ids.length; i < idsLength; i++)
//            {
//                int inRlIx1 = rl.findIndex(TablesTypes.KEY_FNAME, ids[i]);
//                data[i] = rl.get(inRlIx1);
//                String pred_name1 = data[i].getAttribute("PRED_NAME");
//                int ix=pred_name1.lastIndexOf("_");
//                if (ix<0)
//                    pred_name1 = pred_name1 + "_" + deb_ix;
//                else
//                    pred_name1=pred_name1.substring(0,ix) + "_" + deb_ix;
//                data[i].setAttribute("PRED_NAME", pred_name1);
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
            return;
        }


        int[] countWasChanged=new int[]{0};

        long update1=System.currentTimeMillis();
        RecordList rl = extractData4Update(data,countWasChanged);
        long update2=System.currentTimeMillis();
        update1=update2-update1;


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

            listGridWithDesc.setGroupByAsyncThreshold(5000);//TODO проработать проццесс считывания и установки данного правила

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
            else if (countWasChanged[0]!=0)
                listGridWithDesc.setData(rl);

            long update3=System.currentTimeMillis();
            update2=update3-update2;

            listGridWithDesc.resort();
            if (countWasChanged[0]==0)
                listGridWithDesc.markForRedraw();

            update3=System.currentTimeMillis()-update3;
            SC.say(" times:" + update1+" "+ update2+" "+update3);
        }
    }

    private Record[] applyFilters(RecordList rl, List<ICliFilter> filters)
    {
        Record[] rf=rl.toArray();
        for (ICliFilter filter : filters)
            rf=filter.filter(rf);
        return rf;
    }


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

//        long update1=System.currentTimeMillis();

        for (int i = 0; i < data.length; i++)
        {
            Record record = data[i];

            String id = record.getAttributeAsString(TablesTypes.KEY_FNAME);
            Integer actual = record.getAttributeAsInt(TablesTypes.ACTUAL);
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
                        MyRecord rrl = (MyRecord)rl.get(inRlIx);

                        MyRecord newRect = toMyRecord(record);
                        Map mp=newRect.toMap();

                        for (Object key : mp.keySet())
                        {
                            Object value = mp.get(key);

//                            if ("3".equals(key)) //Тест ощибочных изменений в результате рек. сета
//                            {
//                                Object v1=rrl.getAttribute((String)key);
//                                if ((v1==null && value!=null) || !v1.equals(value))
//                                    System.out.println("was value = " + v1+" value; "+value);
//                            }


                            rrl.setAttribute((String) key, value);
                        }
                        rrl.setRowStyle(newRect.getRowStyle());
                        rrl.setLinkText(newRect.getLinkText());
                    }
//                    else
//                       rl.set(inRlIx, toMyRecord(record));
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

        int toAddListSize = toAddList.size();

        for (int i = 0; i < toAddListSize; i++)
            rl.add(toMyRecord(data[i]));

//        long update2=System.currentTimeMillis();
//        update1=update2-update1;

//        for (int i = 0; i < toAddListSize; i++)
//            rl.add(new MyRecord());

//        long update3 = System.currentTimeMillis();
//        update2= update3 -update2;
//
//        for (int i1 = 0;  i1 < toAddListSize; i1++)
//        {
//                Integer i = toAddList.get(i1);
//                toMyRecord2(data[i], (MyRecord)rl.get(i1));
//        }
//
//        update3=System.currentTimeMillis()-update3;
//        SC.say(" times:" + update1+" "+ update2+" "+update3);


//todo ЗАКОМЕНТИРОВАНО 13032015 ТАК КАК НЕ ЯСНО ЗАЧЕМ
//        int recCnt=rl.toArray().length;
//        for (int i=0;i<recCnt;i++)
//            rl.get(i).setAttribute(TablesTypes.ORDIX,i);

        return rl;
    }



    public void updateDataGridWk(Record[] data, boolean resetAll) throws SetGridException
    {
        ListGridWithDesc gridWithDesc = getGrid();
        if (resetAll)
            gridWithDesc.setData(new Record[0]);

        if (data==null || data.length==0)
            return;

        int[] countWasChanged=new int[]{0};
        RecordList rl = extractData4UpdateWK(data, countWasChanged);


        if (rl!=null)
        {

            //gridWithDesc.getGroupV


            gridWithDesc.setGroupByAsyncThreshold(5000);//TODO проработать проццесс считывания и установки данного правила

            if (gridWithDesc.isGrouped())
            {
                    Tree tree=gridWithDesc.getGroupTree();
                    Set<String> openFolders=new HashSet<String>();
                    TreeNode[] nodes = tree.getDescendantFolders();
                    for (TreeNode node : nodes)
                        if (tree.isOpen(node))
                            openFolders.add(node.getAttribute("groupValue"));

                    if (countWasChanged[0]!=0)
                        gridWithDesc.setData(rl);
                    else
                        gridWithDesc.groupBy(gridWithDesc.getGroupByFields());

                    Tree tree1=gridWithDesc.getGroupTree();
                    nodes = tree1.getDescendantFolders();
                    for (TreeNode node : nodes)
                    {
                            if(openFolders.contains(node.getAttribute("groupValue")))
                                tree1.openFolder(node);
                            else
                                tree1.closeFolder(node);
                    }
            }
            else if (countWasChanged[0]!=0)
                gridWithDesc.setData(rl);

            gridWithDesc.setCacheData(rl);

            gridWithDesc.resort();
            if (countWasChanged[0]==0)
                gridWithDesc.markForRedraw();
        }
    }




    protected RecordList extractData4UpdateWK(Record[] data,int[] countWasChanged)
    {
        ListGridWithDesc gridWithDesc = getGrid();

        RecordList rl = gridWithDesc.isGrouped()?gridWithDesc.getOriginalRecordList():gridWithDesc.getDataAsRecordList();


        List<Integer> toAddList = new LinkedList<Integer>();

        for (int i = 0; i < data.length; i++)
        {
            Record record = data[i];

            String id = record.getAttributeAsString(TablesTypes.KEY_FNAME);
            Integer actual = record.getAttributeAsInt(TablesTypes.ACTUAL);
            if (actual==null) actual=0;

            int inRlIx = rl.findIndex(TablesTypes.KEY_FNAME, id);

            if (inRlIx >= 0)
            {
                if (actual > 0)
                {
//                    if (countWasChanged[0]==0)
                    {
                        MyRecord rrl = (MyRecord)rl.get(inRlIx);

                        MyRecord newRect = toMyRecord(record);
                        Map mp=newRect.toMap();

                        for (Object key : mp.keySet())
                        {
                            Object value = mp.get(key);

//                            if ("3".equals(key)) //Тест ощибочных изменений в результате рек. сета
//                            {
//                                Object v1=rrl.getAttribute((String)key);
//                                if ((v1==null && value!=null) || !v1.equals(value))
//                                    System.out.println("was value = " + v1+" value; "+value);
//                            }


                            rrl.setAttribute((String) key, value);
                        }
                        rrl.setRowStyle(newRect.getRowStyle());
                        rrl.setLinkText(newRect.getLinkText());
                    }
//                    else
//                       rl.set(inRlIx, toMyRecord(record));
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
            rl.add(toMyRecord(data[i]));

        int recCnt=rl.toArray().length;
        for (int i=0;i<recCnt;i++)
            rl.get(i).setAttribute(TablesTypes.ORDIX,i);

        return rl;
    }


    public void __updateDataGrid(Record[] data) throws SetGridException
    {
        int[] ints=new int[]{0};
        RecordList rl = __extractData4Update(data, ints);
        if (rl!=null)
        {
            final ListGridWithDesc gridWithDesc = getGrid();
            gridWithDesc.setGroupByAsyncThreshold(300);//TODO проработать проццесс считывания и установки данного правила



            if (gridWithDesc.isGrouped())
            {
                String[] groupByFields = gridWithDesc.getGroupByFields();
                Tree tree=(Tree)rl;


//                for (String groupByField : groupByFields)
//                {
//                    if (changed.get(groupByField))
//                    {
//                        Tree tree=gridWithDesc.getGroupTree(); //Нельзя так при добавлении/удалении у нас не сохраняются (открыто или закрыто) состояния папок

                final Set<String> openFolders=new HashSet<String>();
                {
                        TreeNode[] nodes = tree.getDescendantFolders();
                        for (TreeNode node : nodes)
                            if (tree.isOpen(node))
                                openFolders.add(node.getAttribute("groupValue"));
                        if (ints[0]==0)
                            gridWithDesc.groupBy(groupByFields);
                }

//                gridWithDesc.addGroupByCompleteHandler(new GroupByCompleteHandler() {
//                    @Override
//                    public void onGroupByComplete(GroupByCompleteEvent event) {
                Tree tree1=gridWithDesc.getGroupTree();
                TreeNode[] nodes = tree1.getDescendantFolders();
                for (TreeNode node : nodes)
                {
                        if(openFolders.contains(node.getAttribute("groupValue")))
                            tree1.openFolder(node);
                        else
                            tree1.closeFolder(node);
                }
//                    }
//                });

//                        break;
//                    }
//                }
            }
//            else
           gridWithDesc.resort();
           gridWithDesc.markForRedraw();
        }
    }

    protected RecordList __extractData4Update(Record[] data,int[] ints)
    {
        ListGridWithDesc gridWithDesc = getGrid();

        RecordList rl = gridWithDesc.getDataAsRecordList();

        List<Integer> toAddList = new LinkedList<Integer>();


        for (int i = 0; i < data.length; i++)
        {
            Record record = data[i];

            String id = record.getAttributeAsString(TablesTypes.KEY_FNAME);
            Integer actual = record.getAttributeAsInt(TablesTypes.ACTUAL);
            if (actual==null) actual=0;

            int inRlIx = rl.findIndex(TablesTypes.KEY_FNAME, id);

            MyRecord rrl=null;
            if (rl instanceof Tree)
                rrl= (MyRecord) rl.find(TablesTypes.KEY_FNAME, id);

            if (inRlIx >= 0 || rrl!=null)
            {
                if (actual > 0)
                {
                    if (rl instanceof Tree)
                    {
                        MyRecord newRect = toMyRecord(record);
                        Map mp=newRect.toMap();

                        for (Object key : mp.keySet())
                        {
                            Object value = mp.get(key);
//                            Boolean aBoolean = isChanged.get(key);
//                            if (aBoolean!=null && aBoolean)
//                                rrl.setAttribute((String)key, value);
//                            else if (
//                                     (value!=null &&  !value.equals(rrl.getAttributeAsObject((String) key)))
//                                     ||
//                                     (value==null && rrl.getAttributeAsObject((String) key)!=null)
//                                    )
//                            {
//                                isChanged.put(key, true);
//                                rrl.setAttribute((String)key, value);
//                            }
                            rrl.setAttribute((String)key, value);
                        }
                        rrl.setRowStyle(newRect.getRowStyle());
                        rrl.setLinkText(newRect.getLinkText());
                    }
                    else
                        rl.set(inRlIx, toMyRecord(record));
                }
                else
                {
                    if (rrl!=null)
                        gridWithDesc.removeData(rrl);
                    else
                    {
                        rl.removeAt(inRlIx);
                        ints[0]=1;
                    }
                }
            }
            else if (actual > 0)
            {
                toAddList.add(i);
                ints[0]=1;
            }
        }

        for (Integer i : toAddList)
        {
            //rl.add(toMyRecord(data[i])); //TODO Важно даже не сколько удаление, сколько динамическое добавление в дерево
            gridWithDesc.addData(toMyRecord(data[i]));
        }

        int recCnt=rl.toArray().length;
        for (int i=0;i<recCnt;i++)
            rl.get(i).setAttribute(TablesTypes.ORDIX,i);

//        if (totalChange)
//            for (Object o : isChanged.keySet())
//                isChanged.put(o,true);

        return rl;
    }

}
