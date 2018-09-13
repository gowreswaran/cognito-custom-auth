# cognito-custom-auth
Import the Project as Maven Project<br/>
Edit custom-auth/src/main/java/com/kd/auth/AuthenticateUser.java and update userPool, username, password, clientId, region variables.<br/>
Create a Nodejs Lambda Function and replace the content with content from define-custom-auth-lambda-trigger.js<br/>
Naviagte to Cognito User Pool and Select Triggers under General Settings.<br/>
Select the above created Lambda Function as "Define Auth Challenge Lambda Function" and Click Save.<br/>
Run custom-auth/src/main/java/com/kd/auth/AuthenticateUser.java <br/>

# SDK Versions
aws-java-sdk.version - 1.11.104<br/>
