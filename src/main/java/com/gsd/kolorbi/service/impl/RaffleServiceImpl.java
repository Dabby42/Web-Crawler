package com.gsd.kolorbi.service.impl;

import com.gsd.kolorbi.model.Raffle;
import com.gsd.kolorbi.model.RafflePlayed;
import com.gsd.kolorbi.repository.RafflePlayedRepository;
import com.gsd.kolorbi.repository.RaffleRepository;
import com.gsd.kolorbi.service.CounterGenerator;
import com.gsd.kolorbi.service.RaffleService;
import com.gsd.kolorbi.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class RaffleServiceImpl implements RaffleService {

    @Autowired
    private RafflePlayedRepository rafflePlayedRepository;

    @Autowired
    private SMSService smsService;

    @Autowired
    private RaffleRepository raffleRepository;

    @Autowired
    private CounterGenerator counterGenerator;

    @Qualifier("threadPoolTaskExecutor")
    @Autowired
    private TaskExecutor taskExecutor;

    @Value("${kolorbi.raffle.size}")
    private int raffleSize;

    @Override
    public void drawRaffle(@NotNull Raffle raffle){
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                    DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                    symbols.setCurrencySymbol(""); // Don't use null.
                    formatter.setDecimalFormatSymbols(symbols);

                    //get the entire ticketNumber played
                    Date date = new Date();
                    RafflePlayed winner = null;
                    List<RafflePlayed> rafflePlayeds = rafflePlayedRepository.findActiveRafflePlayedRaffleId(raffle.getId());
                    if (rafflePlayeds.size() > 1) {
                        //get a lucky winner
                        Collections.shuffle(rafflePlayeds);
                        winner = rafflePlayeds.get(ThreadLocalRandom.current().nextInt(rafflePlayeds.size()));

                        for (RafflePlayed rafflePlayed : rafflePlayeds) {
                            rafflePlayed.setActive(false);
                            rafflePlayed.setCloseDate(date);
                        }

                        winner.setWinCode(date.getTime() + "");
                        winner.setWinClaimed(false);
                        winner.setWinAmount(raffle.getPrizeInNaira() * (Math.floor(rafflePlayeds.size() * 0.9)));
                    }

                    rafflePlayedRepository.saveAll(rafflePlayeds);

                    //notify the winner
                    String message = "Congratulations! your raffle ticket "+winner.getTicketNumber()+" have won NGN" + formatter.format(winner.getWinAmount()) + " through Kolorbi " + raffle.getPrizeInNaira() + " Naira raffle.\n" +
                            "Your winning claim code is " + winner.getWinCode() + ", visit kolorbi.com/raffle to claim your money.";

                    smsService.sendSMS(winner.getPhone(), message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public RafflePlayed playRaffle(@NotNull String phone, @NotNull String deviceId, @NotNull String raffleId,
                                   @NotNull String paymentReference, @NotNull String paymentChannel) throws Exception {
        Raffle raffle = raffleRepository.findById(raffleId).orElse(null);
        if(raffle == null){
            raffle = new Raffle();
            raffle.setName("RAFFLE_"+raffleId.trim());
            raffle.setPrizeInNaira(Double.valueOf(raffleId));
            raffle.setId(raffleId);

            raffle = raffleRepository.save(raffle);
        }

        long count = counterGenerator.getNextCounter(raffle.getName());
        String ticketNumber = "TKT"+raffle.getName()+count;
        RafflePlayed rafflePlayed = new RafflePlayed();
        rafflePlayed.setDeviceId(deviceId);
        rafflePlayed.setRaffleId(raffle.getId());
        rafflePlayed.setActive(true);
        rafflePlayed.setCreateDate(new Date());
        rafflePlayed.setPhone(phone);
        rafflePlayed.setPaymentChannel(paymentChannel);
        rafflePlayed.setPaymentReference(paymentReference);
        rafflePlayed.setTicketNumber(ticketNumber);
        rafflePlayedRepository.save(rafflePlayed);

        //notify the player
        String message = "NGN "+rafflePlayed.getRaffleId()+" raffle played successfully, your Raffle ticket number is "+rafflePlayed.getTicketNumber();

        smsService.sendSMS(rafflePlayed.getPhone(), message);

        if((count % raffleSize) == 0){
            drawRaffle(raffle);
        }

        return rafflePlayed;
    }
}
