package com.murat.firebasecalismam.Utils;


import com.google.firebase.database.Exclude;

public class Upload {

    private String mName;
    private String mImageUrl;
    private String mUsersKey;
    private String isim;
    private String soyisim;
    private String profilLink;
    private String imageKey;
    private String tamAd;
    private String mKey;


    public Upload() {
        //empty constructor needed
    }


    public Upload(String imageName, String imageUrl, String usersKey, String isims, String soyisims, String linkProfil, String uploadId,String tamIsim) {
        if (imageName.trim().equals("")) {
            imageName = "No Name";
        }

        mName = imageName;
        mImageUrl = imageUrl;
        mUsersKey = usersKey;
        isim = isims;
        soyisim = soyisims;
        profilLink = linkProfil;
        imageKey = uploadId;
        tamAd=tamIsim;
    }

    public String getmUsersKey() {
        return mUsersKey;
    }

    public void setmUsersKey(String mUsersKey) {
        this.mUsersKey = mUsersKey;
    }

    public String getName() {
        return mName;
    }

    public void setName(String imageName) {
        mName = imageName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getSoyisim() {
        return soyisim;
    }

    public void setSoyisim(String soyisim) {
        this.soyisim = soyisim;
    }


    public String getProfilLink() {
        return profilLink;
    }

    public void setProfilLink(String profilLink) {
        this.profilLink = profilLink;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getTamAd() {
        return tamAd;
    }

    public void setTamAd(String tamAd) {
        this.tamAd = tamAd;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}
