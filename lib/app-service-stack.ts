import { CfnOutput, Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import * as iam from "aws-cdk-lib/aws-iam";
import * as path from "path";
import { ApplicationService } from "./constructs/application-service";

export interface AppServiceStackProps extends StackProps {
    readonly internalNLBApiDomain: string
    readonly eksClusterName: string
    readonly eksClusterOIDCProviderArn: string
    readonly codebuildKubectlRoleArn: string
    readonly appSiteDistributionId: string
    readonly appSiteCloudFrontDomain: string
    readonly sharedServiceAccountName: string
    readonly appHostedZoneId?: string
    readonly customDomain?: string

    readonly serviceName: string
    readonly ecrImageName: string
    readonly serviceUrlPrefix: string
    readonly serviceSourceDir: string
}

export class AppServiceStack extends Stack {
    constructor(scope: Construct, id: string, props: AppServiceStackProps) {
        super(scope, id, props);

        const role = iam.Role.fromRoleArn(this, "CodebuildKubectlRole", props.codebuildKubectlRoleArn);

        const appSvc = new ApplicationService(this,  props.serviceName, {
            internalApiDomain: props.internalNLBApiDomain,
            eksClusterName: props.eksClusterName,
            codebuildKubectlRole: role,
            name: props.serviceName,
            ecrImageName: props.ecrImageName,
            serviceUrlPrefix: props.serviceUrlPrefix,
            assetDirectory: path.join(__dirname, "..", "services", "application-services", props.serviceSourceDir)
        });
        new CfnOutput(this, props.serviceName + "Repository", {
            value: appSvc.codeRepositoryUrl
        });
    }
}
