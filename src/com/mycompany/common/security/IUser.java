package com.mycompany.common.security;

/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public interface IUser {
    /**
     * Gets the value of the roles property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRoles();

    /**
     * Sets the value of the roles property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRoles(String value) ;

    /**
     * Gets the value of the dolId property.
     *
     */
    public int getDolId();

    /**
     * Sets the value of the dolId property.
     *
     */
    public void setDolId(int value);

    /**
     * Gets the value of the dorKod property.
     *
     */
    public int getDorKod() ;

    /**
     * Sets the value of the dorKod property.
     *
     */
    public void setDorKod(int value);

    /**
     * Gets the value of the firstName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFirstName();

    /**
     * Sets the value of the firstName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFirstName(String value);

    /**
     * Gets the value of the idLevel property.
     *
     */
    public int getIdLevel();

    /**
     * Sets the value of the idLevel property.
     *
     */
    public void setIdLevel(int value);

    /**
     * Gets the value of the idPers property.
     *
     */
    public int getIdPers();

    /**
     * Sets the value of the idPers property.
     *
     */
    public void setIdPers(int value);

    /**
     * Gets the value of the idPredType property.
     *
     */
    public int getIdPredType();

    /**
     * Sets the value of the idPredType property.
     *
     */
    public void setIdPredType(int value);

    /**
     * Gets the value of the idUser property.
     *
     */
    public int getIdUser();

    /**
     * Sets the value of the idUser property.
     *
     */
    public void setIdUser(int value);

    /**
     * Gets the value of the idXoz property.
     *
     */
    public int getIdXoz();

    /**
     * Sets the value of the idXoz property.
     *
     */
    public void setIdXoz(int value);

    /**
     * Gets the value of the lastName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLastName();

    /**
     * Sets the value of the lastName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLastName(String value);

    /**
     * Gets the value of the middleName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMiddleName();

    /**
     * Sets the value of the middleName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMiddleName(String value);

    /**
     * Gets the value of the otdelId property.
     *
     */
    public int getOtdelId();

    /**
     * Sets the value of the otdelId property.
     *
     */
    public void setOtdelId(int value);

    /**
     * Gets the value of the podrId property.
     *
     */
    public int getPodrId();

    /**
     * Sets the value of the podrId property.
     *
     */
    public void setPodrId(int value);

    /**
     * Gets the value of the predId property.
     *
     */
    public int getPredId();

    /**
     * Sets the value of the predId property.
     *
     */
    public void setPredId(int value);

    /**
     * Gets the value of the stanId property.
     *
     */
    public int getStanId();

    /**
     * Sets the value of the stanId property.
     *
     */
    public void setStanId(int value);


    public  String getJSONRepresentation();


}
