# AWS

```bash
├── README.md                                     <-- This instructions file
├── Meetup                                        <-- Source for Meetup Lambda Functions
│   ├── pom.xml                                   <-- Java dependencies
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── demosam.meetup
│       │   │       ├── AttendeeHandler.java      <-- Lambda functions code
│       │   │       └── Attendee.java             <-- POJO for Attendee entity
│       │   └───resources                         <-- Sample requests
│       │       └── put-request.json
│       └── test
│           └── java
│               └── demosam.meetup
│                   └── AttendeeHandlerTest.java  <-- Integration test
└── template.yaml                                 <-- Infrastructure description
```

## Requirements

* AWS CLI already configured with Administrator permission
* [Java SE Development Kit 8 installed](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Docker installed](https://www.docker.com/community-edition)
* [Maven](https://maven.apache.org/install.html)

## Setup process

### Installing dependencies

```bash
sam build
```

You can also build on a Lambda like environment by using

```bash
sam build --use-container
```

### Local development

**Invoking function locally through local API Gateway**

```bash
sam local start-api
```

If the previous command ran successfully you should now be able to hit the following local endpoint to invoke your function `http://localhost:3000/hello`

**SAM CLI** is used to emulate both Lambda and API Gateway locally and uses our `template.yaml` to understand how to bootstrap this environment (runtime, where the source code is, etc.) - The following excerpt is what the CLI will read in order to initialize an API and its routes:

```yaml
...
Events:
    PutAttendee:
        Type: Api
        Properties:
            Path: /meetup
            Method: put
```

## Packaging and deployment

AWS Lambda Java runtime accepts either a zip file or a standalone JAR file - We use the latter in this example. SAM will use `CodeUri` property to know where to look up for both application and dependencies:

```yaml
...
    PutAttendeeFunction:
        Type: AWS::Serverless::Function
        Properties:
            CodeUri: Meetup
            Handler: demosam.meetup.AttendeeHandler::handleRequest
```

Firstly, we need a `S3 bucket` where we can upload our Lambda functions packaged as ZIP before we deploy anything - If you don't have a S3 bucket to store code artifacts then this is a good time to create one:

```bash
aws s3 mb s3://BUCKET_NAME
```

Next, run the following command to package our Lambda function to S3:

```bash
sam package \
    --output-template-file packaged.yaml \
    --s3-bucket REPLACE_THIS_WITH_YOUR_S3_BUCKET_NAME
```

Next, the following command will create a Cloudformation Stack and deploy your SAM resources.

```bash
sam deploy \
    --template-file packaged.yaml \
    --stack-name aws \
    --capabilities CAPABILITY_IAM
```

> **See [Serverless Application Model (SAM) HOWTO Guide](https://github.com/awslabs/serverless-application-model/blob/master/HOWTO.md) for more details in how to get started.**

After deployment is complete you can run the following command to retrieve the API Gateway Endpoint URL:

```bash
aws cloudformation describe-stacks \
    --stack-name aws \
    --query 'Stacks[].Outputs'
```

## Testing

We use `JUnit` for testing our code and you can simply run the following command to run our tests:

```bash
cd Meetup
mvn test
```

# Appendix

## AWS CLI commands

AWS CLI commands to package, deploy and describe outputs defined within the cloudformation stack:

```bash
sam package \
    --template-file template.yaml \
    --output-template-file packaged.yaml \
    --s3-bucket REPLACE_THIS_WITH_YOUR_S3_BUCKET_NAME

sam deploy \
    --template-file packaged.yaml \
    --stack-name aws \
    --capabilities CAPABILITY_IAM \
    --parameter-overrides MyParameterSample=MySampleValue

aws cloudformation describe-stacks \
    --stack-name aws --query 'Stacks[].Outputs'
```
