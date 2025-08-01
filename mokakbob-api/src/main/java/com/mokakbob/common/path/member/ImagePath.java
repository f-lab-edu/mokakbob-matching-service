package com.mokakbob.common.path.member;

import com.mokakbob.common.path.ApiVersion;

public class ImagePath {

    private static final String BASE = ApiVersion.V1 + "/image";

    public static final String UPLOAD = BASE + "/upload/{memberId}";
}
