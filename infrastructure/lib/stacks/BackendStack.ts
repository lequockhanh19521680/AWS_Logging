import { Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import { Cluster } from "aws-cdk-lib/aws-ecs";
export class BackendStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);
    new Cluster(this, "Cluster", {});
  }
}

