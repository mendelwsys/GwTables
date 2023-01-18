package com.mycompany.common.analit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 18.10.14
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class NNode
{
    public NNode(){}
    public NNode(NNode parent){this.parent=parent;}
    public NNode(String name){this.name=name;}
    public String header;
    public String name;
    public String tplColId;
    public NNode parent;
    public Map<String,NNode> nodes= new HashMap<String,NNode>();
}
