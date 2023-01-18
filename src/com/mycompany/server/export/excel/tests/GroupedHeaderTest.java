package com.mycompany.server.export.excel.tests;

import com.mycompany.common.tables.HeaderSpanMimic;

/**
 * Created by Anton.Pozdnev on 21.08.2015.
 */
public class GroupedHeaderTest {


    int[] findMaxHeight(HeaderSpanMimic node, int currentHeight) {
        currentHeight++;
        int maxHeight = 0;
        if (node.getSubs() == null || node.getSubs().length == 0) return new int[]{currentHeight, currentHeight};
        for (int i = 0; i < node.getSubs().length; i++) {
            int[] height = findMaxHeight(node.getSubs()[i], currentHeight);
            if (height[1] > maxHeight) maxHeight = height[1];
        }

        return new int[]{currentHeight, maxHeight};
    }


    int[] setRowColSpans(HeaderSpanMimic[] node, int maxHeight, int currentHeight) {
        // if (node==null) return new int [] {currentHeight,1};
        currentHeight++;
        int colSpans = 1;
        for (int i = 0; i < node.length; i++) {
            if (node[i].getSubs() == null && node[i].getFieldNames() != null) {
                node[i].setColspan(node[i].getFieldNames().length);
                colSpans += node[i].getFieldNames().length;
            } else if (node[i].getSubs() != null) {
                int[] height = setRowColSpans(node[i].getSubs(), maxHeight, currentHeight);
                node[i].setRowspan(maxHeight - height[0]);
                node[i].setColspan(height[1]);
                colSpans += height[1];
            } else {
                node[i].setRowspan(maxHeight - currentHeight);
                node[i].setColspan(colSpans);

            }

        }

        return new int[]{currentHeight, colSpans};
    }


    int[] findMaxHeight2(HeaderSpanMimic node, int currentHeight) {
        currentHeight++;
        int maxHeight = 0;
        if (node.getSubs() == null || node.getSubs().length == 0) return new int[]{currentHeight, currentHeight};
        for (int i = 0; i < node.getSubs().length; i++) {
            int[] height = findMaxHeight2(node.getSubs()[i], currentHeight);
            if (height[1] > maxHeight) maxHeight = height[1];
        }

        return new int[]{currentHeight, maxHeight};
    }


    int[] setRowColSpans2(HeaderSpanMimic[] node, int maxHeight, int currentLevel) {
        // if (node==null) return new int [] {currentHeight,1};
        currentLevel++;
        int colSpans = 0;
        int downrowSpan = currentLevel;
        int maxRowSpan = 1;
        int toUpperHeight = 0;
        for (int i = 0; i < node.length; i++) {
            if (node[i].getSubs() == null && node[i].getFieldNames() != null) {
                node[i].setColspan(node[i].getFieldNames().length);
                colSpans += node[i].getFieldNames().length;
                if (maxRowSpan > maxHeight - downrowSpan) {
                    node[i].setRowspan(maxRowSpan);
                    toUpperHeight = maxRowSpan + 1;

                } else {
                    node[i].setRowspan(maxHeight - downrowSpan);
                    toUpperHeight = node[i].getRowspan() + 1;
                    if (maxRowSpan < maxHeight - downrowSpan)
                        maxRowSpan = maxHeight - downrowSpan;
                }
                //  if (maxHeight-currentHeight>rowSpan)
                // rowSpan += node[i].getRowspan()-1;

            } else if (node[i].getSubs() != null) {
                int[] height = setRowColSpans2(node[i].getSubs(), maxHeight, currentLevel);
                node[i].setRowspan((maxHeight - height[0]) / currentLevel);
                if (downrowSpan < height[0])
                    downrowSpan = height[0];
                if ((maxHeight - height[0]) / currentLevel > maxRowSpan)
                    maxRowSpan = ((maxHeight - height[0]) / currentLevel);
                node[i].setColspan(height[1]);
                colSpans += height[1];
                toUpperHeight = downrowSpan + ((maxHeight - height[0])) / currentLevel;
            } /*else {
                node[i].setRowspan(maxHeight - currentLevel);
                node[i].setColspan(colSpans + 1);
                // if (maxHeight-currentHeight>rowSpan)
                //rowSpan = maxHeight-currentHeight;
            }*/


        }

        return new int[]{toUpperHeight, colSpans};
    }

