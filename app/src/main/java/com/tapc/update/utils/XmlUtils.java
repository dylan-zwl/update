package com.tapc.update.utils;

import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * PULL解析XML内容
 */
public class XmlUtils {
    public static Map<String, String> getXmlMap(InputStream inputStream) {
        Map<String, String> map = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        map = new HashMap<>();
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (!TextUtils.isEmpty(name) && !name.equals("resources")) {
                            map.put(name, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            map = null;
        }
        return map;
    }
}