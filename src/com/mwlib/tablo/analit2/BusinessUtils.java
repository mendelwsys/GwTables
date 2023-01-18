package com.mwlib.tablo.analit2;

import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.db.IMetaProvider;
import com.smartgwt.client.types.ListGridFieldType;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 11.02.15
 * Time: 19:11
 * To change this template use File | Settings | File Templates.
 */
public class BusinessUtils
{

    public static void fillTupleByPreds(Map tuple, Map<String, Object> hs) {
        Object o_vid_id = tuple.get(TablesTypes.VID_ID);
        if (o_vid_id==null)
            tuple.put(TablesTypes.VID_ID,o_vid_id=TablesTypes.Z_ID_SERVAL);

        if (Integer.valueOf(TablesTypes.Z_ID_SERVAL).equals(o_vid_id))
        {
            final Object hoz_id = tuple.get(TablesTypes.HOZ_ID);
            if (hoz_id instanceof Number)
            {
                BusinessUtils.fillVIDByHozId(hs, ((Number) hoz_id).intValue());
                final Object value = hs.get(TablesTypes.VID_ID);
                if (value!=null)
                    tuple.put(TablesTypes.VID_ID, value);
            }
        }
    }


    public static String fillVIDByHozId(Map<String, Object> tuple, int hoz_id)
    {
        String rv=null;
        switch (hoz_id)
        {
            case 150305:
                tuple.put(TablesTypes.VID_NAME,rv="П");
                tuple.put(TablesTypes.VID_ID, 28);
                break;
            case 150306:
                tuple.put(TablesTypes.VID_NAME,rv="Ш");
                tuple.put(TablesTypes.VID_ID, 70);
                break;
            case 150309:
                tuple.put(TablesTypes.VID_NAME,rv="Э");
                tuple.put(TablesTypes.VID_ID, 40);
                break;
            case 150304:
                tuple.put(TablesTypes.VID_NAME,rv="В");
                tuple.put(TablesTypes.VID_ID, 44);
                break;
            default:
            {
                tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
            }
        }
        return rv;
    }

    public static  String fillVIDByBidName(Map<String, Object> tuple, String  vid_name)
    {
        String rv=vid_name;
        if (vid_name==null)
            vid_name="";

        tuple.put(TablesTypes.VID_NAME,vid_name);
        switch (vid_name)
        {
            case "П":
                tuple.put(TablesTypes.VID_ID, 28);
                break;
            case "Ш":
                tuple.put(TablesTypes.VID_ID, 70);
                break;
            case "Э":
                tuple.put(TablesTypes.VID_ID, 40);
                break;
            case "В":
                tuple.put(TablesTypes.VID_ID, 44);
                break;
            case "ДПМ":
                tuple.put(TablesTypes.VID_ID, 6666);
                break;
            default:
            {
                rv=null;
                tuple.put(TablesTypes.VID_NAME,TablesTypes.Z_SERVAL);
                tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
            }
        }
        return rv;

    }

    public static  String fillVIDByPred(Map<String, Object> tuple, int pred_id) {

        Directory.Pred pred = Directory.getByPredId(pred_id);
        return fillVIDByPred(tuple, pred);
    }

    public static String fillVIDByPred(Map<String, Object> tuple, Directory.Pred pred) {
        if (pred ==null)
        {
            tuple.put(TablesTypes.VID_NAME,TablesTypes.Z_SERVAL);
            tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
            return null;
        }

        final Integer gr_id1 = pred.getGR_ID();
        if (gr_id1==null)
        {
            tuple.put(TablesTypes.VID_NAME,TablesTypes.Z_SERVAL);
            tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
            return null;
        }

        Directory.UkGR vid = Directory.getByGrId(gr_id1);
        Integer gr_id = vid.getGR_ID();
        tuple.put(TablesTypes.VID_ID, gr_id);

        if (gr_id==null)
            gr_id=-1;

        String rv=null;
        switch (gr_id)
        {
            case 25:
            case 28:
                tuple.put(TablesTypes.VID_NAME,rv="П");
                tuple.put(TablesTypes.VID_ID, 28);
                break;
            case 70:
                tuple.put(TablesTypes.VID_NAME,rv="Ш");
                break;
            case 40:
                tuple.put(TablesTypes.VID_NAME,rv="Э");
                break;
            case 44:
                tuple.put(TablesTypes.VID_NAME,rv="В");
                break;
            default:
                tuple.put(TablesTypes.VID_NAME,TablesTypes.Z_SERVAL);
                tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
        }

        return rv;
    }

//------------------------------------- TODO потом перенести куда нить в более подходящее место 12022015 (УНИФИЦИРОВАТЬ С ПРОЦЕДУРОЙ КОНСОЛИДАЦИИ) ------------------------------------------//

