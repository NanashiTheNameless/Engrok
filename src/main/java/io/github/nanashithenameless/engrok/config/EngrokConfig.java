package io.github.nanashithenameless.engrok.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "engrok")
public class EngrokConfig implements ConfigData {
    public boolean enabled = true;
    public String ngrokAuthToken = "Insert your Ngrok auth token here";

    public enum regionSelectEnum {
        AUTO(null),
        US("us"),
        US_CAL_1("us-cal-1"),
        EU("eu"),
        AP("ap"),
        AU("au"),
        SA("sa"),
        JP("jp"),
        IN("in");

        private final String ngrokRegionCode;

        regionSelectEnum(String ngrokRegionCode) {
            this.ngrokRegionCode = ngrokRegionCode;
        }

        public String getNgrokRegionCode() {
            return ngrokRegionCode;
        }
    }

    public regionSelectEnum regionSelect = regionSelectEnum.AUTO;

    public String gitHubAuthToken = "Insert your GitHub auth token here";
    public String gistId = "Insert your gist ID here";
}
