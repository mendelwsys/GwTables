
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
 *         &lt;element name="getProgsIdByUserReturn" type="{http://auth.vniias}ArrayOf_xsd_nillable_int"/>
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
    "getProgsIdByUserReturn"
})
@XmlRootElement(name = "getProgsIdByUserResponse")
public class GetProgsIdByUserResponse {

    @XmlElement(required = true, nillable = true)
    protected ArrayOfXsdNillableInt getProgsIdByUserReturn;

    /**
     * Gets the value of the getProgsIdByUserReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfXsdNillableInt }
     *     
     */
    public ArrayOfXsdNillableInt getGetProgsIdByUserReturn() {
        return getProgsIdByUserReturn;
    }

    /**
     * Sets the value of the getProgsIdByUserReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfXsdNillableInt }
     *     
     */
    public void setGetProgsIdByUserReturn(ArrayOfXsdNillableInt value) {
        this.getProgsIdByUserReturn = value;
    }

}
