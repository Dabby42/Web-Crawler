package com.gsd.kolorbi.controller;

import com.gsd.kolorbi.model.News;
import com.gsd.kolorbi.repository.NewsRepository;
import com.gsd.kolorbi.repository.RaffleRepository;
import com.gsd.kolorbi.service.CounterGenerator;
import com.gsd.kolorbi.service.NewsService;
import com.gsd.kolorbi.service.ViewCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class KolorbiController  implements ErrorController {

    @Value("${kolorbi.raffle.size}")
    private int raffleSize;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsService newsService;

    @Autowired
    private RaffleRepository raffleRepository;

    @Autowired
    private CounterGenerator counterGenerator;

    @Autowired
    private ViewCounterService viewCounterService;

    @RequestMapping(value ={"/", "/raffle", "/gift"})
    public String home(@RequestParam(name = "page", required = false) Integer page,
                       Map<String, Object> model,
                       @CookieValue(value="deviceId", required=false) String deviceId) throws Exception {

        boolean deviceIdExists = true;
        if(deviceId == null){
            deviceId = "NG";
            deviceIdExists = false;
        }
        if(page == null){
            page = 1;
        }

        Map<String, Long> counters = new HashMap<>();
        counters.put("100", 0l);
        counters.put("200", 0l);
        counters.put("500", 0l);
        counters.put("1000", 0l);
        raffleRepository.findAll().forEach(raffle -> {
            counters.put(raffle.getId(), counterGenerator.getCount(raffle.getName()) % raffleSize);
        });

        List<News> allNews = newsService.getNewsForDeviceId(deviceId, page, 15);
        model.put("allNews", allNews);
        model.put("counters", counters);
        model.put("deviceIdExists", deviceIdExists);
        model.put("page", page);
        return "index";
    }

    @RequestMapping("/news/{newsId}/**")
    public String welcome(@PathVariable(name = "newsId") String newsId,
                          Map<String, Object> model,
                            @CookieValue(value="deviceId", required=false) String deviceId) throws Exception {

        News news = newsRepository.findById(newsId).orElse(null);
        List<News> relatedNews = null;
        if(news != null){
            relatedNews = newsService.getRelatedNews(news.getCategory(), 1, 5);
        }
        model.put("news", news);
        model.put("relatedNews", relatedNews);
        viewCounterService.count(deviceId, news.getSource(), news.getCategory());

        return "news";
    }

    @RequestMapping(value = "/error")
    public String handleIOException(HttpServletRequest request, Map<String, Object> model) {
        // prepare responseEntity
        String message = "Unable to process request, Try again later";
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                message = "Invalid Web Address";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                message = "Unable To Process Request";
            }
        }

        model.put("message", message);
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
