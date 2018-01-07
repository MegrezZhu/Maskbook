package com.zyuco.maskbook.lib;

import com.zyuco.maskbook.service.APIService;

import java.net.MalformedURLException;
import java.net.URL;

public class URLFormatter {
    public static URL formatImageURL(String image) {
        try {
            URL base = new URL(APIService.BASE_URL);
            URL imageURL = new URL(base, image);
            return imageURL;
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
