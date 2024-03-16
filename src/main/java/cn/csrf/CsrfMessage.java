package cn.csrf;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;

public class CsrfMessage {
    final Integer row;
    final HttpRequest baseRequest;

    public HttpResponse baseResponse;

    public HttpResponse noCookieResponse;
    public HttpResponse randomRefererResponse;

    CsrfMessage(Integer row, HttpRequest baseRequest) {
        this.row = row;
        this.baseRequest = baseRequest;
    }

}
