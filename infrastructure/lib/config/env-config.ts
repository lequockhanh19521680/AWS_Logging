import * as cdk from "aws-cdk-lib";

export class EnvConfig {
  static for(name: string) {
    if (name === "prod") return { env: { account: process.env.CDK_DEFAULT_ACCOUNT, region: "us-east-1" } as cdk.Environment };
    if (name === "staging") return { env: { account: process.env.CDK_DEFAULT_ACCOUNT, region: "us-east-1" } as cdk.Environment };
    return { env: { account: process.env.CDK_DEFAULT_ACCOUNT, region: "us-east-1" } as cdk.Environment };
  }
}

