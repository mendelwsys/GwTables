
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
 *         &lt;element name="getSystemUpdateInfoReturn" type="{http://auth.vniias}ArrayOfUpdateInfo"/>
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
    "getSystemUpdateInfoReturn"
})
@XmlRootElement(name = "getSystemUpdateInfoResponse")
public class GetSystemUpdateInfoResponse {

    @XmlElement(required = true, nillable = true)
    protected ArrayOfUpdateInfo getSystemUpdateInfoReturn;

    /**
     * Gets the value of the getSystemUpdateInfoReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUpdateInfo }
     *     
     */
    public ArrayOfUpdateInfo getGetSystemUpdateInfoReturn() {
        return getSystemUpdateInfoReturn;
    }

    /**
     * Sets the value of the getSystemUpdateInfoReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUpdateInfo }
     *     
     */
    public void setGetSystemUpdateInfoReturn(ArrayOfUpdateInfo value) {
        this.getSystemUpdateInfoReturn = value;
    }

}
