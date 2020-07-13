package com.gsd.kolorbi.enums;

public enum AdPlacement {
    ADS_NATIVE("ADS_NATIVE"),
    ADS_350_250("ADS_350_250"),
    ADS_728_90("ADS_728_90");

    private final String placement;

    private AdPlacement(String placement) {
        switch(placement){
            case "ADS_NATIVE":
                this.placement = " <span id=\"native_ad_1\"></span>\n" +
                        " <script>\n" +
                        " request_native_ad({\n" +
                        " html_template:'',\n" +
                        " adx_adsvr_adspace_id:'3178',\n" +
                        " allowed_ad_types:'', /* Leave Empty for no Restriction */\n" +
                        "  var adx_adsvr_adspace_vAppRoot=\"https://ads.dochase.com/adx-dir-d/\",\n" +
                        "  var adx_custom=\"\",\n" +
                        " adx_nid:13,\n" +
                        " ad_num:1\n" +
                        " });\n" +
                        " </script>";
                break;
            case "ADS_350_250":
                this.placement = "<script type=\"text/javascript\">\n" +
                        "var adx_adsvr_adspace_vAppRoot=\"https://ads.dochase.com/adx-dir-d/\";\n" +
                        "var adx_adsvr_adspace_id=\"3177\";\n" +
                        "var adx_size=\"300x250\";\n" +
                        "var adx_custom=\"\";\n" +
                        "var adx_nid=\"13\";\n" +
                        "</script>\n" +
                        "<script type=\"text/javascript\" src=\"https://j.dochase.com/adxads.js\"></script>";
                break;
            case "ADS_728_90":
                this.placement = "<script type=\"text/javascript\">\n" +
                        "var adx_adsvr_adspace_vAppRoot=\"https://ads.dochase.com/adx-dir-d/\";\n" +
                        "var adx_adsvr_adspace_id=\"3176\";\n" +
                        "var adx_size=\"728x90\";\n" +
                        "var adx_custom=\"\";\n" +
                        "var adx_nid=\"13\";\n" +
                        "</script>\n" +
                        "<script type=\"text/javascript\" src=\"https://j.dochase.com/adxads.js\"></script>";
                break;
            default:
                this.placement = "<script type=\"text/javascript\">\n" +
                        "var adx_adsvr_adspace_vAppRoot=\"https://ads.dochase.com/adx-dir-d/\";\n" +
                        "var adx_adsvr_adspace_id=\"3177\";\n" +
                        "var adx_size=\"300x250\";\n" +
                        "var adx_custom=\"\";\n" +
                        "var adx_nid=\"13\";\n" +
                        "</script>\n" +
                        "<script type=\"text/javascript\" src=\"https://j.dochase.com/adxads.js\"></script>";
                break;
        }
    }

    public String getPlacement() {
        return placement;
    }
}
