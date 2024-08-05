# Product Service

## Description

This service handles Inventory Management for the Aventine Platform. This service manages:

- Categories
- Vendors
- Products
- Units of Measure (UOM)
- Volume Pricing

### Categories

Categories are Departments that a Product can belong to. These follow a tree structure that can currently supports up to
3 levels deep
```
| main category
|--sub category
|----group
```

### Vendors

Vendors distribute the product. A product can come from multiple vendors

### Products

The main model that holds relevant info about the products that Aventine tenants sell. 

### Units of Measure (UOM)

Units of Measure are the packaging that a product comes in. For example, EACH, CASE, and PACKAGE. A UOM has a 1-to-1 mapping
to a barcode and alternate ID. An Alternate ID is a separate SKU that came from a previous Vendor.

### Volume Pricing

Volume Pricing are different discounts set for a Product that's based on the volume of a UOM.

## Architecture

![Product Service Architecture](./diagrams/ProductService.svg)

## Local Development

### Prerequisites

- [aws cli](https://aws.amazon.com/cli/) installed and configured using your AWS credentials
  - Note: This service uses in-house dependencies that come from AWS Artifacts so see that page in the AWS dashboard
for configuring your terminal profile. Example:
```bash
 export CODEARTIFACT_AUTH_TOKEN=`aws codeartifact get-authorization-token --domain stallionstech --domain-owner {domainOwner} --region {region} --query authorizationToken --output text`  
 ```

### Build

If you don't have IntelliJ installed...

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```






