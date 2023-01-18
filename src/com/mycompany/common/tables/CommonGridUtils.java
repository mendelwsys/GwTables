package com.mycompany.common.tables;


import com.mycompany.client.utils.AnalitGridUtils;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.analit2.UtilsData;
import com.smartgwt.client.widgets.grid.HeaderSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Anton.Pozdnev on 20.08.2015.
 */
public class CommonGridUtils {


    public static List<Map> getKeyNumberMappings(IAnalisysDesc desc) throws Exception {
        List<Map> mappings = new ArrayList<Map>();
        Map<String, Integer> key2Number = new HashMap<String, Integer>();
        Map<String, NNode2> key2NNode = new HashMap<String, NNode2>();
        Map<Integer, String> number2Key = new HashMap<Integer, String>();
        NNode2 root = CommonGridUtils.findRootNode(desc);



        UtilsData.getKey2key2Number2(root.getNodes(), "", key2Number, key2NNode, 0);
        number2Key = UtilsData.number2Key(key2Number);
        mappings.add(key2Number);
        mappings.add(number2Key);
        mappings.add(key2NNode);
        return mappings;

    }


    public static HeaderSpanMimic buildSpans(NNode2 root, Map<String, Integer> key2Number) throws Exception {
        HeaderSpan rootSpan = new HeaderSpan();
        int dHeight = 30;
        int headerHeight = AnalitGridUtils.buildSpans2(rootSpan, root, "", key2Number, dHeight);

        // List<HeaderSpan> spans = new LinkedList<HeaderSpan>();
        // AnalitGridUtils.removeEdgeSpans(rootSpan,spans);

        HeaderSpanMimic hsmroot = convertHeaderSpansToHeaderSpanMimics(rootSpan);
        hsmroot.setHeight(headerHeight);
        return hsmroot;
    }


    private static HeaderSpanMimic convertHeaderSpansToHeaderSpanMimics(HeaderSpan root) {
        HeaderSpanMimic hsmroot = new HeaderSpanMimic();
        hsmroot.setHeight(root.getHeight());
        hsmroot.setName(root.getName());
        hsmroot.setSubs(convertHeaderSpansToHeaderSpanMimics2(root.getSpans()));
        return hsmroot;

    }

    private static HeaderSpanMimic[] convertHeaderSpansToHeaderSpanMimics2(HeaderSpan[] node) {
        if (node == null || node.length == 0) return null;
        HeaderSpanMimic[] mimics = new HeaderSpanMimic[node.length];
        for (int i = 0; i < node.length; i++) {
            mimics[i] = new HeaderSpanMimic();
            mimics[i].setName(node[i].getTitle());

            mimics[i].setHeight(node[i].getHeight());
            if (node[i].getFields() != null && node[i].getFields().length > 0)
                mimics[i].setFieldNames(node[i].getFields());
            if (node[i].getSpans() != null)
                mimics[i].setSubs(convertHeaderSpansToHeaderSpanMimics2(node[i].getSpans()));

        }
        return mimics;

    }

    public static NNode2 findRootNode(IAnalisysDesc desc) {
        NNode2[] nodes = desc.getNodes();
        if (nodes == null) return null;
        NNode2 root = null;

        for (int i = 0; i < nodes.length; i++) {
            if (i == 0) {
                root = nodes[i].getParent();


            } else if (root != null && nodes[i].getParent() == root) {
                break;

            } else if (root == null)
                break;
            else if (nodes[i].getParent() != root) {
                root = null;
                break;

            }


        }
        if (root == null)

            root = new NNode2("ROOT", "", "ROOT", NNode2.NNodeType, null, false, null, desc.getNodes(), null);
        return root;

    }


}
