package com.mycompany.client.apps;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.widgets.tree.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 06.06.14
 * Time: 16:06
 *
 */
public class FiltersAndGroups
{


//    public static TreeGrid buildTree(String title)
//    {
//        TreeGrid gridTree=buildTree(title);
//        final Tree data = gridTree.getData();
//        data.setData(operationData);
//        gridTree.setData(data);
//
//        return gridTree;
//    }

    public static TreeGrid buildTree(TreeNode[] operationData,String title,FieldType idType,final String idDs)
    {

//        Tree windowsTree = new Tree();
//        windowsTree.setModelType(TreeModelType.PARENT);
//        windowsTree.setRootValue(1);
//        windowsTree.setNameProperty("Name");
//        windowsTree.setIdField("OperationId");
//        windowsTree.setParentIdField("ParentOperationId");
        //windowsTree.setData(operationData);


        for (TreeNode treeNode : operationData)
        {
            final Boolean isBuildIn = treeNode.getAttributeAsBoolean(OperationNode.ISBUILDIN);
            treeNode.set_canRemove(isBuildIn==null || !isBuildIn);
        }

        DataSource ds =DataSource.getDataSource(idDs);
        if (ds==null)
        {
            ds = new DataSource();
            ds.setID(idDs);
            DataSourceField dsName=new DataSourceField(OperationNode.NAME_NODE, FieldType.TEXT);
            dsName.setRequired(true);
            ds.addField(dsName);

            DataSourceField buildIn=new DataSourceField(OperationNode.ISBUILDIN, FieldType.BOOLEAN);
            dsName.setRequired(false);
            ds.addField(buildIn);


            DataSourceField dsOperation=new DataSourceField(OperationNode.OPERATION_ID, idType);
            dsOperation.setPrimaryKey(true);
            dsOperation.setRequired(true);
            ds.addField(dsOperation);

            DataSourceField dsParent = new DataSourceField(OperationNode.PARENT_OPERATION_ID, idType);
            dsParent.setForeignKey(idDs + "."+OperationNode.OPERATION_ID);
            dsParent.setRootValue("");
            dsParent.setRequired(true);
            ds.addField(dsParent);

            ds.setClientOnly(true);
            ds.setCacheAllData(true);
            ds.setCacheData(operationData);
        }
        else
        {
            ds.setCacheData();
            ds.setCacheData(operationData);
        }


        final TreeGrid treeGrid = new TreeGrid()
        {
             @Override
            public Boolean willAcceptDrop(){
                return new Boolean(true);
            }
        };

        treeGrid.setDataSource(ds);


        treeGrid.setAutoFetchData(true);
        treeGrid.setLoadDataOnDemand(false);
//        ((ResultTree)treeGrid.getTree()).setDisableCacheSync(false);
//        ((ResultTree)treeGrid.getTree()).setUpdateCacheFromRequest(true);



        treeGrid.setWidth100();
        treeGrid.setHeight100();



//                treeGrid.setData(windowsTree);
//                treeGrid.setNodeIcon("icons/16/person.png");
//                treeGrid.setFolderIcon("icons/16/person.png");
//                treeGrid.setShowOpenIcons(false);
//                treeGrid.setShowDropIcons(false);
//                treeGrid.setClosedIconSuffix("");
//                treeGrid.setAutoFetchData(true);

        TreeGridField nameField = new TreeGridField("Name",title);
        treeGrid.setFields(nameField);
        treeGrid.setDataFetchMode(FetchMode.LOCAL);

        return treeGrid;
    }



    //public static TreeGrid buildTree2(TreeNode[] operationData, String title, final String idDs, FieldType idType)

}
