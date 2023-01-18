
package com.mycompany.server.wsauthclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="getUsersByProgReturn" type="{http://auth.vniias}ArrayOfUserInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getUsersByProgReturn"
})
@XmlRootElement(name = "getUsersByProgResponse")
public class GetUsersByProgResponse {

    @XmlElement(required = true, nillable = true)
    protected ArrayOfUserInfo getUsersByProgReturn;

    /**
     * Gets the value of the getUsersByProgReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUserInfo }
     *     
     */
    public ArrayOfUserInfo getGetUsersByProgReturn() {
        return getUsersByProgReturn;
    }

    /**
     * Sets the value of the getUsersByProgReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUserInfo }
     *     
     */
    public void setGetUsersByProgReturn(ArrayOfUserInfo value) {
        this.getUsersByProgReturn = value;
    }

}
