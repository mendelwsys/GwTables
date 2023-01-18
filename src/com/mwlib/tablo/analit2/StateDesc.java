package com.mwlib.tablo.analit2;

import com.mwlib.tablo.tables.AField;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mycompany.common.FieldException;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.*;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.smartgwt.client.types.ListGridFieldType;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * Описатель таблицы консолидации
 */
abstract public class StateDesc extends BaseTableDesc
{
    private AField[] fieldTranslator;


    Map<String, Integer> key2Number;
    Map<String, NNode2> key2NNode;

    public IAnalisysDesc getDesc() {
        return desc;
    }

    protected IAnalisysDesc desc;


    public Map<String,Integer> getKey2Number() throws FieldException
    {
           return key2Number;
    }


    public StateDesc(IAnalisysDesc desc,boolean test) throws Exception
    {
        super(test);
        this.desc = desc;

        int startIx=0;//TODO Потом переделать на 2 после отладки независимых заголовков
        UtilsData.getKey2key2Number2(desc.getNodes(), "", key2Number = new HashMap<String, Integer>(), key2NNode = new HashMap<String, NNode2>(), startIx);

        List<AField> fields = new LinkedList<AField>();
        fields.add(new AField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false){
            @Override
            public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {
                return tuple.get(name);
            }
        }); //TODO То-же подпорка

        GrpDef[] keyDef = desc.getGrpXHierarchy();
        Map<String, ColDef> tupleDef = desc.getTupleDef();
        for (GrpDef grpDef : keyDef)
        {
            ColDef colDef=tupleDef.get(grpDef.getTid());
            fields.add(new AField(colDef.getColName(),colDef.getTitle(),ListGridFieldType.TEXT.toString(),colDef.isHide()){
                @Override
                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {
                    return tuple.get(name);
                }
            });//TODO тип консолидационных данных должен быть описан в XML

            if (!grpDef.getTid().equals(grpDef.gettColId()))
            {
                colDef=tupleDef.get(grpDef.gettColId());
                fields.add(new AField(colDef.getColName(),colDef.getTitle(),ListGridFieldType.TEXT.toString(),colDef.isHide()){

                    @Override
                    public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {
                        return tuple.get(name);
                    }
                });//TODO тип консолидационных данных должен быть описан в XML
            }
        }

        Map<Integer, String> number2Key = getNumber2Key();
        Set<Integer> ixs = number2Key.keySet();
        Map<String, ColDef> str2ColDef = desc.getTupleDef();

        for (AField translator : fields)
            translator.setAlwaysSend(true);

        for (Integer ix : ixs)
        {
           NNode2 nnode = key2NNode.get(number2Key.get(ix));
           if (nnode.getColN()!=null)
               startIx=nnode.getColN();
           else
           {
               nnode.setColN(startIx);
               startIx++;
           }

           ColDef colDef=str2ColDef.get(nnode.getColId());
           String ftype=colDef.getFtype()!=null?colDef.getFtype():ListGridFieldType.TEXT.getValue();

           fields.add(
                   new AField(String.valueOf(ix),String.valueOf(ix),ftype,true)
                   {
                       @Override
                       public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                       {
                           return tuple.get(name);
                       }
                   }

           );//TODO тип консолидационных данных должен быть описан в XML
        }
        initFieldTranslator(fields);
    }

    protected void initFieldTranslator(List<AField> fields) {
        fieldTranslator=fields.toArray(new AField[fields.size()]);
    }


    public Map translateTuple(Map rawTuple, ColumnHeadBean[] rawHead, Set<String> sendMask) throws FieldException
    {

//        FieldTranslator[] fieldTranslator = getFieldTranslator();
        Map<String,Object> outTuple = new HashMap<String,Object>();
        for (int ix = 0, fieldTranslatorLength = fieldTranslator.length; ix < fieldTranslatorLength; ix++)
        {

            AField translator = fieldTranslator[ix];
            Object val = translator.getS(rawHead, rawTuple, outTuple);
            String name = translator.getColumnHeadBean().getName();
            if (rawTuple.containsKey(name)) //TODO подпорка надо это как-то обрабатывать на уровне outTuple
                if (translator.isAlwaysSend() || sendMask==null || sendMask.contains(name))
                    outTuple.put(name, val);

            if (val!=null && !outTuple.containsKey(name))
                System.out.println("name = " + name+" val:"+val);

        }
        outTuple.put(TablesTypes.ACTUAL, 1);
        return outTuple;
    }

    abstract public String getTableType();

//    @Override
//    public String getTableType() {
//        return TablesTypes.STATEDESC;
//    }

    @Override
    public FieldTranslator[] getFieldTranslator()
    {
        return  fieldTranslator;
    }

    public  void addMeta2Type(IMetaProvider metaProvider)
    {
    }


    @Override
    protected IRowOperation _getRowOperation()
    {
        return  null;
//                new IRowOperation()
//        {
//            @Override
//            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception
//            {
//            }
//        };
    }

    @Override
    public int[] getDataTypes()
    {
        return new int[]{-100};
    }
}
