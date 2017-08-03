package com.tapc.platform.model.vaplayer;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;

public class VaPlayListAnalysis {
    public PlayEntity getvaInfor(InputStream xml) throws Exception {
        PlayEntity vaPlayList = new PlayEntity();
        vaPlayList.evtList = new ArrayList<String>();
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(xml, "UTF-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            String nodeName = pullParser.getName();
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if ("name".equals(nodeName)) {
                        vaPlayList.name = pullParser.nextText();
                    } else if ("description".equals(nodeName)) {
                        vaPlayList.description = pullParser.nextText();
                    } else if ("location".equals(nodeName)) {
                        vaPlayList.location = pullParser.nextText();
                    } else if ("still".equals(nodeName)) {
                        vaPlayList.still = pullParser.nextText().replace("\\", "/");
                    } else if ("uniqueid".equals(nodeName)) {
                        vaPlayList.uniqueid = pullParser.nextText();
                    } else if ("video".equals(nodeName)) {
                        vaPlayList.evtList.add(pullParser.nextText().replace("\\", "/"));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            event = pullParser.next();
        }
        return vaPlayList;
    }
}
