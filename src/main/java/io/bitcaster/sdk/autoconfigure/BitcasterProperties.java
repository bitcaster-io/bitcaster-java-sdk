package io.bitcaster.sdk.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bitcaster")
public class BitcasterProperties {
    private String bae = "";
    private String project = "";
    private String application = "";
    private String distributionList = "";

    public String getBae() { return bae; }
    public void setBae(String bae) { this.bae = bae; }
    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }
    public String getApplication() { return application; }
    public void setApplication(String application) { this.application = application; }
    public String getDistributionList() { return distributionList; }
    public void setDistributionList(String distributionList) { this.distributionList = distributionList; }
}