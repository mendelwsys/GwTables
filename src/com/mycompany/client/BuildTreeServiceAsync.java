package com.mycompany.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mycompany.common.DescOperation;
import com.mycompany.common.DmmyType;
import com.mycompany.common.ListGridDescriptor;
import com.mycompany.common.UserProfile;
import com.mycompany.common.tables.HeaderSpanMimic;

import java.math.BigDecimal;
import java.util.List;

/**
 * The async counterpart of <code>BuildTreeService</code>.
 */
public interface BuildTreeServiceAsync {
  void greetServer(String input, AsyncCallback<String> callback);
  void saveNodes(String userId, UserProfile profile, DescOperation operations, AsyncCallback<UserProfile> async);
  void testSerializer(UserProfile profile, DescOperation operations, AsyncCallback<DescOperation> async);
  void getNodes(UserProfile pofile, AsyncCallback<DescOperation> async);

  void deleteProfile(UserProfile profile, AsyncCallback<Void> async);

  void getProfiles(String userId,AsyncCallback<UserProfile[]> async);

   void dummy(DmmyType dummyVal, AsyncCallback<DmmyType> async);

    void buildExcelReport(String records, String[] headers, AsyncCallback<String> async);

    void getProfiles_new(BigDecimal userId, AsyncCallback<UserProfile[]> async);

    void deleteProfile_new(UserProfile profile, AsyncCallback<Void> async);

    void getNodes_new(BigDecimal userId,UserProfile profile, AsyncCallback<DescOperation> async);

    void saveNodes_new(BigDecimal userId, UserProfile profile, DescOperation operations, AsyncCallback<UserProfile> async);


    void buildExcelReport_new(String records, String styles, ListGridDescriptor headers, HeaderSpanMimic ad, AsyncCallback<String[]> async);



    void getProfileIdToLoadFirst(BigDecimal userId, int type, AsyncCallback<List<UserProfile>> async);

    void checkVersion(String inVersion, AsyncCallback<String> async);

    void shouldRestart(AsyncCallback<Boolean> async);
}
