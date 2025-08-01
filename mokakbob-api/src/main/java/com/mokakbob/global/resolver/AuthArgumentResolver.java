package com.mokakbob.global.resolver;

import com.mokakbob.common.exception.exceptions.ApiException;
import com.mokakbob.global.exception.GlobalErrorCode;
import com.mokakbob.global.resolver.annotation.Auth;
import com.mokakbob.global.support.AuthConstants;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object memberId = Objects.requireNonNull(webRequest.getNativeRequest(HttpServletRequest.class))
                .getAttribute(AuthConstants.TOKEN_ATTRIBUTE);

        if (memberId == null) {
            throw new ApiException(GlobalErrorCode.NOT_FOUND_TOKEN_MEMBER_ID);
        }

        return memberId;
    }
}
