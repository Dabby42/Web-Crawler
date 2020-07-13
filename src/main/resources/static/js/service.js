class Service {

    constructor(){
        //this.serviceHost = 'https://staging-api.kolorbi.com/';
        //this.serviceHost = 'http://localhost:8090/';
        this.serviceHost = '/';
        this.serviceEndpoint = this.serviceHost+'endpoint-api/';
    }

    getNewsbyID(id) {
        let data = new FormData();
        data.append('id',id);

        return axios.post(this.serviceEndpoint + 'get-news-by-id', data);
    }

    getNewsbyDevicePreference(deviceId, page, size) {
        let data = new FormData();
        data.append('deviceId',deviceId);
        data.append('page',page);
        data.append('size',size);

        return axios.post(this.serviceEndpoint + 'get-news', data);
    }

    playRaffle(phone, deviceId, raffleId, paymentReference, paymentChannel) {
        let data = new FormData();
        data.append('phone',phone);
        data.append('deviceId',deviceId);
        data.append('raffleId',raffleId);
        data.append('paymentReference',paymentReference);
        data.append('paymentChannel',paymentChannel);

        return axios.post(this.serviceEndpoint + 'no-cache/play-raffle', data);
    }

    sendRaffleOTP(deviceId, winCode) {
        let data = new FormData();
        data.append('deviceId',deviceId);
        data.append('winCode',winCode);

        return axios.post(this.serviceEndpoint + 'no-cache/send-raffle-otp', data);
    }

    claimRaffle(deviceId, accountNumber, bankCode, otp, winCode) {
        let data = new FormData();
        data.append('deviceId',deviceId);
        data.append('accountNumber',accountNumber);
        data.append('bankCode',bankCode);
        data.append('otp',otp);
        data.append('winCode',winCode);

        return axios.post(this.serviceEndpoint + 'no-cache/claim-raffle', data);
    }

    playGift(deviceId) {
        let data = new FormData();
        data.append('deviceId',deviceId);

        return axios.post(this.serviceEndpoint + 'no-cache/play-gift', data);
    }

    claimGift(deviceId, phone, winCode, type) {
        let data = new FormData();
        data.append('deviceId',deviceId);
        data.append('phone',phone);
        data.append('winCode',winCode);
        data.append('type',type);

        return axios.post(this.serviceEndpoint + 'no-cache/claim-gift', data);
    }

    getRaffleCounters() {
        return axios.post(this.serviceEndpoint + 'get-raffle-counter');
    }

}