    public static void fillVidTupleByPredId(Map<String, Object> tuple)
    {
        if (tuple.containsKey(TablesTypes.PRED_ID) && !tuple.containsKey(TablesTypes.VID_ID))
        {
            Integer pred_id = (Integer) tuple.get(TablesTypes.PRED_ID);
            Directory.Pred pred;
            if (pred_id!=null && (pred=Directory.getByPredId(pred_id))!=null)
                BusinessUtils.fillVIDByPred(tuple, pred.getPRED_ID());
            else
            {
                tuple.put(TablesTypes.VID_NAME,TablesTypes.Z_SERVAL);
                tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
            }
        }
    }


    public static void fillVidTupleByPredIdHozId(Map<String, Object> tuple)
    {
        if (tuple.containsKey(TablesTypes.PRED_ID) && tuple.containsKey(TablesTypes.HOZ_ID) && !tuple.containsKey(TablesTypes.VID_ID))
        {
            Integer pred_id = (Integer) tuple.get(TablesTypes.PRED_ID);
            Directory.Pred pred;
            if (pred_id!=null && (pred=Directory.getByPredId(pred_id))!=null)
                BusinessUtils.fillVIDByPred(tuple, pred.getPRED_ID());
            else
            {
                Integer hoz_id = (Integer) tuple.get(TablesTypes.HOZ_ID);
                if (hoz_id==null) hoz_id=0;
                BusinessUtils.fillVIDByHozId(tuple, hoz_id);
            }
        }
    }


    public static  void fillVidTupleByServChar(Map<String, Object> tuple)
    {
            if (tuple.containsKey("SERV_CHAR"))
            {
                String serv_char = (String) tuple.get("SERV_CHAR");
                if (serv_char==null)
                    serv_char="";

                tuple.put(TablesTypes.VID_NAME, serv_char);
                switch (serv_char)
                {

                    case "П":
                        tuple.put(TablesTypes.VID_ID, 28);
                        break;
                    case "Ш":
                        tuple.put(TablesTypes.VID_ID, 70);
                        break;
                    case "Э":
                        tuple.put(TablesTypes.VID_ID, 40);
                        break;
                    case "В":
                        tuple.put(TablesTypes.VID_ID, 44);
                        break;
                    case "ДПМ":
                        tuple.put(TablesTypes.VID_ID, 6666);
                        break;
//                    case "В":
//                        tuple.put(TablesTypes.VID_ID, 700);
//                        break;
//                    case "Э":
//                        tuple.put(TablesTypes.VID_ID, 1400);
//                        break;
//                    case "П":
//                        tuple.put(TablesTypes.VID_ID, 800);
//                        break;
//                    case "Ш":
//                        tuple.put(TablesTypes.VID_ID, 900);
//                        break;
                    default:
                    {
                        tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                        tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
                    }
                        break;
                }
            }
    }


    public static void fillMetaByVid(IMetaProvider metaProvider, int typeid)
    {
        metaProvider.addColumnByEventType(typeid,new ColumnHeadBean(TablesTypes.VID_ID, TablesTypes.VID_ID, ListGridFieldType.INTEGER.toString()));
        metaProvider.addColumnByEventType(typeid,new ColumnHeadBean(TablesTypes.VID_NAME, TablesTypes.VID_NAME, ListGridFieldType.TEXT.toString()));
    }


}
