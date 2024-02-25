package cn.csrf;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class Csrf4burp implements BurpExtension {

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        montoyaApi.extension().setName("CSRF4Burp");
        CsrfTab csrfTab = new CsrfTab(montoyaApi);
        montoyaApi.userInterface().registerSuiteTab("CSRF4Burp", csrfTab);
        montoyaApi.proxy().registerRequestHandler(new CsrfHttpRequestHandler(montoyaApi, csrfTab));
    }
}
