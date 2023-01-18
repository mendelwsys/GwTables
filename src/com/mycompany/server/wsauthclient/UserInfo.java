
package com.mycompany.server.wsauthclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/**
 * <p>Java class for UserInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="remoteHost" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pwdDb" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="userDb" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="roles" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dolId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dorKod" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="idLevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idPers" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idPredType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idUser" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idXoz" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="otdelId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="podrId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="predId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="stanId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="userMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="aliasDb" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserInfo", propOrder = {
    "remoteHost",
    "pwdDb",
    "userDb",
    "roles",
    "dolId",
    "dorKod",
    "firstName",
    "idLevel",
    "idPers",
    "idPredType",
    "idUser",
    "idXoz",
    "lastName",
    "middleName",
    "otdelId",
    "podrId",
    "predId",
    "stanId",
    "userMessage",
    "aliasDb"
})
public class UserInfo implements Serializable {

    @XmlElement(required = true, nillable = true)
    protected String remoteHost;
    @XmlElement(required = true, nillable = true)
    protected String pwdDb;
    @XmlElement(required = true, nillable = true)
    protected String userDb;
    @XmlElement(required = true, nillable = true)
    protected String roles;
    protected int dolId;
    protected int dorKod;
    @XmlElement(required = true, nillable = true)
    protected String firstName;
    protected int idLevel;
    protected int idPers;
    protected int idPredType;
    protected int idUser;
    protected int idXoz;
    @XmlElement(required = true, nillable = true)
    protected String lastName;
    @XmlElement(required = true, nillable = true)
    protected String middleName;
    protected int otdelId;
    protected int podrId;
    protected int predId;
    protected int stanId;
    @XmlElement(required = true, nillable = true)
    protected String userMessage;
    @XmlElement(required = true, nillable = true)
    protected String aliasDb;

    /**
     * Gets the value of the remoteHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * Sets the value of the remoteHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteHost(String value) {
        this.remoteHost = value;
    }

    /**
     * Gets the value of the pwdDb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPwdDb() {
        return pwdDb;
    }

    /**
     * Sets the value of the pwdDb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPwdDb(String value) {
        this.pwdDb = value;
    }

    /**
     * Gets the value of the userDb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserDb() {
        return userDb;
    }

    /**
     * Sets the value of the userDb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserDb(String value) {
        this.userDb = value;
    }

    /**
     * Gets the value of the roles property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoles() {
        return roles;
    }

    /**
     * Sets the value of the roles property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoles(String value) {
        this.roles = value;
    }

    /**
     * Gets the value of the dolId property.
     * 
     */
    public int getDolId() {
        return dolId;
    }

    /**
     * Sets the value of the dolId property.
     * 
     */
    public void setDolId(int value) {
        this.dolId = value;
    }

    /**
     * Gets the value of the dorKod property.
     * 
     */
    public int getDorKod() {
        return dorKod;
    }

    /**
     * Sets the value of the dorKod property.
     * 
     */
    public void setDorKod(int value) {
        this.dorKod = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the idLevel property.
     * 
     */
    public int getIdLevel() {
        return idLevel;
    }

    /**
     * Sets the value of the idLevel property.
     * 
     */
    public void setIdLevel(int value) {
        this.idLevel = value;
    }

    /**
     * Gets the value of the idPers property.
     * 
     */
    public int getIdPers() {
        return idPers;
    }

    /**
     * Sets the value of the idPers property.
     * 
     */
    public void setIdPers(int value) {
        this.idPers = value;
    }

    /**
     * Gets the value of the idPredType property.
     * 
     */
    public int getIdPredType() {
        return idPredType;
    }

    /**
     * Sets the value of the idPredType property.
     * 
     */
    public void setIdPredType(int value) {
        this.idPredType = value;
    }

    /**
     * Gets the value of the idUser property.
     * 
     */
    public int getIdUser() {
        return idUser;
    }

    /**
     * Sets the value of the idUser property.
     * 
     */
    public void setIdUser(int value) {
        this.idUser = value;
    }

    /**
     * Gets the value of the idXoz property.
     * 
     */
    public int getIdXoz() {
        return idXoz;
    }

    /**
     * Sets the value of the idXoz property.
     * 
     */
    public void setIdXoz(int value) {
        this.idXoz = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the otdelId property.
     * 
     */
    public int getOtdelId() {
        return otdelId;
    }

    /**
     * Sets the value of the otdelId property.
     * 
     */
    public void setOtdelId(int value) {
        this.otdelId = value;
    }

    /**
     * Gets the value of the podrId property.
     * 
     */
    public int getPodrId() {
        return podrId;
    }

    /**
     * Sets the value of the podrId property.
     * 
     */
    public void setPodrId(int value) {
        this.podrId = value;
    }

    /**
     * Gets the value of the predId property.
     * 
     */
    public int getPredId() {
        return predId;
    }

    /**
     * Sets the value of the predId property.
     * 
     */
    public void setPredId(int value) {
        this.predId = value;
    }

    /**
     * Gets the value of the stanId property.
     * 
     */
    public int getStanId() {
        return stanId;
    }

    /**
     * Sets the value of the stanId property.
     * 
     */
    public void setStanId(int value) {
        this.stanId = value;
    }

    /**
     * Gets the value of the userMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserMessage() {
        return userMessage;
    }

    /**
     * Sets the value of the userMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserMessage(String value) {
        this.userMessage = value;
    }

    /**
     * Gets the value of the aliasDb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAliasDb() {
        return aliasDb;
    }

    /**
     * Sets the value of the aliasDb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAliasDb(String value) {
        this.aliasDb = value;
    }

}
