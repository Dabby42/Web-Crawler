/* kolorbi app object instance */
let kolorbi = new KolorbiApp();

/*events handlers ***********************************/


function ID() {
    // Math.random should be unique because of its seeding algorithm.
    // Convert it to base 36 (numbers + letters), and grab the first 9 characters
    // after the decimal.
    return '_' + Math.random().toString(36).substr(2, 9);
};

$(document).ready(function(){
    $(".owl-carousel").owlCarousel({
        loop: true,
        autoplay: false,
        rtl: true,
        nav: false,
        touchDrag: true,
    });
});

//handle youtube play
$('.play').on('click', function () {
    let newsSourceURL = $(this).data('url');
    let e = $(this)[0];
    var iframe = document.createElement("iframe");
    iframe.setAttribute("src", newsSourceURL + '?autoplay=1');
    iframe.setAttribute("frameborder", "0");
    iframe.setAttribute("allowfullscreen", "1");
    e.parentNode.replaceChild(iframe, e);
});

//back button click
$(".app-header__back-icon").on('click', function () {
    //kolorbi.handleBackButton();
    window.history.back();
});