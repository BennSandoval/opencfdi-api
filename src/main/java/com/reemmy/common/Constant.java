package com.reemmy.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.android.gcm.server.*;
import com.reemmy.api.models.database.GoogleCloudMessage;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
public class Constant {

    public static String rfcIntegrador="BBB010101001";
    public static String integrador="2b3a8764-d586-4543-9b7e-82834443f219";
    public static String idAlta="DF627BC3-A872-4806-BF37-DBD040CBAC7C";

    public static final String WEB_CLIENT_ID = "16988659161-k0j246jvjkk6i89sdqpb38nqqusseuor.apps.googleusercontent.com";
    public static final String ANDROID_CLIENT_ID = "16988659161-e6tlpq6i8nb87euqpiivtp4nm10d532f.apps.googleusercontent.com";
    public static final String IOS_CLIENT_ID = "";
    public static final String EXPLORE_CLIENT_ID = "292824132082.apps.googleusercontent.com";
    public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;
    private static final String API_KEY_GOOGLE_CLOUD = "AIzaSyAc_ZMVmWWRpCaK0P-UtpwZLhgCd_Spl58";


    public static void sendMessageToDevice(String titulo, List<GoogleCloudMessage> gcms) {

        Sender sender = new Sender(Constant.API_KEY_GOOGLE_CLOUD);
        Message message = new Message.Builder().addData("Mensaje", titulo).build();
        List<String> devices = new ArrayList<String>();
        for(GoogleCloudMessage gcm:gcms){
            devices.add(gcm.getGcmKey());
        }
        MulticastResult result = null;
        try {
            result = sender.send(message, devices, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void sendMessageToDevice(String titulo, List<GoogleCloudMessage> gcms,List<Map.Entry<String,String>> mensajes) {

        Sender sender = new Sender(Constant.API_KEY_GOOGLE_CLOUD);
        Message.Builder message = new Message.Builder();
        message.addData("Mensaje", titulo);

        for(Map.Entry<String,String> mensaje:mensajes){
            message.addData(mensaje.getKey(),mensaje.getValue());
        }

        List<String> devices = new ArrayList<String>();
        for(GoogleCloudMessage gcm:gcms){
            devices.add(gcm.getGcmKey());
        }
        MulticastResult result = null;
        try {
            result = sender.send(message.build(), devices, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
