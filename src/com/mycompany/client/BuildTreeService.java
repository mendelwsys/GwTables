package com.mycompany.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mycompany.common.DescOperation;
import com.mycompany.common.DmmyType;
import com.mycompany.common.ListGridDescriptor;
import com.mycompany.common.UserProfile;
import com.mycompany.common.tables.HeaderSpanMimic;

import java.math.BigDecimal;
import java.util.List;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface BuildTreeService extends RemoteService
{
  String greetServer(String name);
  DescOperation getNodes(UserProfile pofile);

    DescOperation getNodes_new(BigDecimal userId,UserProfile profile);

  UserProfile saveNodes(String userId, UserProfile profile, DescOperation operations);



    /*
        // Set up the callback object.
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
            // TODO: Do something with errors.
            }

            public void onSuccess(String result)
            {
              SC.say("Hello World:" + result);
            }
        };
        greetingService.greetServer("Init it",callback);
     */

    DescOperation testSerializer(UserProfile profile, DescOperation operations);

    DmmyType dummy(DmmyType dummyVal);

    UserProfile[] getProfiles(String userId);

    UserProfile[] getProfiles_new(BigDecimal userId);

    void deleteProfile(UserProfile profile);

    public void deleteProfile_new(UserProfile profile);

    String buildExcelReport(String records, String[] headers);

    UserProfile saveNodes_new(BigDecimal userId, UserProfile profile, DescOperation operations);



    List<UserProfile> getProfileIdToLoadFirst(BigDecimal userId, int type);


    String checkVersion(String inVersion);

    Boolean shouldRestart();

    String[] buildExcelReport_new(String records, String styles, ListGridDescriptor headers, HeaderSpanMimic ad);
}
