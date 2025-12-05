import { Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import { StringParameter } from "aws-cdk-lib/aws-ssm";

export class SecretsStack extends Stack {
  readonly jwtSecretParameter: StringParameter;
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);
    this.jwtSecretParameter = new StringParameter(this, "JwtSecretParam", {
      parameterName: "/aws-learning/jwt/secret",
      stringValue: "change-me",
    });
  }
}

