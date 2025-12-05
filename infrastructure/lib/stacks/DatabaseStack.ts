import { Stack, StackProps, Duration } from "aws-cdk-lib";
import { Construct } from "constructs";
import { DatabaseInstance, DatabaseInstanceEngine, PostgresEngineVersion, Credentials } from "aws-cdk-lib/aws-rds";
import { Vpc, SecurityGroup, SubnetType, Port } from "aws-cdk-lib/aws-ec2";

interface Props extends StackProps {
  vpc: Vpc;
  appSecurityGroup: SecurityGroup;
}

export class DatabaseStack extends Stack {
  constructor(scope: Construct, id: string, props: Props) {
    super(scope, id, props);
    const dbSg = new SecurityGroup(this, "DbSG", { vpc: props.vpc });
    dbSg.addIngressRule(props.appSecurityGroup, Port.tcp(5432));
    new DatabaseInstance(this, "RdsPostgres", {
      vpc: props.vpc,
      vpcSubnets: { subnetType: SubnetType.PRIVATE_ISOLATED },
      engine: DatabaseInstanceEngine.postgres({ version: PostgresEngineVersion.V16 }),
      securityGroups: [dbSg],
      credentials: Credentials.fromGeneratedSecret("aws"),
      allocatedStorage: 20,
      multiAz: false
    });
  }
}
