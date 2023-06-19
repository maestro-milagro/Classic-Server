import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

public class Request {
    private String method;
    private List<String> header;
    private String body;
    private String path;
    private List<NameValuePair> query;
    public Request(String method, String path, String body, List<String> header, List<NameValuePair> query){
        this.body = body;
        this.header = header;
        this.method = method;
        this.path = path;
        this.query = query;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setQuery(List<NameValuePair> query) {
        this.query = query;
    }
    public String getQueryParam(String name){
        for(NameValuePair nvp : query){
            if (nvp.getName().equals(name)){
                return nvp.getValue();
            }
        }
        return null;
    }
    public List<String> getQueryParams() {
        List<String> params = new ArrayList<>();
        for (NameValuePair nvp : query) {
            params.add(nvp.getValue());
        }
        return params;
    }
}
