# CodeStar Human Notifications

Listens to CodeStar Notifications and republish them in a human-friendly format

```shell
serverless deploy \
  --region eu-east-1 \
  --account $(aws sts get-caller-identity | jq -r .Account) \
  --bucket my-amazing-s3-bucket \
  --codestarSnsTopicArn arn:aws:sns:us-west-2:$(aws sts get-caller-identity | jq -r .Account):codecommit-notifications
  --stage sandbox
```

```
serverless deploy \
          --region us-west-2 \
          --account 006467937747 \
          --bucket dwolla-code-sandbox \
          --stage sandbox \
          --codestarSnsTopicArn arn:aws:sns:us-west-2:799546647898:terraform-multi-account-codecommit-notifications
```