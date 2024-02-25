package cn.csrf;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;

public class CsrfHttpRequestHandler implements ProxyRequestHandler {
    private final Logging logging ;
    private final CsrfTab tab;

    CsrfHttpRequestHandler(MontoyaApi montoyaApi, CsrfTab tab) {
        this.logging = montoyaApi.logging();
        this.tab = tab;
    }
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        String body = interceptedRequest.bodyToString();
        logging.logToOutput("Request received: " + interceptedRequest.url());
        logging.logToOutput("Request method: " + interceptedRequest.method());
        logging.logToOutput("Request headers: " + interceptedRequest.headers());
        logging.logToOutput("Request body: " + body);
        return null;
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return null;
    }
}
