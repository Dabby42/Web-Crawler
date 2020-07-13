/* kolorbi app object instance */
let kolorbi = new KolorbiApp();
kolorbi.initializeNewsListHandler();
kolorbi.getDeviceId();

/*events handlers ***********************************/


$("#awoof_redeem").submit(function (event) {
    event.preventDefault();
    let phone = $(this).find('input[name="tel"]').val();
    if (kolorbi.isValidNaijaNumber(phone)) {
        $(".card-box--play-lottery").fadeOut();
        kolorbi.gift.phone = phone;

        NProgress.start();
        let promise = service.claimGift(kolorbi.getDeviceId(), kolorbi.gift.phone, kolorbi.gift.winCode, 'AIRTIME');
        promise.then(function (response) {
            if (response.data.status == 0) {
                //swal('Awoof reward transferred successfully');
                $(".card-box--win").fadeOut();
                $(".play-card--success").addClass("animated fadeInUp").show();
            } else {
                swal(response.data.data);
            }
        }).catch(function (error) {
            swal('Win reward transfer failed! Try again later');
        }).then(() => {
            kolorbi.raffle = undefined;
            NProgress.done();
        });
    }else{
        $(this).find('input[name="tel"]')[0].setCustomValidity('Please enter a valid nigerian phone number');
    }
});


$('#raffle_redeem').submit(function (event) {
    event.preventDefault();
    kolorbi.raffleClaim = {};

    kolorbi.raffleClaim.winCode = $(this).find('input[name="winCode"]').val();
    kolorbi.raffleClaim.bankCode = $('#bankCode').val();//$(this).find('input[name="bankCode"]').val();
    kolorbi.raffleClaim.accountNumber = $(this).find('input[name="accountNumber"]').val();
    kolorbi.raffleClaim.otp = $(this).find('input[name="otp"]').val();
    let submit = $(this).find('input[name="submit"]').val();

    if(submit == 'Proceed'){
        NProgress.start();
        let promise = service.sendRaffleOTP(kolorbi.getDeviceId(), kolorbi.raffleClaim.winCode);
        promise.then(function (response) {
            if (response.data.status == 0) {
                swal('OTP has been sent to your phone, Insert OTP to claim win');
                $('#otp-container')[0].style.display = 'block';
                $('#raffle_redeem').find('input[name="submit"]').val('Redeem');
            } else {
                swal(response.data.data);
                kolorbi.raffleClaim == undefined;
            }
        }).catch(function (error) {
            //console.log(error);
            swal('OTP process failed, Try again later');
        }).then(() => {
            NProgress.done();
        });
    }else{
        if(kolorbi.raffleClaim.otp){
            NProgress.start();
            let promise = service.claimRaffle(kolorbi.getDeviceId(), kolorbi.raffleClaim.accountNumber, kolorbi.raffleClaim.bankCode, kolorbi.raffleClaim.otp, kolorbi.raffleClaim.winCode);
            promise.then(function (response) {
                if (response.data.status == 0) {
                    $(".card-box--raffle-win").fadeOut();
                    $(".play-card--success").addClass("animated fadeInUp").show();
                    kolorbi.raffleClaim = undefined;
                } else {
                    swal(response.data.data);
                }
            }).catch(function (error) {
                //console.log(error);
                swal('OTP process failed, Try again later');
            }).then(() => {
                NProgress.done();
            });
        }else{
            $(this).find('input[name="otp"]')[0].setCustomValidity('Insert your valid OTP');
        }
    }
});

$('#resend-otp').on('click',function (event){
    if(kolorbi.raffleClaim.winCode){
        NProgress.start();
        let promise = service.sendRaffleOTP(kolorbi.getDeviceId(), kolorbi.raffleClaim.winCode);
        promise.then(function (response) {
            if (response.data.status == 0) {
                swal('OTP has been sent to your phone, Insert OTP to claim win');
                $('#otp-container')[0].style.display = 'block';
            }
        }).catch(function (error) {
            //console.log(error);
            swal('OTP process failed, Try again later');
        }).then(() => {
            NProgress.done();
        });
    }
});

