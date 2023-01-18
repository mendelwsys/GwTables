package com.mycompany.common;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 22.07.14
 * Time: 12:23
 * To change this template use File | Settings | File Templates.
 */
public class LinkObject
{
    public LinkObject() {
    }

    public LinkObject(String linkText, String link) {
        this.linkText = linkText;
        this.link = link;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    private String linkText;
    private String link;
}
