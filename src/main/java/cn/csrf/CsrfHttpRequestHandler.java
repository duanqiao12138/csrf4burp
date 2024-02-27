package cn.csrf;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;

import java.net.MalformedURLException;

public class CsrfHttpRequestHandler implements ProxyRequestHandler {
    private final Logging logging ;
    private final CsrfTab tab;

    CsrfHttpRequestHandler(MontoyaApi montoyaApi, CsrfTab tab) {
        this.logging = montoyaApi.logging();
        this.tab = tab;
    }
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        try {
            tab.requestHandler(interceptedRequest);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return null;
    }
}
