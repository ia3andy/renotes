package rest;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.Qute;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.http.HttpServerResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
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
        TRIGGER_AFTER_SETTLE("HX-Trigger-After-Settle");

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

    public static TemplateInstance concatTemplates(TemplateInstance... templates) {
        return Qute.fmt("{#each elements}{it.raw}{/each}")
                .cache()
                .data("elements", Arrays.stream(templates).map(TemplateInstance::createUni))
                .instance();
    }

    protected void flashHxRequest() {
        flash(HX_REQUEST_HEADER, isHxRequest());
    }

    protected void onlyHxRequest() {
        if (!isHxRequest()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST).entity("Only Hx request are allowed on this method").build());
        }
    }

    @Override
    protected void prepareForErrorRedirect() {
        flashHxRequest();
        super.prepareForErrorRedirect();
    }

    protected boolean isHxRequest() {
        final boolean hxRequest = Objects.equals(flash.get(HX_REQUEST_HEADER), true);
        if (hxRequest) {
            return true;
        }
        return Objects.equals(httpHeaders.getHeaderString(HX_REQUEST_HEADER), "true");
    }

    protected void hx(HxResponseHeader hxHeader, String value) {
        response.headers().set(hxHeader.key(), value);
    }

}
