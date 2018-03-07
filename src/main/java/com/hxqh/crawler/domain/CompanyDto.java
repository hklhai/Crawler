package com.hxqh.crawler.domain;

/**
 * Created by Ocean lin on 2018/3/7.
 *
 * @author Ocean lin
 */
public class CompanyDto {

    private String productCompany;
    private String issueCompany;
    private String otherInfo;


    public CompanyDto() {
    }

    public CompanyDto(String productCompany, String issueCompany, String otherInfo) {
        this.productCompany = productCompany;
        this.issueCompany = issueCompany;
        this.otherInfo = otherInfo;
    }

    public String getProductCompany() {
        return productCompany;
    }

    public void setProductCompany(String productCompany) {
        this.productCompany = productCompany;
    }

    public String getIssueCompany() {
        return issueCompany;
    }

    public void setIssueCompany(String issueCompany) {
        this.issueCompany = issueCompany;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }
}
