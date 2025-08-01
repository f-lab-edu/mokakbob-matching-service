package com.mokakbob.common.path.auth;

import com.mokakbob.common.path.ApiVersion;

public class AuthApiPath {

    private static final String BASE = ApiVersion.V1 + "/auth";

    public static final String SIGN_UP = BASE + "/signUp";
    public static final String LOGIN = BASE + "/login";
    public static final String REISSUE = BASE + "/reissue";
}
