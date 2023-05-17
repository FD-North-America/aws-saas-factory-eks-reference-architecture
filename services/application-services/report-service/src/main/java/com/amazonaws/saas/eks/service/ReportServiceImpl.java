package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.secretsmanager.SecretsClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    @Autowired
    private SecretsClient secretsClient;
}