    public static void main(String[] args) {
        GroupedHeaderTest ght = new GroupedHeaderTest();
        HeaderSpanMimic root = init();
        int[] height = ght.findMaxHeight2(root, 0);
        ght.setRowColSpans2(root.getSubs(), height[1], 0);
        root.setRowspan(height[1]);
        System.out.println("!");
    }

    private static HeaderSpanMimic init() {
        HeaderSpanMimic root = new HeaderSpanMimic();
      /*  HeaderSpanMimic n1 = new HeaderSpanMimic();
        HeaderSpanMimic n2 = new HeaderSpanMimic();
        HeaderSpanMimic n3 = new HeaderSpanMimic();
        HeaderSpanMimic n4 = new HeaderSpanMimic();
        HeaderSpanMimic n5 = new HeaderSpanMimic();
        HeaderSpanMimic n6 = new HeaderSpanMimic();
        n4.setSubs(new HeaderSpanMimic[]{n5,n6});
        n2.setSubs(new HeaderSpanMimic[]{n3,n4});
        root.setSubs(new HeaderSpanMimic[]{n1,n2});*/

        HeaderSpanMimic n1 = new HeaderSpanMimic("Службы");
        n1.setFieldNames(new String[]{"1"});
        HeaderSpanMimic n2 = new HeaderSpanMimic("Предупреждения");
        HeaderSpanMimic n3 = new HeaderSpanMimic("Заложено графиком");
        HeaderSpanMimic n4 = new HeaderSpanMimic("Действует");
        HeaderSpanMimic n5 = new HeaderSpanMimic("Длительные");
        HeaderSpanMimic n6 = new HeaderSpanMimic("шт");
        n6.setFieldNames(new String[]{"2"});
        HeaderSpanMimic n7 = new HeaderSpanMimic("км");
        n7.setFieldNames(new String[]{"3"});
        HeaderSpanMimic n8 = new HeaderSpanMimic("шт");
        n8.setFieldNames(new String[]{"4"});
        HeaderSpanMimic n9 = new HeaderSpanMimic("км");
        n9.setFieldNames(new String[]{"5"});
        HeaderSpanMimic n10 = new HeaderSpanMimic("шт");
        n10.setFieldNames(new String[]{"6"});
        HeaderSpanMimic n11 = new HeaderSpanMimic("км");
        n11.setFieldNames(new String[]{"7"});
        HeaderSpanMimic n12 = new HeaderSpanMimic("С нарушением приказа");
        n12.setFieldNames(new String[]{"8"});
        n3.setSubs(new HeaderSpanMimic[]{n6, n7});
        n4.setSubs(new HeaderSpanMimic[]{n8, n9});
        n5.setSubs(new HeaderSpanMimic[]{n10, n11});
        n2.setSubs(new HeaderSpanMimic[]{n3, n4, n5, n12});

        HeaderSpanMimic n13 = new HeaderSpanMimic("Технологические окна");
        HeaderSpanMimic n14 = new HeaderSpanMimic("Подход техники");
        n14.setFieldNames(new String[]{"9"});
        HeaderSpanMimic n15 = new HeaderSpanMimic("Все");
        HeaderSpanMimic n16 = new HeaderSpanMimic("Запланированные");
        n16.setFieldNames(new String[]{"10"});
        HeaderSpanMimic n17 = new HeaderSpanMimic("Предоставленные");
        n17.setFieldNames(new String[]{"11"});
        HeaderSpanMimic n18 = new HeaderSpanMimic("Передержанные");
        n18.setFieldNames(new String[]{"12"});
        n15.setSubs(new HeaderSpanMimic[]{n16, n17, n18});
        n13.setSubs(new HeaderSpanMimic[]{n14, n15});

        HeaderSpanMimic n19 = new HeaderSpanMimic("Отказы технических средств");
        HeaderSpanMimic n20 = new HeaderSpanMimic("Принятые к учету");
        n20.setFieldNames(new String[]{"13"});
        HeaderSpanMimic n21 = new HeaderSpanMimic("Непринятые к учету");
        n21.setFieldNames(new String[]{"14"});
        n19.setSubs(new HeaderSpanMimic[]{n20, n21});
        HeaderSpanMimic n22 = new HeaderSpanMimic("Брошенные выгоны");
        n22.setFieldNames(new String[]{"15"});
        HeaderSpanMimic n23 = new HeaderSpanMimic("Выгоны в ТОР");
        n23.setFieldNames(new String[]{"16"});
        root.setSubs(new HeaderSpanMimic[]{n1, n2, n13, n19, n22, n23});
        return root;


    }
}
