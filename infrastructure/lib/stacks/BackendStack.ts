import { Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import { Cluster } from "aws-cdk-lib/aws-ecs";
import {
  ApplicationLoadBalancer,
  ApplicationProtocol,
  ApplicationTargetGroup,
  ListenerAction,
} from "aws-cdk-lib/aws-elasticloadbalancingv2";
import { Vpc } from "aws-cdk-lib/aws-ec2";
export class BackendStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);
    const vpc = new Vpc(this, "Vpc", { maxAzs: 2 });
    const cluster = new Cluster(this, "Cluster", { vpc });
    const alb = new ApplicationLoadBalancer(this, "Alb", {
      vpc,
      internetFacing: true,
    });
    const listener = alb.addListener("Http", {
      port: 80,
      protocol: ApplicationProtocol.HTTP,
    });
    const gatewayTg = new ApplicationTargetGroup(this, "GatewayTG", {
      vpc,
      port: 8081,
    });
    const contentTg = new ApplicationTargetGroup(this, "ContentTG", {
      vpc,
      port: 8082,
    });
    const authTg = new ApplicationTargetGroup(this, "AuthTG", {
      vpc,
      port: 8083,
    });
    listener.addAction("Default", {
      action: ListenerAction.forward([gatewayTg]),
    });
    listener.addTargets("ContentRule", {
      priority: 10,
      conditions: [{ pathPatterns: ["/api/questions*", "/api/steps*"] } as any],
      targetGroups: [contentTg],
    });
    listener.addTargets("AuthRule", {
      priority: 20,
      conditions: [{ pathPatterns: ["/api/auth*", "/oauth2*"] } as any],
      targetGroups: [authTg],
    });
  }
}
