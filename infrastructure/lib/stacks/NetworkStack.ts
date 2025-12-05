import { Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import { Vpc, SubnetType, SecurityGroup, Peer, Port } from "aws-cdk-lib/aws-ec2";

export class NetworkStack extends Stack {
  readonly vpc: Vpc;
  readonly appSecurityGroup: SecurityGroup;

  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);
    this.vpc = new Vpc(this, "Vpc", {
      maxAzs: 2,
      subnetConfiguration: [
        { name: "public", subnetType: SubnetType.PUBLIC },
        { name: "private", subnetType: SubnetType.PRIVATE_WITH_EGRESS },
        { name: "isolated", subnetType: SubnetType.PRIVATE_ISOLATED }
      ]
    });
    this.appSecurityGroup = new SecurityGroup(this, "AppSG", { vpc: this.vpc, allowAllOutbound: true });
    this.appSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(8080));
  }
}

