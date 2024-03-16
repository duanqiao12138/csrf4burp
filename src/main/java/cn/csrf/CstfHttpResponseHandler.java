package cn.csrf;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.http.InterceptedResponse;
import burp.api.montoya.proxy.http.ProxyResponseHandler;
import burp.api.montoya.proxy.http.ProxyResponseReceivedAction;
import burp.api.montoya.proxy.http.ProxyResponseToBeSentAction;

import java.net.MalformedURLException;

public class CstfHttpResponseHandler implements ProxyResponseHandler {
    private final Logging logging ;
    private final CsrfTab tab;
    public CstfHttpResponseHandler(MontoyaApi montoyaApi, CsrfTab tab) {
        this.logging = montoyaApi.logging();
        this.tab = tab;
    }
    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        tab.responseHandler(interceptedResponse);
        return null;
    }

    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return null;
    }
}
