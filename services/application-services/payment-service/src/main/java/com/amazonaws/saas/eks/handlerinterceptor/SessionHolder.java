package com.amazonaws.saas.eks.handlerinterceptor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionHolder {
    @Getter
    @Setter
    private String sessionKey;

    @Getter
    @Setter
    private String merchantId;

    @Getter
    @Setter
    private String hsn;
}
