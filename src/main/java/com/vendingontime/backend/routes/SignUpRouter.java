package com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.bodymodels.SignUpData;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.Response;
import com.vendingontime.backend.services.SignUpService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import spark.Service;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by alberto on 28/3/17.
 */
public class SignUpRouter implements SparkRouter {
    private final SignUpService service;

    public final static String MALFORMED_JSON = "MALFORMED_JSON";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Response response;

    @Inject
    public SignUpRouter(SignUpService service, Response response) {
        this.service = service;
        this.response = response;
    }

    @Override
    public void configure(Service http) {
        http.post("/api-v1/signup/supervisor", (req, res) -> signUpSupervisor(req.body()));
    }

    public AppRoute signUpSupervisor(String body) {
        try {
            SignUpData supervisorCandidate = mapper.readValue(body, SignUpData.class);

            return response.created(service.createSupervisor(supervisorCandidate));
        } catch (BusinessLogicException ex) {
            return response.badRequest(ex.getCauses());
        } catch (IOException ex) {
            return response.badRequest(MALFORMED_JSON);
        }
    }
}
