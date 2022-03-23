package com.jemmy.framework.component.location;

import com.jemmy.framework.utils.request.Uri;

import java.net.URI;

public class AMapUri {
    private final static String key = "48a9b25a49511aa81c9b7622db489d13";

    private final static String uri = "https://restapi.amap.com";

    public static URI geography(Double lng, Double lat) {
        return Uri.of(uri + "/v3/geocode/regeo")
                .setParam("key", key)
                .setParam("location", lng + "," + lat)
                .setParam("extensions", "all")
                .build();
    }

    public static URI ip(String ip) {
        return Uri.of(uri + "/v5/ip")
                .setParam("key", key)
                .setParam("type", "4")
                .setParam("ip", ip)
                .build();
    }
}