$('.claim-raffle-win').on('click', function (event){
    $('#otp-container')[0].style.display = 'none';
    $('#raffle_redeem').find('input[name="otp"]').val('');
    $('#raffle_redeem').find('input[name="submit"]').val('Proceed');
    $('.card-box--raffle-win').addClass("animated fadeInUp").show();
});

//close popups on click
kolorbi.closePopups();

$(".card-box--close").on('click', function () {
    kolorbi.closeLotteryPopups(this);
});

$("#raffle_form").submit(function (event) {
    event.preventDefault();
    let phone = $(this).find('input[name="tel"]').val();
    if (kolorbi.isValidNaijaNumber(phone)) {
        $(".card-box--play-lottery").fadeOut();
        kolorbi.raffle.phone = phone;

        let x = getpaidSetup({
            PBFPubKey: kolorbi.flutterwaveApiPublicKey,
            customer_email: kolorbi.raffle.phone + "@kolorbi.com",
            amount: kolorbi.raffle.amount,
            customer_phone: kolorbi.raffle.phone,
            currency: "NGN",
            txref: "rave" + ID(),
            meta: [{
                metaname: "deviceID",
                metavalue: kolorbi.getDeviceId()
            }],
            onclose: function () {
            },
            callback: function (response) {
                let txref = response.tx.txRef; // collect txRef returned and pass to a 					server page to complete status check.
                if (
                    response.tx.chargeResponseCode == "00" ||
                    response.tx.chargeResponseCode == "0"
                ) {
                    // redirect to a success page
                    NProgress.start();
                    let promise = service.playRaffle(kolorbi.raffle.phone, kolorbi.getDeviceId(),
                        kolorbi.raffle.amount, txref, 'FLUTTERWAVE_RAVE');

                    promise.then(function (response) {
                        if (response.data.status == 0) {
                            swal('Raffle played successfully, your Raffle ticket number is '+response.data.data.ticketNumber);
                        } else {
                            swal(response.data.data);
                        }
                    }).catch(function (error) {
                        //console.log(error);
                        swal('Unable to complete process, check internet connection');
                    }).then(() => {
                        kolorbi.raffle = undefined;
                        NProgress.done();
                    });
                } else {
                    // redirect to a failure page.
                    swal('Raffle payment failed, try again later');
                }

                x.close(); // use this to close the modal immediately after payment.
            }
        });
    } else {
        $(this).find('input[name="tel"]')[0].setCustomValidity('Please enter a valid nigerian phone number');
    }
});

$('.card-box__wheel').on("click", function () {

    kolorbi.rotateWheel();

});

//try rolling the wheel again
$(".button-try-wheel").click(function () {
    kolorbi.retryRotateWheel();
});

//handle play click
$('.lottery-btn').on('click', function () {
    kolorbi.raffle = {};
    kolorbi.raffle.amount = $(this).data('amount');
    kolorbi.raffle.winAmount = $(this).data('win-amount');
    $('#raffle_win_amount').text(kolorbi.formatMoney(kolorbi.raffle.winAmount));
    $('#raffle_amount').text(kolorbi.formatMoney(kolorbi.raffle.amount));
    kolorbi.showLotteryForm();
});

// close  data and airtime wheel pop ups
$(".card-box--close").click(function () {
    kolorbi.closePopups();
});

// closes the redeem price popup
$(".play-card--close").click(function () {
    kolorbi.closeRedeemPopUp();
});

//handle try again
$(".button-try").click(function () {
    kolorbi.retryLottery();
});

$('.view').click(function(e){
    e.preventDefault();
    $.router.go('/ara', 'title');
})

//handle push notifications
// kolorbi.pushNotifications();

//handle swipe effect
kolorbi.swipeEffect();

//disable anim after 3000
kolorbi.disableAnim(3000);

function ID() {
    // Math.random should be unique because of its seeding algorithm.
    // Convert it to base 36 (numbers + letters), and grab the first 9 characters
    // after the decimal.
    return '_' + Math.random().toString(36).substr(2, 9);
};

// TODO add service worker code here
/*if ('serviceWorker' in navigator) {
navigator.serviceWorker
         .register('/service-worker.js')
         .then(function() { console.log('Service Worker Registered'); });
}*/

$(document).ready(function(){

     kolorbi.bankCodes.forEach(bankCode => {
           $('#bankCode').append($("<option />").val(bankCode.Code).text(bankCode.Name));
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