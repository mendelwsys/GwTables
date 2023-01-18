
package com.mycompany.server.wsauthclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="definstdir" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="exeName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ftp_ip" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ftp_pass" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ftp_path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ftp_port" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ftp_un" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="id_prog" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="id_prog_parent" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="instpathkey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="need_sys" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="proxy_ip" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="proxy_pass" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="proxy_port" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="proxy_un" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="reestr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="server_type" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="appName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ver" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateInfo", propOrder = {
    "definstdir",
    "exeName",
    "ftpIp",
    "ftpPass",
    "ftpPath",
    "ftpPort",
    "ftpUn",
    "idProg",
    "idProgParent",
    "instpathkey",
    "needSys",
    "proxyIp",
    "proxyPass",
    "proxyPort",
    "proxyUn",
    "reestr",
    "serverType",
    "appName",
    "ver"
})
public class UpdateInfo {

    @XmlElement(required = true, nillable = true)
    protected String definstdir;
    @XmlElement(required = true, nillable = true)
    protected String exeName;
    @XmlElement(name = "ftp_ip", required = true, nillable = true)
    protected String ftpIp;
    @XmlElement(name = "ftp_pass", required = true, nillable = true)
    protected String ftpPass;
    @XmlElement(name = "ftp_path", required = true, nillable = true)
    protected String ftpPath;
    @XmlElement(name = "ftp_port")
    protected int ftpPort;
    @XmlElement(name = "ftp_un", required = true, nillable = true)
    protected String ftpUn;
    @XmlElement(name = "id_prog")
    protected int idProg;
    @XmlElement(name = "id_prog_parent")
    protected int idProgParent;
    @XmlElement(required = true, nillable = true)
    protected String instpathkey;
    @XmlElement(name = "need_sys", required = true, nillable = true)
    protected String needSys;
    @XmlElement(name = "proxy_ip", required = true, nillable = true)
    protected String proxyIp;
    @XmlElement(name = "proxy_pass", required = true, nillable = true)
    protected String proxyPass;
    @XmlElement(name = "proxy_port")
    protected int proxyPort;
    @XmlElement(name = "proxy_un", required = true, nillable = true)
    protected String proxyUn;
    @XmlElement(required = true, nillable = true)
    protected String reestr;
    @XmlElement(name = "server_type")
    protected int serverType;
    @XmlElement(required = true, nillable = true)
    protected String appName;
    @XmlElement(required = true, nillable = true)
    protected String ver;

    /**
     * Gets the value of the definstdir property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefinstdir() {
        return definstdir;
    }

    /**
     * Sets the value of the definstdir property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefinstdir(String value) {
        this.definstdir = value;
    }

    /**
     * Gets the value of the exeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExeName() {
        return exeName;
    }

    /**
     * Sets the value of the exeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExeName(String value) {
        this.exeName = value;
    }

    /**
     * Gets the value of the ftpIp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFtpIp() {
        return ftpIp;
    }

    /**
     * Sets the value of the ftpIp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFtpIp(String value) {
        this.ftpIp = value;
    }

    /**
     * Gets the value of the ftpPass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFtpPass() {
        return ftpPass;
    }

    /**
     * Sets the value of the ftpPass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFtpPass(String value) {
        this.ftpPass = value;
    }

    /**
     * Gets the value of the ftpPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFtpPath() {
        return ftpPath;
    }

    /**
     * Sets the value of the ftpPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFtpPath(String value) {
        this.ftpPath = value;
    }

    /**
     * Gets the value of the ftpPort property.
     * 
     */
    public int getFtpPort() {
        return ftpPort;
    }

    /**
     * Sets the value of the ftpPort property.
     * 
     */
    public void setFtpPort(int value) {
        this.ftpPort = value;
    }

    /**
     * Gets the value of the ftpUn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFtpUn() {
        return ftpUn;
    }

    /**
     * Sets the value of the ftpUn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFtpUn(String value) {
        this.ftpUn = value;
    }

    /**
     * Gets the value of the idProg property.
     * 
     */
    public int getIdProg() {
        return idProg;
    }

    /**
     * Sets the value of the idProg property.
     * 
     */
    public void setIdProg(int value) {
        this.idProg = value;
    }

    /**
     * Gets the value of the idProgParent property.
     * 
     */
    public int getIdProgParent() {
        return idProgParent;
    }

    /**
     * Sets the value of the idProgParent property.
     * 
     */
    public void setIdProgParent(int value) {
        this.idProgParent = value;
    }

    /**
     * Gets the value of the instpathkey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstpathkey() {
        return instpathkey;
    }

    /**
     * Sets the value of the instpathkey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstpathkey(String value) {
        this.instpathkey = value;
    }

    /**
     * Gets the value of the needSys property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNeedSys() {
        return needSys;
    }

    /**
     * Sets the value of the needSys property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNeedSys(String value) {
        this.needSys = value;
    }

    /**
     * Gets the value of the proxyIp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProxyIp() {
        return proxyIp;
    }

    /**
     * Sets the value of the proxyIp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProxyIp(String value) {
        this.proxyIp = value;
    }

    /**
     * Gets the value of the proxyPass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProxyPass() {
        return proxyPass;
    }

    /**
     * Sets the value of the proxyPass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProxyPass(String value) {
        this.proxyPass = value;
    }

    /**
     * Gets the value of the proxyPort property.
     * 
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Sets the value of the proxyPort property.
     * 
     */
    public void setProxyPort(int value) {
        this.proxyPort = value;
    }

    /**
     * Gets the value of the proxyUn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProxyUn() {
        return proxyUn;
    }

    /**
     * Sets the value of the proxyUn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProxyUn(String value) {
        this.proxyUn = value;
    }

    /**
     * Gets the value of the reestr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReestr() {
        return reestr;
    }

    /**
     * Sets the value of the reestr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReestr(String value) {
        this.reestr = value;
    }

    /**
     * Gets the value of the serverType property.
     * 
     */
    public int getServerType() {
        return serverType;
    }

    /**
     * Sets the value of the serverType property.
     * 
     */
    public void setServerType(int value) {
        this.serverType = value;
    }

    /**
     * Gets the value of the appName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Sets the value of the appName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppName(String value) {
        this.appName = value;
    }

    /**
     * Gets the value of the ver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVer() {
        return ver;
    }

    /**
     * Sets the value of the ver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVer(String value) {
        this.ver = value;
    }

}
