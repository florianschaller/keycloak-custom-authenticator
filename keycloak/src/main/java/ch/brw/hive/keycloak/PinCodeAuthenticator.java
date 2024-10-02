package ch.brw.hive.keycloak;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.messages.Messages;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class PinCodeAuthenticator implements Authenticator {

    private static final String PIN_CODE = "pin";
    private static final String ATTEMPTS = "pin_attempts";
    private static final String BLOCKED_UNTIL = "blocked_until";
    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION = TimeUnit.MINUTES.toMillis(5);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        UserModel user = context.getUser();

        if (isBlocked(authSession)) {
            context.failureChallenge(AuthenticationFlowError.USER_TEMPORARILY_DISABLED,
                    context.form().setError(Messages.ACCOUNT_DISABLED).createForm("login-pincode.ftl"));
            return;
        }

        String pin = context.getHttpRequest().getDecodedFormParameters().getFirst(PIN_CODE);
        if (pin == null) {
            context.challenge(context.form().createForm("login-pincode.ftl"));
            return;
        }

        if (isValidPin(user, pin)) {
            resetAttempts(authSession);
            context.success();
        } else {
            incrementAttempts(authSession);
            if (getAttempts(authSession) >= MAX_ATTEMPTS) {
                blockUser(authSession);
                context.failureChallenge(AuthenticationFlowError.USER_TEMPORARILY_DISABLED,
                        context.form().setError(Messages.ACCOUNT_DISABLED).createForm("login-pincode.ftl"));
            } else {
                context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS,
                        context.form().setError(Messages.INVALID_USER).createForm("login-pincode.ftl"));
            }
        }
    }

    private boolean isValidPin(UserModel user, String pin) {
        // Implement your PIN validation logic here
        return "1234".equals(pin); // Example: hardcoded PIN for demonstration
    }

    private boolean isBlocked(AuthenticationSessionModel authSession) {
        String blockedUntilStr = authSession.getAuthNote(BLOCKED_UNTIL);
        if (blockedUntilStr == null) {
            return false;
        }
        long blockedUntil = Long.parseLong(blockedUntilStr);
        return Instant.now().toEpochMilli() < blockedUntil;
    }

    private void blockUser(AuthenticationSessionModel authSession) {
        authSession.setAuthNote(BLOCKED_UNTIL, String.valueOf(Instant.now().toEpochMilli() + BLOCK_DURATION));
    }

    private void incrementAttempts(AuthenticationSessionModel authSession) {
        int attempts = getAttempts(authSession);
        authSession.setAuthNote(ATTEMPTS, String.valueOf(attempts + 1));
    }

    private void resetAttempts(AuthenticationSessionModel authSession) {
        authSession.removeAuthNote(ATTEMPTS);
        authSession.removeAuthNote(BLOCKED_UNTIL);
    }

    private int getAttempts(AuthenticationSessionModel authSession) {
        String attempts = authSession.getAuthNote(ATTEMPTS);
        return attempts == null ? 0 : Integer.parseInt(attempts);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        authenticate(context);
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }
}