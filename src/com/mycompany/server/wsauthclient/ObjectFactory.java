
package com.mycompany.server.wsauthclient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.mycompany.server.wsauthclient package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InvalidVersionException_QNAME = new QName("http://auth.vniias", "InvalidVersionException");
    private final static QName _NsiException_QNAME = new QName("http://auth.vniias", "NsiException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.mycompany.server.wsauthclient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetProgsIdByUserResponse }
     * 
     */
    public GetProgsIdByUserResponse createGetProgsIdByUserResponse() {
        return new GetProgsIdByUserResponse();
    }

    /**
     * Create an instance of {@link ArrayOfXsdNillableInt }
     * 
     */
    public ArrayOfXsdNillableInt createArrayOfXsdNillableInt() {
        return new ArrayOfXsdNillableInt();
    }

    /**
     * Create an instance of {@link NsiException }
     * 
     */
    public NsiException createNsiException() {
        return new NsiException();
    }

    /**
     * Create an instance of {@link InvalidVersionException }
     * 
     */
    public InvalidVersionException createInvalidVersionException() {
        return new InvalidVersionException();
    }

    /**
     * Create an instance of {@link GetProgsIdByUser }
     * 
     */
    public GetProgsIdByUser createGetProgsIdByUser() {
        return new GetProgsIdByUser();
    }

    /**
     * Create an instance of {@link LoginResponse }
     * 
     */
    public LoginResponse createLoginResponse() {
        return new LoginResponse();
    }

    /**
     * Create an instance of {@link UserInfo }
     * 
     */
    public UserInfo createUserInfo() {
        return new UserInfo();
    }

    /**
     * Create an instance of {@link GetSystemUpdateInfoResponse }
     * 
     */
    public GetSystemUpdateInfoResponse createGetSystemUpdateInfoResponse() {
        return new GetSystemUpdateInfoResponse();
    }

    /**
     * Create an instance of {@link ArrayOfUpdateInfo }
     * 
     */
    public ArrayOfUpdateInfo createArrayOfUpdateInfo() {
        return new ArrayOfUpdateInfo();
    }

    /**
     * Create an instance of {@link Login2Response }
     * 
     */
    public Login2Response createLogin2Response() {
        return new Login2Response();
    }

    /**
     * Create an instance of {@link GetSystemUpdateInfo }
     * 
     */
    public GetSystemUpdateInfo createGetSystemUpdateInfo() {
        return new GetSystemUpdateInfo();
    }

    /**
     * Create an instance of {@link GetProgsIdByUserPwd }
     * 
     */
    public GetProgsIdByUserPwd createGetProgsIdByUserPwd() {
        return new GetProgsIdByUserPwd();
    }

    /**
     * Create an instance of {@link Login2 }
     * 
     */
    public Login2 createLogin2() {
        return new Login2();
    }

    /**
     * Create an instance of {@link GetUsersByProgResponse }
     * 
     */
    public GetUsersByProgResponse createGetUsersByProgResponse() {
        return new GetUsersByProgResponse();
    }

    /**
     * Create an instance of {@link ArrayOfUserInfo }
     * 
     */
    public ArrayOfUserInfo createArrayOfUserInfo() {
        return new ArrayOfUserInfo();
    }

    /**
     * Create an instance of {@link Login }
     * 
     */
    public Login createLogin() {
        return new Login();
    }

    /**
     * Create an instance of {@link GetProgsIdByUserPwdResponse }
     * 
     */
    public GetProgsIdByUserPwdResponse createGetProgsIdByUserPwdResponse() {
        return new GetProgsIdByUserPwdResponse();
    }

    /**
     * Create an instance of {@link GetUsersByProg }
     * 
     */
    public GetUsersByProg createGetUsersByProg() {
        return new GetUsersByProg();
    }

    /**
     * Create an instance of {@link UpdateInfo }
     * 
     */
    public UpdateInfo createUpdateInfo() {
        return new UpdateInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidVersionException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://auth.vniias", name = "InvalidVersionException")
    public JAXBElement<InvalidVersionException> createInvalidVersionException(InvalidVersionException value) {
        return new JAXBElement<InvalidVersionException>(_InvalidVersionException_QNAME, InvalidVersionException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NsiException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://auth.vniias", name = "NsiException")
    public JAXBElement<NsiException> createNsiException(NsiException value) {
        return new JAXBElement<NsiException>(_NsiException_QNAME, NsiException.class, null, value);
    }

}
