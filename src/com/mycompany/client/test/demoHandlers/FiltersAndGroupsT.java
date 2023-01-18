package com.mycompany.client.test.demoHandlers;

import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;


/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 06.06.14
 * Time: 16:06
 * To change this template use File | Settings | File Templates.
 */
public class FiltersAndGroupsT
{

    public static class EmployeeTreeNode extends TreeNode
    {
        public EmployeeTreeNode(String employeeId, String reportsTo, String name, String job, boolean isOpen) {
            setAttribute("EmployeeId", employeeId);
            setAttribute("ReportsTo", reportsTo);
            setAttribute("Name", name);
            setAttribute("Job", job);
            setAttribute("isOpen", isOpen);
        }
    }

    public static final TreeNode[] employeeData = new TreeNode[] {
            new EmployeeTreeNode("4", "1", "Charles Madigen", "Chief Operating Officer", true),
            new EmployeeTreeNode("189", "4", "Gene Porter", "Mgr Tech Plng IntIS T", false),
            new EmployeeTreeNode("265", "189", "Olivier Doucet", "Asset Spec Lines Stns", false),
            new EmployeeTreeNode("264", "189", "Cheryl Pearson", "Dsl Sys Rep", false),
            new EmployeeTreeNode("263", "189", "Priya Sambhus", "Line Wrker A", false),
            new EmployeeTreeNode("188", "4", "Rogine Leger", "Mgr Syst P P", true),
            new EmployeeTreeNode("262", "188", "Jacques Desautels", "Line Wrker A", false),
            new EmployeeTreeNode("261", "188", "Kay Monroe", "Stn Opr", false),
            new EmployeeTreeNode("260", "188", "Francine Dugas", "Fire Sec Off", false),
            new EmployeeTreeNode("259", "188", "Jacques Leblanc", "Purch Clk", false),
            new EmployeeTreeNode("258", "188", "Ren Xian", "Mobile Eq Opr", false),
            new EmployeeTreeNode("257", "188", "Olivier Hebert", "Met Read/Coll", false),
            new EmployeeTreeNode("182", "4", "Tamara Kane", "Mgr Site Services", false),
            new EmployeeTreeNode("195", "182", "Kai Kong", "Stores Worker", false),
            new EmployeeTreeNode("194", "182", "Felicia Piper", "Dsl Sys Rep", false),
            new EmployeeTreeNode("193", "182", "Darcy Feeney", "Inventory Ck", false)
    };

    public static TreeGrid initIt()
    {

        Tree employeeTree = new Tree();
//        employeeTree.setModelType(TreeModelType.PARENT);
        employeeTree.setRootValue(1);
        employeeTree.setNameProperty("Name");
        employeeTree.setIdField("EmployeeId");
        employeeTree.setParentIdField("ReportsTo");
        employeeTree.setOpenProperty("isOpen");

        employeeTree.setData(employeeData);

        final TreeGrid treeGrid = new TreeGrid();
//                treeGrid.setCanEdit(true);
                treeGrid.setLoadDataOnDemand(false);
                treeGrid.setWidth100();
                treeGrid.setHeight(400);
                treeGrid.setData(employeeTree);
//                treeGrid.setNodeIcon("icons/16/person.png");
//                treeGrid.setFolderIcon("icons/16/person.png");
//                treeGrid.setShowOpenIcons(false);
//                treeGrid.setShowDropIcons(false);
//                treeGrid.setClosedIconSuffix("");
                treeGrid.setAutoFetchData(true);

            TreeGridField nameField = new TreeGridField("Name");
            TreeGridField jobField = new TreeGridField("Job");
            TreeGridField salaryField = new TreeGridField("Salary");



        return treeGrid;
    }

}
