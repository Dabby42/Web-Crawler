package com.gsd.kolorbi.controller;


import com.gsd.kolorbi.model.*;
import com.gsd.kolorbi.repository.GiftRepository;
import com.gsd.kolorbi.repository.NewsRepository;
import com.gsd.kolorbi.repository.RafflePlayedRepository;
import com.gsd.kolorbi.repository.RaffleRepository;
import com.gsd.kolorbi.service.*;
import com.gsd.kolorbi.util.KolorbiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping(value = "/endpoint-api")
public class KolorbiAPI {

    @Autowired
    private NewsService newsService;

    @Autowired
    private RaffleService raffleService;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private RafflePlayedRepository rafflePlayedRepository;

    @Autowired
    private RaffleRepository raffleRepository;

    @Autowired
    private FlutterWaveService flutterWaveService;

    @Autowired
    private SMSService smsService;

    @Autowired
    private CounterGenerator counterGenerator;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    GiftRepository giftRepository;

    @Value("${kolorbi.gift.count}")
    private int giftCount;

    @Value("${kolorbi.raffle.size}")
    private int raffleSize;

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/get-news-by-id")
    public @ResponseBody
    APIResponse getNewsById(HttpServletRequest request,
                        @RequestParam(name = "id", required = true) String id) throws Exception {

        APIResponse response = new APIResponse();
        News news = newsRepository.findById(id).get();
        if(news != null){
            List<News> relatedNews = newsService.getRelatedNews(news.getCategory(), 1, 5);
            Map<String, Object> map = new HashMap<>();
            map.put("news", news);
            map.put("related", relatedNews);

            response.setMessage(APIResponse.SUCCESS_MESSAGE);
            response.setStatus(APIResponse.SUCCESS_STATUS);
            response.setData(map);
        }else{
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setData("No such news found!");
        }

        return response;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/get-news")
    public @ResponseBody
    APIResponse getNews(HttpServletRequest request,
                        @RequestParam(name = "deviceId", required = true) String deviceId,
                        @RequestParam(name = "page", required = true) int page,
                        @RequestParam(name = "size", required = true) int size) throws Exception {

        List<News> news = newsService.getNewsForDeviceId(deviceId, page, size);
        APIResponse response = new APIResponse();
        response.setMessage(APIResponse.SUCCESS_MESSAGE);
        response.setStatus(APIResponse.SUCCESS_STATUS);
        response.setData(news);

        return response;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/get-related-news")
    public @ResponseBody
    APIResponse getRelatedNews(HttpServletRequest request,
                        @RequestParam(name = "category", required = true) String category,
                        @RequestParam(name = "page", required = true) int page,
                        @RequestParam(name = "size", required = true) int size) throws Exception {

        List<News> news = newsService.getRelatedNews(category, page, size);
        APIResponse response = new APIResponse();
        response.setMessage(APIResponse.SUCCESS_MESSAGE);
        response.setStatus(APIResponse.SUCCESS_STATUS);
        response.setData(news);

        return response;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/no-cache/play-raffle")
    public @ResponseBody
    APIResponse playRaffle(HttpServletRequest request,
                           @RequestParam(name = "phone", required = true) String phone,
                        @RequestParam(name = "deviceId", required = true) String deviceId,
                        @RequestParam(name = "raffleId", required = true) String raffleId,
                        @RequestParam(name = "paymentReference", required = true) String paymentReference,
                        @RequestParam(name = "paymentChannel", required = true) String paymentChannel) throws Exception {
        APIResponse response = new APIResponse();
        if(flutterWaveService.verify(paymentReference, Double.parseDouble(raffleId))) {
            RafflePlayed rafflePlayed = raffleService.playRaffle(phone, deviceId, raffleId, paymentReference, paymentChannel);
            response.setMessage(APIResponse.SUCCESS_MESSAGE);
            response.setStatus(APIResponse.SUCCESS_STATUS);
            response.setData(rafflePlayed);
        }else{
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setData("Unable to verify payment! Try again later");
        }
        return response;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/no-cache/send-raffle-otp")
    public @ResponseBody
    APIResponse sendRaffleOTP(HttpServletRequest request,
                            @RequestParam(name = "deviceId", required = true) String deviceId,
                            @RequestParam(name = "winCode", required = true) String winCode) throws Exception {
        APIResponse response = new APIResponse();

        RafflePlayed rafflePlayed = rafflePlayedRepository.findRafflePlayedByWinCode(winCode);
        if(rafflePlayed == null){
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setData("Invalid winning code! Check your winning code");
            return response;
        }else if(rafflePlayed.isWinClaimed()){
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setData("Win has been claimed");
            return response;
        }

        //send otp
        String otp = String.format("%04d", new Random().nextInt(10000));
        rafflePlayed.setOtp(otp);
        rafflePlayedRepository.save(rafflePlayed);

        String message = "Dear winner, kindly use the code "+otp+" to authenticate your win reward transfer";
        smsService.sendSMS(rafflePlayed.getPhone(), message);

        response.setStatus(APIResponse.SUCCESS_STATUS);
        response.setMessage(APIResponse.SUCCESS_MESSAGE);

        return response;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/no-cache/claim-raffle")
    public @ResponseBody
    APIResponse claimRaffle(HttpServletRequest request,
                           @RequestParam(name = "deviceId", required = true) String deviceId,
                           @RequestParam(name = "accountNumber", required = true) String accountNumber,
                           @RequestParam(name = "bankCode", required = true) String bankCode,
                           @RequestParam(name = "otp", required = true) String otp,
                           @RequestParam(name = "winCode", required = true) String winCode) throws Exception {
        APIResponse response = new APIResponse();
        //confirm otp
        RafflePlayed rafflePlayed = rafflePlayedRepository.findRafflePlayedByWinCode(winCode);
        if(rafflePlayed == null){
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setData("Invalid winning code! Check your winning code");
            return response;
        }else if(rafflePlayed.isWinClaimed()){
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setData("Win has been claimed");
            return response;
        }

        if(!rafflePlayed.getOtp().equalsIgnoreCase(otp)){
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setData("Invalid OTP code!");
            return response;
        }

        try {
            claimService.processRaffleClaim(winCode, accountNumber, bankCode);
            response.setStatus(APIResponse.SUCCESS_STATUS);
            response.setMessage(APIResponse.SUCCESS_MESSAGE);
        }catch(KolorbiException e){
            e.printStackTrace();
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setData(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setData("Win cash transfer failed! Try again later");
        }
        return response;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/no-cache/play-gift")
    public @ResponseBody
    APIResponse playGift(HttpServletRequest request,
                           @RequestParam(name = "deviceId", required = true) String deviceId) throws Exception {
        long count = counterGenerator.getNextCounter(Gift.class.getName());

        APIResponse response = new APIResponse();
        response.setMessage(APIResponse.FAILED_MESSAGE);
        response.setStatus(APIResponse.FAIL_STATUS);
        Gift gift = null;
        if((count % giftCount) == 0){
            gift = new Gift();
            gift.setWinClaimed(false);
            gift.setCreateDate(new Date());
            gift.setDeviceId(deviceId);
            gift.setWinAmount(100);
            gift.setWinCode(new Date().getTime()+"");
            giftRepository.save(gift);

            response.setMessage(APIResponse.SUCCESS_MESSAGE);
            response.setStatus(APIResponse.SUCCESS_STATUS);
            response.setData(gift);
        }
        return response;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/no-cache/claim-gift")
    public @ResponseBody
    APIResponse claimGift(HttpServletRequest request, HttpSession httpSession,
                            @RequestParam(name = "deviceId", required = true) String deviceId,
                            @RequestParam(name = "phone", required = true) String phone,
                            @RequestParam(name = "winCode", required = true) String winCode,
                            @RequestParam(name = "type", required = true) String type) throws Exception {
        APIResponse response = new APIResponse();

        try {
            claimService.processGiftClaim(winCode, phone, type);
            response.setStatus(APIResponse.SUCCESS_STATUS);
            response.setMessage(APIResponse.SUCCESS_MESSAGE);
        }catch(KolorbiException e){
            e.printStackTrace();
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setData(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(APIResponse.FAIL_STATUS);
            response.setMessage(APIResponse.FAILED_MESSAGE);
            response.setData("Win reward transfer failed! Try again later");
        }
        return response;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/get-raffle-counter")
    public @ResponseBody
    APIResponse getRaffleCounters() throws Exception {
        APIResponse response = new APIResponse();

        Map<String, Long> counters = new HashMap<>();
        raffleRepository.findAll().forEach(raffle -> {
            counters.put(raffle.getId(), counterGenerator.getCount(raffle.getName()) % raffleSize);
        });

        response.setMessage(APIResponse.SUCCESS_MESSAGE);
        response.setStatus(APIResponse.SUCCESS_STATUS);
        response.setData(counters);
        return response;
    }

}
