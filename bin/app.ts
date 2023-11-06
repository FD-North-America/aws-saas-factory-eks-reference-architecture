#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { EKSClusterStack } from '../lib/eks-cluster-stack';
import { StaticSitesStack } from '../lib/static-sites-stack';
import { ServicesStack } from '../lib/services-stack';
import { AppServicesStack } from '../lib/app-services-stack';
import { CommonResourcesStack } from '../lib/common-resources-stack';
import { ApiStack } from '../lib/api-stack';
import { AppServiceStack } from '../lib/app-service-stack';

const env = {
    account: process.env.AWS_ACCOUNT_ID,
    region: process.env.AWS_REGION
};

const clusterName = "EKSSaaS";
const ingressControllerName = "saasnginxingressctrl";
const tenantOnboardingProjectName = "TenantOnboardingProject";
const tenantDeletionProjectName = "TenantDeletionProject";
const sharedServiceAccountName = "shared-service-account";

const customDomain = process.env.npm_config_domain && process.env.npm_config_domain.length > 0 ? process.env.npm_config_domain : undefined;
const hostedZoneId = process.env.npm_config_hostedzone && process.env.npm_config_hostedzone.length > 0 ? process.env.npm_config_hostedzone : undefined;
const saasAdminEmail = process.env.npm_config_email!;
const kubecostToken = process.env.npm_config_kubecosttoken && process.env.npm_config_kubecosttoken.length > 0 ? process.env.npm_config_kubecosttoken : undefined;

console.log("Initiating CDK App ...");

const app = new cdk.App();

const clusterStack = new EKSClusterStack(app, 'EKSSaaSCluster', {
    env,
    clusterName: clusterName,
    ingressControllerName: ingressControllerName,
    tenantOnboardingProjectName: tenantOnboardingProjectName,
    tenantDeletionProjectName: tenantDeletionProjectName,
    sharedServiceAccountName: sharedServiceAccountName,
    kubecostToken: kubecostToken,
    customDomain: customDomain,
    hostedZoneId: hostedZoneId
});

const apiStack = new ApiStack(app, 'SaaSApi', {
    env,
    eksClusterName: clusterName,
    ingressControllerName: ingressControllerName,
    internalNLBDomain: clusterStack.nlbDomain,
    vpc: clusterStack.vpc,
    customDomain: customDomain,
    hostedZoneId: hostedZoneId
});
apiStack.addDependency(clusterStack, "EKSSaaSCluster dependency");

const sitesStack = new StaticSitesStack(app, 'StaticSites', {
    env,
    apiUrl: apiStack.apiUrl,
    saasAdminEmail: saasAdminEmail,
    hostedZoneId: hostedZoneId,
    customBaseDomain: customDomain,
    usingKubeCost: !!kubecostToken,
});
sitesStack.addDependency(apiStack, "SaaSApi dependency");

const commonResource = new CommonResourcesStack(app, 'CommonResources', {
    env,
});

const svcStack = new ServicesStack(app, 'Services', {
    env,
    internalNLBApiDomain: clusterStack.nlbDomain,
    eksClusterName: clusterName,
    eksClusterOIDCProviderArn: clusterStack.openIdConnectProviderArn,
    codebuildKubectlRoleArn: clusterStack.codebuildKubectlRoleArn,
    appSiteDistributionId: sitesStack.applicationSiteDistribution.distributionId,
    appSiteCloudFrontDomain: sitesStack.applicationSiteDistribution.distributionDomainName,
    sharedServiceAccountName: sharedServiceAccountName,
    appHostedZoneId: hostedZoneId,
    customDomain: customDomain,
});
svcStack.addDependency(clusterStack, "EKSSaaSCluster dependency");
svcStack.addDependency(apiStack, "SaaSApi dependency");

const appSvcStack = new AppServicesStack(app, 'AppServices', {
    env,
    internalNLBApiDomain: clusterStack.nlbDomain,
    eksClusterName: clusterName,
    eksClusterOIDCProviderArn: clusterStack.openIdConnectProviderArn,
    codebuildKubectlRoleArn: clusterStack.codebuildKubectlRoleArn,
    appSiteDistributionId: sitesStack.applicationSiteDistribution.distributionId,
    appSiteCloudFrontDomain: sitesStack.applicationSiteDistribution.distributionDomainName,
    sharedServiceAccountName: sharedServiceAccountName,
    appHostedZoneId: hostedZoneId,
    customDomain: customDomain,
});
appSvcStack.addDependency(clusterStack, "EKSSaaSCluster dependency");
appSvcStack.addDependency(apiStack, "SaaSApi dependency");

