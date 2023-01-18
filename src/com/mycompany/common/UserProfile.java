package com.mycompany.common;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 17.12.14
 * Time: 13:49
 * Профиль пользовтеля
 */
public class UserProfile implements Serializable
{
    public static final int DESKTOP_INFORMER_PROFILES = 1;
    public static final int LAST_USED_PROFILE = 2;

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    String profileName;
    int profileId;


    public boolean isDesktopInformers() {
        return desktopInformers;
    }

    public void setDesktopInformers(boolean desktopInformers) {
        this.desktopInformers = desktopInformers;
    }

    boolean desktopInformers = false;


    public String getProfileContents() {
        return profileContents;
    }

    public void setProfileContents(String profileContents) {
        this.profileContents = profileContents;
    }

    String profileContents;

    public UserProfile(int profileId, String profileName) {
        this.profileName = profileName;
        this.profileId = profileId;
    }

    public UserProfile(int profileId, String profileName, String profileDescriptor) {
        this.profileName = profileName;
        this.profileId = profileId;
        this.profileContents = profileDescriptor;
    }

    public UserProfile(UserProfile profile) {
        this.profileName = profile.profileName;
        this.profileId = profile.profileId;
        this.setDesktopInformers(profile.isDesktopInformers());
    }


    public UserProfile() {
    }
}
