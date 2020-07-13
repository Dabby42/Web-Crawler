

(function() {

    handleRouting();

})();

window.onpopstate = function(event) {
    handleRouting();
};

function handleRouting() {
    let  path = window.location.pathname;
    let firstPath = path.split('/')[1];

    switch (firstPath) {

        case '':
            if(kolorbi.newsPositionId){
                $('html, body').animate({
                    scrollTop: $('#'+kolorbi.newsPositionId).offset().top
                }, 0);
                kolorbi.newsPositionId = undefined;
            }
            break;
        case 'raffle':
            kolorbi.swiper.slideTo( 1,1000,false );
            break;
        case 'gift':
            kolorbi.swiper.slideTo( 2,1000,false );
            break;

    }
};