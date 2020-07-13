package com.gsd.kolorbi.service.impl;

import com.gsd.kolorbi.model.Claim;
import com.gsd.kolorbi.model.Gift;
import com.gsd.kolorbi.model.PaymentFeedback;
import com.gsd.kolorbi.model.RafflePlayed;
import com.gsd.kolorbi.repository.ClaimRepository;
import com.gsd.kolorbi.repository.GiftRepository;
import com.gsd.kolorbi.repository.RafflePlayedRepository;
import com.gsd.kolorbi.service.ClaimService;
import com.gsd.kolorbi.service.FlutterWaveService;
import com.gsd.kolorbi.util.KolorbiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClaimServiceImpl implements ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private RafflePlayedRepository rafflePlayedRepository;

    @Autowired
    FlutterWaveService flutterWaveService;

    @Override
    public void processRaffleClaim(String winCode, String recipientBankAccountNumber, String bankCode) throws Exception {
        RafflePlayed rafflePlayed = rafflePlayedRepository.findRafflePlayedByWinCode(winCode);
        if(rafflePlayed == null){
            throw new KolorbiException("Invalid winning code! Check your winning code");
        }else if(rafflePlayed.isWinClaimed()){
            throw new KolorbiException("Win has been claimed");
        }

        //flutterwave send money to recipient account
        PaymentFeedback paymentFeedback = flutterWaveService.payToBank(recipientBankAccountNumber, bankCode, rafflePlayed);
        if(!paymentFeedback.isStatus()){
            throw new KolorbiException(paymentFeedback.getMessage());
        }

        rafflePlayed.setWinClaimed(true);
        rafflePlayedRepository.save(rafflePlayed);

        Claim claim = new Claim();
        claim.setAmount(rafflePlayed.getWinAmount());
        claim.setTimestamp(new Date());
        claim.setReferenceId(rafflePlayed.getId());
        claim.setType(RafflePlayed.class.getName());
        claim.setPaymentFeedback(paymentFeedback);
        claim.setRecipientAccount(recipientBankAccountNumber);

        claimRepository.save(claim);

    }

    @Override
    public void processGiftClaim(String winCode, String recipientPhoneNumber, String giftRewardType) throws Exception {
        Gift gift = giftRepository.findGiftByWinCode(winCode);
        if(gift == null){
            throw new KolorbiException("Invalid winning code! Check your winning code");
        }else if(gift.isWinClaimed()){
            throw new KolorbiException("Win has been claimed");
        }

        //flutterwave send money to recipient account
        PaymentFeedback paymentFeedback = flutterWaveService.payToPhone(gift, recipientPhoneNumber, giftRewardType);
        if(!paymentFeedback.isStatus()){
            throw new KolorbiException(paymentFeedback.getMessage());
        }

        gift.setWinClaimed(true);
        giftRepository.save(gift);

        Claim claim = new Claim();
        claim.setAmount(gift.getWinAmount());
        claim.setTimestamp(new Date());
        claim.setReferenceId(gift.getId());
        claim.setType(Gift.class.getName());
        claim.setPaymentFeedback(paymentFeedback);
        claim.setRecipientAccount(recipientPhoneNumber);

        claimRepository.save(claim);
    }
}
