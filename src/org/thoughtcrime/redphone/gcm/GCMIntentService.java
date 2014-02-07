package org.thoughtcrime.redphone.gcm;

import android.content.Intent;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import org.thoughtcrime.redphone.ApplicationContext;
import org.thoughtcrime.redphone.Constants;
import org.thoughtcrime.redphone.RedPhoneService;
import org.thoughtcrime.redphone.crypto.EncryptedSignalMessage;
import org.thoughtcrime.redphone.crypto.InvalidEncryptedSignalException;
import org.thoughtcrime.redphone.signaling.SessionDescriptor;
import org.thoughtcrime.redphone.signaling.signals.CompressedInitiateSignalProtocol.CompressedInitiateSignal;
import org.thoughtcrime.redphone.sms.IncomingCallDetails;

// Raghava
import android.app.IntentService;
import android.support.v4.app.NotificationCompat;


public class GCMIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	NotificationCompat.Builder builder;

	public GCMIntentService() {
		super(GCMRegistrationService.GCM_SENDER_ID);
	}

	/*@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.w("GCMIntentService", "GCM Registered!");
		GCMRegistrarHelper.setRegistrationIdOnServer(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.w("GCMIntentService", "GCM Unregistered!");
		GCMRegistrarHelper.unsetRegistrationIdOnServer(context, registrationId);
	}

	@Override
	protected void onError(Context context, String error) {
		Log.w("GCMIntentService", "GCM Registration failed with hard error: "
				+ error);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		String data = intent.getStringExtra("signal");
		IncomingCallDetails callDetails = getIncomingCallDetails(context, data);

		if (callDetails != null) {
			intent.setClass(context, RedPhoneService.class);
			intent.setAction(RedPhoneService.ACTION_INCOMING_CALL);
			intent.putExtra(Constants.REMOTE_NUMBER, callDetails.getInitiator());
			intent.putExtra(
					Constants.SESSION,
					new SessionDescriptor(callDetails.getHost(), callDetails
							.getPort(), callDetails.getSessionId(), callDetails
							.getVersion()));
			context.startService(intent);
		}
	}*/

	private IncomingCallDetails getIncomingCallDetails(String signalString) {
		try {
			Log.w("GCMIntentService", "Got GCM Signal: " + signalString);
			EncryptedSignalMessage encryptedSignalMessage = new EncryptedSignalMessage(
					ApplicationContext.getInstance().getContext(), signalString);
			CompressedInitiateSignal signal = CompressedInitiateSignal
					.parseFrom(encryptedSignalMessage.getPlaintext());

			return new IncomingCallDetails(signal.getInitiator(),
					signal.getPort(), signal.getSessionId(),
					signal.getServerName(), signal.getVersion());
		} catch (InvalidEncryptedSignalException e) {
			Log.w("GCMIntentService", e);
			return null;
		} catch (InvalidProtocolBufferException e) {
			Log.w("GCMIntentService", e);
			return null;
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String data = intent.getStringExtra("signal");
		IncomingCallDetails callDetails = getIncomingCallDetails(data);

		if (callDetails != null) {
			intent.setClass(ApplicationContext.getInstance().getContext(), RedPhoneService.class);
			intent.setAction(RedPhoneService.ACTION_INCOMING_CALL);
			intent.putExtra(Constants.REMOTE_NUMBER, callDetails.getInitiator());
			intent.putExtra(
					Constants.SESSION,
					new SessionDescriptor(callDetails.getHost(), callDetails
							.getPort(), callDetails.getSessionId(), callDetails
							.getVersion()));
			ApplicationContext.getInstance().getContext().startService(intent);
		}

	}
}
