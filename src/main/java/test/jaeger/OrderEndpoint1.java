package test.jaeger;

import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;


@Stateless
@Path("/orders")
public class OrderEndpoint1 {

    @GET
    @Path("/")
    public String placeOrder() {
        Tracer tracer = GlobalTracer.get();
        Scope scope = tracer.buildSpan("Inside Place orders controller").startActive(true);
        // business logic
        int sum = 30 +40;
        double avg = sum/2;
        scope.span().setTag("Avg",avg);
        String response = this.getHttp();
        scope.span().setTag("Customers API call response", response);
        scope.close();
        return "Order placed";
    }

    private String getHttp() {
        Tracer tracer = GlobalTracer.get();
        try {
            OkHttpClient client = new OkHttpClient();
            HttpUrl url = new HttpUrl.Builder().scheme("http").host("localhost").port(8080).addPathSegment("java-microservice-1.0-SNAPSHOT").addPathSegment("customers")
                    .addQueryParameter("name", "Rajni")
                    .build();
            Request.Builder requestBuilder = new Request.Builder().url(url);
            //  In order to pass its context over the HTTP request we need to call tracer.inject before building the HTTP request in Hello#getH
            Tags.SPAN_KIND.set(tracer.activeSpan(), Tags.SPAN_KIND_CLIENT);
            Tags.HTTP_METHOD.set(tracer.activeSpan(), "GET");
            Tags.HTTP_URL.set(tracer.activeSpan(), "/customers");
            tracer.inject(tracer.activeSpan().context(), Format.Builtin.HTTP_HEADERS, new RequestBuilderCarrier(requestBuilder));

            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                throw new RuntimeException("Bad HTTP result: " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}