package rest;

import io.quarkiverse.renarde.Controller;
import io.vertx.core.http.HttpServerResponse;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import java.util.Objects;

public class HxController extends Controller {

    public static final String HX_REQUEST_HEADER = "HX-Request";

    public enum HxResponseHeader {
        // triggers client side events
        TRIGGER("HX-Trigger"),
        // triggers a client-side redirect to a new location
        REDIRECT("HX-Redirect"),
        // triggers a client-side redirect to a new location that acts as a swap
        LOCATION("HX-Location"),
        // if set to "true" the client side will do a full refresh of the page
        REFRESH("HX-Location"),
        // pushes a new URL into the browser’s address bar
        PUSH("HX-Push"),
        // triggers client side events after the swap step
        TRIGGER_AFTER_SWAP("HX-Trigger-After-Swap"),
        // triggers client side events after the settle step
        TRIGGER_AFTER_SETTLE("HX-Trigger-After-Settle")
        ;

        private final String key;

        HxResponseHeader(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }
    }

    @Inject
    protected HttpHeaders httpHeaders;

    @Inject
    protected HttpServerResponse response;

    protected void flashHxRequest() {
        flash(HX_REQUEST_HEADER, isHxRequest());
    }

    @Override
    protected void prepareForErrorRedirect() {
        flashHxRequest();
        super.prepareForErrorRedirect();
    }
    
    protected boolean isHxRequest() {
        final boolean hxRequest = Objects.equals(flash.get(HX_REQUEST_HEADER), true);
        if(hxRequest) {
            return true;
        }
        return Objects.equals(httpHeaders.getHeaderString(HX_REQUEST_HEADER), "true");
    }

    protected void hx(HxResponseHeader hxHeader, String value) {
        response.headers().set(hxHeader.key(), value);
    }

}
