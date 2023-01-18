
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
 *         &lt;element name="getProgsIdByUserPwdReturn" type="{http://auth.vniias}ArrayOf_xsd_nillable_int"/>
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
    "getProgsIdByUserPwdReturn"
})
@XmlRootElement(name = "getProgsIdByUserPwdResponse")
public class GetProgsIdByUserPwdResponse {

    @XmlElement(required = true, nillable = true)
    protected ArrayOfXsdNillableInt getProgsIdByUserPwdReturn;

    /**
     * Gets the value of the getProgsIdByUserPwdReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfXsdNillableInt }
     *     
     */
    public ArrayOfXsdNillableInt getGetProgsIdByUserPwdReturn() {
        return getProgsIdByUserPwdReturn;
    }

    /**
     * Sets the value of the getProgsIdByUserPwdReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfXsdNillableInt }
     *     
     */
    public void setGetProgsIdByUserPwdReturn(ArrayOfXsdNillableInt value) {
        this.getProgsIdByUserPwdReturn = value;
    }

}
