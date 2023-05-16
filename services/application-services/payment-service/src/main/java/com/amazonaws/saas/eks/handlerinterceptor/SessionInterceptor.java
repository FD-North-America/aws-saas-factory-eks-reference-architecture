package com.amazonaws.saas.eks.handlerinterceptor;

import com.amazonaws.saas.eks.util.EncryptionUtils;
import com.amazonaws.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    private static final Logger logger = LogManager.getLogger(SessionInterceptor.class);

    @Autowired
    private SessionHolder sessionHolder;

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String session = request.getHeader("X-Session");
        if (!StringUtils.isNullOrEmpty(session)) {
            try {
                String decryptedSession = encryptionUtils.decrypt(session);
                String[] sessionValues = decryptedSession.split(":");
                sessionHolder.setMerchantId(sessionValues[0]);
                sessionHolder.setHsn(sessionValues[1]);
                sessionHolder.setSessionKey(sessionValues[2]);
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
        return true;
    }
}
