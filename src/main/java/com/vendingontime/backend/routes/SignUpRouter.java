package com.vendingontime.backend.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingontime.backend.models.bodymodels.person.SignUpData;
import com.vendingontime.backend.routes.utils.AppRoute;
import com.vendingontime.backend.routes.utils.ServiceResponse;
import com.vendingontime.backend.services.SignUpService;
import com.vendingontime.backend.services.utils.BusinessLogicException;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by alberto on 28/3/17.
 */
public class SignUpRouter implements SparkRouter {
    public final static String MALFORMED_JSON = "MALFORMED_JSON";

    public static final String V1_SIGN_UP_SUPERVISOR = "/api-v1/signup/supervisor";

    private final SignUpService service;
    private final ServiceResponse serviceResponse;
    private final ObjectMapper mapper;

    @Inject
    public SignUpRouter(SignUpService service, ServiceResponse serviceResponse) {
        this.service = service;
        this.serviceResponse = serviceResponse;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void configure(Service http) {
        http.post(V1_SIGN_UP_SUPERVISOR, map((req, res) -> signUpSupervisor(req.body())));
    }

    public AppRoute signUpSupervisor(String body) {
        try {
            SignUpData supervisorCandidate = mapper.readValue(body, SignUpData.class);

            return serviceResponse.created(service.createSupervisor(supervisorCandidate));
        } catch (BusinessLogicException ex) {
            return serviceResponse.badRequest(ex.getCauses());
        } catch (IOException ex) {
            return serviceResponse.badRequest(MALFORMED_JSON);
        }
    }

    private Route map(Converter c) {
        return (req, res) -> c.convert(req, res).handle(req,res);
    }

    private interface Converter {
        public AppRoute convert(Request req, Response res);
    }
}
