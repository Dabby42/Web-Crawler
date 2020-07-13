package com.gsd.kolorbi.service.impl;

import com.gsd.kolorbi.model.Gift;
import com.gsd.kolorbi.model.PaymentFeedback;
import com.gsd.kolorbi.model.RafflePlayed;
import com.gsd.kolorbi.service.FlutterWaveService;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FlutterWaveServiceImpl implements FlutterWaveService {

    @Value("${flutterwave.verify.endpoint}")
    private String VERIFY_ENDPOINT;

    @Value("${flutterwave.transfer.create.endpoint}")
    private String TRANSFER_CREATE_ENDPOINT;

    @Value("${flutterwave.bill.payment.endpoint}")
    private String BILL_PAYMENT_ENDPOINT;

    @Value("${flutterwave.secret.key}")
    private String SECRET_KEY;

    @Override
    public boolean verify(String txref, double amount) throws Exception {

        JSONObject payload = new JSONObject();
        payload.put("txref", txref);
        payload.put("SECKEY", SECRET_KEY);

        Request request = new Request.Builder()
                .url(VERIFY_ENDPOINT)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), payload.toString()))
                .build();

        Response response = new OkHttpClient().newCall(request).execute();
        JSONObject result = new JSONObject(response.body().string());

        // check of no object is returned
        if (result == null) {
            return false;
        }

        // This get status from returned payload
        String status = result.optString("status", null);

        // this ensures that status is not null
        if (status == null) {
            return false;
        }

        // This confirms the transaction exist on rave
        if (!"success".equalsIgnoreCase(status)) {
            return false;
        }

        JSONObject data = result.getJSONObject("data");

        // This get the amount stored on server
        double actualAmount = data.getDouble("amount");

        // This validates that the amount stored on client is same returned
        if (actualAmount != amount) {
            return false;
        }

        return true;
    }

    @Override
    public PaymentFeedback payToBank(String recipientBankAccountNumber, String bankCode, RafflePlayed rafflePlayed) throws Exception {

        PaymentFeedback paymentFeedback = new PaymentFeedback();
        paymentFeedback.setPaymentChannel("FLUTTERWAVE");
        paymentFeedback.setDate(new Date());

        JSONObject payload = new JSONObject();
        payload.put("account_bank", bankCode);
        payload.put("account_number", recipientBankAccountNumber);
        payload.put("amount", rafflePlayed.getWinAmount());
        payload.put("seckey", SECRET_KEY);
        payload.put("narration", "Winning payout for Kolorbi Raffle with Ticket Number "+rafflePlayed.getTicketNumber());
        payload.put("currency", "NGN");
        payload.put("reference", rafflePlayed.getTicketNumber());

        Request request = new Request.Builder()
                .url(TRANSFER_CREATE_ENDPOINT)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), payload.toString()))
                .build();

        Response response = new OkHttpClient().newCall(request).execute();
        JSONObject result = new JSONObject(response.body().string());

        // check of no object is returned
        if (result == null) {
            paymentFeedback.setPaymentReference(rafflePlayed.getTicketNumber());
            paymentFeedback.setStatus(false);
            paymentFeedback.setRequestLog(payload.toString());
            return paymentFeedback;
        }

        // This get status from returned payload
        String status = result.optString("status", null);
        JSONObject data = result.optJSONObject("data");

        // This confirms the transaction exist on rave
        if (status != null && "success".equalsIgnoreCase(status) && data != null) {
            String dataStatus = data.optString("Status", null);

            if(dataStatus != null && "success".equalsIgnoreCase(dataStatus)){
                paymentFeedback.setPaymentReference(rafflePlayed.getTicketNumber());
                paymentFeedback.setMessage("Transfer created successfully");
                paymentFeedback.setStatus(true);
                paymentFeedback.setRequestLog(payload.toString());
                paymentFeedback.setRequestLog(result.toString());
                return paymentFeedback;
            }else{
                paymentFeedback.setPaymentReference(rafflePlayed.getTicketNumber());
                paymentFeedback.setMessage(data.optString("Message", "Transfer failed"));
                paymentFeedback.setStatus(false);
                paymentFeedback.setRequestLog(payload.toString());
                paymentFeedback.setRequestLog(result.toString());
                return paymentFeedback;
            }

        }else{
            paymentFeedback.setPaymentReference(rafflePlayed.getTicketNumber());
            paymentFeedback.setMessage("Transfer failed");
            paymentFeedback.setStatus(false);
            paymentFeedback.setRequestLog(payload.toString());
            paymentFeedback.setRequestLog(result.toString());
            return paymentFeedback;
        }
    }

    @Override
    public PaymentFeedback payToPhone(Gift gift, String recipientPhoneNumber, String giftRewardType) throws Exception {

        PaymentFeedback paymentFeedback = new PaymentFeedback();
        paymentFeedback.setPaymentChannel("FLUTTERWAVE");
        paymentFeedback.setDate(new Date());


        JSONObject servicePayload = new JSONObject();
        servicePayload.put("Country", "NG");
        servicePayload.put("CustomerId", recipientPhoneNumber);
        servicePayload.put("Reference", gift.getId());
        servicePayload.put("Amount", gift.getWinAmount());
        servicePayload.put("RecurringType", 0);
        servicePayload.put("IsAirtime", true);
        servicePayload.put("BillerName", "AIRTIME");

        JSONObject payload = new JSONObject();
        payload.put("secret_key", SECRET_KEY);
        payload.put("service", "fly_buy");
        payload.put("service_method", "post");
        payload.put("service_version", "v1");
        payload.put("service_channel", "rave");
        payload.put("service_payload", servicePayload);

        Request request = new Request.Builder()
                .url(BILL_PAYMENT_ENDPOINT)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), payload.toString()))
                .build();

        Response response = new OkHttpClient().newCall(request).execute();
        JSONObject result = new JSONObject(response.body().string());

        // check of no object is returned
        if (result == null) {
            paymentFeedback.setPaymentReference(gift.getId());
            paymentFeedback.setStatus(false);
            paymentFeedback.setRequestLog(payload.toString());
            return paymentFeedback;
        }

        // This get status from returned payload
        String status = result.optString("status", null);
        JSONObject data = result.optJSONObject("data");

        // This confirms the transaction exist on rave
        if (status != null && "success".equalsIgnoreCase(status) && data != null) {
            String dataStatus = data.optString("Status", null);

            if(dataStatus != null && "success".equalsIgnoreCase(dataStatus)){
                paymentFeedback.setPaymentReference(gift.getId());
                paymentFeedback.setMessage("Airtime transfer was successfully");
                paymentFeedback.setStatus(true);
                paymentFeedback.setRequestLog(payload.toString());
                paymentFeedback.setRequestLog(result.toString());
                return paymentFeedback;
            }else{
                paymentFeedback.setPaymentReference(gift.getId());
                paymentFeedback.setMessage(data.optString("Message", "Airtime transfer failed"));
                paymentFeedback.setStatus(false);
                paymentFeedback.setRequestLog(payload.toString());
                paymentFeedback.setRequestLog(result.toString());
                return paymentFeedback;
            }

        }else{
            paymentFeedback.setPaymentReference("FLUTTERWAVE");
            paymentFeedback.setMessage("Airtime transfer failed");
            paymentFeedback.setStatus(false);
            paymentFeedback.setRequestLog(payload.toString());
            paymentFeedback.setRequestLog(result.toString());
            return paymentFeedback;
        }
    }
}
