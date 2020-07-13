

service = new Service();
Date.prototype.toShortFormat = function () {

	var month_names = ["Jan", "Feb", "Mar",
		"Apr", "May", "Jun",
		"Jul", "Aug", "Sep",
		"Oct", "Nov", "Dec"];

	var day = this.getDate();
	var month_index = this.getMonth();
	var year = this.getFullYear();

	return month_names[month_index] + " " + day + ", " + year;
}

String.prototype.replaceAt=function(index, replacement) {
    return this.substr(0, index) + replacement+ this.substr(index + replacement.length);
}

class KolorbiApp {

	constructor() {
		this.swiper;
		this.loadingNews = false;

		this.newsPage = 1;
		this.newsPageSize = 10;

		this.newsObjects = {};
		this.gift;
		this.newslist;

		this.raffle;
		this.raffleClaim;
		this.flutterwaveApiPublicKey = "FLWPUBK-8f00aae66e5c27a9c78b71ad9c1503c2-X";

		this.newsPositionId;
		this.bankCodes = [{'Id': 132,'Code': '560','Name': 'Page MFBank'},{'Id': 133,'Code': '304','Name': 'Stanbic Mobile Money'},{'Id': 134,'Code': '308','Name': 'FortisMobile'},{'Id': 135,'Code': '328','Name': 'TagPay'},{'Id': 136,'Code': '309','Name': 'FBNMobile'},{'Id': 137,'Code': '011','Name': 'First Bank of Nigeria'},{'Id': 138,'Code': '326','Name': 'Sterling Mobile'},{'Id': 139,'Code': '990','Name': 'Omoluabi Mortgage Bank'},{'Id': 140,'Code': '311','Name': 'ReadyCash (Parkway)'},{'Id': 141,'Code': '057','Name': 'Zenith Bank'},{'Id': 142,'Code': '068','Name': 'Standard Chartered Bank'},{'Id': 143,'Code': '306','Name': 'eTranzact'},{'Id': 144,'Code': '070','Name': 'Fidelity Bank'},{'Id': 145,'Code': '023','Name': 'CitiBank'},{'Id': 146,'Code': '215','Name': 'Unity Bank'},{'Id': 147,'Code': '323','Name': 'Access Money'},{'Id': 148,'Code': '302','Name': 'Eartholeum'},{'Id': 149,'Code': '324','Name': 'Hedonmark'},{'Id': 150,'Code': '325','Name': 'MoneyBox'},{'Id': 151,'Code': '301','Name': 'JAIZ Bank'},{'Id': 152,'Code': '050','Name': 'Ecobank Plc'},{'Id': 153,'Code': '307','Name': 'EcoMobile'},{'Id': 154,'Code': '318','Name': 'Fidelity Mobile'},{'Id': 155,'Code': '319','Name': 'TeasyMobile'},{'Id': 156,'Code': '999','Name': 'NIP Virtual Bank'},{'Id': 157,'Code': '320','Name': 'VTNetworks'},{'Id': 158,'Code': '221','Name': 'Stanbic IBTC Bank'},{'Id': 159,'Code': '501','Name': 'Fortis Microfinance Bank'},{'Id': 160,'Code': '329','Name': 'PayAttitude Online'},{'Id': 161,'Code': '322','Name': 'ZenithMobile'},{'Id': 162,'Code': '303','Name': 'ChamsMobile'},{'Id': 163,'Code': '403','Name': 'SafeTrust Mortgage Bank'},{'Id': 164,'Code': '551','Name': 'Covenant Microfinance Bank'},{'Id': 165,'Code': '415','Name': 'Imperial Homes Mortgage Bank'},{'Id': 166,'Code': '552','Name': 'NPF MicroFinance Bank'},{'Id': 167,'Code': '526','Name': 'Parralex'},{'Id': 168,'Code': '035','Name': 'Wema Bank'},{'Id': 169,'Code': '084','Name': 'Enterprise Bank'},{'Id': 170,'Code': '063','Name': 'Diamond Bank'},{'Id': 171,'Code': '305','Name': 'Paycom'},{'Id': 172,'Code': '100','Name': 'SunTrust Bank'},{'Id': 173,'Code': '317','Name': 'Cellulant'},{'Id': 174,'Code': '401','Name': 'ASO Savings and & Loans'},{'Id': 175,'Code': '030','Name': 'Heritage'},{'Id': 176,'Code': '402','Name': 'Jubilee Life Mortgage Bank'},{'Id': 177,'Code': '058','Name': 'GTBank Plc'},{'Id': 178,'Code': '032','Name': 'Union Bank'},{'Id': 179,'Code': '232','Name': 'Sterling Bank'},{'Id': 180,'Code': '076','Name': 'Skye Bank'},{'Id': 181,'Code': '082','Name': 'Keystone Bank'},{'Id': 182,'Code': '327','Name': 'Pagatech'},{'Id': 183,'Code': '559','Name': 'Coronation Merchant Bank'},{'Id': 184,'Code': '601','Name': 'FSDH'},{'Id': 185,'Code': '313','Name': 'Mkudi'},{'Id': 186,'Code': '214','Name': 'First City Monument Bank'},{'Id': 187,'Code': '314','Name': 'FET'},{'Id': 188,'Code': '523','Name': 'Trustbond'},{'Id': 189,'Code': '315','Name': 'GTMobile'},{'Id': 190,'Code': '033','Name': 'United Bank for Africa'},{'Id': 191,'Code': '044','Name': 'Access Bank'}];
	}