// const clusterStack = {
//     nlbDomain: "a7080937dc7414b0386f6053f63798c5-b2cf4b80ed225f3d.elb.us-west-1.amazonaws.com",
//     openIdConnectProviderArn: "arn:aws:iam::486225280130:oidc-provider/oidc.eks.us-west-1.amazonaws.com/id/A6445047C39C25DEFD8B32F42BB63721",
//     codebuildKubectlRoleArn: "arn:aws:iam::486225280130:role/EKSSaaSCluster-CodebuildKubectlRole7B5A6607-9W1VQPZYUWD4"

// }
// const sitesStack = {
//     applicationSiteDistribution: {
//         distributionId: "E1D4X3J094EWEA",
//         distributionDomainName: "d2mnyqdjzt0dnw.cloudfront.net"
//     }
// }

const reportServiceName = "ReportService";
const reportSingleAppSvcStack = new AppServiceStack(app, reportServiceName, {
    env,
    internalNLBApiDomain: clusterStack.nlbDomain,
    eksClusterName: clusterName,
    eksClusterOIDCProviderArn: clusterStack.openIdConnectProviderArn,
    codebuildKubectlRoleArn: clusterStack.codebuildKubectlRoleArn,
    appSiteDistributionId: sitesStack.applicationSiteDistribution.distributionId,
    appSiteCloudFrontDomain: sitesStack.applicationSiteDistribution.distributionDomainName,
    sharedServiceAccountName: sharedServiceAccountName,
    appHostedZoneId: hostedZoneId,
    customDomain: customDomain,
    
    serviceName: reportServiceName,
    ecrImageName: "report-svc",
    serviceUrlPrefix: "reports",
    serviceSourceDir: "report-service"
});

const cashDrawerServiceName = "CashDrawerService";
const cashDrawerSingleAppSvcStack = new AppServiceStack(app, cashDrawerServiceName, {
    env,
    internalNLBApiDomain: clusterStack.nlbDomain,
    eksClusterName: clusterName,
    eksClusterOIDCProviderArn: clusterStack.openIdConnectProviderArn,
    codebuildKubectlRoleArn: clusterStack.codebuildKubectlRoleArn,
    appSiteDistributionId: sitesStack.applicationSiteDistribution.distributionId,
    appSiteCloudFrontDomain: sitesStack.applicationSiteDistribution.distributionDomainName,
    sharedServiceAccountName: sharedServiceAccountName,
    appHostedZoneId: hostedZoneId,
    customDomain: customDomain,
    
    serviceName: cashDrawerServiceName,
    ecrImageName: "cashdrawer-svc",
    serviceUrlPrefix: "cashdrawers",
    serviceSourceDir: "cashdrawer-service"
});

const customerServiceName = "CustomerService";
const customerSingleAppSvcStack = new AppServiceStack(app, customerServiceName, {
    env,
    internalNLBApiDomain: clusterStack.nlbDomain,
    eksClusterName: clusterName,
    eksClusterOIDCProviderArn: clusterStack.openIdConnectProviderArn,
    codebuildKubectlRoleArn: clusterStack.codebuildKubectlRoleArn,
    appSiteDistributionId: sitesStack.applicationSiteDistribution.distributionId,
    appSiteCloudFrontDomain: sitesStack.applicationSiteDistribution.distributionDomainName,
    sharedServiceAccountName: sharedServiceAccountName,
    appHostedZoneId: hostedZoneId,
    customDomain: customDomain,
    
    serviceName: customerServiceName,
    ecrImageName: "customer-svc",
    serviceUrlPrefix: "customers",
    serviceSourceDir: "customer-service"
});

const settingsServiceName = "SettingsService";
const settingsSingleAppSvcStack = new AppServiceStack(app, settingsServiceName, {
    env,
    internalNLBApiDomain: clusterStack.nlbDomain,
    eksClusterName: clusterName,
    eksClusterOIDCProviderArn: clusterStack.openIdConnectProviderArn,
    codebuildKubectlRoleArn: clusterStack.codebuildKubectlRoleArn,
    appSiteDistributionId: sitesStack.applicationSiteDistribution.distributionId,
    appSiteCloudFrontDomain: sitesStack.applicationSiteDistribution.distributionDomainName,
    sharedServiceAccountName: sharedServiceAccountName,
    appHostedZoneId: hostedZoneId,
    customDomain: customDomain,
    
    serviceName: settingsServiceName,
    ecrImageName: "settings-svc",
    serviceUrlPrefix: "settings",
    serviceSourceDir: "settings-service"
});
