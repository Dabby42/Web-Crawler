package com.gsd.kolorbi.service;

import com.gsd.kolorbi.model.Raffle;
import com.gsd.kolorbi.model.RafflePlayed;


public interface RaffleService {
    void drawRaffle(Raffle raffle) throws Exception;

    RafflePlayed playRaffle(String phone, String deviceId, String raffleId, String paymentReference, String paymentChannel) throws Exception;
}
