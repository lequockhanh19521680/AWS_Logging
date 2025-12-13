terraform {
  required_version = ">= 1.6.0"
}
provider "aws" {
  region = var.region
}
module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  name   = var.project
}
module "ecs" {
  source = "terraform-aws-modules/ecs/aws"
  cluster_name = "${var.project}-cluster"
}
module "rds" {
  source = "terraform-aws-modules/rds/aws"
  identifier = "${var.project}-pg"
}
module "redis" {
  source = "terraform-aws-modules/elasticache/aws"
  name   = "${var.project}-redis"
}
