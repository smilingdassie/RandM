package com.dsouchon.wayo.visualfusion;

/**
 * Created by Daniel on 2018/05/23.
 */

public class ContentManagementPage {

    public int ID;
    public int SectionNumber;
    public String SectionText;
    public String SectionImageURL;
    public String FontSize;
    public String SectionHeader;


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public int getSectionNumber() {
        return SectionNumber;
    }

    public void setSectionNumber(int SectionNumber) {
        this.SectionNumber = SectionNumber;
    }
    public String getSectionText() {
        return SectionText;
    }

    public void setSectionText(String SectionText) {
        this.SectionText = SectionText;
    }
    public String getSectionImageURL() {
        return SectionImageURL;
    }

    public void setSectionImageURL(String SectionImageURL) {
        this.SectionImageURL = SectionImageURL;
    }
    public String getFontSize() {
        return FontSize;
    }

    public void setFontSize(String FontSize) {
        this.FontSize = FontSize;
    }
    public String getSectionHeader() {
        return SectionHeader;
    }

    public void setSectionHeader(String SectionHeader) {
        this.SectionHeader = SectionHeader;
    }

}
