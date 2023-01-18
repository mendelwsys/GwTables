package com.mycompany.common.security;


/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public class User implements IUser{

    protected String roles;
    protected int dolId=-1;
    protected int dorKod=-1;
    protected String firstName;
    protected int idLevel=-1;
    protected int idPers=-1;
    protected int idPredType=-1;
    protected int idUser=-1;
    protected int idXoz=-1;
    protected String lastName;
    protected String middleName;
    protected int otdelId=-1;
    protected int podrId=-1;
    protected int predId=-1;
    protected int stanId=-1;



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

    @Override
    public String getJSONRepresentation() {
        String json ="{";

        json += "\"dolId\":" + this.getDolId() + ",";
        json += "\"dorKod\":" + this.getDorKod() + ",";
        json+="\"firstName\":\""+this.getFirstName()+"\",";
        json += "\"idLevel\":" + this.getIdLevel() + ",";
        json += "\"idPers\":" + this.getIdPers() + ",";
        json += "\"idPredType\":" + this.getIdPredType() + ",";
        json += "\"idUser\":" + this.getIdUser() + ",";
        json += "\"idXoz\":" + this.getIdXoz() + ",";
        json+="\"lastName\":\""+this.getLastName()+"\",";
        json+="\"middleName\":\""+this.getMiddleName()+"\",";
        json += "\"otdelId\":" + this.getOtdelId() + ",";
        json += "\"podrId\":" + this.getPodrId() + ",";
        json += "\"predId\":" + this.getPredId() + ",";
        json+="\"roles\":\""+this.getRoles()+"\",";
        json += "\"stanId\":" + this.getStanId() + "}";

        return json;
    }


}