	getDeviceId() {
		var deviceId;
		//code for cookies storage
        var fp = new Fingerprint({
            canvas: true,
            ie_activex: true,
            screen_resolution: true
        });

        if (!document.cookie) {
            var expires = new Date();
            date.setDate(date.getDate() + 90);
            deviceId = fp.get();
            document.cookie = 'deviceId=' + deviceId + '; expires=' + expires;
        } else {
            deviceId = document.cookie.split(";")[0].replace("deviceId=", "").trim();
            var expires = new Date();
            expires.setDate(new Date().getDate() + 90);
            deviceId = fp.get();
            document.cookie = 'deviceId=' + deviceId + '; expires=' + expires;
        }

		return deviceId;
	}

	menuHandler(link) {
		//handle active menu
		$(".menu__btn").removeClass("active");
		let button = $(link).addClass('active');

		$(".card-box--win,.card-box--play-lottery").fadeOut();
		//display the appropriate sections
		if (button.hasClass('menu__btn--newsArray')) {
			$(".app-section").fadeOut(function () {

				$(".app--newsArray").show();
			});

			$(".app-header__icon").show();
			$(".app-header__back-icon").fadeOut();

		} else if (button.hasClass('menu__btn--awoof')) {
			$(".app-section").fadeOut(function () {
				$(".app--awoof").show();
			});


			$(".app-heaeader__icon").show();
			$(".app-hder__back-icon").fadeOut();

		} else if (button.hasClass('menu__btn--raffle')) {
			$(".app-section").fadeOut(function () {
				$(".app--play").show();
			});

			$(".app-header__icon").show();
			$(".app-header__back-icon").fadeOut();
		}
	}

	disableAnim(time) {
		setTimeout(function (time) {
			$(".article").removeClass('animated fadeInUp');
			$(".advert-placeholder").removeClass('animated fadeInUp');
			$(".lottery-card").removeClass('animated fadeInUp');
			$(".card-box").removeClass('animated fadeInUp');

		}, time);
	}

	swipeEffect() {
		kolorbi.swiper = new Swiper('.swiper-container', {
			speed: 400,
			spaceBetween: 100,
			autoHeight: true,
			pagination: {
				el: '.swiper-pagination',
				clickable: true,
				renderBullet: function (index, className) {
					let img = "";
					let menubtn = "menu__btn";
					if (index === 0) {
						img = "/images/svg/news.svg";
					} else if (index === 1) {
						img = "/images/svg/raffle.svg";
					} else if (index === 2) {
						img = "/images/svg/gift.svg";
					}
					return '<span class="' + className + " " + menubtn + '">' + '<img src=' + img + '>' + '</span>';
				}
			},
			direction: 'horizontal',
			slidesPerView: 1,

		});

	}

	reinitSwiper() {
		kolorbi.swiper.update();
	}

	closeLotteryPopups(link) {
		let removeLink = $(link);
		if (removeLink.hasClass('card-box__win--close')) {
			$(".card-box--win").fadeOut();
		} else if (removeLink.hasClass('card-box__win_raffle--close')) {
			$(".card-box--raffle-win").fadeOut();
		} else if (removeLink.hasClass('card-box__play--close')) {
			$(".card-box--play-lottery").fadeOut();
		} else if (removeLink.hasClass("play-card__win--close")) {
			$(".play-card--success").fadeOut();
		}
	}

	//close redeem price pop up
	closeRedeemPopUp() {
		$(".play-card--success").fadeOut();
	}

	// app back button handler
	handleBackButton() {
		$('.app--article').hide();
		$(".swiper-container").fadeIn();
		$('.app-header__back-icon').hide();
		$(".app-header__icon").show();
	}

