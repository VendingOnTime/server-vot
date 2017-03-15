package com.vendingontime.backend.routes.utils;

/**
 * Created by alberto on 15/3/17.
 */
public interface Response {
    AppRoute ok(Object body);
    AppRoute created(Object body);
    AppRoute badRequest(Object cause);
    AppRoute unauthorized();
    AppRoute notFound();
}
