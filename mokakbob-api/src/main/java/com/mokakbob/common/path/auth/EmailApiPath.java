package com.mokakbob.common.path.auth;

import com.mokakbob.common.path.ApiVersion;

public class EmailApiPath {

    private static final String BASE = ApiVersion.V1 + "/email";

    public static final String SEND = BASE + "/send";
    public static final String VERIFY = BASE + "/verify";
}