	// app news page handler
	handleNewsPage(news, related) {
        $(".mini-article-wrapper").html('');
		$('#subject').html(news.subject);
		$('#source').html(news.source);
		$('#date').html(new Date(news.crawledDate).toShortFormat());
		$('#content').html('');

        if (news.source == 'youtube.com') {
            let article = '<article class="article animated fadeInUp" >' +
                								'<div class="container-fluid">' +
                								'<div class="row youtube-height">'+
                                                '<div class="youtube-player" >'+
                                                '<span class="article__subject">' + news.subject + '</span><img src="'+news.newsImageURL+'"><div onclick="labnolIframe(\''+news.sourceURL+'\', this)" class="play">'+
                                                '</div>'+
                								'</div>' +
                								'</div>' +
                								'</article>';
            news.contents = [];
            news.contents.push(article);
        }

		for (let i = 0; i < news.contents.length; i++) {
			$('#content').append(news.contents[i].replace(/{{_K_AD_CLIENT_}}/g, "ca-pub-9261824788583668").replace(/{{_K_AD_SLOT_}}/g, "5734509423").replace(/https:\/\/www.vanguardngr.com\/wp-content/g, service.serviceHost+'mirror?url=' +'https://www.vanguardngr.com/wp-content'));
			if (news.contents.length / 2 == i) {
				//add advert here
				//$('#content').append(news.contents[i]);
			}
		}


		$('.swiper-container').fadeOut(500, function () {
			$('.app--article').fadeIn(500);
		});
		$('.app-header__icon').hide();
		$('.app-header__back-icon').show();
		$('html, body').animate({
			scrollTop: $('.full-article').offset().top
		}, 0);

		if(related){
            related.forEach(news =>{
                let html = '<div class="mini-article item">' +
                    '<div class="mini-article__image-wrapper">' +
                    '<img src="'+service.serviceHost+'mirror?url=' + news.newsImageURL + '">' +
                    '</div>' +
                    '<div class="mini-article__info">' +
                    '<h4 class="mini-article__title"><span class="article__subject">' + news.subject + '</span></h4>' +
                    '<div class="mini-article__sub-info">' +
                    '<div><i class="fa fa-globe"></i>'+ news.source +'</div>' +
                    '<div><i class="fa fa-calendar"></i>'+new Date(news.crawledDate).toShortFormat()+'</div>' +
                    '</div>' +
                    '<a data-id="' + news.id + '" class="article__link"  href="'+ this.buildNewslink(news) +'">see more</a>' +
                    '</div>' +
                    '</div>';

                $(".mini-article-wrapper").append(html);
            });
        }
        this.relatedArticle();
        $('.full-article').show();
	}

	showLotteryForm() {
		//handle play click
		$(".card-box--play-lottery").addClass("animated fadeInUp").show();

	}

	//rotate the wheel
	rotateWheel() {


		$(".card-box__wheel").addClass('rotate');

        NProgress.start();
		service.playGift(kolorbi.getDeviceId())
			.then(function (response) {
				if (response.data.data) {
					setTimeout(function () {
						//show the result
						kolorbi.gift = response.data.data;
						$('.card-box--win').addClass("animated fadeInUp").show();
					}, 3000);

				} else {
					setTimeout(function () {
						//show the result
						kolorbi.gift = undefined;
						$('.card-box--fail').addClass("animated fadeInUp").show();
					}, 3000);
				}
			})
			.catch(function (error) {
				kolorbi.gift = undefined;
				//console.log(error);
			}).then(() => {
				setTimeout(function () {
					$(".card-box__wheel").removeClass('rotate');
				}, 3000);
				NProgress.done();
			});


	}

	retryLottery() {
		$(".play-card").fadeOut();
		$(".card-box--play-lottery").addClass("animated fadeInUp").show();
	}

	//handle user wheel game trial
	retryRotateWheel() {
		$('.card-box--fail').fadeOut();
		this.rotateWheel();
	}

	closePopups() {
		//close try again box
		$(".card-box__try--close").click(function () {
			$(".play-card--failure").fadeOut();
		});

		//close try again box
		$(".card-box__try--close").click(function () {
			$(".card-box--fail").fadeOut();
		});
	}


	//newsArray list handler
	initializeNewsListHandler() {
		let newsbody = $(".app--news");
		this.newslist = new infinity.ListView(newsbody);
	}

	//load newsArray contents
	loadNewsContents() {
		// call for more contents for newsArray
		//kolorbi.getDeviceId(), kolorbi.newsPage, kolorbi.newsPageSize
	}

	formatMoney(amount, decimalCount = 0, decimal = ".", thousands = ",") {
		try {
			decimalCount = Math.abs(decimalCount);
			decimalCount = isNaN(decimalCount) ? 2 : decimalCount;

			const negativeSign = amount < 0 ? "-" : "";

			let i = parseInt(amount = Math.abs(Number(amount) || 0).toFixed(decimalCount)).toString();
			let j = (i.length > 3) ? i.length % 3 : 0;

			return negativeSign + (j ? i.substr(0, j) + thousands : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands) + (decimalCount ? decimal + Math.abs(amount - i).toFixed(decimalCount).slice(2) : "");
		} catch (e) {
			//console.log(e)
		}
	};

	isValidNaijaNumber(n) {

		var firstChar;
		var number;
		var pattern = /^([0]{1})([7-9]{1})([0|1]{1})([\d]{1})([\d]{7,8})$/g;

		if (!n || n.length < 5) return false;

		if (typeof n === 'number') {

			// numbers never begin with 0, force this to become a string
			number = '0' + n;

		} else if (typeof n === 'string') {

			firstChar = n.substring(0, 1);

			// user may supply 0 before the number or not
			// e.g 0703 or 703 (two types of people ¯\_(ツ)_/¯)
			// either way supply missing leading 0
			number = (firstChar === '0') ? n : '0' + n;

		} else {

			return false;

		}

		// remove all whitespace(s) before running test
		return pattern.test(number.replace(/\s+/g, ''));

	};

}