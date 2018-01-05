package com.zyuco.maskbook.model;

public enum ErrorResponseCode {
    OK(0),
    Invalid_Arguments(1),
    Duplicate_Username(2),
    Duplicate_Nickname(3),
    Login_Failed(4),
    Login_Required(5),
    Missing_Files(6),
    No_Permission(7),
    Not_Found(404),
    Unknown(1000);

    private int code;

    ErrorResponseCode(int code) {
        this.code = code;
    }

    public static ErrorResponseCode fromInt(int code) {
        switch (code) {
            case 0:
                return OK;
            case 1:
                return Invalid_Arguments;
            case 2:
                return Duplicate_Username;
            case 3:
                return Duplicate_Nickname;
            case 4:
                return Login_Failed;
            case 5:
                return Login_Required;
            case 6:
                return Missing_Files;
            case 7:
                return No_Permission;
            case 404:
                return Not_Found;
            case 1000:
                return Unknown;
            default:
                return null;
        }
    }
}
