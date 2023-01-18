package com.mycompany.common.tables;

import com.smartgwt.client.types.ListGridFieldType;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 15.05.14
 * Time: 16:39
 * Описатель колонок
 */
public class ColumnHeadBean  implements Serializable
{
    private  static final long serialVersionUID = -115115L ;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
    private String name;
    private String type;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    private int num;
    private String linkText;


    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    private String alignment;

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }




    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private boolean visible;

    public boolean isAutofit() {
        return autofit;
    }

    public void setAutofit(boolean autofit) {
        this.autofit = autofit;
    }

    private boolean autofit;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public ColumnHeadBean()

    {

        type = ListGridFieldType.TEXT.getValue();
    }

//    public ColumnHeadBean(String title,String name,String type,boolean visible,String linkText,boolean autofit,int num)
//    {
//        this.title = title;
//        this.name = name;
//        this.type = type;
//        this.visible = visible;
//        this.linkText=linkText;
//        this.autofit=autofit;
//        this.num=num;
//    }

    public ColumnHeadBean(String title,String name,String type,boolean visible,String linkText,boolean autofit)
    {
        this(title,name,type);
        this.visible = visible;
        this.linkText=linkText;
        this.autofit=autofit;
    }

//    public ColumnHeadBean(String title,String name,String type,boolean visible,int num)
//    {
//        this.title = title;
//        this.name = name;
//        this.type = type;
//        this.visible = visible;
//        this.num=num;
//    }

    public ColumnHeadBean(String title,String name,String type,int num)
    {
        this(title,name,type);
        this.num=num;
    }

    public ColumnHeadBean(String title,String name,String type)
    {
        this.title = title;
        this.name = name;
        this.type = type;
    }

//    public ColumnHeadBean(String title,String name,int num)
//    {
//        this.title = title;
//        this.name = name;
//        this.num=num;
//    }

//    public ColumnHeadBean(String title,String name)
//    {
//        this.title = title;
//        this.name = name;
//    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
