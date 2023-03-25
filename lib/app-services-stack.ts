import { CfnOutput, Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import * as iam from "aws-cdk-lib/aws-iam";
import * as path from "path";
import { ApplicationService } from "./constructs/application-service";

export interface AppServicesStackProps extends StackProps {
    readonly internalNLBApiDomain: string
    readonly eksClusterName: string
    readonly eksClusterOIDCProviderArn: string
    readonly codebuildKubectlRoleArn: string
    readonly appSiteDistributionId: string
    readonly appSiteCloudFrontDomain: string
    readonly sharedServiceAccountName: string
    readonly appHostedZoneId?: string
    readonly customDomain?: string
}

export class AppServicesStack extends Stack {
    constructor(scope: Construct, id: string, props: AppServicesStackProps) {
        super(scope, id, props);

        const role = iam.Role.fromRoleArn(this, "CodebuildKubectlRole", props.codebuildKubectlRoleArn);

        // application services

        const productSvc = new ApplicationService(this, "ProductService", {
            internalApiDomain: props.internalNLBApiDomain,
            eksClusterName: props.eksClusterName,
            codebuildKubectlRole: role,
            name: "ProductService",
            ecrImageName: "product-svc",
            serviceUrlPrefix: "products",
            assetDirectory: path.join(__dirname, "..", "services", "application-services", "product-service")
        });
        new CfnOutput(this, "ProductServiceRepository", {
            value: productSvc.codeRepositoryUrl
        });

        const orderSvc = new ApplicationService(this, "OrderService", {
            internalApiDomain: props.internalNLBApiDomain,
            eksClusterName: props.eksClusterName,
            codebuildKubectlRole: role,
            name: "OrderService",
            ecrImageName: "order-svc",
            serviceUrlPrefix: "orders",
            assetDirectory: path.join(__dirname, "..", "services", "application-services", "order-service")
        });
        new CfnOutput(this, "OrderServiceRepository", {
            value: orderSvc.codeRepositoryUrl
        });

        const paymentSvc = new ApplicationService(this, "PaymentService", {
            internalApiDomain: props.internalNLBApiDomain,
            eksClusterName: props.eksClusterName,
            codebuildKubectlRole: role,
            name: "PaymentService",
            ecrImageName: "payment-svc",
            serviceUrlPrefix: "payments",
            assetDirectory: path.join(__dirname, "..", "services", "application-services", "payment-service")
        });
        new CfnOutput(this, "PaymentServiceRepository", {
            value: paymentSvc.codeRepositoryUrl
        });
    }
}
