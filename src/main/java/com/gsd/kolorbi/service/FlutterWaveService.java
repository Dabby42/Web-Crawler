package com.gsd.kolorbi.service;

import com.gsd.kolorbi.model.Gift;
import com.gsd.kolorbi.model.PaymentFeedback;
import com.gsd.kolorbi.model.RafflePlayed;

public interface FlutterWaveService {

    boolean verify(String txref, double amount) throws Exception;

    PaymentFeedback payToBank(String recipientBankAccountNumber, String bankCode, RafflePlayed rafflePlayed) throws Exception;

    PaymentFeedback payToPhone(Gift gift, String recipientPhoneNumber, String giftRewardType) throws Exception;
}
