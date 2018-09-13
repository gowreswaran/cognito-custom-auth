# cognito-custom-auth
1. Import the Project as Maven Project<br/><br/>
2. Edit custom-auth/src/main/java/com/kd/auth/AuthenticateUser.java and update userPool, username, password, clientId, region variables.<br/><br/>
3. Create a Nodejs Lambda Function and replace the content with content from define-custom-auth-lambda-trigger.js<br/><br/>
4. Naviagte to Cognito User Pool and Select Triggers under General Settings.<br/><br/>
5. Select the above created Lambda Function as "Define Auth Challenge Lambda Function" and Click Save.<br/><br/>
6. Run custom-auth/src/main/java/com/kd/auth/AuthenticateUser.java <br/><br/>

# SDK Versions
aws-java-sdk.version - 1.11.104<br/><br/>
