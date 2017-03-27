import spark.Service;

/**
 * Created by alberto on 27/3/17.
 */
public class RESTContext {

    private Service http;

    public RESTContext() {
        this.http = Service.ignite();
    }


}
