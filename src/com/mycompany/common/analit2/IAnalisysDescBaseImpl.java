package com.mycompany.common.analit2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Anton.Pozdnev on 02.09.2015.
 */
public class IAnalisysDescBaseImpl implements IAnalisysDesc {

    GrpDef[] grpX = new GrpDef[0];
    Map<String, ColDef> colName2Def = new HashMap<String, ColDef>();
    NNode2[] nNodes = new NNode2[0];


    public IAnalisysDescBaseImpl(List<GrpDef> grpX, Map<String, ColDef> colName2Def, List<NNode2> nNodes) {
        this.grpX = grpX.toArray(new GrpDef[grpX.size()]);
        this.colName2Def = colName2Def;
        this.nNodes = nNodes.toArray(new NNode2[nNodes.size()]);
    }

    public IAnalisysDescBaseImpl() {
    }

    @Override
    public GrpDef[] getGrpXHierarchy() {
        return grpX;
    }

    @Override
    public Map<String, ColDef> getTupleDef() {
        return colName2Def;
    }

    @Override
    public NNode2[] getNodes() {
        return nNodes;
    }

    @Override
    public void setGrpXHierarchy(GrpDef[] a) {
        this.grpX = a;
    }

    @Override
    public void setTupleDef(Map<String, ColDef> b) {
        this.colName2Def = b;
    }

    @Override
    public void setNodes(NNode2[] c) {
        this.nNodes = c;
    }
}
