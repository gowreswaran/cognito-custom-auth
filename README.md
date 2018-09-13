# cognito-custom-auth
Import the Project as Maven Project
Edit custom-auth/src/main/java/com/kd/auth/AuthenticateUser.java and update userPool, username, password, clientId, region variables.
Create a Nodejs Lambda Function and replace the content with content from define-custom-auth-lambda-trigger.js
Naviagte to Cognito User Pool and Select Triggers under General Settings.
Select the above created Lambda Function as "Define Auth Challenge Lambda Function" and Click Save.
Run custom-auth/src/main/java/com/kd/auth/AuthenticateUser.java 

# SDK Versions
aws-java-sdk.version - 1.11.104
