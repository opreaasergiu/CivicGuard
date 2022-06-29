package com.example.mds;

import android.os.StrictMode;

import java.util.Map;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class testJava {

   StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    public void setPolicy(StrictMode.ThreadPolicy policy) {
        this.policy = policy;
    }

}
