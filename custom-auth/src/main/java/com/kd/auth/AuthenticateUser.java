package com.kd.auth;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeResult;

public class AuthenticateUser {

	public static void main(String[] args) {
		String userPool = "eVJEnGoHO";
		String username = "kd-test";
		String password = "admin@123";
		String clientId = "12t6r2hk058q13u4eeaci148dp";
		String region = "us-east-1";

		AWSCognitoIdentityProvider awsCognitoIdentityProvider = AWSCognitoIdentityProviderClient.builder()
				.withRegion(region).build();

		try {
			AuthenticationHelper helper = new AuthenticationHelper(userPool);
			System.out.println("===== Making an Initiate Auth with Username and SRP_A Auth Parameters====");
			InitiateAuthResult initiateAuthResult = awsCognitoIdentityProvider
					.initiateAuth(new InitiateAuthRequest().withClientId(clientId)
							.withAuthFlow(AuthFlowType.CUSTOM_AUTH).addAuthParametersEntry("USERNAME", username)
							.addAuthParametersEntry("SRP_A", helper.getA().toString(16)));
			System.out.println("===== Done Initiate Auth - Below is the response from Initiate Auth====");
			System.out.println("<== " + initiateAuthResult + "\n");

			System.out.println("====== Responding to PASSWORD VERIFIER CHALLENGE=====");
			String srpB = initiateAuthResult.getChallengeParameters().get("SRP_B");
			BigInteger serverBValue = new BigInteger(srpB, 16);
			String saltSrt = initiateAuthResult.getChallengeParameters().get("SALT");
			String secretBlock = initiateAuthResult.getChallengeParameters().get("SECRET_BLOCK");

			BigInteger salt = new BigInteger(saltSrt, 16);
			byte[] hkdf = helper.getPasswordAuthenticationKey(username, password, serverBValue, salt);
			String timestamp = getTimestamp();
			Mac mac = startHMac(hkdf);
			mac.update(userPool.getBytes(StandardCharsets.UTF_8));
			mac.update(username.getBytes(StandardCharsets.UTF_8));
			mac.update(Base64.decodeBase64(secretBlock));
			byte[] hmac = mac.doFinal(timestamp.getBytes(StandardCharsets.UTF_8));
			String signature = Base64.encodeBase64String(hmac);

			RespondToAuthChallengeResult respondToAuthChallengeResult = awsCognitoIdentityProvider
					.respondToAuthChallenge(new RespondToAuthChallengeRequest().withClientId(clientId)
							.withChallengeName(initiateAuthResult.getChallengeName())
							.addChallengeResponsesEntry("USERNAME", username)
							.addChallengeResponsesEntry("PASSWORD_CLAIM_SECRET_BLOCK", secretBlock)
							.addChallengeResponsesEntry("TIMESTAMP", timestamp)
							.addChallengeResponsesEntry("PASSWORD_CLAIM_SIGNATURE", signature));
			System.out.println(
					"===== Done Responding to PASSWORD_VERIFIER Challenge - Below is the response from Respond to Auth Challenge====");
			System.out.println("<== " + respondToAuthChallengeResult + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getTimestamp() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US);
		simpleDateFormat.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
		return simpleDateFormat.format(new Date());
	}

	static Mac startHMac(byte[] key) throws InvalidKeyException, NoSuchAlgorithmException {
		final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
		Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
		SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA256_ALGORITHM);
		mac.init(signingKey);
		return mac;
	}
}
