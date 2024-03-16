package cn.csrf;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class Csrf4burp implements BurpExtension {
    public static MontoyaApi Api;
    @Override
    public void initialize(MontoyaApi montoyaApi) {
        Api = montoyaApi;
        Api.extension().setName("CSRF4Burp");
        CsrfTab csrfTab = new CsrfTab(montoyaApi);
        Api.userInterface().registerSuiteTab("CSRF4Burp", csrfTab);
        Api.proxy().registerRequestHandler(new CsrfHttpRequestHandler(montoyaApi, csrfTab));
        Api.proxy().registerResponseHandler(new CstfHttpResponseHandler(montoyaApi, csrfTab));
        Api.userInterface().registerContextMenuItemsProvider(new CsrfMenuItemsProvider(Api));
    }
}
