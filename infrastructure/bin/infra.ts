#!/usr/bin/env node
import * as cdk from "aws-cdk-lib";
import { NetworkStack } from "../lib/stacks/NetworkStack";
import { DatabaseStack } from "../lib/stacks/DatabaseStack";
import { EnvConfig } from "../lib/config/env-config";
import { SecretsStack } from "../lib/stacks/SecretsStack";

const app = new cdk.App();
const envName = app.node.tryGetContext("env") ?? "dev";
const cfg = EnvConfig.for(envName);
const network = new NetworkStack(app, `NetworkStack-${envName}`, { env: cfg.env });
new SecretsStack(app, `SecretsStack-${envName}`, { env: cfg.env });
new DatabaseStack(app, `DatabaseStack-${envName}`, { env: cfg.env, vpc: network.vpc, appSecurityGroup: network.appSecurityGroup });
