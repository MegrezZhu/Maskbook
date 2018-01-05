package com.zyuco.maskbook;

import android.app.Application;
import android.support.annotation.NonNull;

import com.zyuco.maskbook.model.User;

public class MaskbookApplication extends Application {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(@NonNull User user) {
        this.user = user;
    }
}
