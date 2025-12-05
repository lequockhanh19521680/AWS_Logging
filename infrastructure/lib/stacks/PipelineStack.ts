import { Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import { Pipeline } from "aws-cdk-lib/aws-codepipeline";

export class PipelineStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);
    new Pipeline(this, "Pipeline", {});
  }
}

