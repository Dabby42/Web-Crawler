package com.gsd.kolorbi.service;

public interface ClaimService {

    void processRaffleClaim(String winCode, String recipientBankAccountNumber, String bankCode) throws Exception;

    void processGiftClaim(String winCode, String recipientPhoneNumber, String giftRewardType) throws Exception;

}
